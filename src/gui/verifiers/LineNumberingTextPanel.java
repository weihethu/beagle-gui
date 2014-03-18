package gui.verifiers;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Element;

public class LineNumberingTextPanel extends JPanel {
	private JTextArea jta;
	private JTextArea lines;
	private JCheckBoxMenuItem lineNumbersItem;
	private JScrollPane jsp;
	
	public JTextArea getTextArea()
	{
		return jta;
	}
	
	public LineNumberingTextPanel()
	{
		jsp = new JScrollPane();
		jta = new JTextArea();
		lines = new JTextArea("1");
 
		lines.setBackground(Color.LIGHT_GRAY);
		lines.setEditable(false);
 
		jta.getDocument().addDocumentListener(new DocumentListener(){
			public String getText(){
				int caretPosition = jta.getDocument().getLength();
				Element root = jta.getDocument().getDefaultRootElement();
				String text = "1" + System.getProperty("line.separator");
				for(int i = 2; i < root.getElementIndex( caretPosition ) + 2; i++){
					text += i + System.getProperty("line.separator");
				}
				return text;
			}
			@Override
			public void changedUpdate(DocumentEvent de) {
				lines.setText(getText());
			}
 
			@Override
			public void insertUpdate(DocumentEvent de) {
				lines.setText(getText());
			}
 
			@Override
			public void removeUpdate(DocumentEvent de) {
				lines.setText(getText());
			}
 
		});
		
		jsp.getViewport().add(jta);
		jsp.setRowHeaderView(lines);
		jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		this.setLayout(new BorderLayout());
		this.add(jsp, BorderLayout.CENTER);
	}
}