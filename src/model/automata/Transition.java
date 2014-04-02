package model.automata;

import events.TransitionEditEvent;
import gui.drawers.DrawableObject;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import utils.Pair;

/**
 * transition
 * 
 * @author Wei He
 * 
 */
public class Transition extends DrawableObject {
	/**
	 * from state
	 */
	private State fromState;
	/**
	 * to state
	 */
	private State toState;
	/**
	 * selected status
	 */
	private boolean selected = false;
	/**
	 * control point
	 */
	private Point controlPt;
	/**
	 * maps which associates labels to <guards, actions>
	 */
	private Map<String, Pair<String, String>> labelsMap = null;

	/**
	 * constructor
	 * 
	 * @param from
	 *            from state
	 * @param to
	 *            to state
	 */
	public Transition(State from, State to) {
		this.setFromState(from);
		this.setToState(to);
		controlPt = null;
		labelsMap = new HashMap<String, Pair<String, String>>();
	}

	/**
	 * called when transition's data edited
	 */
	public void dataChange() {
		fromState.getModule().distributeTransitionEditEvent(
				new TransitionEditEvent(this,
						TransitionEditEvent.EventType.DATA));
	}

	/**
	 * clear labels
	 */
	public void clear() {
		this.labelsMap.clear();
	}

	/**
	 * get description for transitions in the form of labels1; labels2;
	 * labels3...
	 * 
	 * @return descriptions
	 */
	public String getDescription() {
		Set<String> labels = labelsMap.keySet();
		String desc = "";
		for (String label : labels) {
			desc += (label + ";");
		}
		if (!desc.isEmpty())
			desc = desc.substring(0, desc.length() - 1);
		return desc;
	}

	/**
	 * regularize labels representation
	 * 
	 * @param labelsStr
	 *            labels
	 * @return regularized string
	 */
	public static String regularizeLabelsStr(String labelsStr) {
		if (labelsStr == null)
			return null;
		String[] labels = labelsStr.split("(\\s)*,(\\s)*");
		String resultStr = "";
		for (int i = 0; i < labels.length; i++) {
			if (i > 0)
				resultStr += ", ";
			resultStr += labels[i].trim();
		}
		return resultStr.trim();
	}

	/**
	 * add labels with guards & actions not set
	 * 
	 * @param label
	 *            labels
	 */
	public void addLabel(String label) {
		label = regularizeLabelsStr(label);
		if (label != null && !label.isEmpty())
			this.labelsMap.put(label, new Pair<String, String>(null, null));
	}

	/**
	 * add labels with guards & actions associated
	 * 
	 * @param label
	 *            labels
	 * @param guard
	 *            guard condition
	 * @param action
	 *            actions
	 */
	public void addLabel(String label, String guard, String action) {
		label = regularizeLabelsStr(label);
		if (label != null && !label.isEmpty())
			this.labelsMap.put(label, new Pair<String, String>(guard, action));
	}

	/**
	 * get guard & actions pair for labels
	 * 
	 * @param label
	 *            labels
	 * @return
	 */
	public Pair<String, String> getLabel(String label) {
		return labelsMap.get(label);
	}

	/**
	 * get all labels
	 * 
	 * @return labels set
	 */
	public Set<String> getLabels() {
		return this.labelsMap.keySet();
	}

	/**
	 * get from state
	 * 
	 * @return from state
	 */
	public State getFromState() {
		return fromState;
	}

	/**
	 * set from state
	 * 
	 * @param fromState
	 *            from state
	 */
	public void setFromState(State fromState) {
		this.fromState = fromState;
	}

	/**
	 * get to state
	 * 
	 * @return to state
	 */
	public State getToState() {
		return toState;
	}

	/**
	 * set to state
	 * 
	 * @param toState
	 *            to state
	 */
	public void setToState(State toState) {
		this.toState = toState;
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
	 * get control point of curved arrow
	 * 
	 * @return control point
	 */
	public Point getControl() {
		return this.controlPt;
	}

	/**
	 * set control point of curved arrow
	 * 
	 * @param pt
	 *            point
	 */
	public void setControl(Point pt) {
		this.controlPt = pt;
	}
}
