package circlechat.network;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.UUID;

import circlechat.general.Message;
import circlechat.ui.ChatClient;
/**
 * LeachServer runs a thread which allows leeches to connect to this node and maintain a persistent connection.  More than one LeachServer can be made to allow for more than one leach per node (each requires a unique port).
 * @author jcristy
 * TODO handle a disconnected node to allow for a new leech to connect (or for a reconnect)
 */
public class LeachServer implements Runnable {
	int port;
	public ServerSocket inbound;
	Socket reply;

	public LeachServer(int port) {
		this.port = port;
	}

	public void run() {
		try {
			inbound = new ServerSocket(port);
			inbound.setSoTimeout(1000);
			System.out.println("Waiting for leach");
			while (true) {
				try {
					reply = inbound.accept();

					ChatClient.tf_leach_ip.setText(reply.getInetAddress()
							.getHostAddress());

					while (!ChatClient.isQuitting()) {
						Message msg = new Message(reply.getInputStream());
						
						
						if (!ChatClient.removeSentMessage(msg.getUID())) 
						{
							Thread t = new Thread(new SendAMessage(
									UUID.fromString(msg.getUID()), msg.getHandle(), msg.getCommand(),
									msg.getMessage()));
							t.start();
						}
					}
				} catch (SocketTimeoutException ste) {
					if (ChatClient.isQuitting()) {
						inbound.close();
						return;
					}
				}
			}

		} catch (Exception e) {
			try {
				inbound.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}

	public void sendMessage(UUID uuid, String handle, String command,
			String message) {

		if (reply != null) {
			try {
				System.out.println("Connected, will try to send");

				ChatClient.addSentMessage(uuid.toString());

				DataOutputStream dos = new DataOutputStream(
						reply.getOutputStream());
				Message msg = new Message((uuid.toString()),handle,command,message);
				
				msg.sendMessage(dos);
				
				dos.flush();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}