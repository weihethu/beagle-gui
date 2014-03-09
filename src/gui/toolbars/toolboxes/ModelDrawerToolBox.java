package gui.toolbars.toolboxes;

import gui.drawers.ELTSModelDrawer;
import gui.drawers.ObjectDrawer;
import gui.editors.Canvas;
import gui.toolbars.tools.CreateModuleTool;
import gui.toolbars.tools.DeleteTool;
import gui.toolbars.tools.ModelDrawerCursorTool;
import gui.toolbars.tools.Tool;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JScrollBar;
import javax.swing.JToolBar;

public class ModelDrawerToolBox implements ToolBox {

	@Override
	public List<Tool> getTools(Canvas view, ObjectDrawer drawer) {
		if (drawer instanceof ELTSModelDrawer) {
			List<Tool> tools = new ArrayList<Tool>();

			tools.add(new ModelDrawerCursorTool(view, (ELTSModelDrawer) drawer));
			tools.add(new CreateModuleTool(view, (ELTSModelDrawer) drawer));
			tools.add(new DeleteTool(view, drawer));
			return tools;
		} else
			return null;
	}

	@Override
	public void addExtras(JToolBar toolbar) {
		toolbar.addSeparator();
		toolbar.add(new JCheckBox("details"));
	}

}
