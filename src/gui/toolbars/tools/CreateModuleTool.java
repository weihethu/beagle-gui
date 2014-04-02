package gui.toolbars.tools;

import gui.drawers.ModelDrawer;
import gui.editors.Canvas;

import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import model.Model;
import model.Module;

/**
 * module creator tool
 * 
 * @author Wei He
 * 
 */
public class CreateModuleTool extends Tool {
	/**
	 * constructor
	 * 
	 * @param canvas
	 *            canvas
	 * @param drawer
	 *            model drawer
	 */
	public CreateModuleTool(Canvas canvas, ModelDrawer drawer) {
		super(canvas, drawer);
	}

	/**
	 * module created
	 */
	private Module module = null;

	@Override
	public Icon getIcon() {
		URL url = getClass().getResource("/assets/icons/module.gif");
		return new ImageIcon(url);
	}

	/**
	 * get parent model
	 * 
	 * @return model
	 */
	private Model getModel() {
		return (Model) super.getObject();
	}

	@Override
	public void mousePressed(MouseEvent event) {
		module = getModel().createModule(event.getPoint());
	}

	@Override
	public void mouseDragged(MouseEvent event) {
		module.setPoint(event.getPoint());
	}

	@Override
	public String getToolTip() {
		return "Module Creator";
	}

	@Override
	public KeyStroke getKey() {
		return KeyStroke.getKeyStroke('m');
	}
}
