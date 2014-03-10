package gui;

import gui.drawers.DrawableObject;
import gui.drawers.ModelDrawer;
import gui.drawers.ModuleDrawer;
import gui.drawers.ObjectDrawer;
import gui.editors.EditorPane;
import gui.menus.MenuBarCreator;
import gui.toolbars.toolboxes.ModelDrawerToolBox;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;

import model.Model;
import model.Module;

public class Environment extends JFrame {
	private JTabbedPane tabbedPane;
	private static Environment instance = null;
	private Map<DrawableObject, ObjectDrawer> mapObjectsDrawers = null;

	private Environment() {
		mapObjectsDrawers = new HashMap<DrawableObject, ObjectDrawer>();


		tabbedPane = new JTabbedPane();
		this.setJMenuBar(MenuBarCreator.getMenuBar());
		this.setLayout(new BorderLayout());
		this.add(tabbedPane, BorderLayout.CENTER);

		this.setTitle("beagle-gui");
		this.setSize(600, 600);
		this.setLocation(50, 50);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);

		Model model = new Model();
		ModelDrawer modelDrawer = (ModelDrawer) this.getDrawer(model);
		this.addTab(new EditorPane(modelDrawer, new ModelDrawerToolBox()),
				"Editor");
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

	public ObjectDrawer getDrawer(DrawableObject object) {
		if (!this.mapObjectsDrawers.containsKey(object)) {
			if (object instanceof Module)
				this.mapObjectsDrawers.put(object, new ModuleDrawer(
						(Module) object));
			else if (object instanceof Model)
				this.mapObjectsDrawers.put(object, new ModelDrawer(
						(Model) object));
		}
		return this.mapObjectsDrawers.get(object);
	}
}
