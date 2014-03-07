package gui.toolbars.tools;

import gui.drawers.ELTSModuleDrawer;
import gui.editors.EditorCanvas;

import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class CreateStateTool extends Tool {
	public CreateStateTool(EditorCanvas view, ELTSModuleDrawer drawer) {
		super(view, drawer);
	}

	public Icon getIcon() {
		URL url = getClass().getResource("/assets/icons/state.gif");
		return new ImageIcon(url);
	}

	public void mousePressed(MouseEvent event) {
		System.out.println("pressed");

	}

	public void mouseDragged(MouseEvent event) {

	}

	@Override
	public String getToolTip() {
		return "State creator";
	}
}
