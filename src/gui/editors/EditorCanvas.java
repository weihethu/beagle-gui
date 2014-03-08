package gui.editors;

import gui.drawers.ObjectDrawer;
import gui.toolbars.ToolBar;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class EditorCanvas extends JPanel {
	protected ObjectDrawer drawer;
	private ToolBar toolbar = null;;

	public EditorCanvas(ObjectDrawer drawer) {
		this.drawer = drawer;
		// drawer.getObject().addObjectEditListener(new Listener());
		drawer.setView(this);
	}

	public void paintComponent(Graphics graphics) {
		graphics.setColor(Color.white);
		graphics.fillRect(0, 0, this.getWidth(), this.getHeight());

		this.drawer.drawInternal(graphics);
		if (this.toolbar != null)
			this.toolbar.drawTool(graphics);
	}

	public void setToolbar(ToolBar bar) {
		this.toolbar = bar;
	}

	// private class Listener implements ObjectEditListener {
	//
	// @Override
	// public void objectEdit(ObjectEditEvent event) {
	// EditorCanvas.this.repaint();
	// }
	//
	// }
}
