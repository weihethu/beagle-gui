package gui.actions;

import gui.AboutBox;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class AboutAction extends AbstractAction {
	private static final AboutBox Box = new AboutBox();

	public AboutAction() {
		super("About", null);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		Box.displayBox();
	}

}
