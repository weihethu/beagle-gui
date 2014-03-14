package entry;

import gui.Environment;
import gui.drawers.ModelDrawer;
import gui.editors.EditorPane;
import gui.toolbars.toolboxes.ModelDrawerToolBox;
import model.Model;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Environment environment = Environment.getInstance();

		Model model = new Model();
		ModelDrawer modelDrawer = (ModelDrawer) environment.getDrawer(model);
		environment
				.addTab(new EditorPane(modelDrawer, new ModelDrawerToolBox()),
						"Editor");
	}

}
