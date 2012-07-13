import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;

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

	static JRadioButton rb_leach;
	static JRadioButton rb_next_hop;

	static boolean quit = false;

	static ArrayList<String> sent_messages;

	static LeachServer leech_server;
	static LeachClient leach_client;
	static Inbound inbound;

	public static void main(String[] args) {
		sent_messages = new ArrayList<String>();

		theFrame = new JFrame();

		tf_next_hop = new JTextField();
		tf_handle = new JTextField();
		tf_message = new JTextField(50);
		ta_messages = new JTextArea();
		tf_prev_hop = new JTextField();
		tf_leach_ip = new JTextField();

		tf_prev_hop.setEditable(false);
		tf_leach_ip.setEditable(false);

		JPanel Controls = new JPanel();
		Controls.setLayout(new GridLayout(4, 2));

		JPanel NextHopLeach = new JPanel();
		ButtonGroup leach_next_hop = new ButtonGroup();

		rb_leach = new JRadioButton("Leech onto:");
		rb_next_hop = new JRadioButton("Next Hop:");

		rb_next_hop.setSelected(true);
		rb_leach.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (rb_leach.isSelected()) {
					System.out.println("Leech will try to start!");
					leach_client = new LeachClient();
					Thread leach = new Thread(leach_client);
					leach.start();
				}
			}

		});

		leach_next_hop.add(rb_next_hop);
		leach_next_hop.add(rb_leach);
		NextHopLeach.add(rb_next_hop);
		NextHopLeach.add(rb_leach);

		Controls.add(NextHopLeach);
		Controls.add(tf_next_hop);
		Controls.add(new JLabel("Handle:"));
		Controls.add(tf_handle);
		Controls.add(new JLabel("Prev Hop:"));
		Controls.add(tf_prev_hop);
		Controls.add(new JLabel("Leech:"));
		Controls.add(tf_leach_ip);

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
			@Override
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

			@Override
			public void windowActivated(WindowEvent arg0) {
			}

			@Override
			public void windowClosed(WindowEvent arg0) {
				if (inbound != null)
					try {
						inbound.inbound.close();
					} catch (IOException e) {

						e.printStackTrace();
					}
			}

			@Override
			public void windowClosing(WindowEvent arg0) {
			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {
			}

			@Override
			public void windowDeiconified(WindowEvent arg0) {
			}

			@Override
			public void windowIconified(WindowEvent arg0) {
			}

			@Override
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

}
