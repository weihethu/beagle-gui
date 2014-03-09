package events;

import gui.drawers.DrawableObject;

public class StateEditEvent extends ObjectEditEvent {

	boolean isAdd;
	boolean isMove;
	boolean isAttributeChange;

	public StateEditEvent(DrawableObject target, boolean add, boolean move,
			boolean attributeChange) {
		super(target);
		this.isAdd = add;
		this.isMove = move;
		this.isAttributeChange = attributeChange;
	}

	public boolean getIsAdd() {
		return isAdd;
	}

	public boolean getIsMove() {
		return isMove;
	}

	public boolean getIsAttributeChange() {
		return isAttributeChange;
	}
}
