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

public class CreateModuleTool extends Tool {
	public CreateModuleTool(Canvas canvas, ModelDrawer drawer) {
		super(canvas, drawer);
	}

	private Module module = null;

	public Icon getIcon() {
		URL url = getClass().getResource("/assets/icons/module.gif");
		return new ImageIcon(url);
	}

	private Model getModel() {
		return (Model) super.getObject();
	}

	public void mousePressed(MouseEvent event) {
		module = getModel().createModule(event.getPoint());
	}

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
