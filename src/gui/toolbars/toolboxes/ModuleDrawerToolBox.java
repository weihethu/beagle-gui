package gui.toolbars.toolboxes;

import gui.drawers.ModuleDrawer;
import gui.drawers.ObjectDrawer;
import gui.editors.Canvas;
import gui.toolbars.tools.CreateStateTool;
import gui.toolbars.tools.DeleteTool;
import gui.toolbars.tools.ModuleDrawerCursorTool;
import gui.toolbars.tools.Tool;
import gui.toolbars.tools.TransitionTool;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JToolBar;

public class ModuleDrawerToolBox implements ToolBox {

	@Override
	public List<Tool> getTools(Canvas view, ObjectDrawer drawer) {
		if (drawer instanceof ModuleDrawer) {
			List<Tool> tools = new ArrayList<Tool>();

			tools.add(new ModuleDrawerCursorTool(view, (ModuleDrawer) drawer));
			tools.add(new CreateStateTool(view, (ModuleDrawer) drawer));
			tools.add(new TransitionTool(view, (ModuleDrawer) drawer));
			tools.add(new DeleteTool(view, drawer));
			return tools;
		} else
			return null;
	}

	@Override
	public void addExtras(JToolBar toolbar) {
	}

}
