package gui.editors;

import gui.drawers.ObjectDrawer;
import gui.entities.Note;

import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * slider for adjusting graph size
 * 
 * @author Wei He
 * 
 */
public class GraphSizeSlider extends JSlider {
	/**
	 * min value
	 */
	static final int SIZE_MIN = 1;
	/**
	 * max value
	 */
	static final int SIZE_MAX = 800;
	/**
	 * initial value
	 */
	static final int SIZE_INIT = 220;

	/**
	 * graph canvas
	 */
	private Canvas view;
	/**
	 * object drawer
	 */
	private ObjectDrawer drawer;

	/**
	 * constructor
	 * 
	 * @param view
	 * @param drawer
	 */
	public GraphSizeSlider(Canvas view, ObjectDrawer drawer) {
		super(SIZE_MIN, SIZE_MAX, SIZE_INIT);
		this.view = view;
		this.drawer = drawer;
		addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent event) {
				JSlider slider = (JSlider) event.getSource();
				double scale = (double) slider.getValue() / SIZE_INIT;
				GraphSizeSlider.this.view.setScale(scale);
				GraphSizeSlider.this.view.requestTransform();
				ObjectDrawer drawer = GraphSizeSlider.this.view.getDrawer();
				for (Note note : drawer.getNotes())
					note.setFont(new Font(null, Font.PLAIN,
							slider.getValue() / 20));

			}

		});
		setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Graph Size"));
	}
}
