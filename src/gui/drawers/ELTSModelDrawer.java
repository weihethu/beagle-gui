package gui.drawers;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import model.ELTSModel;
import model.ELTSModule;
import events.ModuleEditEvent;
import events.ObjectEditEvent;
import events.StateEditEvent;
import events.listeners.ObjectEditListener;

public class ELTSModelDrawer extends ObjectDrawer {
	public static int MODULE_RADIUS = 20;
	private Rectangle selectionBounds = new Rectangle(0, 0, -1, -1);
	private static final Color MODULE_COLOR = new Color(255, 255, 150);
//	private boolean validBounds = false;
	private Rectangle cachedBounds = null;

	public ELTSModelDrawer(ELTSModel model) {
		super(model);

		DrawerListener listener = new DrawerListener();
		this.getModel().addModuleListener(listener);
	}

	@Override
	public void drawInternal(Graphics2D graphics) {
		Graphics2D tmpGraphics2D = (Graphics2D) graphics.create();
		tmpGraphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		ELTSModule[] modules = ((ELTSModel) this.target).getModules();
		for (ELTSModule module : modules) {
			drawModule(module, tmpGraphics2D);
		}
		drawSelectionBox(tmpGraphics2D);
		tmpGraphics2D.dispose();
	}

	@Override
	public Rectangle getBounds() {

		// return null;
		if (this.validBounds)
			return this.cachedBounds;

		ELTSModule[] modules = getModel().getModules();
		if (modules.length == 0)
			return null;
		Rectangle rect = getBounds(modules[0]);
		for (int i = 1; i < modules.length; i++) {
			rect.add(getBounds(modules[i]));
		}
		this.validBounds = true;
		return this.cachedBounds = this.currentTransform
				.createTransformedShape(rect).getBounds();
//		return this.cachedBounds = rect.getBounds();
	}

	private Rectangle getBounds(ELTSModule module) {
		Point pt = module.getPoint();
		return new Rectangle(pt.x - ELTSModelDrawer.MODULE_RADIUS, pt.y
				- ELTSModelDrawer.MODULE_RADIUS,
				2 * ELTSModelDrawer.MODULE_RADIUS,
				2 * ELTSModelDrawer.MODULE_RADIUS);
	}

	private void drawModule(ELTSModule module, Graphics2D graphics) {

		drawModuleBackground(graphics, module, module.getPoint(), MODULE_COLOR);

		Point currentPt = module.getPoint();
		graphics.setColor(Color.black);
		int strWidth = (int) (graphics.getFontMetrics().getStringBounds(
				module.getName(), graphics).getWidth()) >> 1;
		int strHeight = (int) graphics.getFontMetrics().getAscent() >> 1;

		graphics.drawString(module.getName(), currentPt.x - strWidth,
				currentPt.y - strHeight);
		graphics.drawRect(currentPt.x - ELTSModelDrawer.MODULE_RADIUS,
				currentPt.y - ELTSModelDrawer.MODULE_RADIUS,
				2 * ELTSModelDrawer.MODULE_RADIUS,
				2 * ELTSModelDrawer.MODULE_RADIUS);

	}

	private void drawModuleBackground(Graphics2D graphics, ELTSModule module,
			Point pt, Color color) {
		graphics.setColor(color);
		if (module.isSelected())
			graphics.setColor(new Color(100, 200, 200));

		graphics.fillRect(pt.x - ELTSModelDrawer.MODULE_RADIUS, pt.y
				- ELTSModelDrawer.MODULE_RADIUS,
				2 * ELTSModelDrawer.MODULE_RADIUS,
				2 * ELTSModelDrawer.MODULE_RADIUS);
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

	private void drawSelectionBox(Graphics2D graphics) {
		graphics.drawRect(this.selectionBounds.x, this.selectionBounds.y,
				this.selectionBounds.width, this.selectionBounds.height);
	}

	public void setSelectionBounds(Rectangle rect) {
		selectionBounds = rect;
	}
	
	private void moduleEditHandler(ModuleEditEvent event) {
		//moduleEditHandler((moduleEditEvent))
		invalidateBounds();
		this.getView().repaint();
	}
	private class DrawerListener implements ObjectEditListener {

		@Override
		public void objectEdit(ObjectEditEvent event) {
			if (event instanceof ModuleEditEvent) {
				//ELTSModelDrawer.this.getView().repaint();
				moduleEditHandler((ModuleEditEvent) event);
			}
		}

	}

}
