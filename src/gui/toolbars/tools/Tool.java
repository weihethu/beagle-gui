package gui.toolbars.tools;

import gui.drawers.ObjectDrawer;
import gui.editors.EditorCanvas;

import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class Tool extends MouseAdapter {
	private EditorCanvas view;
	private ObjectDrawer drawer;

	public Tool(EditorCanvas view, ObjectDrawer drawer) {
		this.view = view;
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

	protected EditorCanvas getView() {
		return view;
	}

	public void draw(Graphics graphics) {

	}
}
