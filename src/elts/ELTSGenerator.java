package elts;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import elts.graph.GraphObjectPlacer;
import model.Model;
import model.Module;
import model.automata.State;
import model.automata.Transition;
import utils.Pair;

public class ELTSGenerator {
	private static String EMPTY_LINE = "";
	private static String TAB = "    ";
	private static String NEW_LINE = "\n";

	public static String insertTab(String line) {
		if (line.trim().equals(EMPTY_LINE))
			return line;
		else
			return TAB + line;
	}

	public static String getModelText(Model model) {
		if (model == null)
			return "";
		List<String> lines = generate(model);
		String text = "";
		for (int i = 0; i < lines.size(); i++) {
			text += lines.get(i);
			if (i != lines.size() - 1)
				text += NEW_LINE;
		}
		return text;
	}

	public static List<String> generate(Model model) {
		List<String> lines = new ArrayList<String>();
		lines.add("system");
		for (Module module : model.getModules()) {
			List<String> moduleLines = generate(module);
			for (String moduleLine : moduleLines) {
				lines.add(insertTab(moduleLine));
			}

			lines.add(EMPTY_LINE);
		}
		lines.add("end");
		return lines;
	}

	public static List<String> generate(Module module) {
		List<String> lines = new ArrayList<String>();
		lines.add("module " + module.getName());

		// member declarations
		String memberDeclaration = module.getVarDeclaration();
		if (memberDeclaration != null && !memberDeclaration.isEmpty()) {
			String[] declLines = memberDeclaration.split("\n");
			for (String line : declLines) {
				if (!line.trim().isEmpty())
					lines.add(TAB + line.trim());
			}
			lines.add(EMPTY_LINE);
		}

		// declare locations
		State[] states = module.getStates();
		if (states.length > 0) {
			String line = (TAB + "location");
			for (int i = 0; i < states.length; i++) {
				if (i > 0)
					line += ",";
				line += " " + states[i].getName();
			}
			lines.add(line + ";");
			lines.add(EMPTY_LINE);
		}

		// declare labels
		Transition transitions[] = module.getTransitions();
		if (transitions.length > 0) {
			Set<String> labelsSet = new HashSet<String>();

			for (Transition transition : transitions) {
				for (String labelStr : transition.getLabels()) {
					String labels[] = labelStr.split(",");
					for (String label : labels)
						labelsSet.add(label);
				}
			}

			if (!labelsSet.isEmpty()) {
				String line = TAB + "label";
				String[] labels = labelsSet.toArray(new String[0]);
				for (int i = 0; i < labels.length; i++) {
					if (i > 0)
						line += ",";
					line += " " + labels[i];
				}
				lines.add(line + ";");
				lines.add(EMPTY_LINE);
			}
		}

		// init
		if (module.getInitialState() != null) {
			String line = TAB + "init " + module.getInitialState().getName();
			String initAction = module.getInitialAction();
			if (initAction != null && !initAction.isEmpty()) {
				line += " do {";
				lines.add(line);
				String[] initActionLines = initAction.split("\n");
				for (String curLine : initActionLines) {
					if (!curLine.trim().isEmpty())
						lines.add(TAB + TAB + curLine.trim());
				}
				lines.add(TAB + "};");
			} else {
				line += ";";
				lines.add(line);
			}
			lines.add(EMPTY_LINE);
		}

		// transitions
		for (Transition transition : transitions) {
			List<String> transitionLines = generate(transition);
			for (String line : transitionLines) {
				lines.add(insertTab(line));
			}
			if (transitionLines.size() > 0)
				lines.add(EMPTY_LINE);
		}
		lines.add("end");
		return lines;
	}

	public static List<String> generate(Transition transition) {
		List<String> lines = new ArrayList<String>();
		Set<String> labelStrs = transition.getLabels();
		for (String labelStr : labelStrs) {
			String line = "from " + transition.getFromState().getName()
					+ " to " + transition.getToState().getName() + " on "
					+ labelStr.trim();
			Pair<String, String> guard_action = transition.getLabel(labelStr);
			String guard = guard_action.getFirst();
			String action = guard_action.getSecond();
			if (guard != null && !guard.trim().isEmpty()) {
				line += (" provided (" + guard.trim() + ")");
			}
			if (action != null && !action.trim().isEmpty()) {
				lines.add(line + " do {");
				String[] actionLines = action.trim().split("\n");
				for (String curLine : actionLines) {
					if (!curLine.trim().isEmpty())
						lines.add(TAB + curLine.trim());
				}
				lines.add("};");
			} else
				lines.add(line + ";");
		}
		return lines;
	}
}
