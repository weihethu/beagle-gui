package gui.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.filechooser.FileFilter;

import model.Model;
import model.Module;
import model.automata.State;
import model.automata.Transition;
import utils.BeagleInvoker;
import utils.Config;
import utils.Pair;
import elts.ELTSParser;
import elts.graph.GraphObjectPlacer;
import gui.Environment;

public class OpenAction extends AbstractAction {

	private static String ELTS_EXT = Config.getInstance().get_elts_ext();
	private static String GRAPH_EXT = Config.getInstance().get_graph_ext();

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
				return "model file(*." + ELTS_EXT + ")";
			}

			@Override
			public boolean accept(File f) {
				return f.isDirectory() || f.getName().endsWith("." + ELTS_EXT);
			}

		});
		if (JFileChooser.APPROVE_OPTION == fileChooser
				.showOpenDialog((Component) event.getSource())) {
			File selectedFile = fileChooser.getSelectedFile();
			final ProgressMonitor monitor = new ProgressMonitor(
					(Component) event.getSource(), "Waiting for "
							+ selectedFile.getName() + " to open!", null,
					OpenFileActivity.INIT_DONE, OpenFileActivity.ALL_DONE);

			final OpenFileActivity openActivity = new OpenFileActivity(
					selectedFile);
			openActivity.execute();

			Timer cancelMonitor = new Timer(500, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					if (monitor.isCanceled()) {
						openActivity.cancel();
					} else if (openActivity.isDone()) {
						monitor.close();
					} else {
						int progress = openActivity.getProgress();
						if (progress == OpenFileActivity.INIT_DONE)
							monitor.setNote("tranlating elts to xml...");
						else if (progress == OpenFileActivity.ELT2XML_DONE)
							monitor.setNote("parsing xml...");
						else if (progress == OpenFileActivity.PARSEXML_DONE)
							monitor.setNote("adjusting positions...");
						else
							monitor.setNote("all done");
						monitor.setProgress(progress);
					}
				}
			});
			cancelMonitor.start();
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

	private class OpenFileActivity extends SwingWorker<Void, Integer> {

		public static final int INIT_DONE = 0;
		public static final int ELT2XML_DONE = 50;
		public static final int PARSEXML_DONE = 70;
		public static final int ALL_DONE = 100;

		File selectedFile = null;
		private Model model = null;
		private Pair<Integer, String> elt2XMLRes = null;

		public OpenFileActivity(File file) {
			this.selectedFile = file;
			model = null;
			elt2XMLRes = null;
		}

		@Override
		protected Void doInBackground() throws Exception {
			setProgress(INIT_DONE);
			elt2XMLRes = BeagleInvoker.getIntance().elts2XML(
					selectedFile.getPath());

			setProgress(ELT2XML_DONE);
			if (elt2XMLRes.getFirst() == 0) {
				String xmlContent = elt2XMLRes.getSecond();
				model = ELTSParser.parseModel(xmlContent);
				setProgress(PARSEXML_DONE);
				if (model != null) {
					adjustLocations(
							model,
							OpenAction.this.substitutePathExt(
									selectedFile.getPath(), GRAPH_EXT));

				}
			}
			return null;
		}

		@Override
		protected void done() {

			if (elt2XMLRes != null) {
				if (elt2XMLRes.getFirst() == 0) {
					if (model != null) {
						Environment.getInstance().setModel(model);
						Environment.getInstance().setCurrentPath(
								selectedFile.getPath());
					} else {
						JOptionPane
								.showMessageDialog(
										Environment.getInstance(),
										"Beagle executable returns invalid XML format!",
										"Parsing Error",
										JOptionPane.ERROR_MESSAGE);
					}
				} else {
					String errorMsg = elt2XMLRes.getSecond();
					JOptionPane.showMessageDialog(Environment.getInstance(),
							"Errors while parsing " + selectedFile.getName()
									+ "!\n" + errorMsg, "Parsing Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
			setProgress(ALL_DONE);
		}

		public void cancel() {
			BeagleInvoker.getIntance().terminateProcess();
		}
	}
}
