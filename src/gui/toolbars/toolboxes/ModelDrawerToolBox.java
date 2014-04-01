package gui.toolbars.toolboxes;

import gui.drawers.ModelDrawer;
import gui.drawers.ObjectDrawer;
import gui.editors.Canvas;
import gui.toolbars.tools.CreateModuleTool;
import gui.toolbars.tools.DeleteTool;
import gui.toolbars.tools.ModelDrawerCursorTool;
import gui.toolbars.tools.Tool;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * the tool box for drawing models
 * 
 * @author Wei He
 * 
 */
public class ModelDrawerToolBox implements ToolBox {

	private ModelDrawer modelDrawer = null;

	@Override
	public List<Tool> getTools(Canvas view, ObjectDrawer drawer) {
		if (drawer instanceof ModelDrawer) {
			modelDrawer = (ModelDrawer) drawer;
			List<Tool> tools = new ArrayList<Tool>();

			tools.add(new ModelDrawerCursorTool(view, (ModelDrawer) drawer));
			tools.add(new CreateModuleTool(view, (ModelDrawer) drawer));
			tools.add(new DeleteTool(view, drawer));
			return tools;
		} else
			return null;
	}

	@Override
	public void addExtras(JToolBar toolbar) {
		toolbar.addSeparator();
		final JCheckBox showInternalCB = new JCheckBox("show module internals");
		showInternalCB.setSelected(false);
		showInternalCB.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent event) {
				modelDrawer.setDrawInternal(showInternalCB.isSelected());
			}

		});
		toolbar.add(showInternalCB);
	}
}
