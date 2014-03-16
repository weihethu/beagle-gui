package events;

import gui.drawers.DrawableObject;

public class ModuleEditEvent extends ObjectEditEvent {
	public boolean isAdd;
	public boolean isRemove;
	public boolean isMove;
	public boolean isNameChange;

	public ModuleEditEvent(DrawableObject target, boolean add, boolean remove,
			boolean move, boolean nameChange) {
		super(target);
		this.isAdd = add;
		this.isRemove = remove;
		this.isMove = move;
		this.isNameChange = nameChange;
	}
}
