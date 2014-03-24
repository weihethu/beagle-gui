package elts.graph;

import gui.Environment;

import java.awt.Dimension;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import model.Module;
import model.automata.State;
import model.automata.Transition;

public class GraphObjectPlacer {
	private GraphXMLReader reader;
	private int nModule;
	private Map<String, Integer> nStateMap;
	private Dimension graphDim;
	private int currentModuleIndex;
	private Map<String, Integer> currentStateIndexMap;

	public GraphObjectPlacer() {
		this(null);
	}

	public GraphObjectPlacer(String graphFile) {
		if (graphFile == null)
			reader = null;
		else {
			reader = new GraphXMLReader();
			reader.read(graphFile);
		}
		graphDim = Environment.getInstance().getGraphDimension();
		nModule = 0;
		nStateMap = new HashMap<String, Integer>();
		currentModuleIndex = -1;
		this.currentStateIndexMap = new HashMap<String, Integer>();
	}

	public void setModuleNum(int num) {
		nModule = num;
	}

	public void setStateNum(String moduleName, int num) {
		nStateMap.put(moduleName, num);
	}

	public Point getModulePt(Module module) {
		Point pt = null;
		if (reader != null)
			pt = reader.getModulePt(module);
		if (pt == null) {
			if (graphDim != null) {
				currentModuleIndex++;
				return gridLayout(nModule, currentModuleIndex);
			} else
				pt = new Point(0, 0);
		}
		return pt;
	}

	public Point getStatePt(State state) {
		Point pt = null;
		if (reader != null)
			pt = reader.getStatePt(state);
		if (pt == null) {
			if (graphDim != null) {
				String moduleName = state.getModule().getName();
				int currentStateIndex = this.currentStateIndexMap
						.containsKey(moduleName) ? this.currentStateIndexMap
						.get(moduleName) : -1;
				currentStateIndex++;
				this.currentStateIndexMap.put(moduleName, currentStateIndex);
				int nState = this.nStateMap.containsKey(moduleName) ? this.nStateMap
						.get(moduleName) : 0;
				return gridLayout(nState, currentStateIndex);
			} else
				pt = new Point(0, 0);
		}
		return pt;
	}

	public Point getControlPt(Transition transition) {
		if (reader != null)
			return reader.getTransitionCtrlPt(transition);
		else
			return null;
	}

	public Point gridLayout(int total, int currentIndex) {
		if (total == 0)
			return new Point((int) (Math.random() * graphDim.width),
					(int) (Math.random() * graphDim.height));
		int rowSize = (int) Math.sqrt(total);
		int colSize = total / rowSize;
		if ((total % rowSize) != 0)
			colSize++;

		int colIndex = currentIndex % rowSize;
		int rowIndex = (currentIndex - colIndex) / rowSize;
		int x = (int) (((double) (colIndex + 1) / (double) (rowSize + 2)) * graphDim.width);
		int y = (int) (((double) (rowIndex + 1) / (double) (colSize + 2)) * graphDim.height);
		return new Point(x, y);
	}
}