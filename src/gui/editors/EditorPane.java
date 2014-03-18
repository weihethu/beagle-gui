package gui.editors;

import gui.Note;
import gui.drawers.ObjectDrawer;
import gui.toolbars.ToolBar;
import gui.toolbars.toolboxes.ToolBox;

import java.awt.BorderLayout;
import java.awt.Point;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.BevelBorder;

public class EditorPane extends JComponent {
	private Canvas canvas;
	private ToolBar toolbar;
	private ObjectDrawer drawer;
	private GraphSizeSlider slider;

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

	public ToolBar getToolBar() {
		return this.toolbar;
	}

	public ObjectDrawer getDrawer() {
		return this.drawer;
	}
}
