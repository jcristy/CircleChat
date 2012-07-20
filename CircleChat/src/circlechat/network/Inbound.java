package circlechat.network;
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

import javax.swing.JOptionPane;

import circlechat.general.Message;
import circlechat.general.Settings;
import circlechat.general.Values;
import circlechat.ui.ChatClient;
import circlechat.ui.SettingsDialog;
/**
 * Inbound sets up the server to accept messages from the previous hop
 * @author jcristy
 * 
 */
public class Inbound implements Runnable {

	private ServerSocket inbound;

	
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
						Date date = new Date(Long.parseLong(msg.getValue(Message.KEY_CREATION_TIME)));
						ChatClient.addToMessages(msg.getHandle() + " "+(Settings.isShow_time()?"("+date.toString()+")":"") + ": " + msg.getMessage());
						if (!ChatClient.removeSentMessage(msg.getUID())) {
							Thread t = new Thread(new SendAMessage(
									UUID.fromString(msg.getUID()), msg.getHandle(), msg.getCommand(), msg.getMessage()));
							t.start();
						}
						dos.close();
						reply.close();
						break;
					case Values.JOIN_I:
						Message ourResponse = new Message("","",Values.ACK,ChatClient.getNextHop());
						ourResponse.sendMessage(dos);
						Message theirResponse = new Message(reply.getInputStream());
						if (theirResponse.getCommand().equals(Values.ACK))
							ChatClient.setNextHop(reply.getInetAddress().getHostAddress());
						dos.close();
						reply.close();
						break;
					case Values.LEAVE_I:
						ChatClient.setNextHop(msg.getMessage());
						Message.ACK.sendMessage(dos);
						dos.close();
						reply.close();
						break;
					case Values.REQUEST_JAR_I:
						System.out.println("Send the file back");
						Thread t = new Thread(new FileDownloadHelper(reply, dos));
						t.start();
						break;
					default:
					}
					
					
					
					
				} catch (SocketTimeoutException ste) {
					if (ChatClient.isQuitting()) {
						inbound.close();
						return;
					}
				}
			}

		} catch (IOException e) {
			ChatClient.notify("There May Already be an instance of Chat Client running.  Check the System Tray.","Server Error",JOptionPane.ERROR_MESSAGE,0);
			e.printStackTrace();
			try {
				inbound.close();
			} catch (IOException e1) {
				System.out.println("Seriously failed");
			}
		}
	}
	public void closeSocket() throws IOException
	{
		if (inbound!=null)
			inbound.close();
	}
	/**
	 * FileDownloadHelper is a separate thread to handle requests for the JAR file
	 * @author jcristy
	 *
	 */
	public class FileDownloadHelper implements Runnable
	{
		Socket reply;
		DataOutputStream dos;
		public FileDownloadHelper(Socket reply, DataOutputStream dos) 
		{
			this.reply = reply;
			this.dos = dos;
		}

		public void run()
		{
			try{
				File f = new File(".");
				
				boolean wroteFile = false;
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
						wroteFile = true;
					}
				}
				if (!wroteFile)
				{
					String html = "<html><body>404 CircleChat Client not available for download from this host!</body></html>";
					System.out.println("File Not Found");
					dos.write("HTTP/1.0 404 Not Found\r\n".getBytes());
					dos.write("Content-Type: text/html\r\n".getBytes());
					dos.write(("Content-Length: "+html.getBytes().length+"\r\n").getBytes());
					dos.write(("\r\n").getBytes());
					dos.write(html.getBytes());
					dos.flush();
				}
			}catch (Exception e)
			{
				
			}
			try 
			{
				dos.close();
				reply.close();
			} catch (IOException e1) 
			{
				e1.printStackTrace();
			}
		}
	}
}
