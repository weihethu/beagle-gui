package events;

import gui.entities.Note;

/**
 * the class for editing events of a Note
 * 
 * @author Wei He
 * 
 */
public class NoteEditEvent extends ObjectEditEvent {

	/**
	 * the event type enumeration for NoteEditEvent
	 * 
	 * @author Wei He
	 * 
	 */
	public enum EventType {
		/**
		 * note text changed
		 */
		TEXT
	};

	/**
	 * the event type
	 */
	private EventType type;

	/**
	 * constructor
	 * 
	 * @param target
	 *            the note edited
	 * @param type
	 *            event type
	 */
	public NoteEditEvent(Note target, EventType type) {
		super(target);
		this.type = type;
	}

	/**
	 * whether the note's text changed
	 * 
	 * @return a boolean indicator
	 */
	public boolean isTextChange() {
		return this.type == EventType.TEXT;
	}
}
