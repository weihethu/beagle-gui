package gui.editors;

import gui.drawers.ObjectDrawer;
import gui.toolbars.ToolBar;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;

import javax.swing.JPanel;
import javax.swing.Scrollable;

public class Canvas extends JPanel implements Scrollable {
	protected ObjectDrawer drawer;
	private ToolBar toolbar = null;;
	private AffineTransform transform = new AffineTransform();
	private double scaleBy = 1.0;
	private boolean transformNeedsReform = true;

	public Canvas(ObjectDrawer drawer) {
		this.drawer = drawer;
		drawer.setView(this);
	}

	public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		if (this.transformNeedsReform)
			reformTransform();

		graphics.setColor(Color.white);
		graphics.fillRect(0, 0, this.getWidth(), this.getHeight());

		Graphics2D graphics2D = (Graphics2D) graphics.create();
		graphics2D.transform(this.transform);

		this.drawer.drawInternal(graphics2D);
		if (this.toolbar != null)
			this.toolbar.drawTool(graphics2D);

		graphics2D.dispose();
	}

	public void setToolbar(ToolBar bar) {
		this.toolbar = bar;
	}

	private Rectangle getObjectBounds() {
		Rectangle rect = this.drawer.getBounds();
		if (rect == null)
			return new Rectangle(getSize());
		return rect;
	}

	private void reformTransform() {
		this.transformNeedsReform = false;

		Rectangle objectSizeRect = new Rectangle(getObjectBounds());
		Rectangle visibleRect = getVisibleRect();

		Point pt = new Point(
				(int) (visibleRect.x - this.transform.getTranslateX()),
				(int) (visibleRect.y - this.transform.getTranslateY()));
		Point pt1 = new Point(Math.min(objectSizeRect.x, Math.min(0, pt.x)),
				Math.min(objectSizeRect.y, Math.min(0, pt.y)));

		this.transform = new AffineTransform();
		this.drawer.setTransform(this.transform);
		this.drawer.invalidateBounds();

		this.transform.translate(-pt1.x, -pt1.y);
		this.transform.scale(this.scaleBy, this.scaleBy);

		Dimension dim = new Dimension(Math.max(objectSizeRect.width
				+ objectSizeRect.x, pt.x + visibleRect.width)
				- pt1.x, Math.max(objectSizeRect.height + objectSizeRect.y,
				pt.y + visibleRect.height) - pt1.y);

		if (dim.equals(getPreferredSize()))
			return;

		setPreferredSize(dim);
		revalidate();
		scrollRectToVisible(visibleRect);
	}

	public void setScale(double value) {
		this.scaleBy = value;
	}

	public double getScale() {
		return this.scaleBy;
	}

	public void requestTransform() {
		this.transformNeedsReform = true;
		repaint();
	}

	public void processMouseEvent(MouseEvent event) {
		transformMouseEvent(event);
		super.processMouseEvent(event);
	}

	public void processMouseMotionEvent(MouseEvent event) {
		transformMouseEvent(event);
		super.processMouseMotionEvent(event);
	}

	private void transformMouseEvent(MouseEvent event) {
		if (this.transformNeedsReform)
			reformTransform();
		Point destPt = new Point(), srcPt = event.getPoint();
		try {
			this.transform.inverseTransform(srcPt, destPt);
		} catch (NoninvertibleTransformException e) {
			e.printStackTrace();
		}
		event.translatePoint(destPt.x - srcPt.x, destPt.y - srcPt.y);
	}

	public Point transfromFromCanvasToView(Point pt) {
		Point dest = new Point();
		this.transform.transform(pt, dest);
		return dest;
	}

	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		return orientation == 1 ? visibleRect.height : visibleRect.width;
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return getPreferredSize().height < getParent().getSize().height;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return getPreferredSize().width < getParent().getSize().width;
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		return 5;
	}
}
