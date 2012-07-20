package circlechat.ui;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JDialog;


public class SettingsDialog extends JDialog implements ActionListener
{
	private static boolean run_background = true;
	private static boolean show_time = false;
	private JCheckBox cb_run_background;
	private JCheckBox cb_show_time;
	public SettingsDialog()
	{
		super();
		cb_run_background =  new JCheckBox("Run In Background");
		add(cb_run_background);
		cb_run_background.setSelected(run_background);
		cb_run_background.addActionListener(this);
		
		cb_show_time = new JCheckBox("Show the time");
		add(cb_show_time);
		cb_show_time.setSelected(show_time);
		cb_show_time.addActionListener(this);
	}
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if (e.getSource()==cb_run_background)
		{
			run_background = cb_run_background.isSelected();
		}
		if (e.getSource()==cb_show_time)
		{
			show_time = cb_show_time.isSelected();
		}
	}
	public static boolean isRun_in_background()
	{
		return run_background;
	}
	public static boolean showTime() {
		// TODO Auto-generated method stub
		return show_time;
	}
}
