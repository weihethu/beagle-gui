package gui.toolbars.tools;

import gui.drawers.ModuleDrawer;
import gui.editors.Canvas;

import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import model.Module;
import model.automata.State;

/**
 * state creator tool
 * 
 * @author Wei He
 * 
 */
public class CreateStateTool extends Tool {

	/**
	 * state created
	 */
	private State state = null;

	/**
	 * constructor
	 * 
	 * @param canvas
	 *            canvas
	 * @param drawer
	 *            module drawer
	 */
	public CreateStateTool(Canvas canvas, ModuleDrawer drawer) {
		super(canvas, drawer);
	}

	@Override
	public Icon getIcon() {
		URL url = getClass().getResource("/assets/icons/state.gif");
		return new ImageIcon(url);
	}

	/**
	 * get parent module
	 * 
	 * @return module
	 */
	private Module getModule() {
		return (Module) super.getObject();
	}

	@Override
	public void mousePressed(MouseEvent event) {
		state = getModule().createState(event.getPoint());
	}

	@Override
	public void mouseDragged(MouseEvent event) {
		state.setPoint(event.getPoint());
	}

	@Override
	public String getToolTip() {
		return "State creator";
	}

	@Override
	public KeyStroke getKey() {
		return KeyStroke.getKeyStroke('s');
	}
}
