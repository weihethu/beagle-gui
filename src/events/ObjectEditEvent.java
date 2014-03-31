package events;

import java.util.EventObject;

/**
 * the abstract class for editing a object
 * @author Wei He
 *
 */
public class ObjectEditEvent extends EventObject {
	/**
	 * the object edited
	 */
	private Object target;

	/**
	 * constructor
	 * @param target the edited object
	 */
	public ObjectEditEvent(Object target) {
		super(target);

		this.target = target;
	}

	/**
	 * get the object being edited
	 * @return object
	 */
	public Object getTarget() {
		return target;
	}
}
