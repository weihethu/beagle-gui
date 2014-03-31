package events.listeners;

import java.util.EventListener;

import events.ObjectEditEvent;

/**
 * the event listener for object edit events
 * 
 * @author Wei He
 * 
 */
public interface ObjectEditListener extends EventListener {
	/**
	 * the handler for object edit events
	 * 
	 * @param event
	 *            event
	 */
	public void objectEdit(ObjectEditEvent event);
}
