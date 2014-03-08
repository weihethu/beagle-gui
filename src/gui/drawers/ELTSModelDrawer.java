package gui.drawers;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import model.ELTSModel;
import model.ELTSModule;
import events.ModuleEditEvent;
import events.ObjectEditEvent;
import events.listeners.ObjectEditListener;

public class ELTSModelDrawer extends ObjectDrawer {
	public static int MODULE_RADIUS = 20;
	private Rectangle selectionBounds = new Rectangle(0, 0, -1, -1);
	private static final Color MODULE_COLOR = new Color(255, 255, 150);
	
	public ELTSModelDrawer(ELTSModel model) {
		super(model);

		DrawerListener listener = new DrawerListener();
		this.getModel().addModuleListener(listener);
	}

	@Override
	public void drawInternal(Graphics graphics) {
		ELTSModule[] modules = ((ELTSModel) this.target).getModules();
		for (ELTSModule module : modules) {
			drawModule(module, graphics);
		}
		drawSelectionBox(graphics);
	}
	
	private void drawModule(ELTSModule module, Graphics graphics) {
		drawModuleBackground(graphics, module, module.getPoint(), MODULE_COLOR);

		Point currentPt = module.getPoint();
		graphics.setColor(Color.black);
		int strWidth = (int) (graphics.getFontMetrics().getStringBounds(
				module.getName(), graphics).getWidth()) >> 1;
		int strHeight = (int) graphics.getFontMetrics().getAscent() >> 1;

		graphics.drawString(module.getName(), currentPt.x - strWidth,
				currentPt.y - strHeight);
		graphics.drawRect(currentPt.x - ELTSModelDrawer.MODULE_RADIUS, currentPt.y
				- ELTSModelDrawer.MODULE_RADIUS, 2 * ELTSModelDrawer.MODULE_RADIUS,
				2 * ELTSModelDrawer.MODULE_RADIUS);
	}

	private void drawModuleBackground(Graphics graphics, ELTSModule module, Point pt,
			Color color) {
		graphics.setColor(color);
		if (module.isSelected())
			graphics.setColor(new Color(100, 200, 200));

		graphics.fillRect(pt.x - ELTSModelDrawer.MODULE_RADIUS, pt.y - ELTSModelDrawer.MODULE_RADIUS,
				2 * ELTSModelDrawer.MODULE_RADIUS, 2 * ELTSModelDrawer.MODULE_RADIUS);
	}
	
	public ELTSModel getModel() {
		return (ELTSModel) target;
	}

	public ELTSModule moduleAtPoint(Point pt) {
		ELTSModule[] modules = getModel().getModules();
		for (ELTSModule module : modules) {
			if (pt.distance(module.getPoint()) <= ELTSModelDrawer.MODULE_RADIUS)
				return module;
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

	private class DrawerListener implements ObjectEditListener {

		@Override
		public void objectEdit(ObjectEditEvent event) {
			if (event instanceof ModuleEditEvent) {
				ELTSModelDrawer.this.getView().repaint();
			}
		}

	}
}
