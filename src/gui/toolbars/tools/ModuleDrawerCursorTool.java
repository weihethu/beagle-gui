package gui.toolbars.tools;

import gui.Environment;
import gui.drawers.ModuleDrawer;
import gui.editors.Canvas;
import gui.editors.TransitionEditor;
import gui.entities.CurvedArrow;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import model.Module;
import model.automata.State;
import model.automata.Transition;

public class ModuleDrawerCursorTool extends Tool {
	private State lastClickedState = null;
	private Transition lastClickedTransition = null;
	private Point initialPointClicked = new Point();
	private StateMenu stateMenu = new StateMenu();
	private TransitionMenu transitionMenu = new TransitionMenu();
	private Transition selectedTransition = null;
	private boolean transitionInFlux = false;

	public ModuleDrawerCursorTool(Canvas view, ModuleDrawer drawer) {
		super(view, drawer);
	}

	public Icon getIcon() {
		URL url = getClass().getResource("/assets/icons/arrow.gif");
		return new ImageIcon(url);
	}

	@Override
	public String getToolTip() {
		return "Attribute Editor";
	}

	private Module getModule() {
		return (Module) super.getObject();
	}

	@Override
	protected ModuleDrawer getDrawer() {
		return (ModuleDrawer) super.getDrawer();
	}

	@Override
	public void mouseClicked(MouseEvent event) {
		Transition transition = getDrawer().transitionAtPoint(event.getPoint());
		if (transition != null) {
			if (event.getClickCount() == 1) {
				if (transition.isSelected()) {
					transition.setSelect(false);
					this.selectedTransition = null;
				} else {
					if (this.selectedTransition != null)
						this.selectedTransition.setSelect(false);
					transition.setSelect(true);
					this.selectedTransition = transition;
				}
			} else if (event.getClickCount() == 2
					&& event.getButton() == MouseEvent.BUTTON1) {
				if(this.selectedTransition != null)
					this.selectedTransition.setSelect(false);
				transition.setSelect(true);
				this.selectedTransition = transition;
				editTransition(transition);
			}
		} else {
			if (event.getClickCount() == 1) {
				getModule().unselectAll();
				getCanvas().repaint();
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent event) {
		this.initialPointClicked.setLocation(event.getPoint());
		lastClickedState = getDrawer().stateAtPoint(event.getPoint());
		if (lastClickedState == null)
			lastClickedTransition = getDrawer().transitionAtPoint(
					event.getPoint());

		if (event.isPopupTrigger()) {
			showPopup(event);
		}
		if (lastClickedState != null) {
			this.initialPointClicked.setLocation(lastClickedState.getPoint());
			if (!this.lastClickedState.isSelected()) {
				getModule().unselectAll();
				this.lastClickedState.setSelect(true);
			}
			getCanvas().repaint();
		}

		Transition[] transitions = getModule().getTransitions();
		for (int i = 0; i < transitions.length; i++) {
			if (transitions[i].isSelected()) {
				this.selectedTransition = transitions[i];
				return;
			}
		}
		this.selectedTransition = null;
	}

	@Override
	public void mouseDragged(MouseEvent event) {
		if (this.lastClickedState != null) {
			if (event.isPopupTrigger())
				return;
			Point currentPt = event.getPoint();

			// drag all selected states
			State[] states = getModule().getStates();
			for (State state : states) {
				if (state.isSelected()) {
					Point oldPoint = state.getPoint();
					state.setPoint(new Point(oldPoint.x + currentPt.x
							- initialPointClicked.x, oldPoint.y + currentPt.y
							- initialPointClicked.y));
				}
			}
			this.initialPointClicked.setLocation(currentPt);
			getCanvas().repaint();
		} else {
			if (!this.transitionInFlux) {
				int currentX = event.getPoint().x;
				int currentY = event.getPoint().y;
				int initX = this.initialPointClicked.x;
				int initY = this.initialPointClicked.y;

				Rectangle rect = new Rectangle(Math.min(currentX, initX),
						Math.min(currentY, initY), Math.abs(currentX - initX),
						Math.abs(currentY - initY));

				getDrawer().getModule().selectStatesWithinBounds(rect);
				getDrawer().setSelectionBounds(rect);
				getCanvas().repaint();
			}
		}

		if (this.selectedTransition != null) {
			CurvedArrow arrow = getDrawer().transitionToArrowMap
					.get(this.selectedTransition);
			Point eventPt = event.getPoint();
			Point2D ctrlPt = arrow.getCurve().getCtrlPt();
			if (this.transitionInFlux
					|| (Point.distance(eventPt.x, eventPt.y, ctrlPt.getX(),
							ctrlPt.getY())) < 15.0) {
				this.selectedTransition.setControl(eventPt);
				arrow.refreshCurve();
				this.transitionInFlux = true;
				this.getCanvas().repaint();
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		this.transitionInFlux = false;
		if (event.isPopupTrigger()) {
			showPopup(event);
		}
		getDrawer().setSelectionBounds(new Rectangle(0, 0, -1, -1));
		this.lastClickedState = null;
		this.lastClickedTransition = null;
		getCanvas().repaint();
	}

	private void editTransition(Transition transition) {
		Environment.getInstance().addTab(new TransitionEditor(transition),
				"transition editor");
	}

	private void showPopup(MouseEvent event) {
		if (this.lastClickedState != null) {
			this.stateMenu.show(this.lastClickedState, getCanvas(), getCanvas()
					.transfromFromCanvasToView(event.getPoint()));
			return;
		}
		if (this.lastClickedTransition != null) {
			this.transitionMenu.show(this.selectedTransition, getCanvas(),
					getCanvas().transfromFromCanvasToView(event.getPoint()));
		}

		this.lastClickedState = null;
		this.lastClickedTransition = null;
	}

	private class TransitionMenu extends JPopupMenu implements ActionListener {
		private Transition transition;
		private JMenuItem edit;

		public TransitionMenu() {
			this.edit = new JMenuItem("Edit");
			this.edit.addActionListener(this);

			add(this.edit);
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			JMenuItem item = (JMenuItem) event.getSource();
			if (item == this.edit) {
				editTransition(this.transition);
			}
		}

		public void show(Transition transition, Component parent, Point pt) {
			this.transition = transition;
			show(parent, pt.x, pt.y);
		}
	}

	private class StateMenu extends JPopupMenu implements ActionListener {
		private State state;
		private JCheckBoxMenuItem makeInitial;
		private JMenuItem setName;

		public StateMenu() {
			this.makeInitial = new JCheckBoxMenuItem("Initial");
			this.makeInitial.addActionListener(this);

			this.setName = new JMenuItem("Set Name");
			this.setName.addActionListener(this);

			add(this.makeInitial);
			add(this.setName);
		}

		public void show(State state, Component parent, Point pt) {
			this.state = state;
			this.makeInitial.setSelected(ModuleDrawerCursorTool.this
					.getModule().getInitialState() == state);
			show(parent, pt.x, pt.y);
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			JMenuItem menuItem = (JMenuItem) event.getSource();
			if (menuItem == this.makeInitial) {
				if (!menuItem.isSelected())
					this.state = null;
				getModule().setInitialState(this.state);
			} else if (menuItem == this.setName) {
				String currentName = this.state.getName();
				String newName = (String) JOptionPane.showInputDialog(this,
						"Input a new name", "New Name",
						JOptionPane.QUESTION_MESSAGE, null, null, currentName);
				if (newName == null || newName.trim().isEmpty())
					return;
				this.state.setName(newName);
			}
		}
	}
}
