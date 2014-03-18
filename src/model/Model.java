package model;

import events.ModuleEditEvent;
import events.ObjectEditEvent;
import events.listeners.ObjectEditListener;
import gui.drawers.DrawableObject;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Model extends DrawableObject {
	private Set<Module> modules;
	private Module[] cachedModules = null;
	private Set<ObjectEditListener> moduleListeners = null;

	public Model() {
		modules = new HashSet<Module>();
		moduleListeners = new HashSet<ObjectEditListener>();

		this.addModuleListener(new ObjectEditListener() {

			@Override
			public void objectEdit(ObjectEditEvent event) {
				if (event instanceof ModuleEditEvent) {
					ModuleEditEvent moduleEvt = (ModuleEditEvent) event;
					if (moduleEvt.isNameChange())
						Model.this.cachedModules = null;
				}
			}

		});
	}

	public Module createModule(Point pt) {
		Module module = new Module(getDefaultModuleName(), pt, this);
		addModule(module);
		distributeModuleEditEvent(new ModuleEditEvent(module,
				ModuleEditEvent.EventType.ADD));
		return module;
	}

	private void addModule(Module module) {
		this.modules.add(module);
		this.cachedModules = null;
	}

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

	public void selectModulesWithinBounds(Rectangle rect) {
		Module[] modules = getModules();
		for (int i = 0; i < modules.length; i++) {
			modules[i].setSelect(false);
			if (rect.contains(modules[i].getPoint()))
				modules[i].setSelect(true);
		}
	}

	public void unselectAll() {
		Module[] modules = getModules();
		for (Module module : modules)
			module.setSelect(false);
	}

	public void addModuleListener(ObjectEditListener listener) {
		this.moduleListeners.add(listener);
	}

	public void distributeModuleEditEvent(ModuleEditEvent event) {
		Iterator<ObjectEditListener> iter = moduleListeners.iterator();
		while (iter.hasNext()) {
			ObjectEditListener currentListener = iter.next();
			currentListener.objectEdit(event);
		}
	}

	public void removeModule(Module module) {
		this.modules.remove(module);
		this.cachedModules = null;
		this.distributeModuleEditEvent(new ModuleEditEvent(module,
				ModuleEditEvent.EventType.REMOVE));
	}

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
