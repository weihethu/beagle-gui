package gui.actions;

import elts.ELTSGenerator;
import elts.graph.GraphXMLSaver;
import gui.Environment;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;

import model.Model;
import utils.Config;
import utils.StringUtil;

/**
 * the action for save as menu item
 * 
 * @author Wei He
 * 
 */
public class SaveAsAction extends AbstractAction {

	/**
	 * the elts model file extension
	 */
	private static String ELTS_EXT = Config.getInstance().get_elts_ext();
	/**
	 * the graph file extension
	 */
	private static String GRAPH_EXT = Config.getInstance().get_graph_ext();

	/**
	 * the save type enumeration
	 * 
	 * @author Wei He
	 * 
	 */
	public enum SAVE_TYPE {
		/**
		 * only save model
		 */
		TEXT,
		/**
		 * save both model and graph
		 */
		TEXT_AND_GRAPH
	};

	/**
	 * constructor
	 */
	public SaveAsAction() {
		super("Save as", null);
		putValue(
				"AcceleratorKey",
				KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK
						| KeyEvent.SHIFT_DOWN_MASK));
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setAcceptAllFileFilterUsed(false);

		// set the default save path to the parent of current
		if (Environment.getInstance().getCurrentPath() != null) {
			File currentFilePath = new File(Environment.getInstance()
					.getCurrentPath());
			fileChooser.setCurrentDirectory(currentFilePath.getParentFile());
		}

		// add chooser for model files
		fileChooser.addChoosableFileFilter(new FileFilter() {

			@Override
			public String getDescription() {
				return "model file, only text";
			}

			@Override
			public boolean accept(File f) {
				return f.isDirectory() || f.getName().endsWith("." + ELTS_EXT);
			}

		});

		// add chooser for model & graph files
		fileChooser.addChoosableFileFilter(new FileFilter() {

			@Override
			public boolean accept(File f) {
				return f.isDirectory() || f.getName().endsWith("." + ELTS_EXT)
						|| f.getName().endsWith(GRAPH_EXT);
			}

			@Override
			public String getDescription() {
				return "model file, text and graph";
			}

		});

		if (JFileChooser.APPROVE_OPTION == fileChooser
				.showSaveDialog((Component) event.getSource())) {
			File selectedFile = fileChooser.getSelectedFile();
			FileFilter filter = fileChooser.getFileFilter();
			if (filter.getDescription().contains("graph")) {
				save(selectedFile.getPath(), SAVE_TYPE.TEXT_AND_GRAPH);
			} else
				save(selectedFile.getPath(), SAVE_TYPE.TEXT);
		}
	}

	/**
	 * save files to disk
	 * 
	 * @param filePath
	 *            the file path
	 * @param type
	 *            save type
	 */
	public void save(String filePath, SAVE_TYPE type) {
		Model model = Environment.getInstance().getModel();
		if (type == SAVE_TYPE.TEXT_AND_GRAPH) {
			// save graph file
			String path = StringUtil.substitutePathExt(filePath, GRAPH_EXT);
			if (!GraphXMLSaver.save(model, path))
				JOptionPane.showMessageDialog(null, "Error in saving " + path
						+ "!");
		}
		// save model file
		String path = StringUtil.substitutePathExt(filePath, ELTS_EXT);
		String modelText = ELTSGenerator.getModelText(model);
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter(new File(path)));
			bw.write(modelText);
			bw.close();
		} catch (IOException ex) {
			ex.printStackTrace();
			JOptionPane
					.showMessageDialog(null, "Error in saving " + path + "!");
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		Environment.getInstance().setCurrentPath(path);
	}
}
