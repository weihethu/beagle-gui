package actions;

import gui.Environment;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class SaveAction extends AbstractAction {

	public SaveAction() {
		super("Save", null);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (Environment.getInstance().getCurrentPath() == null) {

		} else {

		}
	}

}
