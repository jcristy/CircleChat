package circlechat.general;
/**
 * Global values
 * @author jcristy
 *
 */
public class Values 
{
	//PORTS
	/** Socket for a LEECH to connect to, should become a range in the future, or rather it should be released once a leech is connected */
	public final static int LEECH_SOCKET = 8083;
	/**The port for ring communications to be sent to */
	public final static int RING_SOCKET = 8082;
	
	
	public final static String ACK = "ACK";
	
	//COMMANDS
	/** Command to send a message */
	public final static String SEND_MESSAGE = "message";
	public final static int SEND_MESSAGE_I = 0;
	
	/** Command to ask to join the network */
	public final static String JOIN = "join";
	public final static int JOIN_I = 1;
	
	/** Command to announce you are leaving */
	public final static String LEAVE = "bye";
	public final static int LEAVE_I = 2;
	
	/** Command to request the JAR File, should not be set by a client...only by a server interpreting a command from a browser*/
	public final static String REQUEST_JAR = "jar";
	public final static int REQUEST_JAR_I = 3;
	
	public final static char END_OF_TRANSMISSION_BLOCK = (char)23;
	public final static char END_OF_TRANSMISSION = (char)4;
}
