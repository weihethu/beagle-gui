package gui.toolbars;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class ToolAdapter implements MouseListener, MouseMotionListener {

	private MouseAdapter activeAdapter = null;

	public ToolAdapter() {
		activeAdapter = null;
	}

	public void setAdapter(MouseAdapter adapter) {
		this.activeAdapter = adapter;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (activeAdapter != null)
			activeAdapter.mouseClicked(e);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if (activeAdapter != null)
			activeAdapter.mouseEntered(e);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if (activeAdapter != null)
			activeAdapter.mouseExited(e);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (activeAdapter != null)
			activeAdapter.mousePressed(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (activeAdapter != null)
			activeAdapter.mouseReleased(e);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (activeAdapter != null)
			activeAdapter.mouseDragged(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if (activeAdapter != null)
			activeAdapter.mouseMoved(e);
	}
}
