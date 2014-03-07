package model.automata;

import gui.drawers.DrawableObject;

import java.awt.Point;

import model.ELTSModule;

public class State extends DrawableObject {
	private Point point;
	private int id;
	private ELTSModule module;
	private String name = null;
	private boolean selected = false;

	public State(int id, Point pt, ELTSModule module) {
		this.point = pt;
		this.id = id;
		this.module = module;
		this.name = "Module";
		this.selected = false;
	}

	public Point getPoint() {
		return this.point;
	}

	public void setPoint(Point point) {
		this.point = point;
		module.distributeObjectEditEvent();
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
		module.distributeObjectEditEvent();
	}
}
