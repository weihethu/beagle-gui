package events;

import gui.drawers.DrawableObject;

public class TransitionEditEvent extends ObjectEditEvent {
	boolean isAdd;
	boolean isAttributeChange;

	public TransitionEditEvent(DrawableObject target, boolean add,
			boolean attributeChange) {
		super(target);
		this.isAdd = add;
		this.isAttributeChange = attributeChange;
	}
}
