package actions;

import gui.Environment;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import model.Model;

public class NewAction extends AbstractAction {

	public NewAction() {
		super("New", null);
		putValue("AcceleratorKey",
				KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));
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
