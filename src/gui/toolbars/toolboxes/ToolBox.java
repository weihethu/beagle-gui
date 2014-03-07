package gui.toolbars.toolboxes;

import gui.drawers.ObjectDrawer;
import gui.editors.EditorCanvas;
import gui.toolbars.tools.Tool;

import java.util.List;

public interface ToolBox {
	public List<Tool> getTools(EditorCanvas view, ObjectDrawer drawer);
}
