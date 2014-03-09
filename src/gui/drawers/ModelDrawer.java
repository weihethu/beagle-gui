package gui.drawers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import model.Model;
import model.Module;
import events.ModuleEditEvent;
import events.ObjectEditEvent;
import events.listeners.ObjectEditListener;
import gui.Environment;

public class ModelDrawer extends ObjectDrawer {
	public static int MODULE_RADIUS = 40;
	private Rectangle selectionBounds = new Rectangle(0, 0, -1, -1);
	private static final Color MODULE_COLOR = new Color(255, 255, 150);
	private static final Color SELECTED_COLOR = new Color(100, 200, 200);
	private Rectangle cachedBounds = null;
	private boolean detailed = true;
	private static final int MARGINAL_LEN = 10;

	public ModelDrawer(Model model) {
		super(model);

		DrawerListener listener = new DrawerListener();
		this.getModel().addModuleListener(listener);
	}

	@Override
	public void drawInternal(Graphics2D graphics) {
		Graphics2D tmpGraphics2D = (Graphics2D) graphics.create();
		tmpGraphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		Module[] modules = ((Model) this.target).getModules();
		for (Module module : modules) {
			drawModule(module, tmpGraphics2D);
		}
		drawSelectionBox(tmpGraphics2D);
		tmpGraphics2D.dispose();
	}

	@Override
	public Rectangle getUntransformedBounds() {
		if (this.validBounds)
			return this.cachedBounds;

		Module[] modules = getModel().getModules();
		if (modules.length == 0)
			return null;
		Rectangle rect = getBounds(modules[0]);
		for (int i = 1; i < modules.length; i++) {
			rect.add(getBounds(modules[i]));
		}
		this.validBounds = true;
		return this.cachedBounds = rect;
	}

	private Rectangle getBounds(Module module) {
		Point pt = module.getPoint();
		if (!this.detailed || module.getStates().length == 0) {
			return new Rectangle(pt.x - ModelDrawer.MODULE_RADIUS, pt.y
					- ModelDrawer.MODULE_RADIUS, 2 * ModelDrawer.MODULE_RADIUS,
					2 * ModelDrawer.MODULE_RADIUS);
		} else {
			ModuleDrawer moduleDrawer = (ModuleDrawer) Environment
					.getInstance().getDrawer(module);

			Rectangle moduleBounds = moduleDrawer.getUntransformedBounds();
			Rectangle internalBounds = new Rectangle(moduleBounds.x
					- MARGINAL_LEN, moduleBounds.y - MARGINAL_LEN,
					moduleBounds.width + 2 * MARGINAL_LEN, moduleBounds.height
							+ 2 * MARGINAL_LEN);
			return new Rectangle(pt.x - internalBounds.width / 2, pt.y
					- internalBounds.height / 2, internalBounds.width,
					internalBounds.height);
		}
	}

	private void drawModule(Module module, Graphics2D graphics) {
		if (!detailed || module.getStates().length == 0) {
			Point currentPt = module.getPoint();
			drawModuleBackground(graphics, module, new Rectangle(currentPt.x
					- MODULE_RADIUS, currentPt.y - MODULE_RADIUS,
					2 * MODULE_RADIUS, 2 * MODULE_RADIUS), MODULE_COLOR);

			graphics.setColor(Color.black);
			int strWidth = (int) (graphics.getFontMetrics().getStringBounds(
					module.getName(), graphics).getWidth()) >> 1;
			int strHeight = (int) graphics.getFontMetrics().getAscent() >> 1;

			graphics.drawString(module.getName(), currentPt.x - strWidth,
					currentPt.y - strHeight);
			graphics.drawRect(currentPt.x - ModelDrawer.MODULE_RADIUS,
					currentPt.y - ModelDrawer.MODULE_RADIUS,
					2 * ModelDrawer.MODULE_RADIUS,
					2 * ModelDrawer.MODULE_RADIUS);
		} else {
			Graphics2D tmpGraphics2D = (Graphics2D) graphics.create();
			Point currentPt = module.getPoint();

			ModuleDrawer moduleDrawer = (ModuleDrawer) Environment
					.getInstance().getDrawer(module);

			Rectangle moduleBounds = moduleDrawer.getUntransformedBounds();
			Rectangle internalBounds = new Rectangle(moduleBounds.x
					- MARGINAL_LEN, moduleBounds.y - MARGINAL_LEN,
					moduleBounds.width + 2 * MARGINAL_LEN, moduleBounds.height
							+ 2 * MARGINAL_LEN);

			drawModuleBackground(tmpGraphics2D, module, new Rectangle(
					currentPt.x - internalBounds.width / 2, currentPt.y
							- internalBounds.height / 2, internalBounds.width,
					internalBounds.height), MODULE_COLOR);
			tmpGraphics2D.setColor(Color.black);
			tmpGraphics2D.drawRect(currentPt.x - internalBounds.width / 2,
					currentPt.y - internalBounds.height / 2,
					internalBounds.width, internalBounds.height);

			tmpGraphics2D.translate(-internalBounds.x, -internalBounds.y);
			tmpGraphics2D.translate(currentPt.x - internalBounds.width / 2,
					currentPt.y - internalBounds.height / 2);
			moduleDrawer.drawExternal(tmpGraphics2D);
			tmpGraphics2D.dispose();
		}

	}

	private void drawModuleBackground(Graphics2D graphics, Module module,
			Rectangle rect, Color color) {
		graphics.setColor(color);
		if (module.isSelected())
			graphics.setColor(SELECTED_COLOR);
		graphics.fill(rect);
	}

	public Model getModel() {
		return (Model) target;
	}

	public Module moduleAtPoint(Point pt) {
		Module[] modules = getModel().getModules();
		for (Module module : modules) {
			if (getBounds(module).contains(pt))
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
		invalidateBounds();
		if (getView() != null)
			this.getView().repaint();
	}

	private class DrawerListener implements ObjectEditListener {

		@Override
		public void objectEdit(ObjectEditEvent event) {
			if (event instanceof ModuleEditEvent) {
				moduleEditHandler((ModuleEditEvent) event);
			}
		}

	}

}
