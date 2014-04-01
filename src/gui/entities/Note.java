package gui.entities;

import events.NoteEditEvent;
import events.listeners.ObjectEditListener;
import gui.Environment;
import gui.editors.Canvas;
import gui.editors.EditorPane;
import gui.toolbars.tools.ModuleDrawerCursorTool;
import gui.toolbars.tools.Tool;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import model.Module;

/**
 * Note for displaying & editing text on graph
 * 
 * @author Wei He
 * 
 */
public class Note extends JTextArea {
	/**
	 * the current point of note
	 */
	private Point point = null;
	/**
	 * last point of note when dragging
	 */
	private Point lastPoint;
	/**
	 * the initial point clicked when dragging started
	 */
	private Point initialPointClicked;
	/**
	 * canvas
	 */
	private Canvas canvas;
	/**
	 * note title
	 */
	private String title;
	/**
	 * text to display when note is empty
	 */
	private final String EMPTY_STR = "<insert text>";
	/**
	 * parent module
	 */
	private Module module;
	/**
	 * title for declaring variables
	 */
	public static String VAR = "variables:";
	/**
	 * title for declaring initial actions
	 */
	public static String INIT_ACTION = "init actions:";
	/**
	 * listeners for NoteEditEvents
	 */
	private Set<ObjectEditListener> noteListeners = null;

	/**
	 * constructor
	 * 
	 * @param module
	 *            module
	 * @param title
	 *            title
	 * @param content
	 *            content
	 */
	public Note(Module module, String title, String content) {
		this.module = module;
		this.title = title;
		this.noteListeners = new HashSet<ObjectEditListener>();

		if (content != null && !content.isEmpty())
			setText(content);
		else
			setText(EMPTY_STR);
		this.canvas = null;
		setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), title));

		this.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent event) {
				if (getText().trim().equals(EMPTY_STR)) {
					setText("");
				}
			}

			@Override
			public void focusLost(FocusEvent event) {
				String text = getText().trim();
				if (text.isEmpty())
					setText(EMPTY_STR);
			}

		});

		this.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(DocumentEvent event) {

			}

			@Override
			public void insertUpdate(DocumentEvent event) {
				onTextEdited();
			}

			@Override
			public void removeUpdate(DocumentEvent event) {
				onTextEdited();
			}

		});

		this.addNoteListener(Environment.getInstance().getVerifierPane());
	}

	/**
	 * event handler when note text edited
	 */
	public void onTextEdited() {
		String value = "";
		if (!this.getText().trim().equals(EMPTY_STR))
			value = this.getText().trim();
		if (this.title.equals(VAR))
			module.setVarDeclaration(value);
		else if (this.title.equals(INIT_ACTION))
			module.setInitialAction(value);

		this.distributeNoteEditEvent(new NoteEditEvent(this,
				NoteEditEvent.EventType.TEXT));
	}

	/**
	 * initialize for view on canvas
	 * 
	 * @param pt
	 *            point
	 * @param can
	 *            canvas
	 */
	public void initializeForView(Point pt, Canvas can) {
		this.canvas = can;
		this.point = pt;
		setLocationManually(this.point);
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
						setLocationManually(new Point(lastPoint.x + dx,
								lastPoint.y + dy));
						lastPoint = new Point(getPoint());
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
				lastPoint = new Point(getPoint());
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

	/**
	 * get current point of note
	 * 
	 * @return point
	 */
	public Point getPoint() {
		return this.point;
	}

	/**
	 * set location of note on canvas
	 * 
	 * @param pt
	 *            location point
	 */
	private void setLocationManually(Point pt) {
		this.point = pt;
		if (this.canvas != null)
			setLocation(this.canvas.transfromFromCanvasToView(pt));
	}

	/**
	 * update view on canvas
	 */
	public void updateView() {
		setLocationManually(this.point);
	}

	/**
	 * add event listener for NoteEditEvents
	 * 
	 * @param listener
	 *            event listener
	 */
	public void addNoteListener(ObjectEditListener listener) {
		this.noteListeners.add(listener);
	}

	/**
	 * distribute NoteEditEvents to all event listeners
	 * 
	 * @param event
	 *            NoteEditEvent
	 */
	public void distributeNoteEditEvent(NoteEditEvent event) {
		Iterator<ObjectEditListener> iter = noteListeners.iterator();
		while (iter.hasNext()) {
			ObjectEditListener currentListener = iter.next();
			currentListener.objectEdit(event);
		}
	}
}
