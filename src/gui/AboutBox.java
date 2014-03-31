package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * the about box
 * 
 * @author Wei He
 * 
 */
public class AboutBox extends JFrame {
	/**
	 * the width for about box
	 */
	private static int WIDTH = 500;
	/**
	 * the height for about box
	 */
	private static int HEIGHT = 200;

	/**
	 * constructor
	 */
	public AboutBox() {
		this.setTitle("about beagle-gui");
		this.setLayout(new BorderLayout());

		// the beagle icon
		ImageIcon icon = new ImageIcon(getClass().getResource(
				"/assets/icons/icon.png"));
		icon.setImage(icon.getImage().getScaledInstance((int) (WIDTH * 0.3),
				(int) (HEIGHT * 0.7), Image.SCALE_DEFAULT));
		JLabel imageLabel = new JLabel(icon);
		imageLabel.setOpaque(true);
		imageLabel.setBackground(Color.white);

		// the text for showing about information
		JEditorPane infoPane = new JEditorPane();
		infoPane.setEditable(false);
		infoPane.setBackground(Color.white);
		infoPane.setContentType("text/html");
		infoPane.setText(getInfo());

		this.add(imageLabel, BorderLayout.WEST);
		this.add(infoPane, BorderLayout.CENTER);

		this.setSize(WIDTH, HEIGHT);
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		this.setVisible(false);
	}

	/**
	 * display the about box
	 */
	public void displayBox() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension boxSize = new Dimension(WIDTH, HEIGHT);
		setLocation((screenSize.width - boxSize.width) / 2,
				(screenSize.height - boxSize.height) / 2);
		// the about box is never disposed, so we just need to set it to visible
		// and bring it to front
		this.toFront();
		this.setVisible(true);
	}

	/**
	 * get the about information to be displayed
	 * 
	 * @return information text
	 */
	private String getInfo() {
		return "<html><body><h1>beagle-gui</h1>"
				+ "<p>Version:0.1</p>"
				+ "<p>Beagle homepage: <a href=\"http://sts.thss.tsinghua.edu.cn/beagle\">http://sts.thss.tsinghua.edu.cn/beagle</a></p>"
				+ "<p>Contact: <a href=\"mailto:beagle.thu@gmail.com\">beagle.thu@gmail.com</a></p>"
				+ "</body></html>";
	}
}
