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

public class ToolBar extends JToolBar implements ActionListener {
	private ToolAdapter adapter;
	private Map<JToggleButton, Tool> buttonsToTools = null;
	private Component canvas;
	private List<Tool> tools;
	private Tool currentTool = null;

	public ToolBar(Canvas canvas, ObjectDrawer drawer, ToolBox box) {
		this.adapter = new ToolAdapter();
		this.canvas = canvas;
		this.tools = box.getTools(canvas, drawer);

		initBar();
		canvas.addMouseListener(this.adapter);
		canvas.addMouseMotionListener(this.adapter);
		currentTool = null;
	}

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
			btn.addActionListener(this);

			KeyStroke ks = tool.getKey();
			if (ks != null) {
				InputMap inputMap = btn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
				inputMap.put(ks, tool.getToolTip());
				btn.getActionMap().put(tool.getToolTip(), new ButtonClicker(btn));
			}
			
			if(this.currentTool == null) {
				this.currentTool = tool;
				btn.doClick();
			}
		}
	}

	public void actionPerformed(ActionEvent event) {
		Tool tool = this.buttonsToTools.get(event.getSource());

		if (tool != null) {
			this.adapter.setAdapter(tool);
			this.canvas.setCursor(new Cursor(0));
		}
		if (tool instanceof DeleteTool) {
			Toolkit localToolkit = Toolkit.getDefaultToolkit();

			URL url = getClass().getResource("/assets/icons/deletecursor.gif");
			Image image = getToolkit().getImage(url);
			Point pt = new Point(5, 5);
			Cursor deleteCursor = localToolkit.createCustomCursor(image, pt,
					"Delete");
			this.canvas.setCursor(deleteCursor);
		}
		this.currentTool = tool;
	}

	public void drawTool(Graphics graphics) {
		if (this.currentTool == null)
			return;
		this.currentTool.draw(graphics);
	}

	public Tool getCurrentTool() {
		return this.currentTool;
	}

	private class ButtonClicker extends AbstractAction {

		AbstractButton btn;

		public ButtonClicker(AbstractButton b) {
			btn = b;
		}

		@Override
		public void actionPerformed(ActionEvent event) {
			this.btn.doClick();
		}

	}
}
