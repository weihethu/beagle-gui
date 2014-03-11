package model.automata;

import events.StateEditEvent;
import gui.drawers.DrawableObject;

import java.awt.Point;

import javax.swing.JOptionPane;

import model.Module;

public class State extends DrawableObject {
	private Point point;
	private Module module;
	private String name = null;
	private boolean selected = false;

	public State(String name, Point pt, Module module) {
		this.point = pt;
		this.name = name;
		this.module = module;
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
		if (this.name.equals(name))
			return;
		State states[] = module.getStates();
		for (State state : states) {
			if (state == this)
				continue;
			if (state.name.equals(name)) {
				JOptionPane.showMessageDialog(null,
						"Name used by other states! Please pick another one!");
				return;
			}
		}
		this.name = name;
		module.distributeStateEditEvent(new StateEditEvent(this, false, false,
				true));
	}

	public Module getModule() {
		return this.module;
	}
}
