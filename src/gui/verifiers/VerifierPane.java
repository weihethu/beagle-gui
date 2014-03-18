package gui.verifiers;

import elts.ELTSGenerator;
import gui.Environment;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class VerifierPane extends JPanel {

	JSplitPane innerPane, outerPane;
	JTextArea modelTa = null;

	public VerifierPane() {
		JPanel modelPanel = new JPanel();
		modelPanel.setLayout(new BorderLayout());
		modelPanel.add(new JLabel("model:"), BorderLayout.NORTH);
		modelTa = new JTextArea();
		modelTa.setEditable(false);
		modelPanel.add(new JScrollPane(modelTa), BorderLayout.CENTER);

		JPanel propertiesPanel = new JPanel();
		propertiesPanel.setLayout(new BorderLayout());
		propertiesPanel.add(new JLabel("properties:"), BorderLayout.NORTH);

		Vector<String> dummyVector = new Vector<String>();
		dummyVector.add("Property");

		TableModel propertiesTableModel = new DefaultTableModel(
				set2Vector(Environment.getInstance().getProperties()),
				dummyVector);

		final JTable table = new JTable(propertiesTableModel);
		table.setShowGrid(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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

		JButton addPropBtn = new JButton("Add");
		JButton removePropBtn = new JButton("Remove");

		toolbar.add(new JLabel("Property:"));
		toolbar.add(addPropBtn);
		toolbar.add(removePropBtn);

		toolbar.add(new JToolBar.Separator());
		toolbar.add(new JLabel("Verifier:"));
		JButton startBtn = new JButton("Start");
		toolbar.add(startBtn);

		this.add(toolbar, BorderLayout.NORTH);
		this.add(outerPane, BorderLayout.CENTER);
		init();

		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent evt) {
				innerPane.setDividerLocation(0.7);
				outerPane.setDividerLocation(0.7);
			}

		});
	}

	public void init() {
		modelTa.setText(ELTSGenerator.getModelText(Environment.getInstance()
				.getModel()));
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
}
