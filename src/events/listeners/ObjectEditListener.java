package events.listeners;

import java.util.EventListener;

import events.ObjectEditEvent;

public interface ObjectEditListener extends EventListener {
	public void objectEdit(ObjectEditEvent event);
}
