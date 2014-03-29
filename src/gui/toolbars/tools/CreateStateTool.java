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

public class CreateStateTool extends Tool {

	private State state = null;

	public CreateStateTool(Canvas canvas, ModuleDrawer drawer) {
		super(canvas, drawer);
	}

	public Icon getIcon() {
		URL url = getClass().getResource("/assets/icons/state.gif");
		return new ImageIcon(url);
	}

	private Module getModule() {
		return (Module) super.getObject();
	}

	public void mousePressed(MouseEvent event) {
		state = getModule().createState(event.getPoint());
	}

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
