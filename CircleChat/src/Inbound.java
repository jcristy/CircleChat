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

	@Override
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
					
					if (msg.command.equals(Values.SEND_MESSAGE))
						dos.write(("Got it!\r\n").getBytes());

					dos.close();
					reply.close();
					message_br.close();
					ChatClient.addToMessages(msg.handle + " " + ": " + msg.message);
					if (!ChatClient.sent_messages.remove(msg.uid)) {
						Thread t = new Thread(new SendAMessage(
								UUID.fromString(msg.uid), msg.handle, msg.command, msg.message));
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
