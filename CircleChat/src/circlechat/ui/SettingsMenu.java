package circlechat.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class SettingsMenu extends JMenu 
{
	public SettingsMenu()
	{
		super("Settings");
		JMenuItem mi_show_options = new JMenuItem("Simplest Settings");
		add(mi_show_options);
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
		addSeparator();
		
	}
}
