package events;

import gui.drawers.DrawableObject;

public class TransitionEditEvent extends ObjectEditEvent {
	boolean isAdd;

	public TransitionEditEvent(DrawableObject target, boolean add) {
		super(target);
		this.isAdd = add;
	}
}
