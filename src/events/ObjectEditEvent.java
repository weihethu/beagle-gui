package events;

import java.util.EventObject;

public class ObjectEditEvent extends EventObject {
	private Object target;

	public ObjectEditEvent(Object target) {
		super(target);

		this.target = target;
	}

	public Object getTarget() {
		return target;
	}
}
