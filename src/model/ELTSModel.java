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

public class ELTSModel extends DrawableObject {
	private Set<ELTSModule> modules;
	private ELTSModule[] cachedModules = null;
	private Set<ObjectEditListener> moduleListeners = null;

	public ELTSModel() {
		modules = new HashSet<ELTSModule>();
		moduleListeners = new HashSet<ObjectEditListener>();
	}

	public ELTSModule createModule(Point pt) {
		int id = 0;
		while (getModuleWithID(id) != null)
			id++;
		ELTSModule module = new ELTSModule(id, pt, this);
		addModule(module);
		distributeModuleEditEvent(new ModuleEditEvent(module, true, false,
				false));
		return module;
	}

	public ELTSModule getModuleWithID(int id) {
		Iterator<ELTSModule> iter = this.modules.iterator();
		while (iter.hasNext()) {
			ELTSModule currentModule = iter.next();
			if (currentModule.getID() == id)
				return currentModule;
		}
		return null;
	}

	private void addModule(ELTSModule module) {
		this.modules.add(module);
		this.cachedModules = null;
	}

	@SuppressWarnings("unchecked")
	public ELTSModule[] getModules() {
		if (this.cachedModules == null) {
			this.cachedModules = (ELTSModule[]) this.modules
					.toArray(new ELTSModule[0]);
			Arrays.sort(this.cachedModules, new Comparator() {

				@Override
				public int compare(Object obj1, Object obj2) {
					return ((ELTSModule) obj1).getID()
							- ((ELTSModule) obj2).getID();
				}

			});
		}
		return this.cachedModules;
	}

	public void selectModulesWithinBounds(Rectangle rect) {
		ELTSModule[] modules = getModules();
		for (int i = 0; i < modules.length; i++) {
			modules[i].setSelect(false);
			if (rect.contains(modules[i].getPoint()))
				modules[i].setSelect(true);
		}
	}

	public void unselectAll() {
		ELTSModule[] modules = getModules();
		for (ELTSModule module : modules)
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
}
