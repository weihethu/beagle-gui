package gui.toolbars.tools;

import gui.drawers.ModuleDrawer;
import gui.editors.Canvas;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
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
	private Point initialPointClicked = new Point();
	private StateMenu stateMenu = new StateMenu();
	private Transition selectedTransition = null;

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
			if (transition.isSelected()) {
				transition.setSelect(false);
				this.selectedTransition = null;
			} else {
				if (this.selectedTransition != null)
					this.selectedTransition.setSelect(false);
				transition.setSelect(true);
				this.selectedTransition = transition;
			}
		}

		if (transition != null) {
			if (event.getClickCount() == 1) {
				getModule().unselectAll();
				getView().repaint();
			} else if (event.getClickCount() == 2
					&& event.getButton() == MouseEvent.BUTTON1) {
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent event) {
		this.initialPointClicked.setLocation(event.getPoint());
		lastClickedState = getDrawer().stateAtPoint(event.getPoint());

		if (event.isPopupTrigger()) {
			showPopup(event);
		}
		if (lastClickedState != null) {
			this.initialPointClicked.setLocation(lastClickedState.getPoint());
			if (!this.lastClickedState.isSelected()) {
				getModule().unselectAll();
				this.lastClickedState.setSelect(true);
			}
			getView().repaint();
		}
	}

	@Override
	public void mouseDragged(MouseEvent event) {
		if (this.lastClickedState != null) {
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
			getView().repaint();
		} else {
			int currentX = event.getPoint().x;
			int currentY = event.getPoint().y;
			int initX = this.initialPointClicked.x;
			int initY = this.initialPointClicked.y;

			Rectangle rect = new Rectangle(Math.min(currentX, initX), Math.min(
					currentY, initY), Math.abs(currentX - initX),
					Math.abs(currentY - initY));
			getDrawer().getModule().selectStatesWithinBounds(rect);
			getDrawer().setSelectionBounds(rect);
			getView().repaint();
		}
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		if (event.isPopupTrigger()) {
			showPopup(event);
		}
		getDrawer().setSelectionBounds(new Rectangle(0, 0, -1, -1));
		this.lastClickedState = null;
		getView().repaint();
	}

	private void showPopup(MouseEvent event) {
		if (this.lastClickedState != null)
			this.stateMenu.show(this.lastClickedState, getView(),
					event.getPoint());
	}

	private class StateMenu extends JPopupMenu implements ActionListener {
		private State state;
		private JCheckBoxMenuItem makeInitial;
		private JMenuItem setName;
		private JMenuItem edit;

		public StateMenu() {
			this.makeInitial = new JCheckBoxMenuItem("Initial");
			this.makeInitial.addActionListener(this);

			this.setName = new JMenuItem("Set Name");
			this.setName.addActionListener(this);

			this.edit = new JMenuItem("Edit");
			this.edit.addActionListener(this);

			add(this.makeInitial);
			add(this.setName);
			add(this.edit);
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
			} else if (menuItem == this.edit) {
				System.out.println("edit state");
			}
		}
	}
}
