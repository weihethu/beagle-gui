package gui.drawers;

import gui.editors.Canvas;
import gui.entities.Note;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

/**
 * drawer for drawable object
 * @author Wei He
 *
 */
public abstract class ObjectDrawer {
	/**
	 * object to be drawed
	 */
	protected DrawableObject target;
	/**
	 * canvas to draw on
	 */
	private Canvas canvas;
	/**
	 * current transformation applied
	 */
	protected AffineTransform currentTransform = new AffineTransform();
	/**
	 * whether cached bounds is updated
	 */
	protected boolean validBounds = false;
	
	/**
	 * constructor
	 * @param target object to be drawed
	 */
	public ObjectDrawer(DrawableObject target) {
		this.target = target;
		canvas = null;
	}
	
	/**
	 * draw internal of the object on canvas
	 * @param graphics graphics
	 */
	public abstract void drawInternal(Graphics2D graphics);
	
	/**
	 * set the object to be drawed
	 * @param target object
	 */
	public void setObject(DrawableObject target) {
		this.target = target;
	}
	
	/**
	 * get the object to be drawed
	 * @return object
	 */
	public DrawableObject getObject() {
		return this.target;
	}
	
	/**
	 * get canvas
	 * @return canvas
	 */
	public Canvas getCanvas() {
		return canvas;
	}
	
	/**
	 * set canvas
	 * @param canvas canvas
	 */
	public void setCanvas(Canvas canvas) {
		this.canvas = canvas;
	}
	
	/**
	 * get bounds of objects on canvas
	 * @return bounds
	 */
	public Rectangle getBounds() {
		Rectangle rect = getUntransformedBounds();
		if (rect == null)
			return null;
		return (Rectangle) this.currentTransform.createTransformedShape(rect)
				.getBounds();
	}
	
	/**
	 * get untransformed bounds of objects on canvas
	 * @return bounds
	 */
	public Rectangle getUntransformedBounds() {
		return null;
	}
	
	/**
	 * invalidate bounds, bounds needs to be recomputed
	 */
	public void invalidateBounds() {
		this.validBounds = false;
	}
	
	/**
	 * set the transformation matrix
	 * @param transform transform
	 */
	public void setTransform(AffineTransform transform) {
		this.currentTransform = transform;
	}
	
	/**
	 * get notes on canvas
	 * @return notes
	 */
	public Note[] getNotes() {
		return new Note[] {};
	}
}
