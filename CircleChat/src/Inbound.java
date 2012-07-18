import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Date;
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
					
					DataOutputStream dos = new DataOutputStream(
							reply.getOutputStream());

					//BufferedReader message_br = new BufferedReader(	new InputStreamReader(reply.getInputStream()));
					
					Message msg = new Message(reply.getInputStream());
					
					
					switch (msg.getCommandInt())
					{
					case Values.SEND_MESSAGE_I:
						ChatClient.setPrevHop(reply.getInetAddress()
								.getHostAddress());
						Message.ACK.sendMessage(dos);
						ChatClient.addToMessages(msg.getHandle() + " " + ": " + msg.getMessage());
						if (!ChatClient.sent_messages.remove(msg.getUID())) {
							Thread t = new Thread(new SendAMessage(
									UUID.fromString(msg.getUID()), msg.getHandle(), msg.getCommand(), msg.getMessage()));
							t.start();
						}
						break;
					case Values.JOIN_I:
						Message ourResponse = new Message("","",Values.ACK,ChatClient.getNextHop());
						ourResponse.sendMessage(dos);
						Message theirResponse = new Message(reply.getInputStream());
						if (theirResponse.getCommand().equals(Values.ACK))
							ChatClient.setNextHop(reply.getInetAddress().getHostAddress());
						break;
					case Values.LEAVE_I:
						ChatClient.setNextHop(msg.getMessage());
						break;
					case Values.REQUEST_JAR_I:
						System.out.println("Send the file back");
						
						File f = new File(".");
						
						for (File file :f.listFiles())
						{
						if (file.getName().equals("CircleChat.jar"))
							{
								dos.write("HTTP/1.0 200 OK\r\n".getBytes());
								dos.write(("Date: Fri, 31 Dec 1999 23:59:59 GMT\r\n").getBytes());
								dos.write("Content-Type: binary/octet-stream\r\n".getBytes());
								//dos.write("Content-Type: text/txt\r\n".getBytes());
								dos.write("Content-Disposition: attachment; filename=CircleChat.jar\r\n".getBytes());
								dos.write(("Content-Length: "+file.length()+"\r\n").getBytes());
								//dos.write(("Content-Length: "+("Good!".getBytes().length)+"\r\n").getBytes());
								dos.write("\r\n".getBytes());
								
								
								dos.flush();
								FileInputStream fis = new FileInputStream(file);
								for (int i=0; i<file.length();i++)
									dos.write(fis.read());
								 
							}
						}
						break;
					default:
					}
					
					dos.close();
					reply.close();
					
					
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
