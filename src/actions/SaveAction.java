package actions;

import gui.Environment;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

public class SaveAction extends AbstractAction {

	public SaveAction() {
		super("Save", null);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (Environment.getInstance().getCurrentPath() == null) {
			(new SaveAsAction()).actionPerformed(e);
		} else {
			String options[] = new String[] { "text only", "text and graph",
					"cancel" };
			int code = JOptionPane.showOptionDialog((Component) e.getSource(),
					"Which do you want to save?", "Save Options", 0,
					JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
			if (code == 0)
				(new SaveAsAction()).save(Environment.getInstance()
						.getCurrentPath(), SaveAsAction.SAVE_TYPE.TEXT);
			else
				(new SaveAsAction()).save(Environment.getInstance()
						.getCurrentPath(),
						SaveAsAction.SAVE_TYPE.TEXT_AND_GRAPH);
		}
	}
}
