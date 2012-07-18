import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

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
	private static String KEY_UID = "UID";
	private static String KEY_HANDLE = "HANDLE";
	private static String KEY_COMMAND = "COMMAND";
	private static String KEY_MESSAGE = "MESSAGE";
	
	private HashMap<String,String> info;
//	private String uid;
//	private String handle;
//	private int command;
//	private String message;
	public static final Message ACK;
	static
	{
		ACK = new Message();
		ACK.setKeyValue(KEY_COMMAND, Values.ACK);
	}
	private Message()
	{
		info = new HashMap<String,String>();
	}
	/**
	 * Generates a message from the given Reader.  For now, expects 4 lines on the data stream.
	 * @param message_br the reader
	 * @deprecated Replaced by Message(InputStream)
	 */
	public Message (BufferedReader message_br)
	{
		try
		{
			System.err.println("NOT SUPPORTED ANYMORE");
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * Generates a message from the given stream.
	 * @param input the InputStream
	 */
	public Message(InputStream input)
	{
		info = new HashMap<String,String>();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
	    BufferedInputStream bis = new BufferedInputStream(input);
	    byte data = 0;
	    try {
		    do
		    {
				data = (byte)bis.read();
				if (data!=Values.END_OF_TRANSMISSION_BLOCK && data!= Values.END_OF_TRANSMISSION) baos.write(data);
		    }while(data!=Values.END_OF_TRANSMISSION_BLOCK && data!= Values.END_OF_TRANSMISSION);
	    } catch (IOException e) {
			e.printStackTrace();
		}
	    System.out.println(baos.toString());
	    
	    SAXParserFactory spf = SAXParserFactory.newInstance();
	    try {
			SAXParser parser = spf.newSAXParser();
			parser.parse(new ByteArrayInputStream(baos.toByteArray()), new DefaultHandler()
			{
				String key;
				public void startElement(String uri, String localName,String qName, org.xml.sax.Attributes attributes) throws SAXException 
		        {
					key = qName;
		        }
				public void endElement(String uri, String localName,String qName) throws SAXException 
				{
					key = "";
				}
				public void characters(char ch[], int start, int length) throws SAXException 
				{
					info.put(key,new String(ch, start, length));
				}
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
		info = new HashMap<String,String>();
		info.put(KEY_UID, uid);
		info.put(KEY_HANDLE, handle);
		info.put(KEY_COMMAND, Command);
		info.put(KEY_MESSAGE, message);
	}
	public Message (String uid, String handle, int command, String message)
	{
		this(uid,handle,commandStr(command),message);
	}
	/**
	 * Utility method to make sending a previously created message easier
	 * @param dos the data output stream to write the message to
	 * @throws IOException see DataOutputStream.write(Byte[])
	 */
	public void sendMessage(DataOutputStream dos) throws IOException
	{
		/*
		dos.write((uid + "\r\n").getBytes());
		dos.write((handle + "\r\n").getBytes());
		dos.write((getCommandString() + "\r\n").getBytes());
		dos.write((message + "\r\n").getBytes());
		*/
		dos.write(("<Message>").getBytes());
		for (String key :info.keySet())
		{
			dos.write(("<"+key+">"+info.get(key)+"</"+key+">").getBytes());
		}
		dos.write(("</Message>"+Values.END_OF_TRANSMISSION).getBytes());
		dos.flush();
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
	public String getHandle()
	{
		return info.get(KEY_HANDLE);
	}
	public String getMessage()
	{
		return info.get(KEY_MESSAGE);
	}
	public String getCommand()
	{
		return info.get(KEY_COMMAND);
	}
	public int getCommandInt()
	{
		return commandInt(getCommand());
	}
	public String getUID()
	{
		return info.get(KEY_UID);
	}
	private void setKeyValue(String Key, String value)
	{
		info.put(Key, value);
	}
}
