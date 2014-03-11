package gui.toolbars.tools;

import gui.drawers.ObjectDrawer;
import gui.editors.Canvas;

import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class Tool extends MouseAdapter {
	private Canvas canvas;
	private ObjectDrawer drawer;

	public Tool(Canvas view, ObjectDrawer drawer) {
		this.canvas = view;
		this.drawer = drawer;
	}

	public Icon getIcon() {
		URL url = getClass().getResource("assets/icons/default.gif");
		return new ImageIcon(url);
	}

	public String getToolTip() {
		return "Tool";
	}

	protected Object getObject() {
		return drawer.getObject();
	}

	protected ObjectDrawer getDrawer() {
		return drawer;
	}

	protected Canvas getCanvas() {
		return canvas;
	}

	public void draw(Graphics graphics) {

	}
}
