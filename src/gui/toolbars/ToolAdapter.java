package gui.toolbars;

import gui.toolbars.tools.Tool;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * tool adapter which dispatches mouse events to tools
 * 
 * @author Wei He
 * 
 */
public class ToolAdapter implements MouseListener, MouseMotionListener {

	/**
	 * the mouse adapter for active tool
	 */
	private Tool activeTool = null;

	/**
	 * constructor
	 */
	public ToolAdapter() {
		activeTool = null;
	}

	/**
	 * set current tool
	 * 
	 * @param tool
	 *            tool
	 */
	public void setCurrentTool(Tool tool) {
		this.activeTool = tool;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (activeTool != null)
			activeTool.mouseClicked(e);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if (activeTool != null)
			activeTool.mouseEntered(e);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if (activeTool != null)
			activeTool.mouseExited(e);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (activeTool != null)
			activeTool.mousePressed(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (activeTool != null)
			activeTool.mouseReleased(e);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (activeTool != null)
			activeTool.mouseDragged(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (activeTool != null)
			activeTool.mouseMoved(e);
	}
}
