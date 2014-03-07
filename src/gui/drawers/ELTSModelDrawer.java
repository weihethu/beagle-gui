package gui.drawers;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import model.ELTSModel;
import model.ELTSModule;

public class ELTSModelDrawer extends ObjectDrawer{
	private Rectangle selectionBounds = new Rectangle(0, 0, -1, -1);

	public ELTSModelDrawer(ELTSModel model) {
		super(model);
	}

	@Override
	public void drawInternal(Graphics graphics) {
		ELTSModuleDrawer moduleDrawer = new ELTSModuleDrawer(null);
		ELTSModule[] modules = ((ELTSModel)this.target).getModules();
		for (ELTSModule module : modules) {
			moduleDrawer.setObject(module);
			moduleDrawer.drawExternal(graphics);
		}
		drawSelectionBox(graphics);
	}

	@Override
	public void drawExternal(Graphics graphics) {
		
	}
	
	public ELTSModel getModel() {
		return (ELTSModel)target;
	}

	public ELTSModule moduleAtPoint(Point pt) {
		ELTSModule[] modules = getModel().getModules();
		for (ELTSModule module : modules) {
			if (pt.distance(module.getPoint()) <= ELTSModuleDrawer.radius)
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
}
