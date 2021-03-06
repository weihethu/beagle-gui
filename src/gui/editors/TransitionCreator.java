package gui.editors;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractCellEditor;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import model.Module;
import model.automata.State;
import model.automata.Transition;
import utils.Pair;
import utils.StringUtil;

/**
 * transition creator & editor
 * 
 * @author Wei He
 * 
 */
public class TransitionCreator {
	/**
	 * table for editing transitions
	 */
	private JTable editingTable = null;
	/**
	 * scroll pane for transition editing table
	 */
	private JScrollPane editingTableSp = null;
	/**
	 * transition
	 */
	private Transition transition = null;
	/**
	 * canvas
	 */
	private Canvas canvas = null;
	/**
	 * data in edit for transition, which is bounded to the editing table
	 */
	private List<Pair<String, Pair<String, String>>> dataInEdit;
	/**
	 * the maximum row height
	 */
	private static int MAX_ROW_HEIGHT = 150;
	/**
	 * preferred width for editing table scroll pane
	 */
	public static int WIDTH = 400;
	/**
	 * preferred height for editing table scroll pane
	 */
	public static int HEIGHT = 200;

	/**
	 * constructor
	 * 
	 * @param canvas
	 *            canvas
	 */
	public TransitionCreator(Canvas canvas) {
		this.canvas = canvas;
		canvas.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent event) {
				// if canvas got focus, then stop editing transition data
				stopEditing();
			}
		});
	}

	/**
	 * create table for editing transition data
	 * 
	 * @param transition
	 *            transition
	 * @return editing table
	 */
	private JTable createTable(Transition transition) {
		TableModel tableModel = new AbstractTableModel() {

			@Override
			public int getColumnCount() {
				return 3;
			}

			@Override
			public int getRowCount() {
				return dataInEdit.size();
			}

			@Override
			public Object getValueAt(int row, int col) {
				if (row >= dataInEdit.size())
					return null;
				String value = "";
				String values[] = new String[] {
						dataInEdit.get(row).getFirst(),
						dataInEdit.get(row).getSecond().getFirst(),
						dataInEdit.get(row).getSecond().getSecond() };

				value = values[col];
				value = ((value == null) ? "" : value.trim());
				return value;
			}

			@Override
			public void setValueAt(Object aValue, int rowIndex, int colIndex) {
				String strValue = aValue.toString().trim();
				if (rowIndex < dataInEdit.size()) {
					if (colIndex == 0)
						strValue = Transition.regularizeLabelsStr(strValue);
					switch (colIndex) {
					case 0:
						dataInEdit.get(rowIndex).setFirst(strValue);
						break;
					case 1:
						dataInEdit.get(rowIndex).getSecond().setFirst(strValue);
						break;
					case 2:
						dataInEdit.get(rowIndex).getSecond()
								.setSecond(strValue);
						break;
					}
				}
				if (rowIndex == this.getRowCount() - 1 && colIndex == 0
						&& !strValue.isEmpty()) {
					// if the 1st column i.e. labels has been filled in the last
					// row, then insert a new empty row
					insertEmptyRow();
					this.fireTableRowsInserted(this.getRowCount() - 1,
							this.getRowCount() - 1);
				}
			}

			@Override
			public String getColumnName(int colIndex) {
				String colNames[] = new String[] { "Label", "Guard", "Action" };
				return colNames[colIndex];
			}
		};

		JTable table = new JTable(tableModel) {

			@Override
			public boolean processKeyBinding(KeyStroke ks, KeyEvent e,
					int condition, boolean pressed) {
				if (ks.getKeyCode() == KeyEvent.VK_DELETE
						&& !ks.isOnKeyRelease()) {
					// if table is being edited, then DELELE is likely meant to
					// delete some characters in cell
					if (editingTable.isEditing())
						return false;
					// delete selected rows
					int selectedRows[] = editingTable.getSelectedRows();
					if (selectedRows.length > 0) {
						dataInEdit.removeAll(dataInEdit.subList(
								selectedRows[0],
								selectedRows[selectedRows.length - 1] + 1));
						if (dataInEdit.isEmpty())
							insertEmptyRow();
						editingTable.updateUI();
						return true;
					} else
						return false;
				}
				return super.processKeyBinding(ks, e, condition, pressed);
			}

			@Override
			public boolean isCellEditable(int row, int column) {
				return true;
			}
		};

		table.setGridColor(Color.gray);
		table.setBorder(new EtchedBorder());
		table.getColumnModel().getColumn(2)
				.setCellRenderer(new TextAreaRenderer());
		table.getColumnModel().getColumn(2).setCellEditor(new TextAreaEditor());
		return table;

	}

	/**
	 * insert a new empty row in data edited, the last row is always empty for
	 * users to insert values
	 */
	private void insertEmptyRow() {
		dataInEdit.add(new Pair<String, Pair<String, String>>("",
				new Pair<String, String>(null, null)));
	}

	/**
	 * create transition
	 * 
	 * @param fromState
	 *            from State
	 * @param toState
	 *            to State
	 */
	public void createTransition(State fromState, State toState) {
		editTransition(new Transition(fromState, toState), null);
	}

	/**
	 * edit transition
	 * 
	 * @param transition
	 *            transition
	 * @param pt
	 *            the location where editing table should pop from
	 */
	public void editTransition(Transition transition, Point pt) {
		stopEditing();
		this.transition = transition;
		this.dataInEdit = new ArrayList<Pair<String, Pair<String, String>>>();
		Set<String> labels = transition.getLabels();
		for (String label : labels) {
			dataInEdit.add(new Pair<String, Pair<String, String>>(label,
					transition.getLabel(label)));
		}
		this.insertEmptyRow();

		editingTable = createTable(transition);
		editingTable
				.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		editingTableSp = new JScrollPane(editingTable);
		canvas.add(this.editingTableSp);

		if (pt == null) {
			pt = new Point((transition.getFromState().getPoint().x + transition
					.getToState().getPoint().x) / 2, (transition.getFromState()
					.getPoint().y + transition.getToState().getPoint().y) / 2);
		}
		pt = canvas.transfromFromCanvasToView(pt);
		pt.translate(-WIDTH / 2, 0);
		this.editingTable.setCellSelectionEnabled(true);
		this.editingTable.changeSelection(0, 0, false, false);
		this.editingTable.requestFocus();

		canvas.setTableSpPoint(pt);
		canvas.revalidate();
		canvas.repaint();
	}

	/**
	 * stop editing
	 */
	private void stopEditing() {
		if (this.editingTable == null || transition == null)
			return;
		try {
			TableCellEditor editor = this.editingTable.getCellEditor();
			if (editor != null)
				editor.stopCellEditing();
		} catch (Exception ex) {
		}
		if (save()) {
			((Module) canvas.getDrawer().getObject())
					.addTransition(this.transition);
			canvas.remove(this.editingTableSp);
			canvas.validate();
			canvas.repaint();
			this.editingTable = null;
			canvas.requestFocus();
		}
	}

	/**
	 * save data editing
	 * 
	 * @return whether successfully saved
	 */
	private boolean save() {
		if (dataInEdit.size() > 0) {
			// delete last row if empty
			Pair<String, Pair<String, String>> lastRow = dataInEdit
					.get(dataInEdit.size() - 1);
			String label = lastRow.getFirst();
			String guard = lastRow.getSecond().getFirst();
			String action = lastRow.getSecond().getSecond();

			if ((label == null || label.isEmpty())
					&& (guard == null || guard.isEmpty())
					&& (action == null || action.isEmpty()))
				dataInEdit.remove(lastRow);
		}
		// check that labels should be valid & non-empty & not duplicate
		Set<String> labels = new HashSet<String>();
		for (int i = 0; i < dataInEdit.size(); i++) {
			String label = dataInEdit.get(i).getFirst();
			if (label == null || label.trim().isEmpty()) {
				JOptionPane.showMessageDialog(canvas,
						"Label must be set on row " + (i + 1) + "!");
				return false;
			}
			if (!StringUtil.validMutipleIdenfitiersSeparatedByComma(label)) {
				JOptionPane.showMessageDialog(canvas, "Label on row " + (i + 1)
						+ " is not valid!");
				return false;
			}
			if (labels.contains(label)) {
				JOptionPane.showMessageDialog(canvas, "Label on row " + (i + 1)
						+ " should not duplicate!");
				return false;
			}
			labels.add(label);
		}
		// save to transition
		this.transition.clear();
		for (int i = 0; i < dataInEdit.size(); i++) {
			this.transition.addLabel(dataInEdit.get(i).getFirst(), dataInEdit
					.get(i).getSecond().getFirst(), dataInEdit.get(i)
					.getSecond().getSecond());
		}
		this.transition.dataChange();
		return true;
	}

	/**
	 * a multi-line text area renderer for displaying actions column in editing
	 * table
	 * 
	 * @author Wei He
	 * 
	 */
	private class TextAreaRenderer extends JTextArea implements
			TableCellRenderer {

		public TextAreaRenderer() {
		}

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object obj, boolean isSelected, boolean hasFocus, int row,
				int col) {
			setText(obj.toString());
			editingTable.setRowHeight(row, Math.min(MAX_ROW_HEIGHT, (int) this
					.getPreferredSize().getHeight()));
			return new JScrollPane(this);
		}

	}

	/**
	 * a multi-line text area editor for editing actions column in editing table
	 * 
	 * @author Wei He
	 * 
	 */
	private class TextAreaEditor extends AbstractCellEditor implements
			TableCellEditor {

		private JTextArea ta;
		private int currentRow = -1;

		/**
		 * handler when text changes, increase editor height when starting new
		 * lines, but not exceeding MAX_ROW_HEIGHT
		 */
		private void textEdit() {
			if (currentRow >= 0) {
				editingTable.setRowHeight(currentRow, Math.min(MAX_ROW_HEIGHT,
						(int) this.ta.getPreferredSize().getHeight()));
			}
		}

		public TextAreaEditor() {
			ta = new JTextArea();

			ta.getDocument().addDocumentListener(new DocumentListener() {

				@Override
				public void changedUpdate(DocumentEvent event) {

				}

				@Override
				public void insertUpdate(DocumentEvent event) {
					textEdit();
				}

				@Override
				public void removeUpdate(DocumentEvent event) {
					textEdit();
				}

			});
		}

		@Override
		public Object getCellEditorValue() {
			return ta.getText();
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object obj,
				boolean isSelected, int row, int col) {
			currentRow = row;
			ta.setText(obj.toString());
			return new JScrollPane(ta);
		}

	}
}
