package gui.entities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import model.automata.Transition;

public class CurvedArrow {
	private Point startPt;
	private Point endPt;
	private Point controlPt;
	private Point highPt;
	private QuadCurve2D.Double curve;
	private double curvy;
	private Transition transition;
	private boolean needsRefresh = true;
	private static double ARROW_ANGLE = Math.PI / 10;
	private static double ARROW_LENGTH = 15.0;
	private static double HEIGHT = 30;
	private static int CONTROLPT_RADIUS = 5;
	private static Color HIGHLIGHT_COLOR = new Color(255, 0, 0, 128);
	private String label = "";
	private Rectangle2D bounds = new Rectangle(0, 0);
	private static BufferedImage bufferedImg = new BufferedImage(1, 1,
			BufferedImage.TYPE_INT_RGB);
	private static Graphics GRAPHICS = bufferedImg.getGraphics();
	private static FontMetrics METRICS = GRAPHICS.getFontMetrics();
	private AffineTransform affineToText;

	public CurvedArrow(Point start, Point end, double curvy,
			Transition transition) {
		this.curve = new QuadCurve2D.Double();
		setStartPoint(start);
		setEndPoint(end);
		this.controlPt = new Point();
		this.highPt = new Point();
		setCurvy(curvy);
		this.transition = transition;
	}

	public Point getStartPoint() {
		return startPt;
	}

	public void setStartPoint(Point startPt) {
		this.startPt = startPt;
		this.needsRefresh = true;
	}

	public Point getEndPoint() {
		return endPt;
	}

	public void setEndPoint(Point endPt) {
		this.endPt = endPt;
		this.needsRefresh = true;
	}

	private void setCurvy(double curvy) {
		this.curvy = curvy;
		this.needsRefresh = true;
	}

	public void draw(Graphics2D graphics) {
		graphics.setColor(Color.black);
		if (this.needsRefresh)
			refreshCurve();
		graphics.draw(this.curve);
		drawArrow(graphics);
		drawText(graphics);
	}

	public void drawHighlight(Graphics2D graphics) {
		if (this.needsRefresh)
			refreshCurve();
		Graphics2D tmpGraphics = (Graphics2D) graphics.create();
		tmpGraphics.setStroke(new BasicStroke(6.0F));
		tmpGraphics.setColor(HIGHLIGHT_COLOR);
		tmpGraphics.draw(this.curve);
		tmpGraphics.transform(this.affineToText);
		tmpGraphics.fill(this.bounds);
		tmpGraphics.dispose();
	}

	private void drawArrow(Graphics2D graphics) {
		double angle = Math.atan2(this.controlPt.y - this.endPt.y,
				this.controlPt.x - this.endPt.x);

		angle += ARROW_ANGLE;
		int endX = (int) (Math.cos(angle) * ARROW_LENGTH) + this.endPt.x;
		int endY = (int) (Math.sin(angle) * ARROW_LENGTH) + this.endPt.y;
		graphics.drawLine(this.endPt.x, this.endPt.y, endX, endY);

		angle -= 2 * ARROW_ANGLE;
		endX = (int) (Math.cos(angle) * ARROW_LENGTH) + this.endPt.x;
		endY = (int) (Math.sin(angle) * ARROW_LENGTH) + this.endPt.y;
		graphics.drawLine(this.endPt.x, this.endPt.y, endX, endY);
	}

	private void drawText(Graphics2D graphics) {
		Graphics2D tmpGraphics2D = (Graphics2D) graphics.create();
		tmpGraphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		tmpGraphics2D.transform(this.affineToText);

		FontMetrics fontMetrics = tmpGraphics2D.getFontMetrics();
		this.bounds = fontMetrics.getStringBounds(getLabel(), tmpGraphics2D);

		int tmp = this.endPt.x < this.startPt.x ? 1 : 0;
		double d1 = this.bounds.getWidth() / 2;
		double d2 = ((this.curvy < 0 ? 1 : 0) ^ tmp) != 0 ? fontMetrics
				.getAscent() : -fontMetrics.getDescent();

		this.bounds.setRect(this.bounds.getX() - d1, this.bounds.getY() + d2,
				this.bounds.getWidth(), this.bounds.getHeight());

		for (int i = 0; i < this.label.length(); i += 4) {
			String str = this.label.substring(i,
					Math.min(i + 4, this.label.length()));
			tmpGraphics2D.drawString(str, -(int) d1, (int) d2);
			d1 = d1
					- fontMetrics.getStringBounds(str, tmpGraphics2D)
							.getWidth();
		}
		tmpGraphics2D.dispose();
	}

	public void setLabel(String str) {
		this.label = str;

		this.bounds = METRICS.getStringBounds(getLabel(), GRAPHICS);
		int tmp = this.endPt.x < this.startPt.x ? 1 : 0;
		double d1 = this.bounds.getWidth() / 2.0;
		double d2 = ((this.curvy < 0 ? 1 : 0) ^ tmp) != 0 ? METRICS.getAscent()
				: -METRICS.getDescent();

		this.bounds.setRect(this.bounds.getX() - d1, this.bounds.getY() + d2,
				this.bounds.getWidth(), this.bounds.getHeight());
	}

	public String getLabel() {
		return this.label;
	}

	public void refreshCurve() {
		this.needsRefresh = false;
		double dx = this.endPt.x - this.startPt.x;
		double dy = this.endPt.y - this.startPt.y;
		double midX = (this.startPt.x + this.endPt.x) / 2;
		double midY = (this.startPt.y + this.endPt.y) / 2;

		double dist = Math.sqrt(dx * dx + dy * dy);
		double cos = (dist == 0 ? 0 : dx / dist);
		double sin = (dist == 0 ? 0 : dy / dist);

		if (this.transition.getControl() == null) {
			this.controlPt.x = (int) (midX + this.curvy * HEIGHT * sin);
			this.controlPt.y = (int) (midY - this.curvy * HEIGHT * cos);
			this.highPt.x = (int) (midX + this.curvy * HEIGHT * sin / 2.0);
			this.highPt.y = (int) (midY - this.curvy * HEIGHT * cos / 2.0);
		} else {
			this.controlPt.x = this.transition.getControl().x;
			this.controlPt.y = this.transition.getControl().y;
			this.highPt.x = (int) ((this.controlPt.x + midX) / 2.0);
			this.highPt.y = (int) ((this.controlPt.y + midY) / 2.0);
		}
		this.curve.setCurve(this.startPt, this.controlPt, this.endPt);

		this.affineToText = new AffineTransform();
		this.affineToText.translate(this.highPt.x, this.highPt.y);
		this.affineToText.rotate(Math.atan2(dy, dx));
		if (this.endPt.x < this.startPt.x)
			this.affineToText.rotate(Math.PI);
	}

	public Transition getTransition() {
		return transition;
	}

	public void drawControlPoint(Graphics2D graphics) {
		graphics.setColor(Color.black);
		graphics.drawOval(this.controlPt.x - CONTROLPT_RADIUS, this.controlPt.y
				- CONTROLPT_RADIUS, 2 * CONTROLPT_RADIUS, 2 * CONTROLPT_RADIUS);
	}

	public boolean isNear(Point pt, int maxDist) {
		if (this.needsRefresh)
			refreshCurve();
		return intersects(pt, maxDist, this.curve);
	}

	private boolean intersects(Point pt, int maxDist, QuadCurve2D.Double curve) {
		if (!curve.intersects(pt.x - maxDist, pt.y - maxDist, 2 * maxDist,
				2 * maxDist))
			return false;
		if (curve.getFlatness() < maxDist)
			return true;
		QuadCurve2D.Double leftCurve = new QuadCurve2D.Double();
		QuadCurve2D.Double rightCurve = new QuadCurve2D.Double();
		curve.subdivide(leftCurve, rightCurve);
		return intersects(pt, maxDist, leftCurve)
				|| intersects(pt, maxDist, rightCurve);
	}

	public Rectangle getBounds() {
		if (this.needsRefresh)
			refreshCurve();
		Rectangle rect = this.curve.getBounds();
		return rect;
	}

	public QuadCurve2D.Double getCurve() {
		return this.curve;
	}
}
