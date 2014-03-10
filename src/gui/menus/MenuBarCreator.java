package gui.menus;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class MenuBarCreator {

	public static JMenuBar getMenuBar() {
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
		return menubar;
	}

	private static void addItem(JMenu menu, Action action) {
		JMenuItem item = new JMenuItem(action);
		item.setAccelerator((KeyStroke) action.getValue("AccelleratorKey"));
		menu.add(item);
	}

	private static JMenu getFileMenu() {
		JMenu fileMenu = new JMenu("File");
		
		fileMenu.add(new JMenuItem("PlaceHolder"));
		// addItem(fileMenu, new NewAction());
		// addItem(fileMenu, new OpenAction());
		// addItem(fileMenu, new SaveAction());
		// addItem(fileMenu, new SaveAsAction());
		// addItem(fileMenu, new CloseAction());
		// addItem(fileMenu, new CloseWindowAction());
		return fileMenu;
	}

	private static JMenu getVerificationMenu() {
		JMenu verificationMenu = new JMenu("Verification");
		verificationMenu.add(new JMenuItem("PlaceHolder"));
		
		return verificationMenu;
	}

	private static JMenu getHelpMenu() {
		JMenu helpMenu = new JMenu("Help");
		helpMenu.add(new JMenuItem("PlaceHolder"));
		return helpMenu;
	}
}
