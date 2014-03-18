package events;

import gui.drawers.DrawableObject;

public class StateEditEvent extends ObjectEditEvent {

	public enum EventType {
		ADD, REMOVE, MOVE, NAME, INITIAL
	};

	private EventType type;

	public StateEditEvent(DrawableObject target, EventType type) {
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

	public boolean isInitialChange() {
		return this.type == EventType.INITIAL;
	}
}
