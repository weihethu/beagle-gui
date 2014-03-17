package gui;

import gui.editors.Canvas;
import gui.editors.EditorPane;
import gui.toolbars.tools.ModuleDrawerCursorTool;
import gui.toolbars.tools.Tool;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.JTextArea;

public class Note extends JTextArea {
	private Point myAutoPoint = null;
	private Point lastAutoPoint;
	private Point initialPointClicked;
	private Canvas canvas;
	private String title;
	private final String EMPTY_STR = "<insert text>";

	public Note(String title) {
		this.title = title;
		setText(EMPTY_STR);
		this.canvas = null;
		setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), title));

		this.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent event) {

			}

			@Override
			public void focusLost(FocusEvent event) {
				String text = getText().trim();
				if (text.isEmpty())
					setText(EMPTY_STR);
			}

		});
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension dim = super.getPreferredSize();
		return new Dimension(Math.min(150, dim.width), dim.height);
	}

	@Override
	public void setSize(Dimension dim) {
		super.setSize(dim);
	}

	public String getTitle() {
		return title;
	}

	public void initializeForView(Point pt, Canvas view) {
		this.canvas = view;
		this.myAutoPoint = pt;
		setLocationManually(this.myAutoPoint);
		setDisabledTextColor(Color.black);
		setBackground(new Color(255, 255, 150));

		this.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {
				EditorPane pane = canvas.getCreator();
				Tool currentTool = pane.getToolBar().getCurrentTool();
				if (currentTool instanceof ModuleDrawerCursorTool) {
					if (e.isPopupTrigger())
						return;
					if (!((Note) e.getSource()).isEditable()) {
						int dx = e.getPoint().x
								- Note.this.initialPointClicked.x;
						int dy = e.getPoint().y
								- Note.this.initialPointClicked.y;
						setLocationManually(new Point(lastAutoPoint.x + dx,
								lastAutoPoint.y + dy));
						lastAutoPoint = new Point(getAutoPoint());
					}
					canvas.repaint();
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {

			}

		});

		this.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent evt) {
				setEnabled(true);
				setEditable(true);
				setCaretColor(null);
			}

			@Override
			public void mouseEntered(MouseEvent evt) {

			}

			@Override
			public void mouseExited(MouseEvent evt) {

			}

			@Override
			public void mousePressed(MouseEvent evt) {
				lastAutoPoint = new Point(getAutoPoint());
				initialPointClicked = new Point(evt.getX(), evt.getY());
			}

			@Override
			public void mouseReleased(MouseEvent evt) {
			}

		});

		this.canvas.add(this);
		this.setEnabled(true);
		this.setEditable(true);
		this.setCaretColor(null);
		this.setSelectionStart(0);
		this.setSelectionEnd(this.getColumnWidth());
	}

	public Point getAutoPoint() {
		return this.myAutoPoint;
	}

	public void setLocationManually(Point pt) {
		this.myAutoPoint = pt;
		if (this.canvas != null)
			setLocation(this.canvas.transfromFromCanvasToView(pt));
	}

	public void updateView() {
		setLocationManually(this.myAutoPoint);
	}
}
