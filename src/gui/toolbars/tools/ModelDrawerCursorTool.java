package gui.toolbars.tools;

import gui.Environment;
import gui.drawers.ModelDrawer;
import gui.editors.Canvas;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;

import model.Model;
import model.Module;

/**
 * attribute editor tool when drawing models
 * 
 * @author Wei He
 * 
 */
public class ModelDrawerCursorTool extends Tool {
	/**
	 * last clicked module
	 */
	private Module lastClickedModule = null;
	/**
	 * initial point clicked
	 */
	private Point initialPointClicked = new Point();
	/**
	 * context menu for modules
	 */
	private ModuleMenu moduleMenu = new ModuleMenu();

	/**
	 * constructor
	 * 
	 * @param canvas
	 *            graph canvas
	 * @param drawer
	 *            model drawer
	 */
	public ModelDrawerCursorTool(Canvas canvas, ModelDrawer drawer) {
		super(canvas, drawer);
	}

	@Override
	public Icon getIcon() {
		URL url = getClass().getResource("/assets/icons/arrow.gif");
		return new ImageIcon(url);
	}

	@Override
	public String getToolTip() {
		return "Attribute Editor";
	}

	/**
	 * get model
	 * 
	 * @return model
	 */
	private Model getModel() {
		return (Model) super.getObject();
	}

	@Override
	protected ModelDrawer getDrawer() {
		return (ModelDrawer) super.getDrawer();
	}

	@Override
	public void mouseClicked(MouseEvent event) {
		Module module = getDrawer().moduleAtPoint(event.getPoint());
		if (event.getClickCount() == 1) {
			// unselect all modules
			if (module == null) {
				getModel().unselectAll();
				getCanvas().repaint();
			}
		} else if (event.getClickCount() == 2
				&& event.getButton() == MouseEvent.BUTTON1) {
			// edit modules
			if (module != null)
				editModule(module);
		}
	}

	@Override
	public void mousePressed(MouseEvent event) {
		this.initialPointClicked.setLocation(event.getPoint());
		lastClickedModule = getDrawer().moduleAtPoint(event.getPoint());
		// isPopupTrigger should be checked in both mousePressed and
		// mouseReleased for proper cross-platform functionality
		if (event.isPopupTrigger()) {
			showPopup(event);
		}
		if (lastClickedModule != null) {
			this.initialPointClicked.setLocation(lastClickedModule.getPoint());
			// select the clicked module
			if (!this.lastClickedModule.isSelected()) {
				getModel().unselectAll();
				this.lastClickedModule.setSelect(true);
			}
			getCanvas().repaint();
		}
	}

	@Override
	public void mouseDragged(MouseEvent event) {
		if (this.lastClickedModule != null) {
			Point currentPt = event.getPoint();

			// drag all selected modules
			Module[] modules = getModel().getModules();
			for (Module module : modules) {
				if (module.isSelected()) {
					Point oldPoint = module.getPoint();
					module.setPoint(new Point(oldPoint.x + currentPt.x
							- initialPointClicked.x, oldPoint.y + currentPt.y
							- initialPointClicked.y));
				}
			}
			this.initialPointClicked.setLocation(currentPt);
			getCanvas().repaint();
		} else {
			// select modules in selection box
			int currentX = event.getPoint().x;
			int currentY = event.getPoint().y;
			int initX = this.initialPointClicked.x;
			int initY = this.initialPointClicked.y;

			Rectangle rect = new Rectangle(Math.min(currentX, initX), Math.min(
					currentY, initY), Math.abs(currentX - initX),
					Math.abs(currentY - initY));
			getDrawer().getModel().selectModulesWithinBounds(rect);
			getDrawer().setSelectionBounds(rect);
			getCanvas().repaint();
		}
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		// isPopupTrigger should be checked in both mousePressed and
		// mouseReleased for proper cross-platform functionality
		if (event.isPopupTrigger()) {
			showPopup(event);
		}
		getDrawer().setSelectionBounds(new Rectangle(0, 0, -1, -1));
		this.lastClickedModule = null;
		getCanvas().repaint();
	}

	/**
	 * show popup menu for module
	 * 
	 * @param event
	 *            mouse event
	 */
	private void showPopup(MouseEvent event) {
		if (this.lastClickedModule != null)
			this.moduleMenu.show(this.lastClickedModule, getCanvas(),
					getCanvas().transfromFromCanvasToView(event.getPoint()));
	}

	/**
	 * edit module
	 * 
	 * @param module
	 *            module
	 */
	private void editModule(Module module) {
		Environment.getInstance().openModuleEditor(module);
	}

	@Override
	public KeyStroke getKey() {
		return KeyStroke.getKeyStroke('a');
	}

	/**
	 * context menu to popup when right click modules
	 * 
	 * @author Wei He
	 * 
	 */
	private class ModuleMenu extends JPopupMenu implements ActionListener {
		/**
		 * module
		 */
		private Module module;
		/**
		 * set name menu item
		 */
		private JMenuItem setName;
		/**
		 * edit module menu item
		 */
		private JMenuItem edit;

		/**
		 * constructor
		 */
		public ModuleMenu() {
			this.setName = new JMenuItem("Set Name");
			this.setName.addActionListener(this);

			this.edit = new JMenuItem("Edit");
			this.edit.addActionListener(this);

			add(this.setName);
			add(this.edit);
		}

		/**
		 * show menu
		 * 
		 * @param module
		 *            module
		 * @param parent
		 *            parent
		 * @param pt
		 *            show position
		 */
		public void show(Module module, Component parent, Point pt) {
			this.module = module;
			show(parent, pt.x, pt.y);
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			JMenuItem menuItem = (JMenuItem) event.getSource();
			if (menuItem == this.setName) {
				String currentName = this.module.getName();
				String newName = (String) JOptionPane.showInputDialog(this,
						"Input a new name", "New Name",
						JOptionPane.QUESTION_MESSAGE, null, null, currentName);
				if (newName == null || newName.trim().isEmpty())
					return;
				this.module.setName(newName.trim());
			} else if (menuItem == this.edit) {
				editModule(this.module);
			}
		}
	}
}
