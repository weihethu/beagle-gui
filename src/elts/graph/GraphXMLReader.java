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

public class GraphXMLReader {

	private Map<String, Point> moduleLocationsMap = null;
	private Map<String, Map<String, Point>> stateLocationsMap = null;
	private Map<String, Map<String, Point>> transitionControlPtsMap = null;

	public void read(String path) {
		moduleLocationsMap = new HashMap<String, Point>();
		stateLocationsMap = new HashMap<String, Map<String, Point>>();
		transitionControlPtsMap = new HashMap<String, Map<String, Point>>();

		SAXBuilder builder = new SAXBuilder();

		try {
			Document doc = builder.build(new File(path));
			Element rootEle = doc.getRootElement();

			Element modulesEle = rootEle.getChild("Modules");
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

	private void readModule(Element moduleElement) {
		String moduleName = moduleElement.getAttributeValue("Name");
		int x = Integer.parseInt(moduleElement.getAttributeValue("x"));
		int y = Integer.parseInt(moduleElement.getAttributeValue("y"));

		this.moduleLocationsMap.put(moduleElement.getAttributeValue("Name"),
				new Point(x, y));
		this.stateLocationsMap.put(moduleName, new HashMap<String, Point>());
		this.transitionControlPtsMap.put(moduleName,
				new HashMap<String, Point>());

		Element statesEle = moduleElement.getChild("States");
		if (statesEle != null) {
			List<Element> stateEles = statesEle.getChildren("State");
			if (stateEles != null) {
				for (Element stateEle : stateEles)
					readState(moduleName, stateEle);
			}
		}
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

	private void readState(String moduleName, Element stateEle) {
		this.stateLocationsMap.get(moduleName).put(
				stateEle.getAttributeValue("Name"),
				new Point(Integer.parseInt(stateEle.getAttributeValue("x")),
						Integer.parseInt(stateEle.getAttributeValue("y"))));
	}

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

	public Point getModulePt(Module module) {
		return this.moduleLocationsMap.get(module.getName());
	}

	public Point getStatePt(State state) {
		if (this.stateLocationsMap.containsKey(state.getModule().getName())) {
			return this.stateLocationsMap.get(state.getModule().getName()).get(
					state.getName());
		} else
			return null;
	}

	private static String transitionCtrl2Str(String from, String to) {
		return "control_" + from + "_" + to;
	}

	public Point getTransitionCtrlPt(Transition transition) {
		String moduleName = transition.getFromState().getModule().getName();
		if (this.transitionControlPtsMap.containsKey(moduleName)) {
			return this.transitionControlPtsMap.get(moduleName).get(
					transitionCtrl2Str(transition.getFromState().getName(),
							transition.getToState().getName()));
		} else
			return null;
	}
}
