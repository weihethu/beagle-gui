package gui.editors;

import gui.Note;
import gui.drawers.ObjectDrawer;

import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class GraphSizeSlider extends JSlider {
	static final int SIZE_MIN = 1;
	static final int SIZE_MAX = 800;
	static final int SIZE_INIT = 220;

	private Canvas view;
	private ObjectDrawer drawer;

	public GraphSizeSlider(Canvas view, ObjectDrawer drawer) {
		super(SIZE_MIN, SIZE_MAX, SIZE_INIT);
		this.view = view;
		this.drawer = drawer;
		addChangeListener(new SliderListener());
		setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Graph Size"));
	}

	private class SliderListener implements ChangeListener {
		public SliderListener() {

		}

		@Override
		public void stateChanged(ChangeEvent event) {
			JSlider slider = (JSlider) event.getSource();
			double scale = (double) slider.getValue() / SIZE_INIT;
			GraphSizeSlider.this.view.setScale(scale);
			GraphSizeSlider.this.view.requestTransform();
			ObjectDrawer drawer = GraphSizeSlider.this.view.getDrawer();
			for (Note note : drawer.getNotes())
				note.setFont(new Font(null, Font.PLAIN, slider.getValue() / 20));
		}
	}
}
