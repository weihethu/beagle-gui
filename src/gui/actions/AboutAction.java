package gui.actions;

import gui.AboutBox;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

/**
 * Action for the about menu item
 * @author Wei He
 *
 */
public class AboutAction extends AbstractAction {
	private static final AboutBox Box = new AboutBox();
	
	/**
	 * constructor
	 */
	public AboutAction() {
		super("About", null);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		Box.displayBox();
	}

}
