package gui.verifiers;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;
import javax.swing.table.AbstractTableModel;

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
				if (ks.getKeyCode() == KeyEvent.VK_DELETE && ks.isOnKeyRelease()) {
					TableCellEditor editor = this.getCellEditor();
					if(editor != null)
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

		JToolBar toolbar = new JToolBar();

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
}
