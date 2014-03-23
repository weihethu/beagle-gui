package actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import model.Model;
import model.Module;
import model.automata.State;
import model.automata.Transition;
import elts.ELTSParser;
import elts.graph.GraphObjectPlacer;
import gui.Environment;

public class OpenAction extends AbstractAction {

	private static String ELTS_EXT = "elt";

	public OpenAction() {
		super("Open", null);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		// TODO: add warning to save current
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setAcceptAllFileFilterUsed(false);

		fileChooser.addChoosableFileFilter(new FileFilter() {

			@Override
			public String getDescription() {
				return "model file(*.elt)";
			}

			@Override
			public boolean accept(File f) {
				return f.getName().endsWith("." + ELTS_EXT);
			}

		});
		if (JFileChooser.APPROVE_OPTION == fileChooser
				.showOpenDialog((Component) event.getSource())) {
			File selectedFile = fileChooser.getSelectedFile();
			String xmlContent = getModelXML(selectedFile);

			Model model = ELTSParser.parseModel(xmlContent);
			// adjust positions if additional graph file provided
			this.adjustLocations(model,
					this.substitutePathExt(selectedFile.getPath(), "xml"));
			if (model != null) {
				Environment.getInstance().setModel(model);
			}
			Environment.getInstance().setCurrentPath(selectedFile.getPath());
		}
	}

	private String getModelXML(File file) {
		String text = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader("model.xml"));
			String s;
			while ((s = br.readLine()) != null) {
				text += (s + "\n");
			}
			br.close();
		} catch (IOException e) {
			text = null;
			e.printStackTrace();
		}
		return text;
	}

	private void adjustLocations(Model model, String graphFilePath) {
		GraphObjectPlacer objPlacer = null;
		File graphFile = new File(graphFilePath);
		if (graphFile.exists() && graphFile.isFile()) {
			objPlacer = new GraphObjectPlacer(graphFilePath);
			// TODO: add knowledge
		} else
			objPlacer = new GraphObjectPlacer();

		for (Module module : model.getModules()) {
			module.setPoint(objPlacer.getModulePt(module));
			for (State state : module.getStates()) {
				state.setPoint(objPlacer.getStatePt(state));
			}

			for (Transition transition : module.getTransitions()) {
				transition.setControl(objPlacer.getControlPt(transition));
			}
		}
	}

	private String substitutePathExt(String path, String ext) {
		int position = path.lastIndexOf('.');
		if (position >= 0)
			return path.substring(0, position + 1) + ext;
		else
			return path + "." + ext;
	}
}
