package gui.editors;

import gui.drawers.ObjectDrawer;
import gui.entities.Note;
import gui.toolbars.ToolBar;
import gui.toolbars.toolboxes.ToolBox;

import java.awt.BorderLayout;
import java.awt.Point;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.BevelBorder;

/**
 * Editor pane
 * 
 * @author Wei He
 * 
 */
public class EditorPane extends JComponent {
	/**
	 * graph canvas
	 */
	private Canvas canvas;
	/**
	 * tool bar
	 */
	private ToolBar toolbar;
	/**
	 * object drawer
	 */
	private ObjectDrawer drawer;
	/**
	 * graph size slider
	 */
	private GraphSizeSlider slider;

	/**
	 * constructor
	 * 
	 * @param drawer
	 *            object drawer
	 * @param toolBox
	 *            toolbox
	 */
	public EditorPane(ObjectDrawer drawer, ToolBox toolBox) {
		this.drawer = drawer;
		this.canvas = new Canvas(drawer, this);
		this.setLayout(new BorderLayout());

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(new JScrollPane(this.canvas,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS),
				BorderLayout.CENTER);
		mainPanel.setBorder(new BevelBorder(1));

		this.toolbar = new ToolBar(this.canvas, drawer, toolBox);
		toolBox.addExtras(toolbar);
		canvas.setToolbar(toolbar);

		this.slider = new GraphSizeSlider(this.canvas, drawer);
		this.add(mainPanel, BorderLayout.CENTER);
		this.add(this.toolbar, BorderLayout.NORTH);
		this.add(this.slider, BorderLayout.SOUTH);

		Note[] notes = drawer.getNotes();
		for (int i = 0; i < notes.length; i++) {
			notes[i].initializeForView(new Point(100 * i, 0), canvas);
		}
	}

	/**
	 * get tool bar
	 * 
	 * @return tool bar
	 */
	public ToolBar getToolBar() {
		return this.toolbar;
	}

	/**
	 * get object drawer
	 * 
	 * @return drawer
	 */
	public ObjectDrawer getDrawer() {
		return this.drawer;
	}
}
