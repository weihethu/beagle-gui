package events;

import gui.Note;

public class NoteEditEvent extends ObjectEditEvent {
	public enum EventType {
		TEXT
	};

	private EventType type;

	public NoteEditEvent(Note target, EventType type) {
		super(target);
		this.type = type;
	}

	public boolean isTextChange() {
		return this.type == EventType.TEXT;
	}
}
