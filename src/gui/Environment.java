package gui;

import events.ModuleEditEvent;
import events.ObjectEditEvent;
import events.listeners.ObjectEditListener;
import gui.drawers.DrawableObject;
import gui.drawers.ModelDrawer;
import gui.drawers.ModuleDrawer;
import gui.drawers.ObjectDrawer;
import gui.editors.EditorPane;
import gui.menus.MenuBarCreator;
import gui.toolbars.toolboxes.ModelDrawerToolBox;
import gui.toolbars.toolboxes.ModuleDrawerToolBox;
import gui.verifiers.VerifierPane;

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
	private Model model = null;
	private Map<Module, EditorPane> moduleEditorsMap = null;

	private Environment() {
		model = new Model();
		moduleEditorsMap = new HashMap<Module, EditorPane>();
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
		ModelDrawer modelDrawer = (ModelDrawer) this.getDrawer(this.getModel());
		this.addTab(new EditorPane(modelDrawer, new ModelDrawerToolBox()),
				"Model's Editor");

		model.addModuleListener(new ObjectEditListener() {

			@Override
			public void objectEdit(ObjectEditEvent event) {
				if (event instanceof ModuleEditEvent) {
					onModuleEdit((ModuleEditEvent) event);
				}
			}

		});
	}

	private void onModuleEdit(ModuleEditEvent event) {
		Module module = (Module) event.getTarget();
		if (event.isNameChange()) {
			if (this.moduleEditorsMap.containsKey(module)) {
				EditorPane editor = this.moduleEditorsMap.get(module);
				for (int i = 0; i < this.tabbedPane.getTabCount(); i++) {
					if (this.tabbedPane.getComponentAt(i) == editor) {
						this.tabbedPane.setTitleAt(i, module.getName()
								+ "'s editor");
						break;
					}
				}
			}
		} else if (event.isRemove()) {
			if (this.moduleEditorsMap.containsKey(module)) {
				this.tabbedPane.remove(this.moduleEditorsMap.get(module));
			}
		}
	}

	public Model getModel() {
		return model;
	}

	public void addChangeListeners(ChangeListener listener) {
		this.changeListeners.add(listener);
	}

	private void distributeChangeEvent() {
		ChangeEvent event = new ChangeEvent(this);
		for (ChangeListener listener : changeListeners)
			listener.stateChanged(event);
	}

	public void openModuleEditor(Module module) {
		if (this.moduleEditorsMap.containsKey(module)) {
			this.tabbedPane.setSelectedComponent(this.moduleEditorsMap
					.get(module));
		} else {
			EditorPane editor = new EditorPane(getDrawer(module),
					new ModuleDrawerToolBox());
			this.moduleEditorsMap.put(module, editor);
			addTab(editor, module.getName() + "'s editor");
		}
	}

	public void openVerifyTab() {
		for (int i = 0; i < this.tabbedPane.getTabCount(); i++) {
			if (this.tabbedPane.getComponentAt(i) instanceof VerifierPane) {
				((VerifierPane) this.tabbedPane.getComponentAt(i)).init();
				this.tabbedPane.setSelectedIndex(i);
				return;
			}
		}
		VerifierPane verifier = new VerifierPane();
		this.addTab(verifier, "Verifier");

		verifier.init();
	}

	public void addTab(Component tab, String title) {
		this.tabbedPane.add(tab, title);
		this.tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
		this.distributeChangeEvent();
	}

	public void removeCurrentTab() {
		Component activeTab = this.getActiveTab();
		if (activeTab instanceof EditorPane) {
			EditorPane editor = (EditorPane) activeTab;
			if (editor.getDrawer() instanceof ModuleDrawer) {
				Module module = (Module) editor.getDrawer().getObject();
				this.moduleEditorsMap.remove(module);
			}
		}
		this.tabbedPane.remove(this.tabbedPane.getSelectedIndex());
	}

	public int getActiveTabIndex() {
		return this.tabbedPane.getSelectedIndex();
	}

	public Component getActiveTab() {
		return this.tabbedPane.getSelectedComponent();
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

	public Set<String> getProperties() {
		Set<String> properties = new HashSet<String>();
		properties.add("google");
		properties.add("microsoft");
		return properties;
	}
}
