package elts.graph;

import java.io.FileOutputStream;
import java.io.IOException;

import model.Model;
import model.Module;
import model.automata.State;
import model.automata.Transition;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.XMLOutputter;

public class GraphXMLSaver {
	public static void save(Model m, String path) {
		Element rootEle = new Element("Graph");
		Document doc = new Document(rootEle);

		Element modulesEle = new Element("Modules");
		for (Module module : m.getModules()) {
			modulesEle.addContent(getXML(module));
		}
		rootEle.addContent(modulesEle);

		XMLOutputter XMLOut = new XMLOutputter();
		try {
			XMLOut.output(doc, new FileOutputStream(path));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private static Element getXML(Module module) {
		Element moduleEle = new Element("Module");
		moduleEle.setAttribute("Name", module.getName());
		moduleEle.setAttribute("x", String.valueOf(module.getPoint().x));
		moduleEle.setAttribute("y", String.valueOf(module.getPoint().y));

		Element statesEle = new Element("States");
		for (State state : module.getStates()) {
			statesEle.addContent(getXML(state));
		}
		moduleEle.addContent(statesEle);

		Element transitionsEle = new Element("Transitions");
		for (Transition transition : module.getTransitions()) {
			if (transition.getControl() != null)
				transitionsEle.addContent(getXML(transition));
		}
		moduleEle.addContent(transitionsEle);

		return moduleEle;
	}

	private static Element getXML(State state) {
		Element stateEle = new Element("State");
		stateEle.setAttribute("Name", state.getName());
		stateEle.setAttribute("x", String.valueOf(state.getPoint().x));
		stateEle.setAttribute("y", String.valueOf(state.getPoint().y));
		return stateEle;
	}

	private static Element getXML(Transition transition) {
		Element transitionEle = new Element("Transition");
		transitionEle.setAttribute("from", transition.getFromState().getName());
		transitionEle.setAttribute("to", transition.getToState().getName());
		if (transition.getControl() != null) {
			transitionEle.setAttribute("control_x",
					String.valueOf(transition.getControl().x));
			transitionEle.setAttribute("control_y",
					String.valueOf(transition.getControl().y));
		} else {
			transitionEle.setAttribute("control_x", "0");
			transitionEle.setAttribute("control_y", "0");
		}
		return transitionEle;
	}
}
