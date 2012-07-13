import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.UUID;
/**
 * SendAMessage handles sending a message to the next hop (or to the leach server if in leach mode)
 * @author jcristy
 *
 */
public class SendAMessage implements Runnable {
	UUID uuid;
	String handle;
	String command;
	String message;

	public SendAMessage(UUID uuid, String handle, String command, String message) {
		this.uuid = uuid;
		this.handle = handle;
		this.command = command;
		this.message = message;
	}

	public void run() {
		try {
			if (ChatClient.rb_next_hop.isSelected()) {
				Socket s = new Socket();
				s.connect(new InetSocketAddress(ChatClient.getNextHop(), Values.RING_SOCKET),
						2000);


				ChatClient.sent_messages.add(uuid.toString());

				DataOutputStream dos = new DataOutputStream(s.getOutputStream());
				Message msg = new Message((uuid.toString()),handle,command,message);
				msg.sendMessage(dos);

				dos.flush();

				BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
				String ack = br.readLine();
				if (ack.equals(Values.ACK)) {
					System.out.println("Success");
				}

				dos.close();

				s.close();
				if (ChatClient.leech_server != null)
					ChatClient.leech_server.sendMessage(uuid, handle, command,
							message);
			} else if (ChatClient.rb_leach.isSelected()
					&& ChatClient.leach_client != null) {
				ChatClient.leach_client.sendMessage(uuid, handle, command,
						message);
			}

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}