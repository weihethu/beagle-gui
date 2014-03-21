package model.automata;

import events.TransitionEditEvent;
import gui.drawers.DrawableObject;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import utils.Pair;

public class Transition extends DrawableObject {
	private State fromState, toState;
	private boolean selected = false;
	private Point controlPt;
	private Map<String, Pair<String, String>> labelsMap = null;

	public Transition(State from, State to) {
		this.setFromState(from);
		this.setToState(to);
		controlPt = null;
		labelsMap = new HashMap<String, Pair<String, String>>();
	}

	public void dataChange() {
		fromState.getModule().distributeTransitionEditEvent(
				new TransitionEditEvent(this,
						TransitionEditEvent.EventType.DATA));
	}

	public void clear() {
		this.labelsMap.clear();
	}

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

	public static String regularizeLabelsStr(String labelsStr) {
		if (labelsStr == null)
			return null;
		String[] labels = labelsStr.split(",");
		String resultStr = "";
		for (int i = 0; i < labels.length; i++) {
			if (i > 0)
				resultStr += ", ";
			resultStr += labels[i].trim();
		}
		return resultStr.trim();
	}

	public void addLabel(String label) {
		label = regularizeLabelsStr(label);
		if (label != null && !label.isEmpty())
			this.labelsMap.put(label, new Pair<String, String>(null, null));
	}

	public void addLabel(String label, String guard, String action) {
		label = regularizeLabelsStr(label);
		if (label != null && !label.isEmpty())
			this.labelsMap.put(label, new Pair<String, String>(guard, action));
	}

	public Pair<String, String> getLabel(String label) {
		return labelsMap.get(label);
	}

	public Set<String> getLabels() {
		return this.labelsMap.keySet();
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

	public void setControl(Point pt) {
		this.controlPt = pt;
	}
}
