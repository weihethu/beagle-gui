package actions;

import gui.Environment;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import model.Model;

public class NewAction extends AbstractAction {

	public NewAction() {
		super("New", null);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (JOptionPane.showConfirmDialog((Component) event.getSource(),
				"All unsaved content will be lost! Do you want to continue?",
				"Warning", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.CANCEL_OPTION)
			return;

		Environment.getInstance().setModel(new Model());
		Environment.getInstance().setCurrentPath(null);
	}
}
