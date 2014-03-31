package elts.graph;

import java.awt.Point;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Module;
import model.automata.State;
import model.automata.Transition;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

/**
 * class reading from graph file, and storing graph information for a model
 * 
 * @author Wei He
 * 
 */
public class GraphXMLReader {

	/**
	 * the map which associates a module and its locations
	 */
	private Map<String, Point> moduleLocationsMap = null;
	/**
	 * the map which associates a module name and the state locations in it
	 */
	private Map<String, Map<String, Point>> stateLocationsMap = null;
	/**
	 * the map which associates a module name and the transition control point
	 * locations in it
	 */
	private Map<String, Map<String, Point>> transitionControlPtsMap = null;

	/**
	 * read graph file
	 * 
	 * @param path
	 *            the path for graph file
	 */
	public void read(String path) {
		moduleLocationsMap = new HashMap<String, Point>();
		stateLocationsMap = new HashMap<String, Map<String, Point>>();
		transitionControlPtsMap = new HashMap<String, Map<String, Point>>();

		SAXBuilder builder = new SAXBuilder();

		try {
			Document doc = builder.build(new File(path));
			Element rootEle = doc.getRootElement();

			Element modulesEle = rootEle.getChild("Modules");
			// get graph information for each module
			if (modulesEle != null) {
				List<Element> moduleEles = modulesEle.getChildren("Module");
				if (moduleEles != null) {
					for (Element moduleEle : moduleEles)
						readModule(moduleEle);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			moduleLocationsMap.clear();
			stateLocationsMap.clear();
			transitionControlPtsMap.clear();
		}
	}

	/**
	 * read graph information for module
	 * 
	 * @param moduleElement
	 *            the XML element of the module
	 */
	private void readModule(Element moduleElement) {
		// read the module location
		String moduleName = moduleElement.getAttributeValue("Name");
		int x = Integer.parseInt(moduleElement.getAttributeValue("x"));
		int y = Integer.parseInt(moduleElement.getAttributeValue("y"));

		this.moduleLocationsMap.put(moduleElement.getAttributeValue("Name"),
				new Point(x, y));
		this.stateLocationsMap.put(moduleName, new HashMap<String, Point>());
		this.transitionControlPtsMap.put(moduleName,
				new HashMap<String, Point>());

		// read the states location
		Element statesEle = moduleElement.getChild("States");
		if (statesEle != null) {
			List<Element> stateEles = statesEle.getChildren("State");
			if (stateEles != null) {
				for (Element stateEle : stateEles)
					readState(moduleName, stateEle);
			}
		}

		// read the transition control points location
		Element transitionsEle = moduleElement.getChild("Transitions");
		if (transitionsEle != null) {
			List<Element> transitionEles = transitionsEle
					.getChildren("Transition");
			if (transitionEles != null) {
				for (Element transitionEle : transitionEles)
					readTransition(moduleName, transitionEle);
			}
		}
	}

	/**
	 * read graph information for state
	 * 
	 * @param moduleName
	 *            parent module name
	 * @param stateEle
	 *            the XML element for the state
	 */
	private void readState(String moduleName, Element stateEle) {
		this.stateLocationsMap.get(moduleName).put(
				stateEle.getAttributeValue("Name"),
				new Point(Integer.parseInt(stateEle.getAttributeValue("x")),
						Integer.parseInt(stateEle.getAttributeValue("y"))));
	}

	/**
	 * read graph information for transition control points
	 * 
	 * @param moduleName
	 *            parent module name
	 * @param transitionEle
	 *            the XML element for the transition
	 */
	private void readTransition(String moduleName, Element transitionEle) {
		String from = transitionEle.getAttributeValue("from");
		String to = transitionEle.getAttributeValue("to");
		this.transitionControlPtsMap.get(moduleName)
				.put(transitionCtrl2Str(from, to),
						new Point(Integer.parseInt(transitionEle
								.getAttributeValue("control_x")), Integer
								.parseInt(transitionEle
										.getAttributeValue("control_y"))));
	}

	/**
	 * get module location
	 * 
	 * @param module
	 *            module
	 * @return location point, if not available in graph file, return null
	 */
	public Point getModulePt(Module module) {
		return this.moduleLocationsMap.get(module.getName());
	}

	/**
	 * get state location
	 * 
	 * @param state
	 *            state
	 * @return location point, if not available in graph file, return null
	 */
	public Point getStatePt(State state) {
		if (this.stateLocationsMap.containsKey(state.getModule().getName())) {
			return this.stateLocationsMap.get(state.getModule().getName()).get(
					state.getName());
		} else
			return null;
	}

	/**
	 * get transition control point location
	 * 
	 * @param transition
	 *            transition
	 * @return location point, if not available in graph file, return null
	 */
	public Point getTransitionCtrlPt(Transition transition) {
		String moduleName = transition.getFromState().getModule().getName();
		if (this.transitionControlPtsMap.containsKey(moduleName)) {
			return this.transitionControlPtsMap.get(moduleName).get(
					transitionCtrl2Str(transition.getFromState().getName(),
							transition.getToState().getName()));
		} else
			return null;
	}

	/**
	 * get the internal string representation for a transition, used as keys in
	 * hashmap
	 * 
	 * @param from
	 *            the from state of the transition
	 * @param to
	 *            the to state of the transition
	 * @return the string representation
	 */
	private static String transitionCtrl2Str(String from, String to) {
		return "control_" + from + "_" + to;
	}
}
