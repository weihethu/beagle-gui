package gui.toolbars.tools;

import gui.drawers.ModuleDrawer;
import gui.editors.Canvas;
import gui.editors.TransitionCreator;

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
import javax.swing.KeyStroke;

import model.Module;
import model.automata.State;

public class TransitionTool extends Tool {
	private State firstState;
	private Point hoverPt;
	private static Stroke STROKE = new BasicStroke(2.4F);
	private static Color COLOR = new Color(0.5F, 0.5F, 0.5F, 0.5F);
	private TransitionCreator creator = null;

	public TransitionTool(Canvas canvas, ModuleDrawer drawer) {
		super(canvas, drawer);
		creator = new TransitionCreator(canvas);
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
		getCanvas().repaint();
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		if (this.firstState == null)
			return;

		State secondState = getDrawer().stateAtPoint(event.getPoint());
		if (secondState != null
				&& ((Module) getDrawer().getObject())
						.getTransitionFromStateToState(firstState, secondState) == null) {
			creator.createTransition(firstState, secondState);
		}
		this.firstState = null;
		getCanvas().repaint();
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

	@Override
	public KeyStroke getKey() {
		return KeyStroke.getKeyStroke('t');
	}
}
