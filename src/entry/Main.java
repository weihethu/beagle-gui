package entry;

import gui.Environment;
import model.Model;

/**
 * entry point for beagle-gui
 * 
 * @author Wei He
 * 
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Environment.getInstance();
		Environment.getInstance().setModel(new Model());
	}

}
