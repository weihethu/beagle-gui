package gui.drawers;

import gui.editors.Canvas;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

public abstract class ObjectDrawer {
	protected DrawableObject target;
	private Canvas view;
	protected AffineTransform currentTransform = new AffineTransform();
	protected boolean valid = false;
	protected boolean validBounds = false;

	public ObjectDrawer(DrawableObject target) {
		this.target = target;
		view = null;
	}

	public abstract void drawInternal(Graphics2D graphics);

	public void setObject(DrawableObject target) {
		this.target = target;
	}

	public DrawableObject getObject() {
		return this.target;
	}

	public Canvas getView() {
		return view;
	}

	public void setView(Canvas view) {
		this.view = view;
	}

	public Rectangle getBounds() {
		Rectangle rect = getUntransformedBounds();
		if(rect == null)
			return null;
		return (Rectangle) this.currentTransform
				.createTransformedShape(rect).getBounds();
	}
	
	public Rectangle getUntransformedBounds() {
		return null;		
	}

	public void invalidate() {
		this.valid = false;
		invalidateBounds();
	}

	public void invalidateBounds() {
		this.validBounds = false;
	}

	public void setTransform(AffineTransform transform) {
		this.currentTransform = transform;
	}
}
