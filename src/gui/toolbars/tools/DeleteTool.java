package gui.toolbars.tools;

import gui.drawers.ObjectDrawer;
import gui.editors.Canvas;

import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class DeleteTool extends Tool {
	public DeleteTool(Canvas view, ObjectDrawer drawer) {
		super(view, drawer);
	}

	public Icon getIcon() {
		URL url = getClass().getResource("/assets/icons/delete.gif");
		return new ImageIcon(url);
	}

	public void mouseClicked(MouseEvent event) {

	}

	@Override
	public String getToolTip() {
		return "Deleter";
	}
}
