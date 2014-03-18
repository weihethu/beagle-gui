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

public class AboutBox extends JFrame {
	private static int WIDTH = 500;
	private static int HEIGHT = 200;

	public AboutBox() {
		this.setTitle("about beagle-gui");
		this.setLayout(new BorderLayout());

		ImageIcon icon = new ImageIcon(getClass().getResource(
				"/assets/icons/icon.png"));
		icon.setImage(icon.getImage().getScaledInstance((int) (WIDTH * 0.3),
				(int) (HEIGHT * 0.7), Image.SCALE_DEFAULT));
		JLabel imageLabel = new JLabel(icon);
		imageLabel.setOpaque(true);
		imageLabel.setBackground(Color.white);

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

	public void displayBox() {

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension boxSize = new Dimension(WIDTH, HEIGHT);
		setLocation((screenSize.width - boxSize.width) / 2,
				(screenSize.height - boxSize.height) / 2);

		this.toFront();
		this.setVisible(true);

	}

	public String getInfo() {
		return "<html><body><h1>beagle-gui</h1>"
				+ "<p>Version:0.1</p>"
				+ "<p>Beagle homepage: <a href=\"http://sts.thss.tsinghua.edu.cn/beagle\">http://sts.thss.tsinghua.edu.cn/beagle</a></p>"
				+ "<p>Contact: <a href=\"mailto:beagle.thu@gmail.com\">beagle.thu@gmail.com</a></p>"
				+ "</body></html>";
	}
}
