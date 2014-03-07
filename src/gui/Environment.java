package gui;

import gui.drawers.ELTSModelDrawer;
import gui.editors.EditorPane;
import gui.toolbars.toolboxes.ModelDrawerToolBox;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import model.ELTSModel;

public class Environment extends JFrame {
	private JTabbedPane tabbedPane;
	private static Environment instance = null;

	private Environment() {
		this.setLayout(new BorderLayout());
		tabbedPane = new JTabbedPane();

		this.add(tabbedPane, BorderLayout.CENTER);

		this.setTitle("beagle-gui");
		this.setSize(600, 600);
		this.setLocation(50, 50);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);

		this.addTab(new EditorPane(new ELTSModelDrawer(new ELTSModel()),
				new ModelDrawerToolBox()), "Editor");
	}

	public void addTab(Component tab, String title) {
		this.tabbedPane.add(tab, title);
		this.tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
	}

	public static Environment getInstance() {
		if (instance == null) {
			instance = new Environment();
		}
		return instance;
	}
}
