package gui.editors;

import java.awt.BorderLayout;

import gui.drawers.ObjectDrawer;
import gui.toolbars.ToolBar;
import gui.toolbars.toolboxes.ToolBox;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.BevelBorder;

public class EditorPane extends JComponent {
	protected EditorCanvas canvas;
	protected ToolBar toolbar;

	public EditorPane(ObjectDrawer drawer, ToolBox toolBox) {
		this.canvas = new EditorCanvas(drawer);
		this.setLayout(new BorderLayout());

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(new JScrollPane(this.canvas,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS),
				BorderLayout.CENTER);
		mainPanel.setBorder(new BevelBorder(1));

		this.toolbar = new ToolBar(this.canvas, drawer, toolBox);

		this.add(mainPanel, BorderLayout.CENTER);
		this.add(this.toolbar, BorderLayout.NORTH);
	}

}
