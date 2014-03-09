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
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
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
//	private boolean valid = false;
//	private boolean validBounds = false;
	private Rectangle cachedBounds = null;
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
	public void drawInternal(Graphics2D graphics) {
		Graphics2D tmpGraphics2D = (Graphics2D)graphics.create();
		tmpGraphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		if (!this.valid)
			refreshArrowMap();

		drawTransitions(tmpGraphics2D);
		State[] states = getModule().getStates();
		for (State state : states) {
			drawState(state, tmpGraphics2D);
		}
		drawSelectionBox(tmpGraphics2D);
		tmpGraphics2D.dispose();
	}

	@Override
	public Rectangle getBounds() {
		if (this.validBounds)
			return this.cachedBounds;

		if (!this.valid)
			refreshArrowMap();

		State[] states = getModule().getStates();
		if (states.length == 0)
			return null;

		Rectangle resultRect = getBounds(states[0]);
		for (int i = 1; i < states.length; i++) {
			resultRect.add(getBounds(states[i]));
		}

		Iterator<CurvedArrow> iter = this.arrowToTransitionMap.keySet()
				.iterator();
		while (iter.hasNext()) {
			CurvedArrow arrow = iter.next();
			resultRect.add(arrow.getBounds());
		}
		this.validBounds = true;
		return this.cachedBounds = this.currentTransform
				.createTransformedShape(resultRect).getBounds();
//		return this.cachedBounds = resultRect.getBounds();
	}

	public Rectangle getBounds(State state) {
		Point pt = state.getPoint();
		if (getModule().getInitialState() == state) {
			return new Rectangle(pt.x - 2 * ELTSModuleDrawer.STATE_RADIUS, pt.y
					- ELTSModuleDrawer.STATE_RADIUS,
					3 * ELTSModuleDrawer.STATE_RADIUS,
					2 * ELTSModuleDrawer.STATE_RADIUS);
		} else
			return new Rectangle(pt.x - ELTSModuleDrawer.STATE_RADIUS, pt.y
					- ELTSModuleDrawer.STATE_RADIUS,
					2 * ELTSModuleDrawer.STATE_RADIUS,
					2 * ELTSModuleDrawer.STATE_RADIUS);
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

	public Transition transitionAtPoint(Point pt) {
		if (!this.valid)
			refreshArrowMap();
		Set<CurvedArrow> arrows = this.arrowToTransitionMap.keySet();
		Iterator<CurvedArrow> iter = arrows.iterator();
		while (iter.hasNext()) {
			CurvedArrow arrow = iter.next();
			if (arrow.isNear(pt, 2))
				return this.arrowToTransitionMap.get(arrow);
		}
		return null;
	}

	private void drawSelectionBox(Graphics2D graphics) {
		graphics.drawRect(this.selectionBounds.x, this.selectionBounds.y,
				this.selectionBounds.width, this.selectionBounds.height);
	}

	public void setSelectionBounds(Rectangle rect) {
		selectionBounds = rect;
	}

	private void drawTransitions(Graphics2D graphics) {
		Set<CurvedArrow> arrows = this.arrowToTransitionMap.keySet();
		Iterator<CurvedArrow> iter = arrows.iterator();
		while (iter.hasNext()) {
			CurvedArrow arrow = iter.next();
			if (arrow.getTransition().isSelected()) {
				arrow.drawHighlight(graphics);
				arrow.drawControlPoint(graphics);
			} else {
				arrow.draw(graphics);
			}
		}
	}

	private void refreshArrowMap() {
		State[] states = this.getModule().getStates();
		this.arrowToTransitionMap.clear();
		this.transitionToArrowMap.clear();

		for (int i = 0; i < states.length; i++) {
			for (int j = i + 1; j < states.length; j++) {
				Transition transition_ij = this.getModule()
						.getTransitionsFromStateToState(states[i], states[j]);
				Transition transition_ji = this.getModule()
						.getTransitionsFromStateToState(states[j], states[i]);

				if (transition_ij != null) {
					double ang = angle(states[i], states[j]);
					Point startPt = pointOnState(states[i], ang - Math.PI / 25);
					Point endPt = pointOnState(states[j], ang + Math.PI
							+ Math.PI / 25);
					double curvy = (transition_ji != null) ? 0.5 : 0;
					CurvedArrow arrow = new CurvedArrow(startPt, endPt, curvy,
							transition_ij);
					this.arrowToTransitionMap.put(arrow, transition_ij);
					this.transitionToArrowMap.put(transition_ij, arrow);
				}
				if (transition_ji != null) {
					double ang = angle(states[i], states[j]);
					Point startPt = pointOnState(states[j], ang + Math.PI
							- Math.PI / 25);
					Point endPt = pointOnState(states[i], ang + Math.PI / 25);
					double curvy = (transition_ij != null) ? 0.5 : 0;
					CurvedArrow arrow = new CurvedArrow(startPt, endPt, curvy,
							transition_ji);
					this.arrowToTransitionMap.put(arrow, transition_ji);
					this.transitionToArrowMap.put(transition_ji, arrow);
				}
			}

			Transition transition_ii = this.getModule()
					.getTransitionsFromStateToState(states[i], states[i]);
			if (transition_ii != null) {
				Point startPt = pointOnState(states[i], -Math.PI / 3);
				Point endPt = pointOnState(states[i], -Math.PI * 2 / 3);

				CurvedArrow arrow = new CurvedArrow(startPt, endPt, -2.0,
						transition_ii);
				this.arrowToTransitionMap.put(arrow, transition_ii);
				this.transitionToArrowMap.put(transition_ii, arrow);
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
		if(event.getIsAdd())
			invalidateBounds();
		else if(event.getIsMove())
			invalidate();
		//this.valid = false;
		getView().repaint();
	}

	private void transitionEditHandler(TransitionEditEvent event) {
		//this.valid = false;
		invalidate();
		getView().repaint();
	}
	
//	@Override
//	public void invalidate() {
//		this.valid = false;
//	}
//	
//	@Override
//	public void invalidateBounds() {
//
//	}

	private void drawState(State state, Graphics2D graphics) {
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

	private void drawStateBackground(Graphics2D graphics, State state, Point pt,
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
