package gui.editors;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.border.EtchedBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import model.Module;
import model.automata.State;
import model.automata.Transition;

public class TransitionCreator {
	private JTable editingTable = null;
	private Transition transition = null;
	private Canvas canvas = null;
	private String labelsStr;

	public TransitionCreator(Canvas canvas) {
		this.canvas = canvas;
		canvas.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent event) {
				stopEditing();
			}
		});
	}

	private JTable createTable(Transition transition) {
		labelsStr = "";
		TableModel tableModel = new AbstractTableModel() {

			@Override
			public int getColumnCount() {
				return 1;
			}

			@Override
			public int getRowCount() {
				return 1;
			}

			@Override
			public Object getValueAt(int row, int col) {
				return labelsStr;
			}

			public void setValueAt(Object aValue, int rowIndex, int colIndex) {
				labelsStr = aValue.toString();
			}

			@Override
			public String getColumnName(int colIndex) {
				return "Label";
			}
		};

		JTable table = new JTable(tableModel) {
			protected boolean processKeyBinding(KeyStroke ks, KeyEvent e,
					int condition, boolean pressed) {
				if (ks.getKeyCode() == KeyEvent.VK_ENTER)
					stopEditing();
				return super.processKeyBinding(ks, e, condition, pressed);
			}

			@Override
			public boolean isCellEditable(int row, int column) {
				return true;
			}
		};
		table.setGridColor(Color.gray);
		table.setBorder(new EtchedBorder());
		return table;

	}

	public void createTransition(State fromState, State toState) {
		stopEditing();
		this.transition = new Transition(fromState, toState);

		editingTable = createTable(transition);
		canvas.add(this.editingTable);
		canvas.validate();

		Dimension tableDimensions = this.editingTable.getSize();

		Point tablePoint = canvas.transfromFromCanvasToView(new Point(
				(fromState.getPoint().x + toState.getPoint().x) / 2, (fromState
						.getPoint().y + toState.getPoint().y) / 2));
		tablePoint.translate(-tableDimensions.width / 2,
				-tableDimensions.height / 2);
		this.editingTable.setCellSelectionEnabled(true);
		this.editingTable.changeSelection(0, 0, false, false);
		this.editingTable.requestFocus();

		canvas.setTablePoint(tablePoint);
		canvas.revalidate();
		canvas.repaint();
	}

	private void stopEditing() {
		if (this.editingTable == null || transition == null)
			return;
		String str = labelsStr.trim();
		this.transition.clear();
		if (!str.isEmpty()) {
			String[] labels = str.split(";");
			for (String label : labels)
				this.transition.addLabel(label);
		}
		((Module) canvas.getDrawer().getObject())
				.addTransition(this.transition);
		canvas.remove(this.editingTable);
		canvas.validate();
		canvas.repaint();
		this.editingTable = null;
		canvas.requestFocus();
	}
}
