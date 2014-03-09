package gui.toolbars.tools;

import gui.Environment;
import gui.drawers.ModelDrawer;
import gui.editors.Canvas;
import gui.editors.EditorPane;
import gui.toolbars.toolboxes.ModuleDrawerToolBox;

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

import model.Model;
import model.Module;

public class ModelDrawerCursorTool extends Tool {
	private Module lastClickedModule = null;
	private Point initialPointClicked = new Point();
	private ModuleMenu moduleMenu = new ModuleMenu();

	public ModelDrawerCursorTool(Canvas view, ModelDrawer drawer) {
		super(view, drawer);
	}

	public Icon getIcon() {
		URL url = getClass().getResource("/assets/icons/arrow.gif");
		return new ImageIcon(url);
	}

	@Override
	public String getToolTip() {
		return "Attribute Editor";
	}

	private Model getModel() {
		return (Model) super.getObject();
	}

	@Override
	protected ModelDrawer getDrawer() {
		return (ModelDrawer) super.getDrawer();
	}

	@Override
	public void mouseClicked(MouseEvent event) {
		if (event.getClickCount() == 1) {
			getModel().unselectAll();
			getView().repaint();
		} else if (event.getClickCount() == 2
				&& event.getButton() == MouseEvent.BUTTON1) {
			editModule(getDrawer().moduleAtPoint(event.getPoint()));
		}
	}

	@Override
	public void mousePressed(MouseEvent event) {
		this.initialPointClicked.setLocation(event.getPoint());
		lastClickedModule = getDrawer().moduleAtPoint(event.getPoint());

		if (event.isPopupTrigger()) {
			showPopup(event);
		}
		if (lastClickedModule != null) {
			this.initialPointClicked.setLocation(lastClickedModule.getPoint());
			if (!this.lastClickedModule.isSelected()) {
				getModel().unselectAll();
				this.lastClickedModule.setSelect(true);
			}
			getView().repaint();
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
			getView().repaint();
		} else {
			int currentX = event.getPoint().x;
			int currentY = event.getPoint().y;
			int initX = this.initialPointClicked.x;
			int initY = this.initialPointClicked.y;

			Rectangle rect = new Rectangle(Math.min(currentX, initX), Math.min(
					currentY, initY), Math.abs(currentX - initX),
					Math.abs(currentY - initY));
			getDrawer().getModel().selectModulesWithinBounds(rect);
			getDrawer().setSelectionBounds(rect);
			getView().repaint();
		}
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		if (event.isPopupTrigger()) {
			showPopup(event);
		}
		getDrawer().setSelectionBounds(new Rectangle(0, 0, -1, -1));
		this.lastClickedModule = null;
		getView().repaint();
	}

	private void showPopup(MouseEvent event) {
		if (this.lastClickedModule != null)
			this.moduleMenu.show(this.lastClickedModule, getView(),
					event.getPoint());
	}

	private void editModule(Module module) {
		Environment.getInstance().addTab(
				new EditorPane(Environment.getInstance().getDrawer(module),
						new ModuleDrawerToolBox()),
				module.getName() + "'s editor");
	}

	private class ModuleMenu extends JPopupMenu implements ActionListener {
		private Module module;
		private JMenuItem setName;
		private JMenuItem edit;

		public ModuleMenu() {
			this.setName = new JMenuItem("Set Name");
			this.setName.addActionListener(this);

			this.edit = new JMenuItem("Edit");
			this.edit.addActionListener(this);

			add(this.setName);
			add(this.edit);
		}

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
				this.module.setName(newName);
			} else if (menuItem == this.edit) {
				editModule(this.module);
			}
		}
	}
}
