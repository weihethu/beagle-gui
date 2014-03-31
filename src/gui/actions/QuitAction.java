package gui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

/**
 * the action for quit menu item
 * @author Wei He
 *
 */
public class QuitAction extends AbstractAction {
	
	/**
	 * constructor
	 */
	public QuitAction() {
		super("Quit", null);
		putValue("AcceleratorKey",
				KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.exit(0);

	}

}