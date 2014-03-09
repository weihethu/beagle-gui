package model;

import events.ModuleEditEvent;
import events.StateEditEvent;
import events.TransitionEditEvent;
import events.listeners.ObjectEditListener;
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

import model.automata.State;
import model.automata.Transition;

public class Module extends DrawableObject {
	private Point point;
	private int id;
	private Model model;
	private String name = null;
	private boolean selected = false;
	private Set<State> states;
	private State[] cachedStates = null;
	private State initialState = null;
	private Map<State, List<Transition>> transitionFromStateMap = new HashMap<State, List<Transition>>();
	private Map<State, List<Transition>> transitionToStateMap = new HashMap<State, List<Transition>>();
	private Set<Transition> transitions = null;
	private Set<ObjectEditListener> stateListeners = null;
	private Set<ObjectEditListener> transitionListeners = null;
	private Map<State, Transition[]> transitionArrayFromStateMap = null;
	private Map<State, Transition[]> transitionArrayToStateMap = null;

	public Module(int id, Point pt, Model model) {
		this.point = pt;
		this.id = id;
		this.model = model;
		this.name = "Module";
		this.selected = false;
		this.states = new HashSet<State>();
		this.initialState = null;
		this.transitions = new HashSet<Transition>();
		this.stateListeners = new HashSet<ObjectEditListener>();
		this.transitionListeners = new HashSet<ObjectEditListener>();
		this.transitionArrayFromStateMap = new HashMap<State, Transition[]>();
		this.transitionArrayToStateMap = new HashMap<State, Transition[]>();
	}

	public Point getPoint() {
		return this.point;
	}

	public void setPoint(Point point) {
		this.point = point;
		model.distributeModuleEditEvent(new ModuleEditEvent(this, false, true,
				false));
	}

	public int getID() {
		return this.id;
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

	public void setName(String name) {
		this.name = name;
		model.distributeModuleEditEvent(new ModuleEditEvent(this, false, false,
				true));
	}

	public State createState(Point pt) {
		int id = 0;
		while (getStateWithID(id) != null)
			id++;
		State state = new State(id, pt, this);
		addState(state);
		this.distributeStateEditEvent(new StateEditEvent(state, true, false,
				false));
		return state;
	}

	public State getStateWithID(int id) {
		Iterator<State> iter = this.states.iterator();
		while (iter.hasNext()) {
			State currentState = iter.next();
			if (currentState.getID() == id)
				return currentState;
		}
		return null;
	}

	private void addState(State state) {
		this.states.add(state);
		this.transitionFromStateMap.put(state, new LinkedList<Transition>());
		this.transitionToStateMap.put(state, new LinkedList<Transition>());
		this.cachedStates = null;
		this.distributeStateEditEvent(new StateEditEvent(state, true, false,
				false));
	}

	@SuppressWarnings("unchecked")
	public State[] getStates() {
		if (this.cachedStates == null) {
			this.cachedStates = (State[]) this.states.toArray(new State[0]);
			Arrays.sort(this.cachedStates, new Comparator() {

				@Override
				public int compare(Object obj1, Object obj2) {
					return ((State) obj1).getID() - ((State) obj2).getID();
				}

			});
		}
		return this.cachedStates;
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
		this.initialState = state;
		this.distributeStateEditEvent(new StateEditEvent(state, false, false,
				true));
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
		this.distributeTransitionEditEvent(new TransitionEditEvent(transition,
				true, false));
	}

	public Transition getTransitionsFromStateToState(State fromState,
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
}
