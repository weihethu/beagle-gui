package events;

import gui.drawers.DrawableObject;

public class ModuleEditEvent extends ObjectEditEvent {
	boolean isAdd;
	boolean isMove;
	boolean isAttributeChange;

	public ModuleEditEvent(DrawableObject target, boolean add, boolean move,
			boolean attributeChange) {
		super(target);
		this.isAdd = add;
		this.isMove = move;
		this.isAttributeChange = attributeChange;
	}
}
