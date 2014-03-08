package model.automata;

import gui.drawers.DrawableObject;

import java.awt.Point;

public class Transition extends DrawableObject {
	private State fromState, toState;
	private boolean selected = false;
	private Point controlPt;

	public Transition(State from, State to) {
		this.setFromState(from);
		this.setToState(to);
		controlPt = null;
	}

	public State getFromState() {
		return fromState;
	}

	public void setFromState(State fromState) {
		this.fromState = fromState;
	}

	public State getToState() {
		return toState;
	}

	public void setToState(State toState) {
		this.toState = toState;
	}

	public void setSelect(boolean status) {
		selected = status;
	}

	public boolean isSelected() {
		return this.selected;
	}

	public Point getControl() {
		return this.controlPt;
	}
}
