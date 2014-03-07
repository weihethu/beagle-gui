package model;

import gui.drawers.DrawableObject;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import model.automata.State;

public class ELTSModule extends DrawableObject {
	private Point point;
	private int id;
	private ELTSModel model;
	private String name = null;
	private boolean selected = false;
	private Set<State> states;
	private State[] cachedStates = null;

	public ELTSModule(int id, Point pt, ELTSModel model) {
		this.point = pt;
		this.id = id;
		this.model = model;
		this.name = "Module";
		this.selected = false;
		states = new HashSet<State>();
	}

	public Point getPoint() {
		return this.point;
	}

	public void setPoint(Point point) {
		this.point = point;
		model.distributeObjectEditEvent();
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
		model.distributeObjectEditEvent();
	}
	
	public State createState(Point pt) {
		int id = 0;
		while (getStateWithID(id) != null)
			id++;
		State state = new State(id, pt, this);
		addState(state);
		distributeObjectEditEvent();
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
		this.cachedStates = null;
	}

	@SuppressWarnings("unchecked")
	public State[] getStates() {
		if (this.cachedStates == null) {
			this.cachedStates = (State[]) this.states
					.toArray(new State[0]);
			Arrays.sort(this.cachedStates, new Comparator() {

				@Override
				public int compare(Object obj1, Object obj2) {
					return ((State) obj1).getID()
							- ((State) obj2).getID();
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
}
