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

public class TransitionCreator {
	private JTable editingTable = null;
	private JScrollPane editingTableSp = null;
	private Transition transition = null;
	private Canvas canvas = null;
	private List<Pair<String, Pair<String, String>>> dataInEdit;
	private static int MAX_ROW_HEIGHT = 150;
	public static int WIDTH = 400;
	public static int HEIGHT = 200;

	public TransitionCreator(Canvas canvas) {
		this.canvas = canvas;
		canvas.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent event) {
				stopEditing();
			}
		});
	}

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
					if (editingTable.isEditing())
						return false;
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

	private void insertEmptyRow() {
		dataInEdit.add(new Pair<String, Pair<String, String>>("",
				new Pair<String, String>(null, null)));
	}

	public void createTransition(State fromState, State toState) {
		editTransition(new Transition(fromState, toState), null);
	}

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

	private boolean save() {
		// check validity
		if (dataInEdit.size() > 0) {
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
		this.transition.clear();
		for (int i = 0; i < dataInEdit.size(); i++) {
			this.transition.addLabel(dataInEdit.get(i).getFirst(), dataInEdit
					.get(i).getSecond().getFirst(), dataInEdit.get(i)
					.getSecond().getSecond());
		}
		this.transition.dataChange();
		return true;
	}

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

	private class TextAreaEditor extends AbstractCellEditor implements
			TableCellEditor {

		private JTextArea ta;
		private int currentRow = -1;

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
