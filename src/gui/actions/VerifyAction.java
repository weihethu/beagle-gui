package gui.actions;

import gui.Environment;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

/**
 * action for verify menu item
 * @author Wei He
 *
 */
public class VerifyAction extends AbstractAction {

	/**
	 * constructor
	 */
	public VerifyAction() {
		super("Verify", null);
		putValue("AcceleratorKey",
				KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK));
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		Environment.getInstance().openVerifyTab();
	}

}
