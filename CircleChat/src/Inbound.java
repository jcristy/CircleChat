import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.UUID;
/**
 * Inbound sets up the server to accept messages from the previous hop
 * @author jcristy
 * 
 */
public class Inbound implements Runnable {

	public ServerSocket inbound;

	
	public void run() {
		try {

			inbound = new ServerSocket(Values.RING_SOCKET);
			inbound.setSoTimeout(1000);
			while (true) {
				try {
					Socket reply = inbound.accept();
					System.out.println("Got a message");
					ChatClient.setPrevHop(reply.getInetAddress()
							.getHostAddress());
					DataOutputStream dos = new DataOutputStream(
							reply.getOutputStream());

					BufferedReader message_br = new BufferedReader(	new InputStreamReader(reply.getInputStream()));
					
					Message msg = new Message(message_br);
					
					
					switch (msg.getCommand())
					{
					case Values.SEND_MESSAGE_I:
						dos.write(Values.ACK.getBytes());
						ChatClient.addToMessages(msg.handle + " " + ": " + msg.message);
						break;
					case Values.JOIN_I:
						dos.write((ChatClient.getNextHop()+"\r\n").getBytes());
						dos.flush();
						String response = message_br.readLine();
						if (response.equals(Values.ACK))
							ChatClient.setNextHop(reply.getInetAddress().getHostAddress());
						break;
					case Values.LEAVE_I:
						
						break;
					default:
					}
					
					dos.close();
					reply.close();
					message_br.close();
					
					if (!ChatClient.sent_messages.remove(msg.uid)) {
						Thread t = new Thread(new SendAMessage(
								UUID.fromString(msg.uid), msg.handle, msg.getCommandString(), msg.message));
						t.start();
					}
				} catch (SocketTimeoutException ste) {
					if (ChatClient.quit) {
						inbound.close();
						return;
					}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
			try {
				inbound.close();
			} catch (IOException e1) {
				System.out.println("Seriously failed");
			}
		}
	}

}
