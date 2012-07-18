import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Message, handles the current protocol:
 * uid
 * handle
 * command
 * message
 * @author jcristy
 * TODO Expand to handle commands such as "who is your next node" and "let me join", etc.
 */
public class Message 
{
	String uid;
	String handle;
	private int command;
	String message;
	
	/**
	 * Generates a message from the given Reader.  For now, expects 4 lines on the data stream.
	 * @param message_br the reader
	 * @deprecated
	 */
	public Message (BufferedReader message_br)
	{
		try
		{
			uid = message_br.readLine();
			handle = message_br.readLine();
			String temp = message_br.readLine();
			command = commandInt(temp);
			message = message_br.readLine();
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public Message (InputStream input)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
	    BufferedInputStream bis = new BufferedInputStream(input);
	    byte data = 0;
	    try {
		    do
		    {
				data = (byte)bis.read();
				baos.write(data);
		    }while(data!=Values.END_OF_TRANSMISSION_BLOCK && data!= Values.END_OF_TRANSMISSION);
	    } catch (IOException e) {
			e.printStackTrace();
		}
	    byte[] all_data = baos.toByteArray();
	    
	    
	    
	    SAXParserFactory spf = SAXParserFactory.newInstance();
	    try {
			SAXParser parser = spf.newSAXParser();
			parser.parse(, new DefaultHandler(){
				
			});
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	}
	/**
	 * Allows creation of a message a user would like to send
	 * @param uid
	 * @param handle
	 * @param command
	 * @param message
	 */
	public Message (String uid, String handle, String Command, String message)
	{
		this(uid,handle,commandInt(Command),message);
	}
	public Message (String uid, String handle, int command, String message)
	{
		this.uid = uid;
		this.handle = handle;
		this.command = command;
		this.message = message;
	}
	/**
	 * Utility method to make sending a previously created message easier
	 * @param dos the data output stream to write the message to
	 * @throws IOException see DataOutputStream.write(Byte[])
	 */
	public void sendMessage(DataOutputStream dos) throws IOException
	{
		dos.write((uid + "\r\n").getBytes());
		dos.write((handle + "\r\n").getBytes());
		dos.write((getCommandString() + "\r\n").getBytes());
		dos.write((message + "\r\n").getBytes());
	}
	public static int commandInt(String cmd)
	{
		if (cmd.equals(Values.SEND_MESSAGE))
			return Values.SEND_MESSAGE_I;
		else if (cmd.equals(Values.JOIN))
			return Values.JOIN_I;
		else if (cmd.equals(Values.LEAVE))
			return Values.LEAVE_I;
		else
			return -1;
	}
	public String getCommandString()
	{
		return commandStr(command);
	}
	public int getCommand()
	{
		return command;
	}
	public static String commandStr(int cmd)
	{
		switch(cmd)
		{
		case Values.SEND_MESSAGE_I:
			return Values.SEND_MESSAGE;
			
		case Values.LEAVE_I:
			return Values.LEAVE;
			
		case Values.JOIN_I:
			return Values.JOIN;
			
		default:
			return "";
		}
	}
}
