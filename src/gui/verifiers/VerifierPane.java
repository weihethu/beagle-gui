package gui.verifiers;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
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

import elts.ELTSGenerator;
import events.ModuleEditEvent;
import events.NoteEditEvent;
import events.ObjectEditEvent;
import events.StateEditEvent;
import events.TransitionEditEvent;
import events.listeners.ObjectEditListener;
import gui.Environment;

public class VerifierPane extends JPanel implements ObjectEditListener {

	JSplitPane innerPane, outerPane;
	LineNumberingTextPanel modelText = null;
	private boolean showBddOptions = true;
	private JRadioButton bddRb = null, bmcRb = null;
	private JComboBox<String> bddMethodCombo = null, bmcMethodCombo = null;
	private JToolBar toolbar = null;
	private JSpinner bmcStepSpinner = null;
	private JLabel bmcStepLabel = null;

	private JTable createPropertiesTable() {
		TableModel tableModel = new AbstractTableModel() {

			@Override
			public int getRowCount() {
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
						Environment.getInstance().getModel()
								.setProperty(strValue, rowIndex);
						this.fireTableCellUpdated(rowIndex, colIndex);
					} else {
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
						&& ks.isOnKeyRelease()) {
					TableCellEditor editor = this.getCellEditor();
					if (editor != null)
						editor.stopCellEditing();

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
		resultPanel.add(new JLabel("Verify results:"), BorderLayout.NORTH);
		JTextArea resultTa = new JTextArea();
		resultPanel.add(new JScrollPane(resultTa), BorderLayout.CENTER);

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

		bddMethodCombo = new JComboBox<String>(new String[] { "whole",
				"separate", "eqsSeparate" });
		bddMethodCombo.setEditable(false);
		bddMethodCombo.setSelectedIndex(0);

		bmcMethodCombo = new JComboBox<String>(new String[] { "std", "inc",
				"incDec", "macro", "macroInc", "macroIncDec" });
		bmcMethodCombo.setEditable(false);
		bmcMethodCombo.setSelectedIndex(0);

		bmcStepLabel = new JLabel("step:");
		bmcStepSpinner = new JSpinner(new SpinnerNumberModel(100, 1, 10000, 1));

		if (this.showBddOptions)
			toolbar.add(bddMethodCombo);
		else {
			toolbar.add(bmcMethodCombo);
			toolbar.add(bmcStepLabel);
			toolbar.add(bmcStepSpinner);
		}

		toolbar.add(new JSeparator());
		JButton startBtn = new JButton("Start Verify");
		toolbar.add(startBtn);

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

	public void reloadModelText() {
		modelText.getTextArea().setText(
				ELTSGenerator
						.getModelText(Environment.getInstance().getModel()));
	}

	@Override
	public void objectEdit(ObjectEditEvent event) {
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
}
