package circlechat.ui;
import help.Help;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;


public class ChatClientMenuBar extends JMenuBar
{
	public ChatClientMenuBar()
	{
		super();
		JMenu menu_settings  = new JMenu("Settings");
		JMenu menu_help = new JMenu("Help");
		add(menu_settings);
		add(menu_help);
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
		JMenuItem mi_show_options = new JMenuItem("Simplest Settings");
		menu_settings.add(mi_show_options);
		mi_show_options.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				System.out.println("Here!");
				JDialog SettingsDialog = new SettingsDialog();
				SettingsDialog.pack();
				SettingsDialog.setVisible(true);
			}
			
		});

	}
}
