package gui.toolbars.tools;

import gui.drawers.ModuleDrawer;
import gui.editors.Canvas;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import model.Module;
import model.automata.State;
import model.automata.Transition;

public class TransitionTool extends Tool {
	private State firstState;
	private Point hoverPt;
	private static Stroke STROKE = new BasicStroke(2.4F);
	private static Color COLOR = new Color(0.5F, 0.5F, 0.5F, 0.5F);

	public TransitionTool(Canvas view, ModuleDrawer drawer) {
		super(view, drawer);
	}

	public Icon getIcon() {
		URL url = getClass().getResource("/assets/icons/transition.gif");
		return new ImageIcon(url);
	}

	@Override
	public ModuleDrawer getDrawer() {
		return (ModuleDrawer) super.getDrawer();
	}

	@Override
	public String getToolTip() {
		return "Transition Creator";
	}

	@Override
	public void mousePressed(MouseEvent event) {
		this.firstState = getDrawer().stateAtPoint(event.getPoint());
		if (this.firstState == null)
			return;
		this.hoverPt = this.firstState.getPoint();
	}

	@Override
	public void mouseDragged(MouseEvent event) {
		if (this.firstState == null)
			return;
		this.hoverPt = event.getPoint();
		getView().repaint();
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		if (this.firstState == null)
			return;

		State endState = getDrawer().stateAtPoint(event.getPoint());
		if (endState != null) {
			((Module) super.getObject()).addTransition(new Transition(
					firstState, endState));
		}
		this.firstState = null;
		getView().repaint();
	}

	@Override
	public void draw(Graphics graphics) {
		if (this.firstState == null)
			return;

		Graphics2D graphics2D = (Graphics2D) graphics;
		Stroke oldStroke = graphics2D.getStroke();
		graphics2D.setStroke(STROKE);
		graphics2D.setColor(COLOR);
		graphics2D.drawLine((int) this.firstState.getPoint().x,
				(int) this.firstState.getPoint().getY(),
				(int) this.hoverPt.getX(), (int) this.hoverPt.getY());
		graphics2D.setStroke(oldStroke);
	}
}
