package gui.actions;

import elts.ELTSParser;
import elts.graph.GraphObjectPlacer;
import gui.Environment;

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
import utils.StringUtil;

/**
 * the action for the open menu item
 * 
 * @author Wei He
 * 
 */
public class OpenAction extends AbstractAction {

	/**
	 * the extension for model file
	 */
	private static String ELTS_EXT = Config.getInstance().get_elts_ext();
	/**
	 * the extension for graph file
	 */
	private static String GRAPH_EXT = Config.getInstance().get_graph_ext();

	/**
	 * constructor
	 */
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

		// set the default open path to the parent of current
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
			// we use a progress monitor to monitor the opening process,
			// normally, due to default MillisToDecideToPopup and MillisToPopup
			// settings, this monitor doesn't popup
			// however, it provides users a way to terminate opening if beagle
			// process hangs on translating elts to xml
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

	/*
	 * adjust the objects placement on canvas with the help of graph file
	 */
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

		// adjust positions
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

	/**
	 * the open file activity
	 * 
	 * @author Wei He
	 * 
	 */
	private class OpenFileActivity extends SwingWorker<Void, Integer> {

		/**
		 * just inited
		 */
		public static final int INIT_DONE = 0;
		/**
		 * finished elts translating to XML
		 */
		public static final int ELT2XML_DONE = 50;
		/**
		 * finished parsing XML
		 */
		public static final int PARSEXML_DONE = 70;
		/**
		 * all finished
		 */
		public static final int ALL_DONE = 100;

		/**
		 * the file to be opened
		 */
		File selectedFile = null;

		/**
		 * the model parsed from file
		 */
		private Model model = null;

		/**
		 * the result of calling beagle process for translating elts to xml, the
		 * first is exit status, the second is message
		 */
		private Pair<Integer, String> elt2XMLRes = null;

		public OpenFileActivity(File file) {
			this.selectedFile = file;
			model = null;
			elt2XMLRes = null;
		}

		@Override
		protected Void doInBackground() throws Exception {
			// translate elts to XML
			setProgress(INIT_DONE);
			elt2XMLRes = BeagleInvoker.getIntance().elts2XML(
					selectedFile.getPath());

			setProgress(ELT2XML_DONE);
			if (elt2XMLRes.getFirst() == 0) {
				// parse xml and get model
				String xmlContent = elt2XMLRes.getSecond();
				model = ELTSParser.parseModel(xmlContent);
				setProgress(PARSEXML_DONE);
				if (model != null) {
					// change objects locations of model on canvas
					adjustLocations(
							model,
							StringUtil.substitutePathExt(
									selectedFile.getPath(), GRAPH_EXT));

				}
			}
			return null;
		}

		@Override
		protected void done() {

			if (elt2XMLRes != null) {
				if (elt2XMLRes.getFirst() == 0) {
					// successfully get xml translation
					if (model != null) {
						// successfully parsed xml
						Environment.getInstance().setModel(model);
						Environment.getInstance().setCurrentPath(
								selectedFile.getPath());
					} else {
						// xml parsing errors
						JOptionPane
								.showMessageDialog(
										Environment.getInstance(),
										"Beagle executable returns invalid XML format!",
										"Parsing Error",
										JOptionPane.ERROR_MESSAGE);
					}
				} else {
					// errors when translating
					String errorMsg = elt2XMLRes.getSecond();
					JOptionPane.showMessageDialog(Environment.getInstance(),
							"Errors while parsing " + selectedFile.getName()
									+ "!\n" + errorMsg, "Parsing Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
			setProgress(ALL_DONE);
		}

		/**
		 * cancel the beagle process
		 */
		public void cancel() {
			BeagleInvoker.getIntance().terminateProcess();
		}
	}
}
