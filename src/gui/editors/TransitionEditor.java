package gui.editors;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import model.automata.Transition;
import utils.Pair;

public class TransitionEditor extends JPanel implements ComponentListener {

	private Transition transition;
	private DefaultTableModel labelTableModel;
	private JScrollPane tableSp;
	private List<Pair<String, Pair<String, String>>> dataInEdit;
	private JTextField guardTf;
	private JTextArea actionTa;
	private final static String NO_GUARD = "No guard specified";
	private final static String NO_ACTION = "No Action specified";
	private final static String INPUT_LABEL = "Please input the label name(s)";
	private int lastRowIndexSelected = -1;

	public TransitionEditor(Transition transition) {
		this.transition = transition;
		dataInEdit = new ArrayList<Pair<String, Pair<String, String>>>();
		Set<String> labels = transition.getLabels();
		for (String label : labels) {
			dataInEdit.add(new Pair<String, Pair<String, String>>(label,
					transition.getLabel(label)));
		}

		this.setLayout(new BorderLayout(20, 0));

		Vector<String> dummyVector = new Vector<String>();
		dummyVector.add("");

		labelTableModel = new DefaultTableModel(
				set2Vector(transition.getLabels()), dummyVector);

		final JTable table = new JTable(labelTableModel);
		table.setShowGrid(false);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BorderLayout());
		leftPanel.add(new JLabel("Labels:"), BorderLayout.NORTH);

		tableSp = new JScrollPane(table);
		tableSp.setPreferredSize(new Dimension(200, this.getHeight() - 50));
		leftPanel.add(tableSp, BorderLayout.CENTER);

		this.labelTableModel.addTableModelListener(new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent event) {
				if (event.getColumn() == 0
						&& event.getFirstRow() == event.getLastRow()) {
					int rowIndex = event.getFirstRow();
					dataInEdit.get(rowIndex).setFirst(
							labelTableModel.getValueAt(rowIndex, 0).toString());
				}
			}

		});

		table.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent event) {
				int currentRowIndexSelected = table.getSelectedRow();
				if (currentRowIndexSelected >= 0
						&& currentRowIndexSelected != lastRowIndexSelected) {
					if (lastRowIndexSelected != -1) {
						// save
						dataInEdit.get(lastRowIndexSelected).setSecond(
								new Pair<String, String>(guardTf.getText()
										.trim(), actionTa.getText()));
					}
					if (currentRowIndexSelected >= 0) {
						Pair<String, String> data = dataInEdit.get(
								currentRowIndexSelected).getSecond();
						String guard = data.getFirst();
						if (guard == null || guard.isEmpty()) {
							guardTf.setText(NO_GUARD);
						} else
							guardTf.setText(guard);
						String action = data.getSecond();
						if (action == null || action.isEmpty()) {
							actionTa.setText(NO_ACTION);
						} else
							actionTa.setText(action);
					}
				}
				lastRowIndexSelected = currentRowIndexSelected;
			}

			@Override
			public void mouseEntered(MouseEvent event) {

			}

			@Override
			public void mouseExited(MouseEvent event) {

			}

			@Override
			public void mousePressed(MouseEvent event) {

			}

			@Override
			public void mouseReleased(MouseEvent event) {

			}

		});

		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BorderLayout());

		JPanel rightTopPanel = new JPanel();
		rightTopPanel.setLayout(new BorderLayout());
		rightTopPanel.add(new JLabel("Guard:"), BorderLayout.WEST);

		guardTf = new JTextField();
		rightTopPanel.add(guardTf, BorderLayout.CENTER);

		rightPanel.add(rightTopPanel, BorderLayout.NORTH);

		JPanel rightBottomPanel = new JPanel();
		rightBottomPanel.setLayout(new BorderLayout());
		rightBottomPanel.add(new JLabel("Action:"), BorderLayout.NORTH);

		actionTa = new JTextArea();
		rightBottomPanel.add(new JScrollPane(actionTa), BorderLayout.CENTER);
		rightPanel.add(rightBottomPanel, BorderLayout.CENTER);

		JToolBar toolbar = new JToolBar();

		JButton addBtn = new JButton("Add");
		addBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				labelTableModel.addRow(new String[] { INPUT_LABEL });
				dataInEdit.add(new Pair<String, Pair<String, String>>("",
						new Pair<String, String>(null, null)));
			}

		});

		JButton removeBtn = new JButton("Remove");
		removeBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				int index = table.getSelectedRow();
				if (index >= 0) {
					labelTableModel.removeRow(index);
					dataInEdit.remove(index);
				}
			}

		});

		toolbar.add(addBtn);
		toolbar.add(removeBtn);

		this.add(toolbar, BorderLayout.NORTH);
		this.add(leftPanel, BorderLayout.WEST);
		this.add(rightPanel, BorderLayout.CENTER);
	}

	private Vector<Vector<String>> set2Vector(Set<String> set) {
		Vector<Vector<String>> rows = new Vector<Vector<String>>();
		Iterator<String> iter = set.iterator();
		while (iter.hasNext()) {
			Vector<String> row = new Vector<String>();
			row.add(iter.next());
			rows.addElement(row);
		}
		return rows;
	}

	@Override
	public void componentHidden(ComponentEvent event) {

	}

	@Override
	public void componentMoved(ComponentEvent event) {

	}

	@Override
	public void componentResized(ComponentEvent event) {
		tableSp.setPreferredSize(new Dimension(200, this.getHeight() - 50));
	}

	@Override
	public void componentShown(ComponentEvent event) {

	}

	public boolean save() {
		// check validity
		Set<String> labels = new HashSet<String>();
		for (int i = 0; i < dataInEdit.size(); i++) {
			String label = dataInEdit.get(i).getFirst();
			if (label == null || label.trim().isEmpty()
					|| label.trim().equals(INPUT_LABEL)) {
				JOptionPane.showMessageDialog(this, "Label must be set!");
				return false;
			}
			if (labels.contains(label)) {
				JOptionPane.showMessageDialog(this,
						"Labels should not duplicate!");
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
		this.transition.descriptionChange();
		return true;
	}
}