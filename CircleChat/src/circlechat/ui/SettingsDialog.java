package circlechat.ui;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JDialog;

import circlechat.general.Settings;


public class SettingsDialog extends JDialog implements ActionListener
{
	private JCheckBox cb_run_background;
	private JCheckBox cb_show_time;
	public SettingsDialog()
	{
		super();
		setLayout(new GridLayout(2,1));
		cb_run_background =  new JCheckBox("Run In Background");
		add(cb_run_background);
		cb_run_background.setSelected(Settings.isRun_background());
		cb_run_background.addActionListener(this);
		
		cb_show_time = new JCheckBox("Show the time");
		add(cb_show_time);
		cb_show_time.setSelected(Settings.isShow_time());
		cb_show_time.addActionListener(this);
	}
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if (e.getSource()==cb_run_background)
		{
			Settings.setRun_background(cb_run_background.isSelected());
		}
		if (e.getSource()==cb_show_time)
		{
			Settings.setShow_time(cb_show_time.isSelected());
		}
	}
}
