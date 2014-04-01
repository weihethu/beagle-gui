package gui.toolbars;

import gui.drawers.ObjectDrawer;
import gui.editors.Canvas;
import gui.toolbars.toolboxes.ToolBox;
import gui.toolbars.tools.DeleteTool;
import gui.toolbars.tools.Tool;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

/**
 * tool bar
 * 
 * @author Wei He
 * 
 */
public class ToolBar extends JToolBar {
	/**
	 * the adapter which dispatches mouse events on graph to current tool
	 */
	private ToolAdapter adapter;
	/**
	 * a map which associates toggle buttons to tools
	 */
	private Map<JToggleButton, Tool> buttonsToTools = null;
	/**
	 * canvas
	 */
	private Component canvas;
	/**
	 * tools in tool bar
	 */
	private List<Tool> tools;
	/**
	 * the current tool
	 */
	private Tool currentTool = null;

	/**
	 * constructor
	 * 
	 * @param canvas
	 *            canvas
	 * @param drawer
	 *            object drawer
	 * @param box
	 *            tool box
	 */
	public ToolBar(Canvas canvas, ObjectDrawer drawer, ToolBox box) {
		this.adapter = new ToolAdapter();
		this.canvas = canvas;
		this.tools = box.getTools(canvas, drawer);

		initBar();
		canvas.addMouseListener(this.adapter);
		canvas.addMouseMotionListener(this.adapter);
		currentTool = null;
	}

	/**
	 * init tool bar
	 */
	private void initBar() {
		Iterator<Tool> localIterator = this.tools.iterator();
		buttonsToTools = new HashMap<JToggleButton, Tool>();
		ButtonGroup buttonGroup = new ButtonGroup();

		while (localIterator.hasNext()) {
			Tool tool = (Tool) localIterator.next();
			JToggleButton btn = new JToggleButton(tool.getIcon());
			btn.setToolTipText(tool.getShortcutToolTip());
			this.buttonsToTools.put(btn, tool);
			buttonGroup.add(btn);

			add(btn);
			btn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent event) {
					Tool tool = buttonsToTools.get(event.getSource());

					if (tool != null) {
						adapter.setCurrentTool(tool);
						canvas.setCursor(new Cursor(0));
					}
					if (tool instanceof DeleteTool) {
						// delete tools has a special mouse cursor
						Toolkit localToolkit = Toolkit.getDefaultToolkit();

						URL url = getClass().getResource(
								"/assets/icons/deletecursor.gif");
						Image image = getToolkit().getImage(url);
						Point pt = new Point(5, 5);
						Cursor deleteCursor = localToolkit.createCustomCursor(
								image, pt, "Delete");
						canvas.setCursor(deleteCursor);
					}
					currentTool = tool;
				}

			});

			// set accelerator keys for tools
			KeyStroke ks = tool.getKey();
			if (ks != null) {
				InputMap inputMap = btn
						.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
				inputMap.put(ks, tool.getToolTip());
				btn.getActionMap().put(tool.getToolTip(),
						new ButtonClicker(btn));
			}

			// set the first tool as the current tool
			if (this.currentTool == null) {
				this.currentTool = tool;
				btn.doClick();
			}
		}
	}

	/**
	 * some tools require drawing on canvas
	 * 
	 * @param graphics
	 *            graphics
	 */
	public void drawTool(Graphics graphics) {
		if (this.currentTool == null)
			return;
		this.currentTool.draw(graphics);
	}

	/**
	 * get current tool
	 * 
	 * @return tool
	 */
	public Tool getCurrentTool() {
		return this.currentTool;
	}

	/**
	 * the action wrapper for click events on buttons
	 * 
	 * @author Wei He
	 * 
	 */
	private class ButtonClicker extends AbstractAction {

		/**
		 * button
		 */
		AbstractButton btn;

		/**
		 * constructor
		 * 
		 * @param b
		 *            button
		 */
		public ButtonClicker(AbstractButton b) {
			btn = b;
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			this.btn.doClick();
		}
	}
}
