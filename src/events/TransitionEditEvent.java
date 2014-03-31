package events;

import model.automata.Transition;

/**
 * the class for editing events of a Transition
 * 
 * @author Wei He
 * 
 */
public class TransitionEditEvent extends ObjectEditEvent {

	/**
	 * the event type enumeration for TransitionEditEvent
	 * 
	 * @author Wei He
	 * 
	 */
	public enum EventType {
		/**
		 * transition added
		 */
		ADD,
		/**
		 * transition removed
		 */
		REMOVE,
		/**
		 * transition's internal data changed
		 */
		DATA
	};

	/**
	 * event type
	 */
	private EventType type;

	/**
	 * constructor
	 * 
	 * @param target
	 *            transition edited
	 * @param type
	 *            event type
	 */
	public TransitionEditEvent(Transition target, EventType type) {
		super(target);
		this.type = type;
	}
}
