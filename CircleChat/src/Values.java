/**
 * Global values
 * @author jcristy
 *
 */
public class Values 
{
	//PORTS
	/** Socket for a LEECH to connect to, should become a range in the future */
	final static int LEECH_SOCKET = 8081;
	/**The port for ring communications to be sent to */
	final static int RING_SOCKET = 8082;
	
	
	final static String ACK = "ACK";
	
	//COMMANDS
	/** Command to send a message */
	final static String SEND_MESSAGE = "message";
	final static int SEND_MESSAGE_I = 0;
	
	final static String JOIN = "join";
	final static int JOIN_I = 1;
	
	final static String LEAVE = "bye";
	final static int LEAVE_I = 2;
	
	final static String REQUEST_JAR = "jar";
	final static int REQUEST_JAR_I = 3;
	
	final static char END_OF_TRANSMISSION_BLOCK = (char)23;
	final static char END_OF_TRANSMISSION = (char)4;
}
