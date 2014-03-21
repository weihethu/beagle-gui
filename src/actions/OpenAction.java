package actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import model.Model;
import elts.ELTSParser;
import gui.Environment;

public class OpenAction extends AbstractAction {

	public OpenAction() {
		super("Open", null);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		//add warning to save current
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setMultiSelectionEnabled(false);
		if (JFileChooser.APPROVE_OPTION == fileChooser
				.showOpenDialog((Component) event.getSource())) {
			File selectedFile = fileChooser.getSelectedFile();
			String xmlContent = getModelXML(selectedFile);

			Model model = ELTSParser.parseModel(xmlContent);
			if (model != null) {
				Environment.getInstance().setModel(model);
			}
		}
	}

	private String getModelXML(File file) {
		String text = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String s;
			while ((s = br.readLine()) != null) {
				text += (s + "\n");
			}
		} catch (IOException e) {
			text = null;
			e.printStackTrace();
		}
		return text;
	}
}
