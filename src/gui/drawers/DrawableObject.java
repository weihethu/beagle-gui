package gui.drawers;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import events.ObjectEditEvent;
import events.listeners.ObjectEditListener;

public class DrawableObject {

	private Set<ObjectEditListener> moduleListeners;
	
	public DrawableObject() {
	moduleListeners = new HashSet<ObjectEditListener>();
	}

	public void addObjectEditListener(ObjectEditListener listener) {
		moduleListeners.add(listener);
	}

	public void distributeObjectEditEvent() {
		Iterator<ObjectEditListener> iter = moduleListeners.iterator();
		while (iter.hasNext()) {
			ObjectEditListener currentListener = iter.next();
			currentListener.objectEdit(new ObjectEditEvent(this));
		}
	}
}
