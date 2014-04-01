package gui.toolbars.tools;

import gui.drawers.ObjectDrawer;
import gui.editors.Canvas;

import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

/**
 * Tool
 * 
 * @author Wei He
 * 
 */
public class Tool extends MouseAdapter {
	/**
	 * canvas
	 */
	private Canvas canvas;
	/**
	 * object drawer
	 */
	private ObjectDrawer drawer;

	/**
	 * constructor
	 * 
	 * @param canvas
	 *            canvas
	 * @param drawer
	 *            object drawer
	 */
	public Tool(Canvas canvas, ObjectDrawer drawer) {
		this.canvas = canvas;
		this.drawer = drawer;
	}

	/**
	 * get tool icon
	 * 
	 * @return icon
	 */
	public Icon getIcon() {
		URL url = getClass().getResource("assets/icons/default.gif");
		return new ImageIcon(url);
	}

	/**
	 * get tool tip
	 * 
	 * @return tool tip
	 */
	public String getToolTip() {
		return "Tool";
	}

	/**
	 * get tool tip including short-cut descriptions
	 * 
	 * @return tool tip
	 */
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

	/**
	 * get accelerator key
	 * 
	 * @return accelerator key
	 */
	public KeyStroke getKey() {
		return null;
	}

	/**
	 * the object drawed
	 * 
	 * @return object
	 */
	protected Object getObject() {
		return drawer.getObject();
	}

	/**
	 * get object drawer
	 * 
	 * @return object drawer
	 */
	protected ObjectDrawer getDrawer() {
		return drawer;
	}

	/**
	 * get canvas
	 * 
	 * @return canvas
	 */
	protected Canvas getCanvas() {
		return canvas;
	}

	/**
	 * some tools(i.e. TransitionTool) needs to draw on canvas
	 * 
	 * @param graphics
	 *            graphics
	 */
	public void draw(Graphics graphics) {

	}

	/**
	 * find position of character in string
	 * 
	 * @param str
	 *            string
	 * @param c
	 *            character
	 * @return position index, -1 if no such character
	 */
	private static int findDominant(String str, char c) {
		int i = str.indexOf(Character.toUpperCase(c));
		if (i != -1)
			return i;
		return str.indexOf(Character.toLowerCase(c));
	}
}
