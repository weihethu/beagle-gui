package events;

import model.Module;

/**
 * the class for editing events of a module
 * 
 * @author Wei He
 * 
 */
public class ModuleEditEvent extends ObjectEditEvent {

	/**
	 * the event type enumeration for ModuleEditEvent
	 * 
	 * @author Wei He
	 * 
	 */
	public enum EventType {
		/**
		 * module added
		 */
		ADD,
		/**
		 * module removed
		 */
		REMOVE,
		/**
		 * module moved
		 */
		MOVE,
		/**
		 * module name changed
		 */
		NAME
	};

	/**
	 * the event type
	 */
	private EventType type;

	/**
	 * constructor
	 * 
	 * @param target
	 *            the module edited
	 * @param type
	 *            event type
	 */
	public ModuleEditEvent(Module target, EventType type) {
		super(target);
		this.type = type;
	}

	/**
	 * whether the module is added
	 * 
	 * @return a boolean indicator
	 */
	public boolean isAdd() {
		return this.type == EventType.ADD;
	}

	/**
	 * whether the module is removed
	 * 
	 * @return a boolean indicator
	 */
	public boolean isRemove() {
		return this.type == EventType.REMOVE;
	}

	/**
	 * whether the module moves position
	 * 
	 * @return a boolean indicator
	 */
	public boolean isMove() {
		return this.type == EventType.MOVE;
	}

	/**
	 * whether the module changes its name
	 * 
	 * @return a boolean indicator
	 */
	public boolean isNameChange() {
		return this.type == EventType.NAME;
	}
}
