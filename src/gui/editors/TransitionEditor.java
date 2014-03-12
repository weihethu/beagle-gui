package gui.editors;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class TransitionEditor extends JPanel {
	JList labelsList;
	public TransitionEditor() {
		this.setLayout(new BorderLayout(20, 0));
		
		labelsList = new JList(new String[] {"One", "Two", "Three"});
		
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BorderLayout());
		leftPanel.add(new JLabel("Labels:"), BorderLayout.NORTH);
		leftPanel.add(labelsList, BorderLayout.CENTER);
		
		JPanel leftBottomPanel = new JPanel();
		leftBottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		
		JButton addBtn = new JButton("Add");
		JButton removeBtn = new JButton("Remove");
		
		leftBottomPanel.add(addBtn);
		leftBottomPanel.add(removeBtn);
		leftPanel.add(leftBottomPanel, BorderLayout.SOUTH);
		
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BorderLayout());
		
		JPanel rightTopPanel = new JPanel();
		rightTopPanel.setLayout(new BorderLayout());
		rightTopPanel.add(new JLabel("Guard:"), BorderLayout.WEST);
		
		JTextField guardTf = new JTextField();
		rightTopPanel.add(guardTf, BorderLayout.CENTER);
		
		rightPanel.add(rightTopPanel, BorderLayout.NORTH);
		
		JPanel rightBottomPanel = new JPanel();
		rightBottomPanel.setLayout(new BorderLayout());
		rightBottomPanel.add(new JLabel("Action:"), BorderLayout.NORTH);
		
		JTextArea actionTa = new JTextArea();
		rightBottomPanel.add(new JScrollPane(actionTa), BorderLayout.CENTER);
		rightPanel.add(rightBottomPanel, BorderLayout.CENTER);
		
		this.add(leftPanel, BorderLayout.WEST);
		this.add(rightPanel, BorderLayout.CENTER);
	}
}