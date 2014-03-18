package events;

import gui.drawers.DrawableObject;

public class TransitionEditEvent extends ObjectEditEvent {
	public enum EventType {
		ADD, REMOVE, DESC
	};

	private EventType type;

	public TransitionEditEvent(DrawableObject target, EventType type) {
		super(target);
		this.type = type;
	}
}
