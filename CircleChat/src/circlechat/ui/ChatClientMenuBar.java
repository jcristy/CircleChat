package circlechat.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import circlechat.help.Help;


public class ChatClientMenuBar extends JMenuBar
{
	public ChatClientMenuBar()
	{
		super();
		JMenu menu_settings = new SettingsMenu();
		JMenu menu_help = new JMenu("Help");
		JMenu menu_file = new JMenu("File");
		
		
		JMenuItem mi_show_help = new JMenuItem("Help");
		mi_show_help.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				JDialog HelpDialog = new Help();
				HelpDialog.setVisible(true);
			}
			
		});
		menu_help.add(mi_show_help);
		JMenuItem mi_clear = new JMenuItem("Clear");
		mi_clear.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				ChatClient.clearMessages();
			}
			
		});
		JMenuItem mi_exit  = new JMenuItem("Exit");
		mi_exit.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				ChatClient.close();
			}
			
		});
		JMenuItem mi_save = new JMenuItem("Save");
		mi_save.setEnabled(false);
		
		menu_file.add(mi_save);
		menu_file.add(mi_clear);
		menu_file.addSeparator();
		menu_file.add(mi_exit);
		
		add(menu_file);
		add(menu_settings);
		add(menu_help);
	}
}
