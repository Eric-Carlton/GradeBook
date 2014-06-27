package gradebookdata;

import java.io.Serializable;

/**
 * Represents information sent between client and server in GradeBook System
 * @author Eric Carlton
 *
 */
public class Packet implements Serializable {

	private static final long serialVersionUID = 1L;
	
	//numeric code that represents a status message from server to client
	//or allows the server to quickly recognize what the client is requesting
	private int intent;
	
	//String that represents a text message from server to client
	//or parameters of an intent from client to server
	private String data;

	/**
	 * Create a new packet
	 * @param intent intent/status code
	 * @param data String of information to send
	 */
	public Packet(int intent, String data) {
		this.intent = intent;
		this.data = data;
	}

	public int getIntent() {
		return this.intent;
	}

	public String getData() {
		return data;
	}

}
