package gui.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.QuadCurve2D;

import model.automata.Transition;

public class CurvedArrow {
	private Point startPt;
	private Point endPt;
	private Point controlPt;
	private QuadCurve2D.Double curve;
	private double curvy;
	private Transition transition;
	private boolean needsRefresh = true;
	private static double ARROW_ANGLE = Math.PI / 10;
	private static double ARROW_LENGTH = 15.0;
	private static double HEIGHT = 30;

	public CurvedArrow(Point start, Point end, double curvy,
			Transition transition) {
		this.curve = new QuadCurve2D.Double();
		setStartPoint(start);
		setEndPoint(end);
		this.controlPt = new Point();
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
		 //drawText(graphics);
//		graphics.drawString("arrow", 100, 100);
//		System.out.println("Draw Arrow");
	}

	public void drawHighlight(Graphics2D graphics) {

	}

	private void drawArrow(Graphics graphics) {
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

	private void refreshCurve() {
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
			this.controlPt.y = (int) (midY + this.curvy * HEIGHT * cos);
		} else {
			this.controlPt.x = this.transition.getControl().x;
			this.controlPt.y = this.transition.getControl().y;
		}
		this.curve.setCurve(this.startPt, this.controlPt, this.endPt);
	}

	public Transition getTransition() {
		return transition;
	}

	public void drawControlPoint(Graphics2D graphics) {

	}
}
