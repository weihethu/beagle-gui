package gui.toolbars.toolboxes;

import gui.drawers.ObjectDrawer;
import gui.editors.Canvas;
import gui.toolbars.tools.Tool;

import java.util.List;

import javax.swing.JToolBar;

/**
 * the tool box interface
 * 
 * @author Wei He
 * 
 */
public interface ToolBox {
	/**
	 * get tools
	 * 
	 * @param canvas
	 *            canvas
	 * @param drawer
	 *            object drawer
	 * @return list of tools
	 */
	public List<Tool> getTools(Canvas canvas, ObjectDrawer drawer);

	/**
	 * add extra components in tool bar
	 * 
	 * @param toolbar
	 *            tool bar
	 */
	public void addExtras(JToolBar toolbar);
}
