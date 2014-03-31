package elts;

import java.awt.Point;
import java.io.StringReader;
import java.util.List;

import model.Model;
import model.Module;
import model.automata.State;
import model.automata.Transition;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

/**
 * class for parsing XML model representation
 * 
 * @author Wei He
 * 
 */
public class ELTSParser {

	/**
	 * parse actions representation in XML format
	 * 
	 * @param actionsElement
	 *            the XML element for actions
	 * @return strings for actions, if multiple action, separate by new lines
	 */
	private static String parseActions(Element actionsElement) {
		if (actionsElement == null)
			return null;
		else {
			List<Element> actionEles = actionsElement.getChildren("Action");
			if (actionEles == null || actionEles.isEmpty())
				return "";
			String actions = "";
			for (int i = 0; i < actionEles.size(); i++) {
				Element actionEle = actionEles.get(i);
				actions += actionEle.getText();
				if (i != actionEles.size() - 1)
					actions += "\n";
			}
			return actions;
		}
	}

	/**
	 * parse init representation in XML format, and add to module
	 * 
	 * @param initElement
	 *            the XML element for init
	 * @param module
	 *            parent module
	 */
	private static void parseInit(Element initElement, Module module) {
		if (initElement == null)
			return;
		String initState = initElement.getAttributeValue("Location");
		module.setInitialState(module.getStateByName(initState));
		module.setInitialAction(parseActions(initElement.getChild("Actions")));
	}

	/**
	 * parse labels representation in XML format
	 * 
	 * @param labelsElement
	 *            the XML element for labels
	 * @return string for labels declaration, if multiple labels, separate by
	 *         commas
	 */
	private static String parseLabels(Element labelsElement) {
		if (labelsElement == null)
			return null;
		String labelsStr = "";
		List<Element> labelEles = labelsElement.getChildren("Label");
		if (labelEles != null && !labelEles.isEmpty()) {
			for (int i = 0; i < labelEles.size(); i++) {
				if (i > 0)
					labelsStr += ", ";
				labelsStr += labelEles.get(i).getAttributeValue("Name");
			}
		}
		return labelsStr;
	}

	/**
	 * parse transitions representation in XML format, and add them in module
	 * 
	 * @param transitionsElement
	 *            the XML element for transitions
	 * @param module
	 *            parent module
	 */
	private static void parseTransitions(Element transitionsElement,
			Module module) {
		if (transitionsElement == null)
			return;
		List<Element> transitionEles = transitionsElement
				.getChildren("Transition");
		if (transitionEles == null || transitionEles.isEmpty())
			return;

		for (Element transitionEle : transitionEles) {
			State fromState = module.getStateByName(transitionEle
					.getAttributeValue("from"));
			State toState = module.getStateByName(transitionEle
					.getAttributeValue("to"));
			if (fromState != null && toState != null) {
				String guard = null;
				if (transitionEle.getAttribute("guard") != null) {
					guard = transitionEle.getAttributeValue("guard");
					if (guard.startsWith("(") && guard.endsWith(")")) {
						guard = guard.substring(1, guard.length() - 1);
					}
				}
				String labels = parseLabels(transitionEle.getChild("Labels"));
				String actions = parseActions(transitionEle.getChild("Actions"));
				Transition transition = module.getTransitionFromStateToState(
						fromState, toState);
				if (transition != null) {
					transition.addLabel(labels, guard, actions);
				} else {
					transition = new Transition(fromState, toState);
					transition.addLabel(labels, guard, actions);
					module.addTransition(transition);
				}
			}
		}
	}

	/**
	 * parse locations representation in XML format, and add them in module
	 * 
	 * @param locationsElement
	 *            the XML element for locations
	 * @param module
	 *            parent module module
	 */
	private static void parseLocations(Element locationsElement, Module module) {
		if (locationsElement == null)
			return;
		List<Element> locationEles = locationsElement.getChildren("Location");
		if (locationEles == null || locationEles.isEmpty())
			return;

		for (Element locationEle : locationEles) {
			String name = locationEle.getAttributeValue("Name");
			module.addState(new State(name, new Point(0, 0), module));
		}
	}

	/**
	 * parse variables representation in XML format
	 * 
	 * @param variablesElement
	 *            the XML element for variables
	 * @return string for variables declaration
	 */
	private static String parseVariables(Element variablesElement) {
		if (variablesElement != null) {
			String varsDecl = "";
			List<Element> varElements = variablesElement
					.getChildren("Variable");
			if (!varElements.isEmpty()) {
				varsDecl = "bool ";
				for (int i = 0; i < varElements.size(); i++) {
					if (i > 0)
						varsDecl += ", ";
					Element varEle = varElements.get(i);
					String name = varEle.getAttributeValue("Name");
					varsDecl += name;
					if (varEle.getAttribute("Value") != null) {
						varsDecl += (" = " + varEle.getAttributeValue("Value"));
					}
				}
				varsDecl += ";";
			}
			return varsDecl;
		} else
			return null;
	}

	/**
	 * parse module representation in XML format
	 * 
	 * @param moduleElement
	 *            the XML element for module
	 * @param parent
	 *            the parent model
	 * @return module
	 */
	private static Module parseModule(Element moduleElement, Model parent) {
		Module module = new Module(moduleElement.getAttributeValue("Name"),
				new Point(0, 0), parent);
		module.setVarDeclaration(parseVariables(moduleElement
				.getChild("Variables")));
		parseLocations(moduleElement.getChild("Locations"), module);
		parseInit(moduleElement.getChild("Init"), module);
		parseTransitions(moduleElement.getChild("Transitions"), module);
		return module;
	}

	/**
	 * parse model representation in XML format
	 * 
	 * @param xml
	 *            input
	 * @return model
	 */
	public static Model parseModel(String xml) {
		Model model = new Model();
		SAXBuilder builder = new SAXBuilder();
		Document doc;
		try {
			doc = builder.build(new StringReader(xml));
			Element root = doc.getRootElement();
			Element modulesEle = root.getChild("Modules");
			// parse modules
			if (modulesEle != null) {
				List<Element> moduleEles = modulesEle.getChildren("Module");
				if (moduleEles != null && !moduleEles.isEmpty()) {
					for (Element moduleEle : moduleEles) {
						Module module = parseModule(moduleEle, model);
						model.addModule(module);
					}
				}
			}
			// parse properties
			Element propsEle = root.getChild("Properties");
			if (propsEle != null) {
				List<Element> propEles = propsEle.getChildren("Property");
				if (propEles != null && !propEles.isEmpty()) {
					for (Element propEle : propEles) {
						model.addProperty(propEle.getText());
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			model = null;
		}
		return model;
	}
}
