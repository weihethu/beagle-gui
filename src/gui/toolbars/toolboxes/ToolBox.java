package gui.toolbars.toolboxes;

import gui.drawers.ObjectDrawer;
import gui.editors.Canvas;
import gui.toolbars.tools.Tool;

import java.util.List;

import javax.swing.JToolBar;

public interface ToolBox {
	public List<Tool> getTools(Canvas view, ObjectDrawer drawer);
	
	public void addExtras(JToolBar toolbar);
}
