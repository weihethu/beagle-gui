package gui.menus;

import gui.Environment;
import gui.actions.AboutAction;
import gui.actions.CloseButton;
import gui.actions.NewAction;
import gui.actions.OpenAction;
import gui.actions.QuitAction;
import gui.actions.SaveAction;
import gui.actions.SaveAsAction;
import gui.actions.VerifyAction;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/**
 * menu bar creator
 * 
 * @author Wei He
 * 
 */
public class MenuBarCreator {

	/**
	 * get menu bar
	 * 
	 * @param env
	 *            environment instance
	 * @return menu bar
	 */
	public static JMenuBar getMenuBar(Environment env) {
		JMenuBar menubar = new JMenuBar();

		JMenu fileMenu = getFileMenu();
		if (fileMenu.getItemCount() > 0)
			menubar.add(fileMenu);
		JMenu verificationMenu = getVerificationMenu();
		if (verificationMenu.getItemCount() > 0)
			menubar.add(verificationMenu);
		JMenu helpMenu = getHelpMenu();
		if (helpMenu.getItemCount() > 0)
			menubar.add(helpMenu);

		CloseButton closeBtn = new CloseButton(env);
		menubar.add(Box.createGlue());
		menubar.add(closeBtn);
		return menubar;
	}

	/**
	 * add menu item in menu
	 * 
	 * @param menu
	 *            menu
	 * @param action
	 *            menu item action
	 */
	private static void addItem(JMenu menu, Action action) {
		JMenuItem item = new JMenuItem(action);
		item.setAccelerator((KeyStroke) action.getValue("AcceleratorKey"));
		menu.add(item);
	}

	/**
	 * get file menu
	 * 
	 * @return file menu
	 */
	private static JMenu getFileMenu() {
		JMenu fileMenu = new JMenu("File");

		addItem(fileMenu, new NewAction());
		addItem(fileMenu, new OpenAction());
		fileMenu.addSeparator();

		addItem(fileMenu, new SaveAction());
		addItem(fileMenu, new SaveAsAction());
		fileMenu.addSeparator();

		addItem(fileMenu, new QuitAction());
		return fileMenu;
	}

	/**
	 * get verification menu
	 * 
	 * @return verification menu
	 */
	private static JMenu getVerificationMenu() {
		JMenu verificationMenu = new JMenu("Verification");
		addItem(verificationMenu, new VerifyAction());

		return verificationMenu;
	}

	/**
	 * get help menu
	 * 
	 * @return help menu
	 */
	private static JMenu getHelpMenu() {
		JMenu helpMenu = new JMenu("Help");
		addItem(helpMenu, new AboutAction());
		return helpMenu;
	}
}
