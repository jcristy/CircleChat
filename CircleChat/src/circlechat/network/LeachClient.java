package circlechat.network;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.UUID;

import circlechat.general.Message;
import circlechat.general.Values;
import circlechat.ui.ChatClient;
/**
 * LeachClient is used by a leech node to maintain the persistent connection to the leech server on the host node
 * @author jcristy
 *
 */
public class LeachClient implements Runnable {
	Socket s;
	String Host_Address;
	
	/**
	 * Standard Constructor
	 * @param Host IP Address of the host
	 */
	public LeachClient(String Host)
	{
		Host_Address = Host;
	}

	public void run() {
		try {

			s = new Socket();
			s.connect(new InetSocketAddress(Host_Address, Values.LEECH_SOCKET), 2000);
			while (true) {
				
				Message msg = new Message(s.getInputStream());

				switch (msg.getCommandInt())
				{
				case Values.SEND_MESSAGE_I:
					ChatClient.addToMessages(msg.getHandle() + " " + ": " + msg.getMessage());
					break;
				case Values.JOIN_I:
					
					break;
				case Values.LEAVE_I:
					
					break;
				default:
				}
			}

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void sendMessage(UUID uuid, String handle, String command,
			String message) {

		if (s != null) {
			try {
				System.out.println("(LeachClient)Connected, will try to send");

				ChatClient.addSentMessage(uuid.toString());

				DataOutputStream dos = new DataOutputStream(s.getOutputStream());
				Message msg = new Message((uuid.toString()),handle,command,message);
				
				msg.sendMessage(dos);

				dos.flush();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}