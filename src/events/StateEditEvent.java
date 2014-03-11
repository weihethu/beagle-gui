package events;

import gui.drawers.DrawableObject;

public class StateEditEvent extends ObjectEditEvent {

	public boolean isAdd;
	public boolean isMove;
	public boolean isNameChange;

	public StateEditEvent(DrawableObject target, boolean add, boolean move,
			boolean nameChange) {
		super(target);
		this.isAdd = add;
		this.isMove = move;
		this.isNameChange = nameChange;
	}
}
