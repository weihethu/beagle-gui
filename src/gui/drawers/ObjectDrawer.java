package gui.drawers;

import gui.editors.EditorCanvas;

import java.awt.Graphics;

public abstract class ObjectDrawer {
	protected DrawableObject target;
	private EditorCanvas view;

	public ObjectDrawer(DrawableObject target) {
		this.target = target;
	}

	public abstract void drawInternal(Graphics graphics);

	public void setObject(DrawableObject target) {
		this.target = target;
	}

	public DrawableObject getObject() {
		return this.target;
	}

	public EditorCanvas getView() {
		return view;
	}

	public void setView(EditorCanvas view) {
		this.view = view;
	}
}
