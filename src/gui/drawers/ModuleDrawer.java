package gui.drawers;

import events.ObjectEditEvent;
import events.StateEditEvent;
import events.TransitionEditEvent;
import events.listeners.ObjectEditListener;
import gui.entities.CurvedArrow;
import gui.entities.Note;

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

/**
 * drawer for modules
 * 
 * @author Wei He
 * 
 */
public class ModuleDrawer extends ObjectDrawer {
	/**
	 * selection box bounds
	 */
	private Rectangle selectionBounds = new Rectangle(0, 0, -1, -1);
	/**
	 * a map which associates transitions to their curved arrows, the curved
	 * arrows are actually drawed on canvas
	 */
	private Map<CurvedArrow, Transition> arrowToTransitionMap = null;
	/**
	 * a map which associates curved arrows to their transitions
	 */
	public Map<Transition, CurvedArrow> transitionToArrowMap = null;
	/**
	 * cached bounds
	 */
	private Rectangle cachedBounds = null;
	/**
	 * color for drawing states
	 */
	private static final Color STATE_COLOR = new Color(255, 255, 150);
	/**
	 * color for drawing states when selected
	 */
	private static final Color SELECTED_STATE_COLOR = new Color(100, 200, 200);
	/**
	 * default radius for drawing state
	 */
	public static int STATE_RADIUS = 30;
	/**
	 * the note for editing initial actions
	 */
	private Note initActionNote;
	/**
	 * the note for editing variables
	 */
	private Note varNote;
	/**
	 * whether computed arrow maps is valid
	 */
	private boolean validArrowMaps = false;

	/**
	 * constructor
	 * 
	 * @param module
	 *            module to be drawed
	 */
	public ModuleDrawer(Module module) {
		super(module);
		arrowToTransitionMap = new HashMap<CurvedArrow, Transition>();
		transitionToArrowMap = new HashMap<Transition, CurvedArrow>();

		module.addStateListener(new ObjectEditListener() {

			@Override
			public void objectEdit(ObjectEditEvent e) {
				if (e instanceof StateEditEvent) {
					StateEditEvent event = (StateEditEvent) e;
					if (event.isMove()) {
						invalidateArrowMaps();
						invalidateBounds();
					} else
						invalidateBounds();
					if (event.isRemove() || event.isInitialChange())
						initActionNote
								.setVisible(getModule().getInitialState() != null);
					if (getCanvas() != null) {
						// recompute transformation and repaint
						getCanvas().requestTransform();
					}
				}
			}

		});

		module.addTransitionListener(new ObjectEditListener() {

			@Override
			public void objectEdit(ObjectEditEvent event) {
				if (event instanceof TransitionEditEvent) {
					invalidateArrowMaps();
					invalidateBounds();
					if (getCanvas() != null)
						getCanvas().repaint();
				}
			}

		});
		this.varNote = new Note(module, Note.VAR, module.getVarDeclaration());
		this.initActionNote = new Note(module, Note.INIT_ACTION,
				module.getInitialAction());
		this.initActionNote.setVisible(module.getInitialState() != null);
	}

	@Override
	public void drawInternal(Graphics2D graphics) {
		drawInternal(graphics, false);
	}

	/**
	 * draw internals
	 * 
	 * @param graphics
	 *            graphics
	 * @param ignoreSelected
	 *            sets whether state & transition's selection status should be
	 *            ignored when drawing
	 */
	public void drawInternal(Graphics2D graphics, boolean ignoreSelected) {
		Graphics2D tmpGraphics2D = (Graphics2D) graphics.create();
		tmpGraphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		// recompute arrow maps if necessary
		if (!this.validArrowMaps)
			refreshArrowMap();

		// draw transitions
		drawTransitions(tmpGraphics2D, ignoreSelected);
		// draw states
		State[] states = getModule().getStates();
		for (State state : states) {
			drawState(state, tmpGraphics2D, ignoreSelected);
		}
		// draw selection box
		if (!ignoreSelected)
			drawSelectionBox(tmpGraphics2D);
		tmpGraphics2D.dispose();
	}

	/**
	 * draw module internals on model's canvas, in this case, we should ignore
	 * selected states when drawing
	 * 
	 * @param graphics
	 */
	public void drawExternal(Graphics2D graphics) {
		drawInternal(graphics, true);
	}

	@Override
	public Note[] getNotes() {
		return new Note[] { this.varNote, this.initActionNote };
	}

	/**
	 * get note for initial actions
	 * 
	 * @return note
	 */
	public Note getInitActionNote() {
		return this.initActionNote;
	}

	/**
	 * get note for variable declarations
	 * 
	 * @return note
	 */
	public Note getVarNote() {
		return this.varNote;
	}

	/**
	 * get object bounds on canvas
	 * 
	 * @param ignoreNotes
	 *            whether we should ignore notes when computing
	 * @return bounds
	 */
	public Rectangle getUntransformedBounds(boolean ignoreNotes) {
		// if cached bounds is updated, return it immediately
		if (this.validBounds)
			return this.cachedBounds;

		if (!this.validArrowMaps)
			refreshArrowMap();

		State[] states = getModule().getStates();
		if (states.length == 0)
			return null;

		// consider all states
		Rectangle resultRect = getBounds(states[0]);
		for (int i = 1; i < states.length; i++) {
			resultRect.add(getBounds(states[i]));
		}
		// consider visible notes
		if (!ignoreNotes) {
			Note[] notes = getNotes();
			for (Note note : notes) {
				if (note.isVisible()) {
					Rectangle rect = new Rectangle(note.getAutoPoint(),
							new Dimension(note.getBounds().getSize()));
					resultRect.add(rect);
				}
			}
		}
		// consider arrowed curves for transitions
		Iterator<CurvedArrow> iter = this.arrowToTransitionMap.keySet()
				.iterator();
		while (iter.hasNext()) {
			CurvedArrow arrow = iter.next();
			resultRect.add(arrow.getBounds());
		}
		// store in cached bounds for future use
		this.validBounds = true;
		return this.cachedBounds = resultRect;
	}

	@Override
	public Rectangle getUntransformedBounds() {
		return getUntransformedBounds(false);
	}

	/**
	 * get state bounds
	 * 
	 * @param state
	 *            state
	 * @return bounds
	 */
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

	/**
	 * get module to be drawed
	 * 
	 * @return module
	 */
	public Module getModule() {
		return (Module) target;
	}

	/**
	 * get state at given point, return null if none
	 * 
	 * @param pt
	 *            point
	 * @return state at given point
	 */
	public State stateAtPoint(Point pt) {
		State[] states = getModule().getStates();
		for (State state : states) {
			if (pt.distance(state.getPoint()) <= ModuleDrawer.STATE_RADIUS)
				return state;
		}
		return null;
	}

	/**
	 * get transition at given point, return null if none
	 * 
	 * @param pt
	 *            point
	 * @return transition at given point
	 */
	public Transition transitionAtPoint(Point pt) {
		if (!this.validArrowMaps)
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

	/**
	 * draw selection box
	 * 
	 * @param graphics
	 *            graphics
	 */
	private void drawSelectionBox(Graphics2D graphics) {
		graphics.drawRect(this.selectionBounds.x, this.selectionBounds.y,
				this.selectionBounds.width, this.selectionBounds.height);
	}

	/**
	 * draw state
	 * 
	 * @param state
	 *            state
	 * @param graphics
	 *            graphics
	 * @param ignoreSelected
	 *            whether state's selection status should be ignored when
	 *            drawing
	 */
	private void drawState(State state, Graphics2D graphics,
			boolean ignoreSelected) {
		// draw state background
		drawStateBackground(graphics, state, ignoreSelected);

		Point currentPt = state.getPoint();
		// draw state name
		graphics.setColor(Color.black);
		int strWidth = (int) (graphics.getFontMetrics().getStringBounds(
				state.getName(), graphics).getWidth()) >> 1;
		int strHeight = (int) graphics.getFontMetrics().getAscent() >> 1;

		graphics.drawString(state.getName(), currentPt.x - strWidth,
				currentPt.y - strHeight);
		// draw state border
		graphics.drawOval(currentPt.x - ModuleDrawer.STATE_RADIUS, currentPt.y
				- ModuleDrawer.STATE_RADIUS, 2 * ModuleDrawer.STATE_RADIUS,
				2 * ModuleDrawer.STATE_RADIUS);

		// draw triangle for initial states
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

	/**
	 * draw state background
	 * 
	 * @param graphics
	 *            graphics
	 * @param state
	 *            state
	 * @param ignoreSelected
	 *            whether state's selection status should be ignored when
	 *            drawing
	 */
	private void drawStateBackground(Graphics2D graphics, State state,
			boolean ignoreSelected) {
		Point pt = state.getPoint();
		graphics.setColor(STATE_COLOR);
		if (state.isSelected() && !ignoreSelected)
			graphics.setColor(SELECTED_STATE_COLOR);

		graphics.fillOval(pt.x - ModuleDrawer.STATE_RADIUS, pt.y
				- ModuleDrawer.STATE_RADIUS, 2 * ModuleDrawer.STATE_RADIUS,
				2 * ModuleDrawer.STATE_RADIUS);
	}

	/**
	 * set selection bounds
	 * 
	 * @param rect
	 *            selection bounds
	 */
	public void setSelectionBounds(Rectangle rect) {
		selectionBounds = rect;
	}

	/**
	 * draw transitions
	 * 
	 * @param graphics
	 *            graphics
	 * @param ignoreSelected
	 *            whether transition's selected status should be ignored when
	 *            drawing
	 */
	private void drawTransitions(Graphics2D graphics, boolean ignoreSelected) {
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

	/**
	 * recompute arrowed curves map
	 */
	private void refreshArrowMap() {
		// remove previously computed result
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
					// if there's transition from state i to j
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
					// if there's transition from state j to i
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
				// if there's transition from state i to itself
				Point startPt = pointOnState(states[i], -Math.PI / 3);
				Point endPt = pointOnState(states[i], -Math.PI * 2 / 3);

				CurvedArrow arrow = new CurvedArrow(startPt, endPt, -2.0,
						transition_ii);

				arrow.setLabel(transition_ii.getDescription());
				this.arrowToTransitionMap.put(arrow, transition_ii);
				this.transitionToArrowMap.put(transition_ii, arrow);
			}
		}
		this.validArrowMaps = true;
	}

	/**
	 * get angle from state to state
	 * 
	 * @param from
	 *            from state
	 * @param to
	 *            to state
	 * @return angle
	 */
	private double angle(State from, State to) {
		double dx = to.getPoint().getX() - from.getPoint().getX();
		double dy = to.getPoint().getY() - from.getPoint().getY();
		return Math.atan2(dy, dx);
	}

	/**
	 * get location of point on border of state given angle
	 * 
	 * @param state
	 *            state
	 * @param ang
	 *            angle
	 * @return point location
	 */
	private Point pointOnState(State state, double ang) {
		Point pt = new Point(state.getPoint());
		double dx = Math.cos(ang) * ModuleDrawer.STATE_RADIUS;
		double dy = Math.sin(ang) * ModuleDrawer.STATE_RADIUS;
		pt.translate((int) dx, (int) dy);
		return pt;
	}

	/**
	 * invalidate arrow curves map
	 */
	public void invalidateArrowMaps() {
		this.validArrowMaps = false;
	}
}
