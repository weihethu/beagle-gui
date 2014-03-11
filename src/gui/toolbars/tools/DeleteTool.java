package gui.toolbars.tools;

import gui.drawers.ModelDrawer;
import gui.drawers.ModuleDrawer;
import gui.drawers.ObjectDrawer;
import gui.editors.Canvas;

import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import model.Model;
import model.Module;
import model.automata.State;
import model.automata.Transition;

public class DeleteTool extends Tool {
	public DeleteTool(Canvas view, ObjectDrawer drawer) {
		super(view, drawer);
	}

	public Icon getIcon() {
		URL url = getClass().getResource("/assets/icons/delete.gif");
		return new ImageIcon(url);
	}

	public void mouseClicked(MouseEvent event) {
		if (getDrawer() instanceof ModuleDrawer) {
			ModuleDrawer drawer = (ModuleDrawer) getDrawer();
			State state = drawer.stateAtPoint(event.getPoint());
			if (state != null) {
				Module module = (Module) super.getObject();
				if (module.getTransitionsFromState(state).length > 0
						|| module.getTransitionsToState(state).length > 0) {
					if (JOptionPane
							.showConfirmDialog(
									getCanvas(),
									"Deleting this state will delete all its associated transitions! Please confirm!",
									"Warning", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION)
						return;
				}
				module.removeState(state);
				getCanvas().repaint();
				return;
			}

			Transition transition = ((ModuleDrawer) getDrawer())
					.transitionAtPoint(event.getPoint());
			if (transition != null) {
				Module module = (Module) super.getObject();
				module.removeTransition(transition);
				getCanvas().repaint();
			}
		} else if (getDrawer() instanceof ModelDrawer) {
			Module module = ((ModelDrawer) getDrawer()).moduleAtPoint(event
					.getPoint());
			if (module != null) {
				if (module.getStates().length > 0) {
					if (JOptionPane
							.showConfirmDialog(
									getCanvas(),
									"Deleting this module will delete all its internal states and transitions! Please confirm!",
									"Warning", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION)
						return;
				}
				Model model = (Model) super.getObject();
				model.removeModule(module);
				getCanvas().repaint();
			}
		}
	}

	@Override
	public String getToolTip() {
		return "Deleter";
	}
}
