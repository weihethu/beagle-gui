package gui.toolbars.tools;

import gui.drawers.ELTSModelDrawer;
import gui.editors.EditorCanvas;

import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import model.ELTSModel;
import model.ELTSModule;

public class CreateModuleTool extends Tool {
	public CreateModuleTool(EditorCanvas view, ELTSModelDrawer drawer) {
		super(view, drawer);
	}

	private ELTSModule module = null;

	public Icon getIcon() {
		URL url = getClass().getResource("/assets/icons/module.gif");
		return new ImageIcon(url);
	}

	private ELTSModel getModel() {
		return (ELTSModel) super.getObject();
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
}
