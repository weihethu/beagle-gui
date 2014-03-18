package entry;

import gui.Environment;
import model.Model;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Environment.getInstance();
		Environment.getInstance().setModel(new Model());
	}

}
