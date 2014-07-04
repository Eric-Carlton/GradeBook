package gradebookclient;

import gradebookdata.Packet;

import java.io.*;
import java.net.*;

/**
 * Client for the GradeBook Server, only handles one request per object 
 * @author Eric Carlton
 * 
 */
public class Client {

	private Socket s = null;
	private OutputStream os = null;
	private ObjectOutputStream out = null;
	private InputStream is = null;
	private ObjectInputStream in = null;
	private Packet rec = null;
	private boolean success = false;
	private Packet toSend = null;

	private static final int PORT = 4701;
	private static final String HOST = "54.191.39.190";

	/**
	 * Create a new Client
	 * 
	 * @param toSend
	 *            the Packet to send to the server
	 */
	public Client(Packet toSend) throws IOException, UnknownHostException, ClassNotFoundException {

		this.toSend = toSend;
		setUpClient();
		runClient();

	}

	/**
	 * Get packet sent back by server
	 * @return the Packet sent back by the server
	 */
	public Packet getResponse() {
		return rec;
	}

	/**
	 * See if the request succeeded
	 * @return true if server sent no error messages, false otherwise
	 */
	public boolean succeeded() {
		return success;
	}

	/**
	 * Sets up sockets and streams to connect to server
	 */
	private void setUpClient() throws IOException, UnknownHostException {

		s = new Socket(HOST, PORT);

		os = s.getOutputStream();
		out = new ObjectOutputStream(os);

		is = s.getInputStream();
		in = new ObjectInputStream(is);
	}

	/**
	 * Sends a Packet to the Server and awaits a response
	 */
	private void runClient() throws ClassNotFoundException, IOException {

		//get initial server response
		rec = (Packet) in.readObject();

		if (rec.getIntent() != 0) {
			System.out.println("Unexpected message from server!!");
			System.exit(-1);
		}

		//send the packet
		sendPacket();

		//get server's response to sent packet

		rec = (Packet) in.readObject();


		s.close();
		os.close();
		out.close();
		is.close();
		in.close();

		//999 is success message from server
		if (rec.getIntent() == 999)
			success = true;
		else
			success = false;
	}

	/**
	 * Writes Packet to ObjectOutputStream
	 */
	private void sendPacket() throws IOException {
		out.writeObject(toSend);
	}
}
