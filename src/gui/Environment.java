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
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import model.Model;
import model.Module;
import utils.BeagleInvoker;

/**
 * the environment(main frame) for beagle-gui, this class is singleton
 * 
 * @author Wei He
 * 
 */
public class Environment extends JFrame {
	/**
	 * the tabbed pane
	 */
	private JTabbedPane tabbedPane;
	/**
	 * the environment instance,
	 */
	private static Environment instance = null;
	/**
	 * a map which associates a object and its drawers
	 */
	private Map<DrawableObject, ObjectDrawer> mapObjectsDrawers = null;
	/**
	 * the listeners set for monitoring the change events of tabbed pane
	 */
	private Set<ChangeListener> changeListeners = null;
	/**
	 * the elts model
	 */
	private Model model = null;
	/**
	 * a map which associates the modules and its editor panes
	 */
	private Map<Module, EditorPane> moduleEditorsMap = null;
	/**
	 * the verifier pane
	 */
	private VerifierPane verifierPane = null;
	/**
	 * the path for the current elts model file opened
	 */
	private String currentPath = null;

	/**
	 * constructor, it's set to private
	 */
	private Environment() {
		changeListeners = new HashSet<ChangeListener>();
		tabbedPane = new JTabbedPane();
		this.setJMenuBar(MenuBarCreator.getMenuBar(this));
		this.setLayout(new BorderLayout());
		this.add(tabbedPane, BorderLayout.CENTER);

		this.setTitle(getTitleWithPath());
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

		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				// if beagle-gui is about to exit and beagle process is still
				// running, we should force to terminate beagle process
				if (BeagleInvoker.getIntance().isProcessRunning())
					BeagleInvoker.getIntance().terminateProcess();
			}
		});
	}

	/**
	 * get the environment instance
	 * 
	 * @return instance
	 */
	public static Environment getInstance() {
		if (instance == null) {
			instance = new Environment();
		}
		return instance;
	}

	/**
	 * set path of the current model file opened
	 * 
	 * @param path
	 *            file path
	 */
	public void setCurrentPath(String path) {
		this.currentPath = path;
		// change the frame title accordingly
		this.setTitle(getTitleWithPath());
	}

	/**
	 * get path of the current model file opened
	 * 
	 * @return file path
	 */
	public String getCurrentPath() {
		return this.currentPath;
	}

	/**
	 * get the frame title which displays the current file information
	 * 
	 * @return
	 */
	public String getTitleWithPath() {
		String title = "beagle-gui:";
		if (this.currentPath == null)
			title += "<untitled>";
		else
			title += "<" + this.currentPath + ">";
		return title;
	}

	/**
	 * set the current ELTS model
	 * 
	 * @param model
	 *            model
	 */
	public void setModel(Model model) {
		// reset data types
		this.model = model;
		if (moduleEditorsMap != null)
			moduleEditorsMap.clear();
		else
			moduleEditorsMap = new HashMap<Module, EditorPane>();
		if (mapObjectsDrawers != null)
			mapObjectsDrawers.clear();
		else
			mapObjectsDrawers = new HashMap<DrawableObject, ObjectDrawer>();
		this.tabbedPane.removeAll();

		// register event listeners for ModuleEditEvent
		model.addModuleListener(new ObjectEditListener() {

			@Override
			public void objectEdit(ObjectEditEvent event) {
				if (event instanceof ModuleEditEvent) {
					onModuleEdit((ModuleEditEvent) event);
				}
			}

		});

		// open the editor pane for model, it's always the first one in tabbed
		// pane
		ModelDrawer modelDrawer = (ModelDrawer) this.getDrawer(this.getModel());
		this.addTab(new EditorPane(modelDrawer, new ModelDrawerToolBox()),
				"Model's Editor");

		if (this.verifierPane != null)
			this.verifierPane.reloadModelText();
	}

	/**
	 * get the dimension for editor pane, can be used when placing objects
	 * 
	 * @return dimension
	 */
	public Dimension getGraphDimension() {
		if (this.tabbedPane.getTabCount() > 0
				&& this.tabbedPane.getComponent(0) instanceof EditorPane) {
			Rectangle rect = ((EditorPane) this.tabbedPane.getComponent(0))
					.getBounds();
			return new Dimension(rect.width, rect.height);
		} else
			return null;
	}

	/**
	 * a event handler for ModuleEditEvent
	 * 
	 * @param event
	 */
	private void onModuleEdit(ModuleEditEvent event) {
		Module module = (Module) event.getTarget();
		// if a module changes its name, change the corresponding tab's name if
		// there's one
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
			// if a module is deleted, close the corresponding tab is there's
			// one
			if (this.moduleEditorsMap.containsKey(module)) {
				this.tabbedPane.remove(this.moduleEditorsMap.get(module));
			}
		}
	}

	/**
	 * get the current model being edited
	 * 
	 * @return
	 */
	public Model getModel() {
		return model;
	}

	/**
	 * get the verifier pane, if there's none, construct one
	 * 
	 * @return
	 */
	public VerifierPane getVerifierPane() {
		if (this.verifierPane == null) {
			this.verifierPane = new VerifierPane();
		}
		return this.verifierPane;

	}

	/**
	 * register a new event listener for tabbed pane's change event
	 * 
	 * @param listener
	 *            event listener
	 */
	public void addChangeListeners(ChangeListener listener) {
		this.changeListeners.add(listener);
	}

	/**
	 * distribute tabbed pane's change event to all listeners
	 */
	private void distributeChangeEvent() {
		ChangeEvent event = new ChangeEvent(this);
		for (ChangeListener listener : changeListeners)
			listener.stateChanged(event);
	}

	/**
	 * open the editor pane for a module
	 * 
	 * @param module
	 *            module
	 */
	public void openModuleEditor(Module module) {
		// if already opened
		if (this.moduleEditorsMap.containsKey(module)) {
			this.tabbedPane.setSelectedComponent(this.moduleEditorsMap
					.get(module));
		} else {
			// if not
			EditorPane editor = new EditorPane(getDrawer(module),
					new ModuleDrawerToolBox());
			this.moduleEditorsMap.put(module, editor);
			addTab(editor, module.getName() + "'s editor");
		}
	}

	/**
	 * open verify pane
	 */
	public void openVerifyTab() {
		this.getVerifierPane();
		for (int i = 0; i < this.tabbedPane.getTabCount(); i++) {
			if (this.tabbedPane.getComponentAt(i) == verifierPane) {
				this.tabbedPane.setSelectedIndex(i);
				return;
			}
		}

		this.addTab(verifierPane, "Verifier");
	}

	/**
	 * add a tab to the tabbed pane
	 * 
	 * @param tab
	 *            tab
	 * @param title
	 *            tab title
	 */
	public void addTab(Component tab, String title) {
		this.tabbedPane.add(tab, title);
		this.tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
		this.distributeChangeEvent();
	}

	/**
	 * remove the current tab opened
	 */
	public void removeCurrentTab() {
		Component activeTab = this.getActiveTab();
		if (activeTab instanceof EditorPane) {
			// if closing a editor pane for module, delete the entry in
			// moduleEditorsMap
			EditorPane editor = (EditorPane) activeTab;
			if (editor.getDrawer() instanceof ModuleDrawer) {
				Module module = (Module) editor.getDrawer().getObject();
				this.moduleEditorsMap.remove(module);
			}
		} else if (activeTab instanceof VerifierPane) {
			// if closing a verifier pane, make sure that the beagle process is
			// not running
			if (BeagleInvoker.getIntance().isProcessRunning()) {
				JOptionPane
						.showMessageDialog(
								activeTab,
								"Please wait for beagle process to exit before closing this tab!",
								"Info", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
		}

		this.tabbedPane.remove(this.tabbedPane.getSelectedIndex());
	}

	/**
	 * get the index for the active tab
	 * 
	 * @return index
	 */
	public int getActiveTabIndex() {
		return this.tabbedPane.getSelectedIndex();
	}

	/**
	 * get the active tab
	 * 
	 * @return tab
	 */
	public Component getActiveTab() {
		return this.tabbedPane.getSelectedComponent();
	}

	/**
	 * get drawer for a object
	 * 
	 * @param object
	 *            drawable object
	 * @return drawer
	 */
	public ObjectDrawer getDrawer(DrawableObject object) {
		// if there's not already one
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
