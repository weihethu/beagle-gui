package events;

import model.automata.State;

/**
 * the class for editing events of a State
 * 
 * @author Wei He
 * 
 */
public class StateEditEvent extends ObjectEditEvent {

	/**
	 * the event type enumeration for StateEditEvent
	 * 
	 * @author Wei He
	 * 
	 */
	public enum EventType {
		/**
		 * state added
		 */
		ADD,
		/**
		 * state removed
		 */
		REMOVE,
		/**
		 * state moved
		 */
		MOVE,
		/**
		 * state's name changed
		 */
		NAME,
		/**
		 * state set/not set an initial state
		 */
		INITIAL
	};

	private EventType type;

	/**
	 * constructor
	 * 
	 * @param target
	 *            state edited
	 * @param type
	 *            event type
	 */
	public StateEditEvent(State target, EventType type) {
		super(target);
		this.type = type;
	}

	/**
	 * whether the state is added
	 * @return a boolean indicator
	 */
	public boolean isAdd() {
		return this.type == EventType.ADD;
	}
	
	/**
	 * whether the state is removed
	 * @return a boolean indicator
	 */
	public boolean isRemove() {
		return this.type == EventType.REMOVE;
	}
	
	/**
	 * whether the state is moved
	 * @return a boolean indicator
	 */
	public boolean isMove() {
		return this.type == EventType.MOVE;
	}

	/**
	 * whether the state's name changed
	 * @return a boolean indicator
	 */
	public boolean isNameChange() {
		return this.type == EventType.NAME;
	}

	/**
	 * whether the state is set/not set an initial state
	 * @return a boolean indicator
	 */
	public boolean isInitialChange() {
		return this.type == EventType.INITIAL;
	}
}
