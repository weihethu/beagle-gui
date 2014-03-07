package gui.drawers;

import java.awt.Graphics;

public abstract class ObjectDrawer {
	protected DrawableObject target;

	public ObjectDrawer(DrawableObject target) {
		this.target = target;
	}

	public abstract void drawExternal(Graphics graphics);
	public abstract void drawInternal(Graphics graphics);
	
	public void setObject(DrawableObject target) {
		this.target = target;
	}

	public DrawableObject getObject() {
		return this.target;
	}
}
