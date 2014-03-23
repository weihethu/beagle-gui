package elts.graph;

import java.awt.Point;

import model.Module;
import model.automata.State;
import model.automata.Transition;

public class GraphObjectPlacer {
	private GraphXMLReader reader;

	public GraphObjectPlacer() {
		reader = null;
	}

	public GraphObjectPlacer(String graphFile) {
		reader = new GraphXMLReader();
		reader.read(graphFile);
	}

	public Point getModulePt(Module module) {
		Point pt = null;
		if (reader != null)
			pt = reader.getModulePt(module);
		if (pt == null)
			pt = new Point(0, 0);
		return pt;
	}

	public Point getStatePt(State state) {
		Point pt = null;
		if (reader != null)
			pt = reader.getStatePt(state);
		if (pt == null)
			pt = new Point(0, 0);
		return pt;
	}

	public Point getControlPt(Transition transition) {
		if (reader != null)
			return reader.getTransitionCtrlPt(transition);
		else
			return null;
	}
}
