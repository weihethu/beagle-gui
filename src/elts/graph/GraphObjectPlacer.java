package elts.graph;

import gui.Environment;

import java.awt.Dimension;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import model.Module;
import model.automata.State;
import model.automata.Transition;

/**
 * class for placing modules, states and transitions of a model on canvas
 * 
 * @author Wei He
 * 
 */
public class GraphObjectPlacer {
	/**
	 * graph xml reader, set to null if we don't have associated graph file
	 */
	private GraphXMLReader reader;
	/**
	 * the number of modules
	 */
	private int nModule;
	/**
	 * the map which associates a module and the number of states in it
	 */
	private Map<String, Integer> nStateMap;
	/**
	 * the dimension for graph canvas
	 */
	private Dimension graphDim;
	/**
	 * the current module index in model to be placed
	 */
	private int currentModuleIndex;
	/**
	 * the map which associates a module name and the state index in it to be
	 * placed
	 */
	private Map<String, Integer> currentStateIndexMap;

	/**
	 * constructor, no graph file provided
	 */
	public GraphObjectPlacer() {
		this(null);
	}

	/**
	 * constructor, graph file provided
	 * 
	 * @param graphFile
	 *            graph file in XML format
	 */
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

	/**
	 * add priori knowledge, set the number of modules in model
	 * 
	 * @param num
	 *            the number of modules
	 */
	public void setModuleNum(int num) {
		nModule = num;
	}

	/**
	 * add priori knowledge, set the number of states in module
	 * 
	 * @param moduleName
	 *            the module name
	 * @param num
	 *            the number of states
	 */
	public void setStateNum(String moduleName, int num) {
		nStateMap.put(moduleName, num);
	}

	/**
	 * get the location of a module on the model canvas
	 * 
	 * @param module
	 *            state
	 * @return location point
	 */
	public Point getModulePt(Module module) {
		Point pt = null;
		// first consult if the graph file knows it
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

	/**
	 * get the location of a state on the module canvas
	 * 
	 * @param state
	 *            state
	 * @return location point
	 */
	public Point getStatePt(State state) {
		Point pt = null;
		// first consult if the graph file knows it
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

	/**
	 * get the location of a control point for transition on the module canvas
	 * 
	 * @param transition
	 *            transition
	 * @return location point
	 */
	public Point getControlPt(Transition transition) {
		// first consult if the graph file knows it
		if (reader != null)
			return reader.getTransitionCtrlPt(transition);
		else
			return null;
	}

	/**
	 * return the location for a object in a grid layout, this is used as the
	 * default method for placing modules/states if no graph file provided
	 * 
	 * @param total
	 *            the total objects number
	 * @param currentIndex
	 *            the current object index
	 * @return location
	 */
	private Point gridLayout(int total, int currentIndex) {
		// if no priori knowledge, we just return a random position
		if (total == 0)
			return new Point((int) (Math.random() * graphDim.width),
					(int) (Math.random() * graphDim.height));
		// calculate the row size & col size of the grid layout
		int rowSize = (int) Math.sqrt(total);
		int colSize = total / rowSize;
		if ((total % rowSize) != 0)
			colSize++;

		// calculate the row index & col index of the current object
		int colIndex = currentIndex % rowSize;
		int rowIndex = (currentIndex - colIndex) / rowSize;
		int x = (int) (((double) (colIndex + 1) / (double) (rowSize + 2)) * graphDim.width);
		int y = (int) (((double) (rowIndex + 1) / (double) (colSize + 2)) * graphDim.height);
		return new Point(x, y);
	}
}