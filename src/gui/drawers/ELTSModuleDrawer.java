package gui.drawers;

import events.ObjectEditEvent;
import events.StateEditEvent;
import events.TransitionEditEvent;
import events.listeners.ObjectEditListener;
import gui.entities.CurvedArrow;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import model.ELTSModule;
import model.automata.State;
import model.automata.Transition;

public class ELTSModuleDrawer extends ObjectDrawer {
	private Rectangle selectionBounds = new Rectangle(0, 0, -1, -1);
	private Map<CurvedArrow, Transition> arrowToTransitionMap = null;
	private Map<Transition, CurvedArrow> transitionToArrowMap = null;
	private boolean valid = false;

	public static final Color STATE_COLOR = new Color(255, 255, 150);
	public static int STATE_RADIUS = 20;

	public ELTSModuleDrawer(ELTSModule module) {
		super(module);
		arrowToTransitionMap = new HashMap<CurvedArrow, Transition>();
		transitionToArrowMap = new HashMap<Transition, CurvedArrow>();

		DrawerListener listener = new DrawerListener();
		module.addStateListener(listener);
		module.addTransitionListener(listener);
	}

	@Override
	public void drawInternal(Graphics graphics) {
		if (!this.valid)
			refreshArrowMap();
		
		drawTransitions(graphics);
		State[] states = getModule().getStates();
		for (State state : states) {
			drawState(state, graphics);
		}
		drawSelectionBox(graphics);
	}

	public ELTSModule getModule() {
		return (ELTSModule) target;
	}

	public State stateAtPoint(Point pt) {
		State[] states = getModule().getStates();
		for (State state : states) {
			if (pt.distance(state.getPoint()) <= ELTSModuleDrawer.STATE_RADIUS)
				return state;
		}
		return null;
	}

	private void drawSelectionBox(Graphics graphics) {
		graphics.drawRect(this.selectionBounds.x, this.selectionBounds.y,
				this.selectionBounds.width, this.selectionBounds.height);
	}

	public void setSelectionBounds(Rectangle rect) {
		selectionBounds = rect;
	}

	private void drawTransitions(Graphics graphics) {
		Set<CurvedArrow> arrows = this.arrowToTransitionMap.keySet();
		Iterator<CurvedArrow> iter = arrows.iterator();
		while (iter.hasNext()) {
			CurvedArrow arrow = iter.next();
			if (arrow.getTransition().isSelected()) {
				arrow.drawHighlight((Graphics2D) graphics);
				arrow.drawControlPoint((Graphics2D) graphics);
			} else {
				arrow.draw((Graphics2D) graphics);
			}
		}
	}

	private void refreshArrowMap() {
		State[] states = this.getModule().getStates();
		this.arrowToTransitionMap.clear();
		this.transitionToArrowMap.clear();

		for (int i = 0; i < states.length; i++) {
			for (int j = i + 1; j < states.length; j++) {
				Transition trans1[] = this.getModule()
						.getTransitionsFromStateToState(states[i], states[j]);
				Transition trans2[] = this.getModule()
						.getTransitionsFromStateToState(states[j], states[i]);

				double d1 = trans2.length > 0 ? 0.5 : 0;
				double d2 = trans1.length > 0 ? 0.5 : 0;

				if (trans1.length + trans2.length > 0) {
					double ang = angle(states[i], states[j]);
					Point startPt = pointOnState(states[i], ang - Math.PI / 25);
					Point endPt = pointOnState(states[j], ang + Math.PI
							+ Math.PI / 25);
					for (int k = 0; k < trans1.length; k++) {
						double curvy = d1 + k;
						CurvedArrow arrow = (k == 0 ? new CurvedArrow(startPt,
								endPt, curvy, trans1[k]) : null);
						this.arrowToTransitionMap.put(arrow, trans1[k]);
						this.transitionToArrowMap.put(trans1[k], arrow);
					}
					startPt = pointOnState(states[i], ang + Math.PI / 25);
					endPt = pointOnState(states[j], ang + Math.PI - Math.PI
							/ 25);
					for (int k = 0; k < trans2.length; k++) {
						double curvy = d2 + k;
						CurvedArrow arrow = (k == 0 ? new CurvedArrow(startPt,
								endPt, curvy, trans2[k]) : null);
						this.arrowToTransitionMap.put(arrow, trans2[k]);
						this.transitionToArrowMap.put(trans2[k], arrow);
					}
				}

				// Transition[] trans = this.getModule()
				// .getTransitionsFromStateToState(states[i], states[i]);
				// if (trans.length > 0) {
				// Point startPt = pointOnState(states[i], -Math.PI / 3);
				// Point endPt = pointOnState(states[i], -Math.PI * 2 / 3);
				// }
			}
		}
		this.valid = true;
	}

	private double angle(State from, State to) {
		double dx = to.getPoint().getX() - from.getPoint().getX();
		double dy = to.getPoint().getY() - from.getPoint().getY();
		return Math.atan2(dy, dx);
	}

	private Point pointOnState(State state, double ang) {
		Point pt = new Point(state.getPoint());
		double dx = Math.cos(ang) * ELTSModuleDrawer.STATE_RADIUS;
		double dy = Math.sin(ang) * ELTSModuleDrawer.STATE_RADIUS;
		pt.translate((int) dx, (int) dy);
		return pt;
	}

	private void stateEditHandler(StateEditEvent event) {
		getView().repaint();
	}

	private void transitionEditHandler(TransitionEditEvent event) {
		this.valid = false;
		getView().repaint();
	}

	private void drawState(State state, Graphics graphics) {
		drawStateBackground(graphics, state, state.getPoint(), STATE_COLOR);

		Point currentPt = state.getPoint();
		graphics.setColor(Color.black);
		int strWidth = (int) (graphics.getFontMetrics().getStringBounds(
				state.getName(), graphics).getWidth()) >> 1;
		int strHeight = (int) graphics.getFontMetrics().getAscent() >> 1;

		graphics.drawString(state.getName(), currentPt.x - strWidth,
				currentPt.y - strHeight);
		graphics.drawOval(currentPt.x - ELTSModuleDrawer.STATE_RADIUS,
				currentPt.y - ELTSModuleDrawer.STATE_RADIUS,
				2 * ELTSModuleDrawer.STATE_RADIUS,
				2 * ELTSModuleDrawer.STATE_RADIUS);

		if (state.getModule().getInitialState() == state) {
			int[] xs = { currentPt.x - ELTSModuleDrawer.STATE_RADIUS,
					currentPt.x - (ELTSModuleDrawer.STATE_RADIUS << 1),
					currentPt.x - (ELTSModuleDrawer.STATE_RADIUS << 1) };
			int[] ys = { currentPt.y,
					currentPt.y - ELTSModuleDrawer.STATE_RADIUS,
					currentPt.y + ELTSModuleDrawer.STATE_RADIUS };

			graphics.setColor(Color.white);
			graphics.fillPolygon(xs, ys, 3);
			graphics.setColor(Color.black);
			graphics.drawPolygon(xs, ys, 3);
		}
	}

	private void drawStateBackground(Graphics graphics, State state, Point pt,
			Color color) {
		graphics.setColor(color);
		if (state.isSelected())
			graphics.setColor(new Color(100, 200, 200));

		graphics.fillOval(pt.x - ELTSModuleDrawer.STATE_RADIUS, pt.y
				- ELTSModuleDrawer.STATE_RADIUS,
				2 * ELTSModuleDrawer.STATE_RADIUS,
				2 * ELTSModuleDrawer.STATE_RADIUS);
	}

	private class DrawerListener implements ObjectEditListener {

		@Override
		public void objectEdit(ObjectEditEvent event) {
			if (event instanceof StateEditEvent) {
				stateEditHandler((StateEditEvent) event);
			} else if (event instanceof TransitionEditEvent) {
				transitionEditHandler((TransitionEditEvent) event);
			}
		}

	}
}
