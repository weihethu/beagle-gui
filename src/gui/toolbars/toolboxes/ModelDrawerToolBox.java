package gui.toolbars.toolboxes;

import gui.drawers.ELTSModelDrawer;
import gui.drawers.ObjectDrawer;
import gui.editors.EditorCanvas;
import gui.toolbars.tools.CreateModuleTool;
import gui.toolbars.tools.DeleteTool;
import gui.toolbars.tools.ModelDrawerCursorTool;
import gui.toolbars.tools.Tool;

import java.util.ArrayList;
import java.util.List;

public class ModelDrawerToolBox implements ToolBox {

	@Override
	public List<Tool> getTools(EditorCanvas view, ObjectDrawer drawer) {
		if(drawer instanceof ELTSModelDrawer) {
		List<Tool> tools = new ArrayList<Tool>();
		
		tools.add(new ModelDrawerCursorTool(view, (ELTSModelDrawer)drawer));
		tools.add(new CreateModuleTool(view, (ELTSModelDrawer)drawer));
		tools.add(new DeleteTool(view, drawer));
		return tools;
		}
		else
			return null;
	}

}
