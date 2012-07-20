package circlechat.network;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
	private int port;
	private ServerSocket inbound;
	private Socket reply;

	public LeachServer(int port) {
		this.port = port;
	}

	public void run() {
		try {
			FileOutputStream fos = new FileOutputStream("log.txt");
			inbound = new ServerSocket(port);
			inbound.setSoTimeout(1000);
			System.out.println("Waiting for leach");
			fos.write("Waiting".getBytes());
			fos.flush();
			while (true) {
				try {
					reply = inbound.accept();
					fos.write("Connected".getBytes());
					fos.flush();
					ChatClient.setLeechText(reply.getInetAddress()
							.getHostAddress());
					
					Message.ACK.sendMessage(new DataOutputStream(reply.getOutputStream()));
					fos.write("Sent the ACK".getBytes());
					fos.flush();
					while (!ChatClient.isQuitting()) {
						Message msg = new Message(reply.getInputStream());
						fos.write("Got a message".getBytes());
						fos.flush();
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
				if (inbound!=null)
					inbound.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				FileOutputStream fos = new FileOutputStream("log.txt");
				fos.write(("Error"+e.getMessage()).getBytes());
				fos.close();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e.printStackTrace(); 
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
	public void closeSocket() throws IOException
	{
		inbound.close();
	}

}