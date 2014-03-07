package gui.editors;

import events.ObjectEditEvent;
import events.listeners.ObjectEditListener;
import gui.drawers.ObjectDrawer;
import gui.drawers.ELTSModelDrawer;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class EditorCanvas extends JPanel {
	protected ObjectDrawer drawer;

	public EditorCanvas(ObjectDrawer drawer) {
		this.drawer = drawer;
		drawer.getObject().addObjectEditListener(new Listener());
	}

	public void paintComponent(Graphics graphics) {
		graphics.setColor(Color.white);
		graphics.fillRect(0, 0, this.getWidth(), this.getHeight());

		this.drawer.drawInternal(graphics);
	}

	private class Listener implements ObjectEditListener {

		@Override
		public void objectEdit(ObjectEditEvent event) {
			EditorCanvas.this.repaint();
		}

	}
}
