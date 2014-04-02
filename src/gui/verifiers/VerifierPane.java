package gui.verifiers;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EtchedBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import model.Model;
import utils.BeagleInvoker;
import utils.Config;
import elts.ELTSGenerator;
import events.ModuleEditEvent;
import events.NoteEditEvent;
import events.ObjectEditEvent;
import events.StateEditEvent;
import events.TransitionEditEvent;
import events.listeners.ObjectEditListener;
import gui.Environment;

/**
 * verifier pane
 * 
 * @author Wei He
 * 
 */
public class VerifierPane extends JPanel implements ObjectEditListener {

	/**
	 * inner pane, which includes a model text area & properties grid
	 */
	JSplitPane innerPane;
	/**
	 * outer pane, which includes inner pane & console
	 */
	JSplitPane outerPane;
	/**
	 * model text area
	 */
	LineNumberingTextPanel modelText = null;
	/**
	 * whether is showing bdd algorithm options
	 */
	private boolean showBddOptions = true;
	/**
	 * radio button for bdd
	 */
	private JRadioButton bddRb = null;
	/**
	 * radio button for bmc
	 */
	private JRadioButton bmcRb = null;
	/**
	 * bdd method combo box
	 */
	private JComboBox<String> bddMethodCombo = null;
	/**
	 * bmc method combo box
	 */
	private JComboBox<String> bmcMethodCombo = null;
	/**
	 * tool bar
	 */
	private JToolBar toolbar = null;
	/**
	 * numeric spinner for setting bmc steps
	 */
	private JSpinner bmcStepSpinner = null;
	/**
	 * bmc steps label
	 */
	private JLabel bmcStepLabel = null;
	/**
	 * console text area
	 */
	private final JTextArea consoleTa;
	/**
	 * writer to the output stream of beagle process
	 */
	private BufferedWriter processWriter = null;

	/**
	 * create properties table
	 * 
	 * @return table
	 */
	private JTable createPropertiesTable() {
		TableModel tableModel = new AbstractTableModel() {

			@Override
			public int getRowCount() {
				// add an extra row for inserting new properties
				return Environment.getInstance().getModel().getProperties().length + 1;
			}

			@Override
			public int getColumnCount() {
				return 1;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				String[] properties = Environment.getInstance().getModel()
						.getProperties();
				if (rowIndex >= properties.length)
					return "";
				else
					return properties[rowIndex];
			}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int colIndex) {
				String strValue = aValue.toString().toString();
				if (strValue.isEmpty()) {
					Environment.getInstance().getModel()
							.removeProperty(rowIndex);
					this.fireTableRowsDeleted(rowIndex, rowIndex);
				} else {
					if (rowIndex < Environment.getInstance().getModel()
							.getProperties().length) {
						// change existing property
						Environment.getInstance().getModel()
								.setProperty(strValue, rowIndex);
						this.fireTableCellUpdated(rowIndex, colIndex);
					} else {
						// add new property
						Environment.getInstance().getModel()
								.addProperty(strValue);
						this.fireTableRowsInserted(rowIndex + 1, rowIndex + 1);
					}
				}
				VerifierPane.this.reloadModelText();
			}

			@Override
			public boolean isCellEditable(int rowIndex, int colIndex) {
				return true;
			}

			@Override
			public String getColumnName(int colIndex) {
				if (colIndex == 0)
					return "Property";
				else
					return "";
			}
		};

		JTable table = new JTable(tableModel) {
			@Override
			public boolean processKeyBinding(KeyStroke ks, KeyEvent e,
					int condition, boolean pressed) {
				if (ks.getKeyCode() == KeyEvent.VK_DELETE
						&& !ks.isOnKeyRelease()) {
					if (this.isEditing())
						return false;

					// delete selected rows
					int selectedRows[] = this.getSelectedRows();
					if (selectedRows.length > 0) {
						int startIndex = selectedRows[0];
						int endIndex = selectedRows[selectedRows.length - 1];
						for (int i = startIndex; i <= endIndex; i++)
							Environment.getInstance().getModel()
									.removeProperty(startIndex);
						this.updateUI();
						VerifierPane.this.reloadModelText();
						return true;
					} else
						return false;
				}
				return super.processKeyBinding(ks, e, condition, pressed);
			}
		};
		table.setBorder(new EtchedBorder());
		table.setShowGrid(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		return table;
	}

	/**
	 * constructor
	 */
	public VerifierPane() {
		JPanel modelPanel = new JPanel();
		modelPanel.setLayout(new BorderLayout());
		modelPanel.add(new JLabel("model:"), BorderLayout.NORTH);
		modelText = new LineNumberingTextPanel();
		modelText.getTextArea().setEditable(false);
		modelPanel.add(modelText, BorderLayout.CENTER);

		JPanel propertiesPanel = new JPanel();
		propertiesPanel.setLayout(new BorderLayout());
		propertiesPanel.add(new JLabel("properties:"), BorderLayout.NORTH);

		final JTable table = createPropertiesTable();
		propertiesPanel.add(table, BorderLayout.CENTER);

		innerPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, modelPanel,
				propertiesPanel);
		innerPane.setContinuousLayout(true);
		innerPane.setOneTouchExpandable(true);

		JPanel resultPanel = new JPanel();
		resultPanel.setLayout(new BorderLayout());
		resultPanel.add(new JLabel("Console:"), BorderLayout.NORTH);
		consoleTa = new JTextArea();
		consoleTa.setEditable(true);

		// the following code makes only the last line of consoleTa editable
		// for details, see
		// http://stackoverflow.com/questions/10030477/make-parts-of-a-jtextarea-non-editable-not-the-whole-jtextarea
		((AbstractDocument) consoleTa.getDocument())
				.setDocumentFilter(new DocumentFilter() {
					@Override
					public void insertString(final FilterBypass fb,
							final int offset, final String string,
							final AttributeSet attr)
							throws BadLocationException {
						int line = consoleTa.getLineOfOffset(offset);
						if (line == consoleTa.getLineCount() - 1) {
							super.insertString(fb, offset, string, attr);
						}
					}

					@Override
					public void remove(final FilterBypass fb, final int offset,
							final int length) throws BadLocationException {
						int line = consoleTa.getLineOfOffset(offset);
						if (line == consoleTa.getLineCount() - 1) {
							super.remove(fb, offset, length);
						}
					}

					@Override
					public void replace(final FilterBypass fb,
							final int offset, final int length,
							final String text, final AttributeSet attrs)
							throws BadLocationException {
						int line = consoleTa.getLineOfOffset(offset);
						if (line == consoleTa.getLineCount() - 1) {
							super.replace(fb, offset, length, text, attrs);
						}
					}
				});

		consoleTa.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					// when press ENTER, send the last line text to bealge
					// process
					int lastLineIndex = consoleTa.getLineCount() - 1;
					try {
						int startOffset = consoleTa
								.getLineStartOffset(lastLineIndex);
						int endOffset = consoleTa
								.getLineEndOffset(lastLineIndex);
						String lastLineText = consoleTa.getDocument().getText(
								startOffset, endOffset - startOffset);
						writeToProcess(lastLineText);
					} catch (BadLocationException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		
		//add context menu in consoleTa to clear text
		JPopupMenu popup = new JPopupMenu();
		JMenuItem item = new JMenuItem("Clear");
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				//we need to reset document filter before we can clear text
				DocumentFilter tmp = ((AbstractDocument) consoleTa
						.getDocument()).getDocumentFilter();
				((AbstractDocument) consoleTa.getDocument())
						.setDocumentFilter(new DocumentFilter());
				consoleTa.setText("");
				((AbstractDocument) consoleTa.getDocument())
						.setDocumentFilter(tmp);
			}

		});

		popup.add(item);
		consoleTa.setComponentPopupMenu(popup);

		resultPanel.add(new JScrollPane(consoleTa), BorderLayout.CENTER);

		outerPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, innerPane,
				resultPanel);
		this.setLayout(new BorderLayout());

		toolbar = new JToolBar();

		toolbar.add(new JLabel("Type:"));

		bddRb = new JRadioButton("bdd");
		bmcRb = new JRadioButton("bmc");

		ButtonGroup functionTypeRbGroup = new ButtonGroup();
		functionTypeRbGroup.add(bddRb);
		functionTypeRbGroup.add(bmcRb);

		bddRb.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				onFunctionTypeChange();
			}
		});

		bmcRb.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				onFunctionTypeChange();
			}
		});

		bddRb.setSelected(true);
		this.showBddOptions = bddRb.isSelected();
		toolbar.add(bddRb);
		toolbar.add(bmcRb);

		toolbar.add(new JLabel("Method:"));

		bddMethodCombo = new JComboBox<String>(Config.getInstance()
				.get_bdd_methods());
		bddMethodCombo.setEditable(false);
		bddMethodCombo.setSelectedIndex(0);

		bmcMethodCombo = new JComboBox<String>(Config.getInstance()
				.get_bmc_methods());
		bmcMethodCombo.setEditable(false);
		bmcMethodCombo.setSelectedIndex(0);

		bmcStepLabel = new JLabel("step:");
		bmcStepSpinner = new JSpinner(
				new SpinnerNumberModel(Config.getInstance()
						.get_bmc_default_step(), Config.getInstance()
						.get_bmc_min_step(), Config.getInstance()
						.get_bmc_max_step(), 1));

		if (this.showBddOptions)
			toolbar.add(bddMethodCombo);
		else {
			toolbar.add(bmcMethodCombo);
			toolbar.add(bmcStepLabel);
			toolbar.add(bmcStepSpinner);
		}

		toolbar.add(new JSeparator());
		JButton startBtn = new JButton("Start");

		startBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				startVerify();
			}
		});
		toolbar.add(startBtn);

		JButton terminateBtn = new JButton("Terminate");

		terminateBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				BeagleInvoker.getIntance().terminateProcess();
			}

		});
		toolbar.add(terminateBtn);

		this.add(toolbar, BorderLayout.NORTH);
		this.add(outerPane, BorderLayout.CENTER);

		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent evt) {
				innerPane.setDividerLocation(0.7);
				outerPane.setDividerLocation(0.7);
			}

		});
		reloadModelText();
	}
	
	/**
	 * fresh model text
	 */
	public void reloadModelText() {
		modelText.getTextArea().setText(
				ELTSGenerator
						.getModelText(Environment.getInstance().getModel()));
	}

	@Override
	public void objectEdit(ObjectEditEvent event) {
		//when model changes, refresh model text
		if (event instanceof ModuleEditEvent) {
			if (!((ModuleEditEvent) event).isMove())
				reloadModelText();
		} else if (event instanceof StateEditEvent) {
			if (!((StateEditEvent) event).isMove())
				reloadModelText();
		} else if (event instanceof TransitionEditEvent) {
			reloadModelText();
		} else if (event instanceof NoteEditEvent) {
			reloadModelText();
		}
	}
	
	/**
	 * called when bdd & bmc radio buttons clicked
	 */
	private void onFunctionTypeChange() {
		if (this.showBddOptions != bddRb.isSelected()) {
			// switch ui
			if (bddRb.isSelected()) {
				int index = toolbar.getComponentIndex(bmcMethodCombo);
				if (index >= 0) {
					toolbar.remove(bmcMethodCombo);
					toolbar.remove(bmcStepLabel);
					toolbar.remove(bmcStepSpinner);
					toolbar.add(bddMethodCombo, index);
					toolbar.updateUI();
				}
			} else {
				int index = toolbar.getComponentIndex(bddMethodCombo);
				if (index >= 0) {
					toolbar.remove(bddMethodCombo);
					toolbar.add(bmcMethodCombo, index);
					toolbar.add(bmcStepLabel, index + 1);
					toolbar.add(bmcStepSpinner, index + 2);
					toolbar.updateUI();
				}
			}
		}
		this.showBddOptions = bddRb.isSelected();
	}
	
	/**
	 * get algorithm arguments
	 * @return arguments
	 */
	public String[] getArguments() {
		if (bddRb.isSelected()) {
			return new String[] { "-bdd",
					"-" + bddMethodCombo.getSelectedItem().toString().trim() };
		} else
			return new String[] { "-bmc",
					"-" + bmcMethodCombo.getSelectedItem().toString().trim(),
					String.valueOf(bmcStepSpinner.getValue()) };
	}
	
	/**
	 * start verify
	 */
	private void startVerify() {
		// Check model is not empty
		Model model = Environment.getInstance().getModel();
		if (model.getModules().length == 0) {
			JOptionPane.showMessageDialog(this, "The model is empty!", "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		// Check properties is not empty
		if (model.getProperties().length == 0) {
			JOptionPane.showMessageDialog(this, "There are no properties!",
					"Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		BeagleInvoker.getIntance().verify(this);
	}
	
	/**
	 * append a new line in consoleTa
	 * @param line
	 */
	public void appendLine(String line) {
		this.consoleTa.append(line + "\n");
		consoleTa.setCaretPosition(consoleTa.getText().length());
	}
	
	/**
	 * write to output stream of beagle process
	 * @param str string
	 */
	private void writeToProcess(String str) {
		if (this.processWriter != null) {
			try {
				this.processWriter.write(str);
				this.processWriter.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * set output stream of beagle process
	 * @param stream output stream
	 */
	public void setProcessOutputStream(OutputStream stream) {
		if (stream != null) {
			this.processWriter = new BufferedWriter(new OutputStreamWriter(
					stream));
		} else {
			// process exited
			if (this.processWriter != null)
				try {
					this.processWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			this.processWriter = null;
		}
	}
}
