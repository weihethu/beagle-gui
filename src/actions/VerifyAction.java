package actions;

import gui.Environment;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class VerifyAction extends AbstractAction {

	public VerifyAction() {
		super("Verify", null);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		Environment.getInstance().openVerifyTab();
	}

}
