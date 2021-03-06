package gui.toolbars.tools;

import gui.drawers.ModuleDrawer;
import gui.editors.Canvas;
import gui.editors.TransitionCreator;
import gui.entities.CurvedArrow;
import gui.entities.Note;

import java.awt.Color;
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
import javax.swing.KeyStroke;

import model.Module;
import model.automata.State;
import model.automata.Transition;

/**
 * attribute editor tool when drawing modules
 * 
 * @author Wei He
 * 
 */
public class ModuleDrawerCursorTool extends Tool {
	/**
	 * last clicked state
	 */
	private State lastClickedState = null;
	/**
	 * last clicked transition
	 */
	private Transition lastClickedTransition = null;
	/**
	 * initial point clicked
	 */
	private Point initialPointClicked = new Point();
	/**
	 * context menu to popup for editing states
	 */
	private StateMenu stateMenu = new StateMenu();
	/**
	 * selected transition
	 */
	private Transition selectedTransition = null;
	/**
	 * whether dragging control points of transition
	 */
	private boolean transitionInFlux = false;
	/**
	 * transition creator
	 */
	private TransitionCreator creator = null;

	/**
	 * constructor
	 * 
	 * @param canvas
	 *            canvas
	 * @param drawer
	 *            module drawer
	 */
	public ModuleDrawerCursorTool(Canvas canvas, ModuleDrawer drawer) {
		super(canvas, drawer);
		creator = new TransitionCreator(canvas);
	}

	@Override
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
				// change selected status of transition
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
				// edit transitions
				if (this.selectedTransition != null)
					this.selectedTransition.setSelect(false);
				transition.setSelect(true);
				this.selectedTransition = transition;
				editTransition(transition, event.getPoint());
			}
		} else {
			State state = getDrawer().stateAtPoint(event.getPoint());
			if (state == null && event.getClickCount() == 1) {
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
		// isPopupTrigger should be checked in both mousePressed and
		// mouseReleased for proper cross-platform functionality
		if (event.isPopupTrigger()) {
			showPopup(event);
		}
		if (lastClickedState != null) {
			this.initialPointClicked.setLocation(lastClickedState.getPoint());
			// change selected status of clicked state
			if (!this.lastClickedState.isSelected()) {
				getModule().unselectAll();
				this.lastClickedState.setSelect(true);
			}
			getCanvas().repaint();
		} else if (lastClickedTransition != null) {
		} else {
			for (Note note : getDrawer().getNotes()) {
				note.setEnabled(false);
				note.setEditable(false);
				note.setCaretColor(new Color(255, 255, 150));
			}
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
				// select states in selection box
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
				// drag control points of curved arrows
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
		// isPopupTrigger should be checked in both mousePressed and
		// mouseReleased for proper cross-platform functionality
		if (event.isPopupTrigger()) {
			showPopup(event);
		}
		getDrawer().setSelectionBounds(new Rectangle(0, 0, -1, -1));
		this.lastClickedState = null;
		this.lastClickedTransition = null;
		getCanvas().repaint();
	}

	/**
	 * edit transition
	 * 
	 * @param transition
	 *            transition
	 * @param pt
	 *            mouse event
	 */
	private void editTransition(Transition transition, Point pt) {
		creator.editTransition(transition, pt);
	}

	/**
	 * show popup menu for editing states
	 * 
	 * @param event
	 *            mouse event
	 */
	private void showPopup(MouseEvent event) {
		if (this.lastClickedState != null) {
			this.stateMenu.show(this.lastClickedState, getCanvas(), getCanvas()
					.transfromFromCanvasToView(event.getPoint()));
			return;
		}

		this.lastClickedState = null;
		this.lastClickedTransition = null;
	}

	@Override
	public KeyStroke getKey() {
		return KeyStroke.getKeyStroke('a');
	}

	/**
	 * context menu to popup when right click states
	 * 
	 * @author Wei He
	 * 
	 */
	private class StateMenu extends JPopupMenu implements ActionListener {
		/**
		 * state
		 */
		private State state;
		/**
		 * make initial check box menu item
		 */
		private JCheckBoxMenuItem makeInitial;
		/**
		 * set name menu item
		 */
		private JMenuItem setName;

		/**
		 * constructor
		 */
		public StateMenu() {
			this.makeInitial = new JCheckBoxMenuItem("Initial");
			this.makeInitial.addActionListener(this);

			this.setName = new JMenuItem("Set Name");
			this.setName.addActionListener(this);

			add(this.makeInitial);
			add(this.setName);
		}

		/**
		 * popup menu
		 * 
		 * @param state
		 *            state
		 * @param parent
		 *            parent component
		 * @param pt
		 *            popup location
		 */
		public void show(State state, Component parent, Point pt) {
			this.state = state;
			boolean isInitial = ModuleDrawerCursorTool.this.getModule()
					.getInitialState() == state;
			this.makeInitial.setSelected(isInitial);
			show(parent, pt.x, pt.y);
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			JMenuItem menuItem = (JMenuItem) event.getSource();
			if (menuItem == this.makeInitial) {
				if (!menuItem.isSelected()) {
					getModule().setInitialState(null);
				} else
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
