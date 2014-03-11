package events;

import gui.drawers.DrawableObject;

public class ModuleEditEvent extends ObjectEditEvent {
	public boolean isAdd;
	public boolean isMove;
	public boolean isNameChange;

	public ModuleEditEvent(DrawableObject target, boolean add, boolean move,
			boolean nameChange) {
		super(target);
		this.isAdd = add;
		this.isMove = move;
		this.isNameChange = nameChange;
	}
}
