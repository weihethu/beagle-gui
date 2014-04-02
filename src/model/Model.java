package model;

import events.ModuleEditEvent;
import events.ObjectEditEvent;
import events.listeners.ObjectEditListener;
import gui.Environment;
import gui.drawers.DrawableObject;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * model
 * 
 * @author Wei He
 * 
 */
public class Model extends DrawableObject {
	/**
	 * modules
	 */
	private Set<Module> modules;
	/**
	 * cached modules
	 */
	private Module[] cachedModules = null;
	/**
	 * event listeners for ModuleEditEvents
	 */
	private Set<ObjectEditListener> moduleListeners = null;
	/**
	 * properties
	 */
	private List<String> properties;

	/**
	 * constructor
	 */
	public Model() {
		modules = new HashSet<Module>();
		moduleListeners = new HashSet<ObjectEditListener>();
		properties = new ArrayList<String>();

		this.addModuleListener(new ObjectEditListener() {

			@Override
			public void objectEdit(ObjectEditEvent event) {
				if (event instanceof ModuleEditEvent) {
					ModuleEditEvent moduleEvt = (ModuleEditEvent) event;
					// cached modules is sorted by name, so if name changes,
					// must
					// recompute
					if (moduleEvt.isNameChange())
						Model.this.cachedModules = null;
				}
			}

		});

		this.addModuleListener(Environment.getInstance().getVerifierPane());
	}

	/**
	 * create module at point
	 * 
	 * @param pt
	 *            point
	 * @return module created
	 */
	public Module createModule(Point pt) {
		Module module = new Module(getDefaultModuleName(), pt, this);
		addModule(module);
		distributeModuleEditEvent(new ModuleEditEvent(module,
				ModuleEditEvent.EventType.ADD));
		return module;
	}

	/**
	 * add module
	 * 
	 * @param module
	 *            module
	 */
	public void addModule(Module module) {
		this.modules.add(module);
		this.cachedModules = null;
	}

	/**
	 * remove module
	 * 
	 * @param module
	 *            module
	 */
	public void removeModule(Module module) {
		this.modules.remove(module);
		this.cachedModules = null;
		this.distributeModuleEditEvent(new ModuleEditEvent(module,
				ModuleEditEvent.EventType.REMOVE));
	}

	/**
	 * get modules
	 * 
	 * @return modules
	 */
	@SuppressWarnings("unchecked")
	public Module[] getModules() {
		if (this.cachedModules == null) {
			this.cachedModules = (Module[]) this.modules.toArray(new Module[0]);
			Arrays.sort(this.cachedModules, new Comparator() {

				@Override
				public int compare(Object obj1, Object obj2) {
					return ((Module) obj1).getName().compareTo(
							((Module) obj2).getName());
				}

			});
		}
		return this.cachedModules;
	}

	/**
	 * select modules within selection box
	 * 
	 * @param rect
	 *            selection box bounds
	 */
	public void selectModulesWithinBounds(Rectangle rect) {
		Module[] modules = getModules();
		for (int i = 0; i < modules.length; i++) {
			modules[i].setSelect(false);
			if (rect.contains(modules[i].getPoint()))
				modules[i].setSelect(true);
		}
	}

	/**
	 * unselect all modules
	 */
	public void unselectAll() {
		Module[] modules = getModules();
		for (Module module : modules)
			module.setSelect(false);
	}

	/**
	 * add property
	 * 
	 * @param prop
	 *            property
	 */
	public void addProperty(String prop) {
		this.properties.add(prop);
	}

	/**
	 * remove property at index
	 * 
	 * @param index
	 *            index
	 */
	public void removeProperty(int index) {
		if (index >= 0 && index < this.properties.size())
			this.properties.remove(index);
	}

	/**
	 * set property at index
	 * 
	 * @param property
	 *            property
	 * @param index
	 *            index
	 */
	public void setProperty(String property, int index) {
		if (index >= 0 && index < this.properties.size())
			this.properties.set(index, property);
	}

	/**
	 * get all properties
	 * 
	 * @return properties
	 */
	public String[] getProperties() {
		return this.properties.toArray(new String[] {});
	}

	/**
	 * add event listeners for ModuleEditEvents
	 * 
	 * @param listener
	 *            event listener
	 */
	public void addModuleListener(ObjectEditListener listener) {
		this.moduleListeners.add(listener);
	}

	/**
	 * distribute ModuleEditEvents to all event listeners
	 * 
	 * @param event
	 *            event
	 */
	public void distributeModuleEditEvent(ModuleEditEvent event) {
		Iterator<ObjectEditListener> iter = moduleListeners.iterator();
		while (iter.hasNext()) {
			ObjectEditListener currentListener = iter.next();
			currentListener.objectEdit(event);
		}
	}

	/**
	 * generate a default name for module in pattern of "module_x" that do not
	 * duplicate with other modules
	 * 
	 * @return name
	 */
	private String getDefaultModuleName() {
		Module[] modules = this.getModules();
		for (int i = 1;; i++) {
			String name = "module_" + i;
			boolean noConflict = true;
			for (Module module : modules) {
				if (name.equals(module.getName())) {
					noConflict = false;
					break;
				}
			}
			if (noConflict)
				return name;
		}
	}
}
