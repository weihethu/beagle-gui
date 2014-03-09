package model.automata;

import events.StateEditEvent;
import gui.drawers.DrawableObject;

import java.awt.Point;

import model.Module;

public class State extends DrawableObject {
	private Point point;
	private int id;
	private Module module;
	private String name = null;
	private boolean selected = false;

	public State(int id, Point pt, Module module) {
		this.point = pt;
		this.id = id;
		this.module = module;
		this.name = "State";
		this.selected = false;
	}

	public Point getPoint() {
		return this.point;
	}

	public void setPoint(Point point) {
		this.point = point;
		module.distributeStateEditEvent(new StateEditEvent(this, false, true,
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
		module.distributeStateEditEvent(new StateEditEvent(this, false, false,
				true));
	}

	public Module getModule() {
		return this.module;
	}
}
