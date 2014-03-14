package gui;

import gui.drawers.DrawableObject;
import gui.drawers.ModelDrawer;
import gui.drawers.ModuleDrawer;
import gui.drawers.ObjectDrawer;
import gui.menus.MenuBarCreator;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import model.Model;
import model.Module;

public class Environment extends JFrame {
	private JTabbedPane tabbedPane;
	private static Environment instance = null;
	private Map<DrawableObject, ObjectDrawer> mapObjectsDrawers = null;
	private Set<ChangeListener> changeListeners = null;

	private Environment() {
		changeListeners = new HashSet<ChangeListener>();
		mapObjectsDrawers = new HashMap<DrawableObject, ObjectDrawer>();

		tabbedPane = new JTabbedPane();
		this.setJMenuBar(MenuBarCreator.getMenuBar(this));
		this.setLayout(new BorderLayout());
		this.add(tabbedPane, BorderLayout.CENTER);

		this.setTitle("beagle-gui");
		this.setSize(600, 600);
		this.setLocation(50, 50);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);

		this.tabbedPane.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				distributeChangeEvent();
			}

		});
	}

	public void addChangeListeners(ChangeListener listener) {
		this.changeListeners.add(listener);
	}

	private void distributeChangeEvent() {
		ChangeEvent event = new ChangeEvent(this);
		for (ChangeListener listener : changeListeners)
			listener.stateChanged(event);
	}

	public void addTab(Component tab, String title) {
		this.tabbedPane.add(tab, title);
		this.tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
		this.distributeChangeEvent();
	}

	public int getActiveTabIndex() {
		return this.tabbedPane.getSelectedIndex();
	}

	public Component getActiveTab() {
		return this.tabbedPane.getSelectedComponent();
	}

	public void removeCurrentTab() {
		this.tabbedPane.remove(this.tabbedPane.getSelectedIndex());
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
