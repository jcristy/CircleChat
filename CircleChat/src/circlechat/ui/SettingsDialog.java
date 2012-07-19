package circlechat.ui;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JDialog;


public class SettingsDialog extends JDialog implements ActionListener
{
	public static boolean run_background = false;
	private JCheckBox cb_run_background;
	public SettingsDialog()
	{
		super();
		cb_run_background =  new JCheckBox("Run In Background");
		add(cb_run_background);
		cb_run_background.setSelected(run_background);
		cb_run_background.addActionListener(this);
		
	}
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if (e.getSource()==cb_run_background)
		{
			run_background = cb_run_background.isSelected();
		}
	}
}
