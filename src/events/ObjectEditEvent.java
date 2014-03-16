package events;

import gui.drawers.DrawableObject;

import java.util.EventObject;

public class ObjectEditEvent extends EventObject {
	private DrawableObject target;

	public ObjectEditEvent(DrawableObject target) {
		super(target);

		this.target = target;
	}

	public DrawableObject getTarget() {
		return target;
	}
}
