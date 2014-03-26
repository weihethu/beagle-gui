package gui.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import model.Model;
import model.Module;
import model.automata.State;
import model.automata.Transition;
import utils.BeagleInvoker;
import utils.Pair;
import elts.ELTSParser;
import elts.graph.GraphObjectPlacer;
import gui.Environment;

public class OpenAction extends AbstractAction {

	private static String ELTS_EXT = "elt";

	public OpenAction() {
		super("Open", null);
		putValue("AcceleratorKey",
				KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK));
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (JOptionPane.showConfirmDialog((Component) event.getSource(),
				"All unsaved content will be lost! Do you want to continue?",
				"Warning", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION)
			return;
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setAcceptAllFileFilterUsed(false);

		if (Environment.getInstance().getCurrentPath() != null) {
			File currentFilePath = new File(Environment.getInstance()
					.getCurrentPath());
			fileChooser.setCurrentDirectory(currentFilePath.getParentFile());
		}

		fileChooser.addChoosableFileFilter(new FileFilter() {

			@Override
			public String getDescription() {
				return "model file(*.elt)";
			}

			@Override
			public boolean accept(File f) {
				return f.isDirectory() || f.getName().endsWith("." + ELTS_EXT);
			}

		});
		if (JFileChooser.APPROVE_OPTION == fileChooser
				.showOpenDialog((Component) event.getSource())) {
			File selectedFile = fileChooser.getSelectedFile();
			Pair<Integer, String> result = BeagleInvoker.getIntance().elts2XML(
					selectedFile.getPath());

			if (result.getFirst() == 0) {
				String xmlContent = result.getSecond();
				Model model = ELTSParser.parseModel(xmlContent);
				if (model != null) {
					this.adjustLocations(model, this.substitutePathExt(
							selectedFile.getPath(), "xml"));
					Environment.getInstance().setModel(model);
					Environment.getInstance().setCurrentPath(
							selectedFile.getPath());
				} else {
					JOptionPane.showMessageDialog(
							(Component) event.getSource(),
							"Beagle executable returns invalid XML format!",
							"Parsing Error", JOptionPane.ERROR_MESSAGE);
				}
			} else {
				String errorMsg = result.getSecond();
				JOptionPane.showMessageDialog((Component) event.getSource(),
						"Errors while parsing " + selectedFile.getName()
								+ "!\n" + errorMsg, "Parsing Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void adjustLocations(Model model, String graphFilePath) {
		GraphObjectPlacer objPlacer = null;
		File graphFile = new File(graphFilePath);
		if (graphFile.exists() && graphFile.isFile()) {
			objPlacer = new GraphObjectPlacer(graphFilePath);
		} else
			objPlacer = new GraphObjectPlacer();

		// add prior knowledge
		objPlacer.setModuleNum(model.getModules().length);
		for (Module module : model.getModules())
			objPlacer.setStateNum(module.getName(), module.getStates().length);

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
