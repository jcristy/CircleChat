import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.UUID;
/**
 * LeachClient is used by a leech node to maintain the persistent connection to the leech server on the host node
 * @author jcristy
 *
 */
public class LeachClient implements Runnable {
	Socket s;

	public void run() {
		try {

			s = new Socket();
			s.connect(new InetSocketAddress(ChatClient.getNextHop(), Values.LEECH_SOCKET), 2000);
			while (true) {
				BufferedReader message_br = new BufferedReader(new InputStreamReader(s.getInputStream()));
				
				Message msg = new Message(message_br);

				switch (msg.getCommand())
				{
				case Values.SEND_MESSAGE_I:
					ChatClient.addToMessages(msg.handle + " " + ": " + msg.message);
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

				ChatClient.sent_messages.add(uuid.toString());

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