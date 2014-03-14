package actions;

import gui.Environment;
import gui.editors.TransitionEditor;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class CloseButton extends JButton {
	private Environment env;

	public CloseButton(Environment environment) {
		this.env = environment;
		setDefaults();

		this.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				Component tab = env.getActiveTab();
				if (env.getActiveTab() instanceof TransitionEditor) {
					TransitionEditor editor = (TransitionEditor) env
							.getActiveTab();
					if (editor.save())
						env.removeCurrentTab();
				} else
					env.removeCurrentTab();
			}

		});

		env.addChangeListeners(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				checkEnabled();
			}

		});
		checkEnabled();
	}

	public void setDefaults() {
		setIcon(new ImageIcon(getClass().getResource("/assets/icons/x.gif")));
		setPreferredSize(new Dimension(22, 22));
		setToolTipText("Dismiss Tab");
	}

	private void checkEnabled() {
		if (this.env.getActiveTabIndex() == 0)
			setEnabled(false);
		else
			setEnabled(true);
	}
}
