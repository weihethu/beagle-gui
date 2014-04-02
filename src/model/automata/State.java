package model.automata;

import events.StateEditEvent;
import gui.drawers.DrawableObject;

import java.awt.Point;

import javax.swing.JOptionPane;

import model.Module;
import utils.StringUtil;

/**
 * state, which corresponds to locations in ELTS module
 * 
 * @author Wei He
 * 
 */
public class State extends DrawableObject {
	/**
	 * point
	 */
	private Point point;
	/**
	 * parent module
	 */
	private Module module;
	/**
	 * state name
	 */
	private String name = null;
	/**
	 * selected status
	 */
	private boolean selected = false;

	/**
	 * constructor
	 * 
	 * @param name
	 *            state name
	 * @param pt
	 *            point
	 * @param module
	 *            parent module
	 */
	public State(String name, Point pt, Module module) {
		this.point = pt;
		this.name = name;
		this.module = module;
		this.selected = false;
	}

	/**
	 * get point
	 * 
	 * @return point
	 */
	public Point getPoint() {
		return this.point;
	}

	/**
	 * set point
	 * 
	 * @param point
	 *            point
	 */
	public void setPoint(Point point) {
		this.point = point;
		module.distributeStateEditEvent(new StateEditEvent(this,
				StateEditEvent.EventType.MOVE));
	}

	/**
	 * get state name
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
	 *            status
	 */
	public void setSelect(boolean status) {
		selected = status;
	}

	/**
	 * whether this state is selected
	 * 
	 * @return a boolean indicator
	 */
	public boolean isSelected() {
		return this.selected;
	}

	/**
	 * set state name
	 * 
	 * @param name
	 *            name
	 */
	public void setName(String name) {
		if (this.name.equals(name))
			return;
		// check name is valid
		if (!StringUtil.validIdentifier(name)) {
			JOptionPane.showMessageDialog(null,
					"Invalid name! Please pick another one!");
			return;
		}
		// check no states with same name in module
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
		module.distributeStateEditEvent(new StateEditEvent(this,
				StateEditEvent.EventType.NAME));
	}

	/**
	 * get parent module
	 * 
	 * @return module
	 */
	public Module getModule() {
		return this.module;
	}
}
