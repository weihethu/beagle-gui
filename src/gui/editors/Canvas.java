package gui.editors;

import gui.drawers.ObjectDrawer;
import gui.entities.Note;
import gui.toolbars.ToolBar;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;

/**
 * graph canvas
 * 
 * @author Wei He
 * 
 */
public class Canvas extends JPanel implements Scrollable {
	/**
	 * drawer
	 */
	private ObjectDrawer drawer;
	/**
	 * tool bar
	 */
	private ToolBar toolbar = null;
	/**
	 * transformation applied
	 */
	private AffineTransform transform = new AffineTransform();
	/**
	 * scale-by factor
	 */
	private double scaleBy = 1.0;
	/**
	 * whether needs to recompute transformation
	 */
	private boolean transformNeedsReform = true;
	/**
	 * scroll pane for tables when editing transitions
	 */
	private JScrollPane tableSp = null;
	/**
	 * location of scroll pane for tables when editing transitions
	 */
	private Point tableSpPoint = null;
	/**
	 * parent editor pane
	 */
	private EditorPane pane = null;

	/**
	 * constructor
	 * 
	 * @param drawer
	 *            object drawer
	 * @param pane
	 *            editor pane
	 */
	public Canvas(ObjectDrawer drawer, EditorPane pane) {
		this.drawer = drawer;
		this.pane = pane;
		drawer.setCanvas(this);

		this.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent evt) {
				requestTransform();
			}
		});
	}

	/**
	 * get object drawer
	 * 
	 * @return object drawer
	 */
	public ObjectDrawer getDrawer() {
		return drawer;
	}

	@Override
	public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		if (this.transformNeedsReform)
			reformTransform();

		// draw background
		graphics.setColor(Color.white);
		graphics.fillRect(0, 0, this.getWidth(), this.getHeight());

		// draw objects
		Graphics2D graphics2D = (Graphics2D) graphics.create();
		graphics2D.transform(this.transform);

		this.drawer.drawInternal(graphics2D);

		// draw for current tools
		if (this.toolbar != null)
			this.toolbar.drawTool(graphics2D);

		graphics2D.dispose();

		// draw notes on canvas
		for (Note note : drawer.getNotes()) {
			note.updateView();
		}
	}

	/**
	 * set tool bar
	 * 
	 * @param bar
	 *            tool bar
	 */
	public void setToolbar(ToolBar bar) {
		this.toolbar = bar;
	}

	/**
	 * get object bounds
	 * 
	 * @return bounds
	 */
	private Rectangle getObjectBounds() {
		Rectangle rect = this.drawer.getBounds();
		if (rect == null)
			return new Rectangle(getSize());
		return rect;
	}

	/**
	 * recompute transformation applied, this function comes from the decompiled
	 * code of JFLAP, actually I don't quite understand it
	 */
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

	/**
	 * set scale by factor
	 * 
	 * @param value
	 *            scale-by value
	 */
	public void setScale(double value) {
		this.scaleBy = value;
	}

	/**
	 * get scale by factor
	 * 
	 * @return scale-by value
	 */
	public double getScale() {
		return this.scaleBy;
	}

	/**
	 * force to recompute transform & repaint
	 */
	public void requestTransform() {
		this.transformNeedsReform = true;
		repaint();
	}

	@Override
	public void processMouseEvent(MouseEvent event) {
		transformMouseEvent(event);
		super.processMouseEvent(event);
	}

	@Override
	public void processMouseMotionEvent(MouseEvent event) {
		transformMouseEvent(event);
		super.processMouseMotionEvent(event);
	}

	/**
	 * get untransformed location for mouse event
	 * 
	 * @param event
	 *            mouse event
	 */
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

	/**
	 * apply transformation to point
	 * 
	 * @param pt
	 *            point
	 * @return transformed point
	 */
	public Point transfromFromCanvasToView(Point pt) {
		Point dest = new Point();
		this.transform.transform(pt, dest);
		return dest;
	}

	@Override
	public Component add(Component comp) {
		if (comp instanceof JScrollPane)
			this.tableSp = (JScrollPane) comp;
		return super.add(comp);
	}

	@Override
	public void remove(Component comp) {
		if (comp instanceof JScrollPane) {
			this.tableSp = null;
			this.tableSpPoint = null;
		}
		super.remove(comp);
	}

	/**
	 * set point for scroll pane for transition editing table
	 * 
	 * @param pt
	 *            point
	 */
	public void setTableSpPoint(Point pt) {
		this.tableSpPoint = pt;
	}

	@Override
	public void doLayout() {
		super.doLayout();
		if (this.tableSp != null && this.tableSpPoint != null) {
			this.tableSp.setLocation(this.tableSpPoint);
			this.tableSp.setSize(TransitionCreator.WIDTH,
					TransitionCreator.HEIGHT);
		}
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

	/**
	 * get parent editor pane
	 * 
	 * @return editor pane
	 */
	public EditorPane getCreator() {
		return pane;
	}
}
