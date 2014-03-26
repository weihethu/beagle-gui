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

import utils.Config;
import model.Model;

public class SaveAsAction extends AbstractAction {

	private static String ELTS_EXT = Config.getInstance().get_elts_ext();
	private static String GRAPH_EXT = Config.getInstance().get_graph_ext();

	public enum SAVE_TYPE {
		TEXT, TEXT_AND_GRAPH
	};

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

		if (Environment.getInstance().getCurrentPath() != null) {
			File currentFilePath = new File(Environment.getInstance()
					.getCurrentPath());
			fileChooser.setCurrentDirectory(currentFilePath.getParentFile());
		}

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

	public void save(String filePath, SAVE_TYPE type) {
		Model model = Environment.getInstance().getModel();
		if (type == SAVE_TYPE.TEXT_AND_GRAPH) {
			String path = substitutePathExt(filePath, GRAPH_EXT);
			if (!GraphXMLSaver.save(model, path))
				JOptionPane.showMessageDialog(null, "Error in saving " + path
						+ "!");
		}
		String path = substitutePathExt(filePath, ELTS_EXT);
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

	private String substitutePathExt(String path, String ext) {
		int position = path.lastIndexOf('.');
		if (position >= 0)
			return path.substring(0, position + 1) + ext;
		else
			return path + "." + ext;
	}
}
