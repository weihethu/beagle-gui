package events;

import gui.drawers.DrawableObject;

public class ModuleEditEvent extends ObjectEditEvent {

	public enum EventType {
		ADD, REMOVE, MOVE, NAME
	};

	private EventType type;

	public ModuleEditEvent(DrawableObject target, EventType type) {
		super(target);
		this.type = type;
	}

	public boolean isAdd() {
		return this.type == EventType.ADD;
	}

	public boolean isRemove() {
		return this.type == EventType.REMOVE;
	}

	public boolean isMove() {
		return this.type == EventType.MOVE;
	}

	public boolean isNameChange() {
		return this.type == EventType.NAME;
	}
}
