package help;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.swing.JDialog;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.text.StyledDocument;
import javax.swing.text.html.HTMLDocument;

public class Help extends JDialog
{
	private JTextPane htmlviewer;
	
	public Help()
	{
		htmlviewer=new JTextPane();
		htmlviewer.setEditable(false);
		BufferedReader reader = new BufferedReader(new InputStreamReader(Help.class.getResourceAsStream("help.html")));
		char[] buf = new char[1024];
		int numRead=0;
		try{
			String fileData = "";
			while((numRead=reader.read(buf)) != -1){
				String readData = String.valueOf(buf, 0, numRead);
				fileData = fileData + readData;
				buf = new char[1024];
			}
			reader.close();
			
			htmlviewer.setText(fileData);
			htmlviewer.setContentType("text/html");
			htmlviewer.setText(fileData);
		}catch(Exception e)
		{
			htmlviewer.setText("Error building help file");
		}
		
		
		
		
		getContentPane().add(htmlviewer);
		this.setSize(500, 750);
	}
	
}
