package gui.drawers;

import events.ModuleEditEvent;
import events.ObjectEditEvent;
import events.listeners.ObjectEditListener;
import gui.Environment;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import model.Model;
import model.Module;

/**
 * drawer for models
 * 
 * @author Wei He
 * 
 */
public class ModelDrawer extends ObjectDrawer {
	/**
	 * radius for drawing modules
	 */
	public static int MODULE_RADIUS = 40;
	/**
	 * selection box
	 */
	private Rectangle selectionBounds = new Rectangle(0, 0, -1, -1);
	/**
	 * color for drawing modules
	 */
	private static final Color MODULE_COLOR = new Color(255, 255, 150);
	/**
	 * color for drawing modules when selected
	 */
	private static final Color SELECTED_MODULE_COLOR = new Color(100, 200, 200);
	/**
	 * cached bounds
	 */
	private Rectangle cachedBounds = null;
	/**
	 * whether show internals of modules
	 */
	private boolean detailed = false;
	/**
	 * the extra side length added when drawing module internals
	 */
	private static final int MARGINAL_LEN = 10;

	/**
	 * constructor
	 * 
	 * @param model
	 *            model to be drawed
	 */
	public ModelDrawer(Model model) {
		super(model);

		this.getModel().addModuleListener(new ObjectEditListener() {

			@Override
			public void objectEdit(ObjectEditEvent event) {
				// if a module is edited, we must compute new bounds &
				// transformation
				invalidateBounds();
				if (getCanvas() != null) {
					getCanvas().requestTransform();
				}
			}

		});
	}

	/**
	 * set whether draw module internals
	 * 
	 * @param enabled
	 *            whether draw module internals
	 */
	public void setDrawInternal(boolean enabled) {
		detailed = enabled;
		getCanvas().repaint();
	}

	@Override
	public void drawInternal(Graphics2D graphics) {
		// turn on anti-aliasing, for better rendering quality
		Graphics2D tmpGraphics2D = (Graphics2D) graphics.create();
		tmpGraphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		// draw all modules
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

	/**
	 * get bounds for module
	 * 
	 * @param module
	 *            module
	 * @return module bounds
	 */
	private Rectangle getBounds(Module module) {
		Point pt = module.getPoint();
		if (!this.detailed || module.getStates().length == 0) {
			// not detailed view
			return new Rectangle(pt.x - ModelDrawer.MODULE_RADIUS, pt.y
					- ModelDrawer.MODULE_RADIUS, 2 * ModelDrawer.MODULE_RADIUS,
					2 * ModelDrawer.MODULE_RADIUS);
		} else {
			ModuleDrawer moduleDrawer = (ModuleDrawer) Environment
					.getInstance().getDrawer(module);

			// get internal module bounds
			Rectangle moduleBounds = moduleDrawer.getUntransformedBounds(true);
			// add extra marginal lengths
			Rectangle internalBounds = new Rectangle(moduleBounds.x
					- MARGINAL_LEN, moduleBounds.y - MARGINAL_LEN,
					moduleBounds.width + 2 * MARGINAL_LEN, moduleBounds.height
							+ 2 * MARGINAL_LEN);
			return new Rectangle(pt.x - internalBounds.width / 2, pt.y
					- internalBounds.height / 2, internalBounds.width,
					internalBounds.height);
		}
	}

	/**
	 * draw module
	 * 
	 * @param module
	 *            module
	 * @param graphics
	 *            graphics
	 */
	private void drawModule(Module module, Graphics2D graphics) {
		if (!detailed || module.getStates().length == 0) {
			// not show in detailed view
			Point currentPt = module.getPoint();
			// draw module background
			drawModuleBackground(graphics, module, new Rectangle(currentPt.x
					- MODULE_RADIUS, currentPt.y - MODULE_RADIUS,
					2 * MODULE_RADIUS, 2 * MODULE_RADIUS), MODULE_COLOR);

			// draw module name
			graphics.setColor(Color.black);
			int strWidth = (int) (graphics.getFontMetrics().getStringBounds(
					module.getName(), graphics).getWidth()) >> 1;
			int strHeight = (int) graphics.getFontMetrics().getAscent() >> 1;

			graphics.drawString(module.getName(), currentPt.x - strWidth,
					currentPt.y - strHeight);
			// draw module borders
			graphics.drawRect(currentPt.x - ModelDrawer.MODULE_RADIUS,
					currentPt.y - ModelDrawer.MODULE_RADIUS,
					2 * ModelDrawer.MODULE_RADIUS,
					2 * ModelDrawer.MODULE_RADIUS);
		} else {
			// show in detailed view
			Graphics2D tmpGraphics2D = (Graphics2D) graphics.create();
			Point currentPt = module.getPoint();

			ModuleDrawer moduleDrawer = (ModuleDrawer) Environment
					.getInstance().getDrawer(module);

			// get internal module bounds and add extra side lengths
			Rectangle moduleBounds = moduleDrawer.getUntransformedBounds(true);
			Rectangle internalBounds = new Rectangle(moduleBounds.x
					- MARGINAL_LEN, moduleBounds.y - MARGINAL_LEN,
					moduleBounds.width + 2 * MARGINAL_LEN, moduleBounds.height
							+ 2 * MARGINAL_LEN);

			// draw module background
			drawModuleBackground(tmpGraphics2D, module, new Rectangle(
					currentPt.x - internalBounds.width / 2, currentPt.y
							- internalBounds.height / 2, internalBounds.width,
					internalBounds.height), MODULE_COLOR);
			// draw module name
			tmpGraphics2D.setColor(Color.black);
			int strHeight = (int) tmpGraphics2D.getFontMetrics().getAscent();
			tmpGraphics2D.drawString(module.getName(), currentPt.x
					- internalBounds.width / 2, currentPt.y
					- internalBounds.height / 2 + strHeight);
			// draw module borders
			tmpGraphics2D.drawRect(currentPt.x - internalBounds.width / 2,
					currentPt.y - internalBounds.height / 2,
					internalBounds.width, internalBounds.height);

			// draw module internals
			tmpGraphics2D.translate(-internalBounds.x, -internalBounds.y);
			tmpGraphics2D.translate(currentPt.x - internalBounds.width / 2,
					currentPt.y - internalBounds.height / 2);
			moduleDrawer.drawExternal(tmpGraphics2D);
			tmpGraphics2D.dispose();
		}

	}

	/**
	 * draw module background
	 * 
	 * @param graphics
	 *            graphics
	 * @param module
	 *            module
	 * @param rect
	 *            background bounds
	 * @param color
	 *            background color
	 */
	private void drawModuleBackground(Graphics2D graphics, Module module,
			Rectangle rect, Color color) {
		graphics.setColor(color);
		if (module.isSelected())
			graphics.setColor(SELECTED_MODULE_COLOR);
		graphics.fill(rect);
	}

	/**
	 * get model to be drawed
	 * 
	 * @return model
	 */
	public Model getModel() {
		return (Model) target;
	}

	/**
	 * get module at point, return null if none
	 * 
	 * @param pt
	 *            point
	 * @return module at point
	 */
	public Module moduleAtPoint(Point pt) {
		Module[] modules = getModel().getModules();
		for (Module module : modules) {
			if (getBounds(module).contains(pt))
				return module;
		}
		return null;
	}

	/**
	 * draw selection box
	 * 
	 * @param graphics
	 *            graphics
	 */
	private void drawSelectionBox(Graphics2D graphics) {
		graphics.drawRect(this.selectionBounds.x, this.selectionBounds.y,
				this.selectionBounds.width, this.selectionBounds.height);
	}

	/**
	 * set selection box
	 * 
	 * @param rect
	 *            selection box bounds
	 */
	public void setSelectionBounds(Rectangle rect) {
		selectionBounds = rect;
	}
}
