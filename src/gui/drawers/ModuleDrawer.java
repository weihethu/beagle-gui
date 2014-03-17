package gui.drawers;

import events.ObjectEditEvent;
import events.StateEditEvent;
import events.TransitionEditEvent;
import events.listeners.ObjectEditListener;
import gui.Note;
import gui.entities.CurvedArrow;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import model.Module;
import model.automata.State;
import model.automata.Transition;

public class ModuleDrawer extends ObjectDrawer {
	private Rectangle selectionBounds = new Rectangle(0, 0, -1, -1);
	private Map<CurvedArrow, Transition> arrowToTransitionMap = null;
	public Map<Transition, CurvedArrow> transitionToArrowMap = null;
	private Rectangle cachedBounds = null;
	public static final Color STATE_COLOR = new Color(255, 255, 150);
	public static int STATE_RADIUS = 30;
	private boolean ignoreSelected = false;

	public ModuleDrawer(Module module) {
		super(module);
		arrowToTransitionMap = new HashMap<CurvedArrow, Transition>();
		transitionToArrowMap = new HashMap<Transition, CurvedArrow>();

		DrawerListener listener = new DrawerListener();
		module.addStateListener(listener);
		module.addTransitionListener(listener);
	}

	@Override
	public void drawInternal(Graphics2D graphics) {
		Graphics2D tmpGraphics2D = (Graphics2D) graphics.create();
		tmpGraphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		if (!this.valid)
			refreshArrowMap();

		drawTransitions(tmpGraphics2D);
		State[] states = getModule().getStates();
		for (State state : states) {
			drawState(state, tmpGraphics2D);
		}
		if (!ignoreSelected)
			drawSelectionBox(tmpGraphics2D);
		tmpGraphics2D.dispose();
	}

	public void drawExternal(Graphics2D graphics) {
		ignoreSelected = true;
		drawInternal(graphics);
		ignoreSelected = false;
	}

	public Rectangle getUntransformedBounds(boolean ignoreNotes) {
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
		if (!ignoreNotes) {
			Note[] notes = getModule().getNotes();
			for (Note note : notes) {
				if (note.isVisible()) {
					Rectangle rect = new Rectangle(note.getAutoPoint(),
							new Dimension(note.getBounds().getSize()));
					resultRect.add(rect);
				}
			}
		}
		Iterator<CurvedArrow> iter = this.arrowToTransitionMap.keySet()
				.iterator();
		while (iter.hasNext()) {
			CurvedArrow arrow = iter.next();
			resultRect.add(arrow.getBounds());
		}
		this.validBounds = true;
		return this.cachedBounds = resultRect;
	}

	@Override
	public Rectangle getUntransformedBounds() {
		return getUntransformedBounds(false);
	}

	public Rectangle getBounds(State state) {
		Point pt = state.getPoint();
		if (getModule().getInitialState() == state) {
			return new Rectangle(pt.x - 2 * ModuleDrawer.STATE_RADIUS, pt.y
					- ModuleDrawer.STATE_RADIUS, 3 * ModuleDrawer.STATE_RADIUS,
					2 * ModuleDrawer.STATE_RADIUS);
		} else
			return new Rectangle(pt.x - ModuleDrawer.STATE_RADIUS, pt.y
					- ModuleDrawer.STATE_RADIUS, 2 * ModuleDrawer.STATE_RADIUS,
					2 * ModuleDrawer.STATE_RADIUS);
	}

	public Module getModule() {
		return (Module) target;
	}

	public State stateAtPoint(Point pt) {
		State[] states = getModule().getStates();
		for (State state : states) {
			if (pt.distance(state.getPoint()) <= ModuleDrawer.STATE_RADIUS)
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
			if (arrow.getTransition().isSelected() && !ignoreSelected) {
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
						.getTransitionFromStateToState(states[i], states[j]);
				Transition transition_ji = this.getModule()
						.getTransitionFromStateToState(states[j], states[i]);

				if (transition_ij != null) {
					double ang = angle(states[i], states[j]);
					Point startPt = pointOnState(states[i], ang - Math.PI / 25);
					Point endPt = pointOnState(states[j], ang + Math.PI
							+ Math.PI / 25);
					double curvy = (transition_ji != null) ? 0.5 : 0;
					CurvedArrow arrow = new CurvedArrow(startPt, endPt, curvy,
							transition_ij);
					arrow.setLabel(transition_ij.getDescription());
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
					arrow.setLabel(transition_ji.getDescription());
					this.arrowToTransitionMap.put(arrow, transition_ji);
					this.transitionToArrowMap.put(transition_ji, arrow);
				}
			}

			Transition transition_ii = this.getModule()
					.getTransitionFromStateToState(states[i], states[i]);
			if (transition_ii != null) {
				Point startPt = pointOnState(states[i], -Math.PI / 3);
				Point endPt = pointOnState(states[i], -Math.PI * 2 / 3);

				CurvedArrow arrow = new CurvedArrow(startPt, endPt, -2.0,
						transition_ii);

				arrow.setLabel(transition_ii.getDescription());
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
		double dx = Math.cos(ang) * ModuleDrawer.STATE_RADIUS;
		double dy = Math.sin(ang) * ModuleDrawer.STATE_RADIUS;
		pt.translate((int) dx, (int) dy);
		return pt;
	}

	private void stateEditHandler(StateEditEvent event) {
		if (event.isAdd || event.isIntialChange)
			invalidateBounds();
		else if (event.isMove)
			invalidate();
		if (getView() != null) {
			getView().requestTransform();
		}
	}

	private void transitionEditHandler(TransitionEditEvent event) {
		invalidate();
		if (getView() != null)
			getView().repaint();
	}

	private void drawState(State state, Graphics2D graphics) {
		drawStateBackground(graphics, state, state.getPoint(), STATE_COLOR);

		Point currentPt = state.getPoint();
		graphics.setColor(Color.black);
		int strWidth = (int) (graphics.getFontMetrics().getStringBounds(
				state.getName(), graphics).getWidth()) >> 1;
		int strHeight = (int) graphics.getFontMetrics().getAscent() >> 1;

		graphics.drawString(state.getName(), currentPt.x - strWidth,
				currentPt.y - strHeight);
		graphics.drawOval(currentPt.x - ModuleDrawer.STATE_RADIUS, currentPt.y
				- ModuleDrawer.STATE_RADIUS, 2 * ModuleDrawer.STATE_RADIUS,
				2 * ModuleDrawer.STATE_RADIUS);

		if (state.getModule().getInitialState() == state) {
			int[] xs = { currentPt.x - ModuleDrawer.STATE_RADIUS,
					currentPt.x - (ModuleDrawer.STATE_RADIUS << 1),
					currentPt.x - (ModuleDrawer.STATE_RADIUS << 1) };
			int[] ys = { currentPt.y, currentPt.y - ModuleDrawer.STATE_RADIUS,
					currentPt.y + ModuleDrawer.STATE_RADIUS };

			graphics.setColor(Color.white);
			graphics.fillPolygon(xs, ys, 3);
			graphics.setColor(Color.black);
			graphics.drawPolygon(xs, ys, 3);
		}
	}

	private void drawStateBackground(Graphics2D graphics, State state,
			Point pt, Color color) {
		graphics.setColor(color);
		if (state.isSelected() && !ignoreSelected)
			graphics.setColor(new Color(100, 200, 200));

		graphics.fillOval(pt.x - ModuleDrawer.STATE_RADIUS, pt.y
				- ModuleDrawer.STATE_RADIUS, 2 * ModuleDrawer.STATE_RADIUS,
				2 * ModuleDrawer.STATE_RADIUS);
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
