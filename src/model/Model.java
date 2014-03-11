package model;

import events.ModuleEditEvent;
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
	}

	public Module createModule(Point pt) {
		int id = 0;
		while (getModuleWithID(id) != null)
			id++;
		Module module = new Module(id, pt, this);
		addModule(module);
		distributeModuleEditEvent(new ModuleEditEvent(module, true, false,
				false));
		return module;
	}

	public Module getModuleWithID(int id) {
		Iterator<Module> iter = this.modules.iterator();
		while (iter.hasNext()) {
			Module currentModule = iter.next();
			if (currentModule.getID() == id)
				return currentModule;
		}
		return null;
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
					return ((Module) obj1).getID() - ((Module) obj2).getID();
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
		this.distributeModuleEditEvent(new ModuleEditEvent(module, false,
				false, false));
	}
}
