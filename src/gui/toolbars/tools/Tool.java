package gui.toolbars.tools;

import gui.drawers.ObjectDrawer;
import gui.editors.Canvas;

import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

public class Tool extends MouseAdapter {
	private Canvas canvas;
	private ObjectDrawer drawer;

	public Tool(Canvas canvas, ObjectDrawer drawer) {
		this.canvas = canvas;
		this.drawer = drawer;
	}

	public Icon getIcon() {
		URL url = getClass().getResource("assets/icons/default.gif");
		return new ImageIcon(url);
	}

	public String getToolTip() {
		return "Tool";
	}

	public String getShortcutToolTip() {
		String str = getToolTip();
		KeyStroke ks = getKey();
		if (ks == null)
			return str;
		int index = findDominant(str, ks.getKeyChar());
		if (index == -1)
			return str + "(" + Character.toUpperCase(ks.getKeyChar()) + ")";
		else
			return str.substring(0, index) + "("
					+ str.substring(index, index + 1) + ")"
					+ str.substring(index + 1, str.length());

	}

	public KeyStroke getKey() {
		return null;
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

	private static int findDominant(String str, char c) {
		int i = str.indexOf(Character.toUpperCase(c));
		if (i != -1)
			return i;
		return str.indexOf(Character.toLowerCase(c));
	}
}
