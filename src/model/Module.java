package model;

import events.ModuleEditEvent;
import events.ObjectEditEvent;
import events.StateEditEvent;
import events.TransitionEditEvent;
import events.listeners.ObjectEditListener;
import gui.Environment;
import gui.drawers.DrawableObject;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import model.automata.State;
import model.automata.Transition;
import utils.StringUtil;

/**
 * Module
 * 
 * @author Wei He
 * 
 */
public class Module extends DrawableObject {
	/**
	 * point
	 */
	private Point point;
	/**
	 * parent model
	 */
	private Model model;
	/**
	 * module name
	 */
	private String name = null;
	/**
	 * selected status
	 */
	private boolean selected = false;
	/**
	 * states
	 */
	private Set<State> states;
	/**
	 * cached states
	 */
	private State[] cachedStates = null;
	/**
	 * initial state
	 */
	private State initialState = null;
	/**
	 * map which associates a state and transitions from it
	 */
	private Map<State, List<Transition>> transitionFromStateMap = new HashMap<State, List<Transition>>();
	/**
	 * map which associates a state and transitions to it
	 */
	private Map<State, List<Transition>> transitionToStateMap = new HashMap<State, List<Transition>>();
	/**
	 * transitions
	 */
	private Set<Transition> transitions = null;
	/**
	 * cached transitions
	 */
	private Transition[] cachedTransitions = null;
	/**
	 * event listeners for StateEditEvents
	 */
	private Set<ObjectEditListener> stateListeners = null;
	/**
	 * event listeners for TransitionEditEvents
	 */
	private Set<ObjectEditListener> transitionListeners = null;
	/**
	 * map which associates a state and transitions from it(cached value)
	 */
	private Map<State, Transition[]> transitionArrayFromStateMap = null;
	/**
	 * map which associates a state and transitions to it(cached value)
	 */
	private Map<State, Transition[]> transitionArrayToStateMap = null;
	/**
	 * variables declaration
	 */
	private String varDecl = "";
	/**
	 * initial actions
	 */
	private String initAction = "";

	/**
	 * constructor
	 * 
	 * @param name
	 *            module name
	 * @param pt
	 *            point
	 * @param model
	 *            model
	 */
	public Module(String name, Point pt, Model model) {
		this.point = pt;
		this.model = model;
		this.name = name;
		this.selected = false;
		this.states = new HashSet<State>();
		this.initialState = null;
		this.transitions = new HashSet<Transition>();
		this.stateListeners = new HashSet<ObjectEditListener>();
		this.transitionListeners = new HashSet<ObjectEditListener>();
		this.transitionArrayFromStateMap = new HashMap<State, Transition[]>();
		this.transitionArrayToStateMap = new HashMap<State, Transition[]>();

		this.addStateListener(new ObjectEditListener() {

			@Override
			public void objectEdit(ObjectEditEvent event) {
				if (event instanceof StateEditEvent) {
					StateEditEvent stateEvt = (StateEditEvent) event;
					// cached states is sorted by name, so if name changes, must
					// recompute
					if (stateEvt.isNameChange())
						Module.this.cachedStates = null;
				}
			}

		});

		this.addStateListener(Environment.getInstance().getVerifierPane());
		this.addTransitionListener(Environment.getInstance().getVerifierPane());
	}

	/**
	 * get module point
	 * 
	 * @return point
	 */
	public Point getPoint() {
		return this.point;
	}

	/**
	 * set module point
	 * 
	 * @param point
	 *            point
	 */
	public void setPoint(Point point) {
		this.point = point;
		model.distributeModuleEditEvent(new ModuleEditEvent(this,
				ModuleEditEvent.EventType.MOVE));
	}

	/**
	 * set initial actions
	 * 
	 * @param value
	 *            actions value
	 */
	public void setInitialAction(String value) {
		if (value == null)
			value = "";
		this.initAction = value;
	}

	/**
	 * get initial actions
	 * 
	 * @return actions
	 */
	public String getInitialAction() {
		return this.initAction.trim();
	}

	/**
	 * set variables declaration
	 * 
	 * @param value
	 */
	public void setVarDeclaration(String value) {
		if (value == null)
			value = "";
		this.varDecl = value;
	}

	/**
	 * get variables declaration
	 * 
	 * @return variables declaration
	 */
	public String getVarDeclaration() {
		return this.varDecl.trim();
	}

	/**
	 * set module name
	 * 
	 * @param name
	 *            name
	 */
	public void setName(String name) {
		if (this.name.equals(name))
			return;
		// check valid
		if (!StringUtil.validIdentifier(name)) {
			JOptionPane.showMessageDialog(null,
					"Invalid name! Please pick another one!");
			return;
		}
		// check no modules share same name
		Module modules[] = model.getModules();
		for (Module module : modules) {
			if (module == this)
				continue;
			if (module.name.equals(name)) {
				JOptionPane.showMessageDialog(null,
						"Name used by other modules! Please pick another one!");
				return;
			}
		}
		this.name = name;
		model.distributeModuleEditEvent(new ModuleEditEvent(this,
				ModuleEditEvent.EventType.NAME));
	}

	/**
	 * get module name
	 * 
	 * @return name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * set selected status
	 * 
	 * @param status
	 *            selected status
	 */
	public void setSelect(boolean status) {
		selected = status;
	}

	/**
	 * is selected
	 * 
	 * @return a boolean indicator
	 */
	public boolean isSelected() {
		return this.selected;
	}

	/**
	 * create state at point
	 * 
	 * @param pt
	 *            point
	 * @return state created
	 */
	public State createState(Point pt) {
		State state = new State(getDefaultStateName(), pt, this);
		addState(state);
		this.distributeStateEditEvent(new StateEditEvent(state,
				StateEditEvent.EventType.ADD));
		return state;
	}

	/**
	 * add state to module
	 * 
	 * @param state
	 *            state
	 */
	public void addState(State state) {
		this.states.add(state);
		this.transitionFromStateMap.put(state, new LinkedList<Transition>());
		this.transitionToStateMap.put(state, new LinkedList<Transition>());
		this.cachedStates = null;
		this.distributeStateEditEvent(new StateEditEvent(state,
				StateEditEvent.EventType.ADD));
	}

	/**
	 * remove state
	 * 
	 * @param state
	 *            state
	 */
	public void removeState(State state) {
		Transition[] transitions = getTransitionsFromState(state);
		for (Transition transition : transitions)
			removeTransition(transition);
		transitions = getTransitionsToState(state);
		for (Transition transition : transitions)
			removeTransition(transition);

		this.states.remove(state);
		if (state == this.initialState) {
			this.initialState = null;
		}
		this.transitionFromStateMap.remove(state);
		this.transitionToStateMap.remove(state);
		this.transitionArrayFromStateMap.remove(state);
		this.transitionArrayToStateMap.remove(state);

		this.cachedStates = null;

		this.distributeStateEditEvent(new StateEditEvent(state,
				StateEditEvent.EventType.REMOVE));
	}

	/**
	 * get states
	 * 
	 * @return states
	 */
	@SuppressWarnings("unchecked")
	public State[] getStates() {
		if (this.cachedStates == null) {
			this.cachedStates = (State[]) this.states.toArray(new State[0]);
			Arrays.sort(this.cachedStates, new Comparator() {

				@Override
				public int compare(Object obj1, Object obj2) {
					return ((State) obj1).getName().compareTo(
							((State) obj2).getName());
				}

			});
		}
		return this.cachedStates;
	}

	/**
	 * get state by name
	 * 
	 * @param name
	 *            state name
	 * @return state, null if no such state
	 */
	public State getStateByName(String name) {
		if (name == null)
			return null;
		for (State s : states) {
			if (s.getName().equals(name))
				return s;
		}
		return null;
	}

	/**
	 * select states within bounds
	 * 
	 * @param rect
	 *            selection box
	 */
	public void selectStatesWithinBounds(Rectangle rect) {
		State[] states = getStates();
		for (int i = 0; i < states.length; i++) {
			states[i].setSelect(false);
			if (rect.contains(states[i].getPoint()))
				states[i].setSelect(true);
		}
	}

	/**
	 * unselect all states
	 */
	public void unselectAll() {
		State[] states = getStates();
		for (State state : states)
			state.setSelect(false);
	}

	/**
	 * set initial state
	 * 
	 * @param state
	 *            state, null if no initial state
	 */
	public void setInitialState(State state) {
		State oldInitialState = this.initialState;
		this.initialState = state;
		if (state != null) {
			this.distributeStateEditEvent(new StateEditEvent(state,
					StateEditEvent.EventType.INITIAL));
		} else {
			if (oldInitialState != null) {
				this.distributeStateEditEvent(new StateEditEvent(
						oldInitialState, StateEditEvent.EventType.INITIAL));
			}
		}
	}

	/**
	 * get initial state
	 * 
	 * @return initial state
	 */
	public State getInitialState() {
		return this.initialState;
	}

	/**
	 * add transition
	 * 
	 * @param transition
	 *            transition
	 */
	public void addTransition(Transition transition) {
		if (this.transitions.contains(transition))
			return;
		// there can be at most 1 transition from state A to state B
		for (Transition eachTransition : transitions) {
			if (eachTransition.getFromState() == transition.getFromState()
					&& eachTransition.getToState() == transition.getToState())
				return;
		}

		if (transition.getFromState() == null
				|| transition.getToState() == null)
			return;
		this.transitions.add(transition);
		this.transitionFromStateMap.get(transition.getFromState()).add(
				transition);
		this.transitionToStateMap.get(transition.getToState()).add(transition);
		this.transitionArrayFromStateMap.remove(transition.getFromState());
		this.transitionArrayToStateMap.remove(transition.getToState());
		this.cachedTransitions = null;
		this.distributeTransitionEditEvent(new TransitionEditEvent(transition,
				TransitionEditEvent.EventType.ADD));
	}

	/**
	 * remove transition
	 * 
	 * @param transition
	 *            transition
	 */
	public void removeTransition(Transition transition) {
		this.transitions.remove(transition);
		this.transitionFromStateMap.get(transition.getFromState()).remove(
				transition);
		this.transitionToStateMap.get(transition.getToState()).remove(
				transition);

		this.transitionArrayFromStateMap.remove(transition.getFromState());
		this.transitionArrayToStateMap.remove(transition.getToState());
		this.cachedTransitions = null;

		this.distributeTransitionEditEvent(new TransitionEditEvent(transition,
				TransitionEditEvent.EventType.REMOVE));
	}

	/**
	 * get transition from a state to a state
	 * 
	 * @param fromState
	 *            from state
	 * @param toState
	 *            to state
	 * @return transition, null if no such transition
	 */
	public Transition getTransitionFromStateToState(State fromState,
			State toState) {
		Transition[] transitions = getTransitionsFromState(fromState);
		for (int i = 0; i < transitions.length; i++) {
			if (transitions[i].getToState() == toState)
				return transitions[i];
		}
		return null;
	}

	/**
	 * get transitions starting from state
	 * 
	 * @param state
	 *            from state
	 * @return transitions
	 */
	public Transition[] getTransitionsFromState(State state) {
		Transition[] cachedResults = this.transitionArrayFromStateMap
				.get(state);
		if (cachedResults == null) {
			List<Transition> list = this.transitionFromStateMap.get(state);
			cachedResults = list.toArray(new Transition[0]);
			this.transitionArrayFromStateMap.put(state, cachedResults);
		}
		return cachedResults;
	}

	/**
	 * get transitions ends at a state
	 * 
	 * @param state
	 *            to state
	 * @return transitions
	 */
	public Transition[] getTransitionsToState(State state) {
		Transition[] cachedResults = this.transitionArrayToStateMap.get(state);
		if (cachedResults == null) {
			List<Transition> list = this.transitionToStateMap.get(state);
			cachedResults = list.toArray(new Transition[0]);
			this.transitionArrayToStateMap.put(state, cachedResults);
		}
		return cachedResults;
	}

	/**
	 * get all transitions
	 * 
	 * @return transitions
	 */
	public Transition[] getTransitions() {
		if (this.cachedTransitions == null)
			this.cachedTransitions = (Transition[]) this.transitions
					.toArray(new Transition[0]);
		return this.cachedTransitions;
	}

	/**
	 * add event listener for StateEditEvents
	 * 
	 * @param listener
	 *            event listener
	 */
	public void addStateListener(ObjectEditListener listener) {
		this.stateListeners.add(listener);
	}

	/**
	 * add event listener for TransitionEditEvents
	 * 
	 * @param listener
	 *            event listener
	 */
	public void addTransitionListener(ObjectEditListener listener) {
		this.transitionListeners.add(listener);
	}

	/**
	 * distribute StateEditEvents to all listeners
	 * 
	 * @param event
	 *            event
	 */
	public void distributeStateEditEvent(StateEditEvent event) {
		Iterator<ObjectEditListener> iter = stateListeners.iterator();
		while (iter.hasNext()) {
			ObjectEditListener currentListener = iter.next();
			currentListener.objectEdit(event);
		}
	}

	/**
	 * distribute TransitionEditEvent to all listeners
	 * 
	 * @param event
	 *            event
	 */
	public void distributeTransitionEditEvent(TransitionEditEvent event) {
		Iterator<ObjectEditListener> iter = transitionListeners.iterator();
		while (iter.hasNext()) {
			ObjectEditListener currentListener = iter.next();
			currentListener.objectEdit(event);
		}
	}

	/**
	 * generate a default name for state in pattern of "state_x" that do not
	 * duplicate with other states
	 * 
	 * @return name
	 */
	private String getDefaultStateName() {
		State[] states = this.getStates();
		for (int i = 1;; i++) {
			String name = "state_" + i;
			boolean noConflict = true;
			for (State state : states) {
				if (name.equals(state.getName())) {
					noConflict = false;
					break;
				}
			}
			if (noConflict)
				return name;
		}
	}
}
