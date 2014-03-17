package events;

import gui.drawers.DrawableObject;

public class StateEditEvent extends ObjectEditEvent {

	public boolean isAdd;
	public boolean isMove;
	public boolean isNameChange;
	public boolean isIntialChange;

	public StateEditEvent(DrawableObject target, boolean add, boolean move,
			boolean nameChange, boolean initialChange) {
		super(target);
		this.isAdd = add;
		this.isMove = move;
		this.isNameChange = nameChange;
		this.isIntialChange = initialChange;
	}
}
