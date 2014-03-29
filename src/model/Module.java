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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import utils.StringUtil;

import model.automata.State;
import model.automata.Transition;

public class Module extends DrawableObject {
	private Point point;
	private Model model;
	private String name = null;
	private boolean selected = false;
	private Set<State> states;
	private State[] cachedStates = null;
	private State initialState = null;
	private Map<State, List<Transition>> transitionFromStateMap = new HashMap<State, List<Transition>>();
	private Map<State, List<Transition>> transitionToStateMap = new HashMap<State, List<Transition>>();
	private Set<Transition> transitions = null;
	private Transition[] cachedTransitions = null;
	private Set<ObjectEditListener> stateListeners = null;
	private Set<ObjectEditListener> transitionListeners = null;
	private Map<State, Transition[]> transitionArrayFromStateMap = null;
	private Map<State, Transition[]> transitionArrayToStateMap = null;
	private String varDecl = "";
	private String initAction = "";

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
					if (stateEvt.isNameChange())
						Module.this.cachedStates = null;
				}
			}

		});

		this.addStateListener(Environment.getInstance().getVerifierPane());
		this.addTransitionListener(Environment.getInstance().getVerifierPane());
	}

	public Point getPoint() {
		return this.point;
	}

	public void setPoint(Point point) {
		this.point = point;
		model.distributeModuleEditEvent(new ModuleEditEvent(this,
				ModuleEditEvent.EventType.MOVE));
	}

	public void setInitialAction(String value) {
		if (value == null)
			value = "";
		this.initAction = value;
	}

	public String getInitialAction() {
		return this.initAction.trim();
	}

	public void setVarDeclaration(String value) {
		if (value == null)
			value = "";
		this.varDecl = value;
	}

	public String getVarDeclaration() {
		return this.varDecl.trim();
	}

	public void setName(String name) {
		if (this.name.equals(name))
			return;
		if (!StringUtil.validIdentifier(name)) {
			JOptionPane.showMessageDialog(null,
					"Invalid name! Please pick another one!");
			return;
		}
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

	public String getName() {
		return this.name;
	}

	public void setSelect(boolean status) {
		selected = status;
	}

	public boolean isSelected() {
		return this.selected;
	}

	public State createState(Point pt) {
		State state = new State(getDefaultStateName(), pt, this);
		addState(state);
		this.distributeStateEditEvent(new StateEditEvent(state,
				StateEditEvent.EventType.ADD));
		return state;
	}

	public void addState(State state) {
		this.states.add(state);
		this.transitionFromStateMap.put(state, new LinkedList<Transition>());
		this.transitionToStateMap.put(state, new LinkedList<Transition>());
		this.cachedStates = null;
		this.distributeStateEditEvent(new StateEditEvent(state,
				StateEditEvent.EventType.ADD));
	}

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

	public State getStateByName(String name) {
		if (name == null)
			return null;
		for (State s : states) {
			if (s.getName().equals(name))
				return s;
		}
		return null;
	}

	public void selectStatesWithinBounds(Rectangle rect) {
		State[] states = getStates();
		for (int i = 0; i < states.length; i++) {
			states[i].setSelect(false);
			if (rect.contains(states[i].getPoint()))
				states[i].setSelect(true);
		}
	}

	public void unselectAll() {
		State[] states = getStates();
		for (State state : states)
			state.setSelect(false);
	}

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

	public State getInitialState() {
		return this.initialState;
	}

	public void addTransition(Transition transition) {
		if (this.transitions.contains(transition))
			return;
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

	public Transition getTransitionFromStateToState(State fromState,
			State toState) {
		Transition[] transitions = getTransitionsFromState(fromState);
		for (int i = 0; i < transitions.length; i++) {
			if (transitions[i].getToState() == toState)
				return transitions[i];
		}
		return null;
	}

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

	public Transition[] getTransitionsToState(State state) {
		Transition[] cachedResults = this.transitionArrayToStateMap.get(state);
		if (cachedResults == null) {
			List<Transition> list = this.transitionToStateMap.get(state);
			cachedResults = list.toArray(new Transition[0]);
			this.transitionArrayToStateMap.put(state, cachedResults);
		}
		return cachedResults;
	}

	public Transition[] getTransitions() {
		if (this.cachedTransitions == null)
			this.cachedTransitions = (Transition[]) this.transitions
					.toArray(new Transition[0]);
		return this.cachedTransitions;
	}

	public void addStateListener(ObjectEditListener listener) {
		this.stateListeners.add(listener);
	}

	public void addTransitionListener(ObjectEditListener listener) {
		this.transitionListeners.add(listener);
	}

	public void distributeStateEditEvent(StateEditEvent event) {
		Iterator<ObjectEditListener> iter = stateListeners.iterator();
		while (iter.hasNext()) {
			ObjectEditListener currentListener = iter.next();
			currentListener.objectEdit(event);
		}
	}

	public void distributeTransitionEditEvent(TransitionEditEvent event) {
		Iterator<ObjectEditListener> iter = transitionListeners.iterator();
		while (iter.hasNext()) {
			ObjectEditListener currentListener = iter.next();
			currentListener.objectEdit(event);
		}
	}

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
