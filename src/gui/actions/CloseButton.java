package gui.actions;

import gui.Environment;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * close button for tab
 * 
 * @author Wei He
 * 
 */
public class CloseButton extends JButton {
	/**
	 * the environment frame
	 */
	private Environment env;

	/**
	 * constructor
	 * 
	 * @param environment
	 *            enviroment instance
	 */
	public CloseButton(Environment environment) {
		this.env = environment;
		setDefaults();

		this.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				env.removeCurrentTab();
			}

		});

		/**
		 * when the active tab changes, we must update whether the close button
		 * is enabled
		 */
		env.addChangeListeners(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				checkEnabled();
			}

		});
		checkEnabled();
	}

	/**
	 * set default properties for the close button
	 */
	private void setDefaults() {
		setIcon(new ImageIcon(getClass().getResource("/assets/icons/x.gif")));
		setPreferredSize(new Dimension(22, 22));
		setToolTipText("Dismiss Tab");
	}

	/**
	 * check whether this button is enabled
	 */
	private void checkEnabled() {
		// the first tab, i.e. the model's editor, is not closable
		if (this.env.getActiveTabIndex() == 0)
			setEnabled(false);
		else
			setEnabled(true);
	}
}
