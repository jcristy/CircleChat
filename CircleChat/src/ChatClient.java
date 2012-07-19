import help.Help;

import java.awt.AWTException;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.MenuBar;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.JButton;
/**
 * Chat client is a ring based chat application
 * @author jcristy
 *
 */
public class ChatClient {
	

	static JFrame theFrame;

	static JTextField tf_next_hop;
	static JTextField tf_handle;
	static JTextField tf_message;
	static JTextArea ta_messages;
	static JTextField tf_prev_hop;
	static JTextField tf_leach_ip;
	static JScrollPane sp_for_messages;
	static JButton btn_join;
	static JButton btn_exit;

	static JRadioButton rb_leach;
	static JRadioButton rb_next_hop;

	static boolean quit = false;

	static ArrayList<String> sent_messages;

	static LeachServer leech_server;
	static LeachClient leach_client;
	static Inbound inbound;
	
	public static ToTheTray ttt;

	public static void main(String[] args) {
		sent_messages = new ArrayList<String>();

		theFrame = new JFrame();
		theFrame.setTitle("Circle Chat");
		JMenuBar themenubar = new ChatClientMenuBar();
		theFrame.setJMenuBar(themenubar);
		

		tf_next_hop = new JTextField();
		tf_handle = new JTextField();
		tf_message = new JTextField(50);
		ta_messages = new JTextArea();
		tf_prev_hop = new JTextField();
		tf_leach_ip = new JTextField();

		final ButtonGroup leach_next_hop = new ButtonGroup();
		
		btn_join = new JButton("Join");
		btn_exit = new JButton("Exit Gracefully");
		btn_join.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				final JRadioButton rb_Automatic = new JRadioButton("Automatic Join Circle");
				final JRadioButton rb_Leech  = new JRadioButton("Leech onto");
				final JRadioButton rb_Manual = new JRadioButton("Manual Join Circle");
				ButtonGroup options = new ButtonGroup();
				final JButton Join = new JButton("Join");
				options.add(rb_Automatic);
				options.add(rb_Leech);
				options.add(rb_Manual);
				
				final JTextField tf_ip = new JTextField();
				
				final JDialog dialog = new JDialog(theFrame,"Join",true);
				dialog.getContentPane().setLayout(new GridLayout(5,1));
				dialog.getContentPane().add(rb_Automatic);
				dialog.getContentPane().add(rb_Leech);
				dialog.getContentPane().add(rb_Manual);
				dialog.getContentPane().add(tf_ip);
				dialog.getContentPane().add(Join);
				dialog.pack();
				
				Join.addActionListener(new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent arg0) 
					{
						if (rb_Automatic.isSelected())
						{
							rb_next_hop.setSelected(true);
							Thread t = new Thread(new SendAMessage(UUID.randomUUID(),
											tf_handle.getText(), 
											Values.JOIN, 
											"",tf_ip.getText()));
							t.start();
						}
						else if (rb_Leech.isSelected())
						{
							rb_leach.setSelected(true);
							System.out.println("Leech will try to start!");
							leach_client = new LeachClient(tf_ip.getText());
							Thread leach = new Thread(leach_client);
							leach.start();
						}
						else if (rb_Manual.isSelected())
						{
							tf_next_hop.setText(tf_ip.getText());
						}
						dialog.setVisible(false);
					}
					
				});
				
				dialog.setVisible(true);
				
			}
		});

		btn_exit.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) 
			{
				//We want to block so the message is sent before we shut the program down
				SendAMessage sam = new SendAMessage(UUID.randomUUID(),
						tf_handle.getText(), 
						Values.LEAVE, 
						tf_next_hop.getText(),tf_prev_hop.getText());
				sam.run();
				tf_next_hop.setText("");
				leach_next_hop.clearSelection();
			}
			
		});
		
		tf_prev_hop.setEditable(false);
		tf_leach_ip.setEditable(false);
		tf_next_hop.setEditable(false);

		JPanel Controls = new JPanel();
		Controls.setLayout(new GridLayout(5, 2));

		JPanel NextHopLeach = new JPanel();
		

		rb_leach = new JRadioButton("Leech onto:");
		rb_leach.setEnabled(false);
		rb_next_hop = new JRadioButton("Next Hop:");
		rb_next_hop.setEnabled(false);

		rb_next_hop.setSelected(true);
		rb_leach.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				if (rb_leach.isSelected()) {
					
				}
			}

		});

		leach_next_hop.add(rb_next_hop);
		leach_next_hop.add(rb_leach);
		NextHopLeach.add(rb_next_hop);
		NextHopLeach.add(rb_leach);

		Controls.add(btn_join);
		Controls.add(btn_exit);
		Controls.add(NextHopLeach);
		Controls.add(tf_next_hop);
		Controls.add(new JLabel("Prev Hop:"));
		Controls.add(tf_prev_hop);
		Controls.add(new JLabel("Leech:"));
		Controls.add(tf_leach_ip);
		Controls.add(new JLabel("Handle:"));
		Controls.add(tf_handle);
		

		ta_messages = new JTextArea(20, 50);
		ta_messages.setEditable(false);
		sp_for_messages = new JScrollPane(ta_messages);

		theFrame.getContentPane().setLayout(
				new BoxLayout(theFrame.getContentPane(), BoxLayout.Y_AXIS));

		theFrame.getContentPane().add(Controls);
		theFrame.getContentPane().add(sp_for_messages);

		JPanel Editor = new JPanel();
		// Editor.add(new JLabel("Message:"));
		Editor.add(tf_message);
		tf_message.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "SEND");
		tf_message.getActionMap().put("SEND", new AbstractAction() {
			
			public void actionPerformed(ActionEvent arg0) {

				Thread t = new Thread(
						new SendAMessage(UUID.randomUUID(),
								tf_handle.getText(), Values.SEND_MESSAGE, tf_message
										.getText()));
				t.start();
				tf_message.setText("");
			}
		});

		theFrame.getContentPane().add(Editor);

		theFrame.addWindowListener(new WindowListener() {

			public void windowActivated(WindowEvent arg0) {
			}

			public void windowClosed(WindowEvent arg0) {
				
				
			}

			public void windowClosing(WindowEvent arg0) 
			{
				if (!SettingsDialog.run_background && inbound != null)
						close();
				if (SettingsDialog.run_background)
				{
					System.out.println("got here");
					theFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
					final ToTheTray ttt = new ToTheTray();
					ttt.toTheTray(theFrame);
				}
			}

			public void windowDeactivated(WindowEvent arg0) {
			}

			public void windowDeiconified(WindowEvent arg0) {
			}

			public void windowIconified(WindowEvent arg0) {
			}

			public void windowOpened(WindowEvent arg0) {
			}

		});

		theFrame.pack();
		theFrame.setVisible(true);
		theFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		inbound = new Inbound();
		Thread inbound_thread = new Thread(inbound);
		inbound_thread.start();

		leech_server = new LeachServer(Values.LEECH_SOCKET);
		Thread leach_thread = new Thread(leech_server);
		leach_thread.start();
	}
	/**
	 * Adds a line to the chat log
	 * @param text	The text to add (typically handle:message
	 */
	public static void addToMessages(String text) {
		
		ttt.notifyMessage();
		ta_messages.setText(ta_messages.getText() + "\r\n" + text);
		ta_messages.setCaretPosition(ta_messages.getDocument().getLength());
		
	}
	/**
	 * Sets the previous hop data field
	 * @param text	sets it to this information, should be an ip address (or a host name)
	 */
	public static void setPrevHop(String text) {
		tf_prev_hop.setText(text);
	}

	/**
	 * Gets the user configured next hop field
	 * @return the next hop not verified
	 */
	public static String getNextHop() {
		return tf_next_hop.getText();
	}
	/**
	 * Sets the next hop field
	 * @parap text the new IP address
	 */
	public static void setNextHop(String text) {
		tf_next_hop.setText(text);
	}
	public static void close()
	{
		System.out.println("Shutting Down");
		try{
			if (inbound!=null)
				inbound.inbound.close();
			if (leech_server!=null)
				leech_server.inbound.close();
		}catch(Exception e){e.printStackTrace();}
		System.exit(0);
	}
}
