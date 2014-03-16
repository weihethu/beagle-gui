package events;

import gui.drawers.DrawableObject;

public class TransitionEditEvent extends ObjectEditEvent {
	boolean isAdd;
	boolean isDescriptionChange;

	public TransitionEditEvent(DrawableObject target, boolean add,
			boolean descriptionChange) {
		super(target);
		this.isAdd = add;
		this.isDescriptionChange = descriptionChange;
	}
}
