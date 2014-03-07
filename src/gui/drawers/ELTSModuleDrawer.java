package gui.drawers;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import model.ELTSModule;
import model.automata.State;

public class ELTSModuleDrawer extends ObjectDrawer {
	public static int radius = 20;
	private static final Color MODULE_COLOR = new Color(255, 255, 150);
	private Rectangle selectionBounds = new Rectangle(0, 0, -1, -1);

	public ELTSModuleDrawer(ELTSModule module) {
		super(module);
	}

	@Override
	public void drawExternal(Graphics graphics) {
		ELTSModule module = (ELTSModule) target;
		drawBackground(graphics, module, module.getPoint(), MODULE_COLOR);

		Point currentPt = module.getPoint();
		graphics.setColor(Color.black);
		int strWidth = (int) (graphics.getFontMetrics().getStringBounds(
				module.getName(), graphics).getWidth()) >> 1;
		int strHeight = (int) graphics.getFontMetrics().getAscent() >> 1;

		graphics.drawString(module.getName(), currentPt.x - strWidth,
				currentPt.y - strHeight);
		graphics.drawRect(currentPt.x - this.radius, currentPt.y - this.radius,
				2 * this.radius, 2 * this.radius);
	}

	@Override
	public void drawInternal(Graphics graphics) {
		StateDrawer stateDrawer = new StateDrawer(null);
		State[] states = getModule().getStates();
		for (State state : states) {
			stateDrawer.setObject(state);
			stateDrawer.drawExternal(graphics);
		}
		drawSelectionBox(graphics);
	}

	private void drawBackground(Graphics graphics, ELTSModule module, Point pt,
			Color color) {
		graphics.setColor(color);
		if (module.isSelected())
			graphics.setColor(new Color(100, 200, 200));

		graphics.fillRect(pt.x - this.radius, pt.y - this.radius,
				2 * this.radius, 2 * this.radius);
	}

	public ELTSModule getModule() {
		return (ELTSModule) target;
	}

	public State stateAtPoint(Point pt) {
		State[] states = getModule().getStates();
		for (State state : states) {
			if (pt.distance(state.getPoint()) <= StateDrawer.radius)
				return state;
		}
		return null;
	}

	private void drawSelectionBox(Graphics graphics) {
		graphics.drawRect(this.selectionBounds.x, this.selectionBounds.y,
				this.selectionBounds.width, this.selectionBounds.height);
	}

	public void setSelectionBounds(Rectangle rect) {
		selectionBounds = rect;
	}
}
