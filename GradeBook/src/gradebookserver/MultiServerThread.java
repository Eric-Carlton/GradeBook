package gradebookserver;

import gradebookdata.Packet;

import java.io.*;
import java.net.*;
import java.sql.Connection;

/**
 * Thread of MultiServer that handles one client
 * @author Eric Carlton
 */
public class MultiServerThread extends Thread {

	protected static Connection connection;
	
	private Socket s = null;
	OutputStream os = null;
	ObjectOutputStream out = null;
	InputStream is = null;
	ObjectInputStream in = null;

	/**
	 * Create a new server thread
	 * @param socket Socket to connect to
	 */
	public MultiServerThread(Socket socket) {
		super("MultiServerThread");
		s = socket;
	}

	public void run() {
		setUpStreams();
		runServer();
	}

	/**
	 * Set up all streams to communicate with client
	 */
	private void setUpStreams() {

		try {
			os = s.getOutputStream();
			out = new ObjectOutputStream(os);
		} catch (IOException e) {
			System.out.println("Error creating Server's OutputStream!");
			System.exit(-1);
		}

		try {
			is = s.getInputStream();
			in = new ObjectInputStream(is);
		} catch (IOException e) {
			System.out.println("Error creating Server's InputStream!");
		}
	}

	/**
	 * Send/receive message(s) from client following GBProtocol
	 */
	private void runServer() {
		Packet input = null;
		Packet output = null;

		GBProtocol p = new GBProtocol();

		output = p.processInput(null);
		try {
			out.writeObject(output);
		} catch (IOException e) {
			System.out.println("Error sending packet on server side!");
			System.exit(-1);
		}

		try {
			input = (Packet) in.readObject();
		} catch (ClassNotFoundException e) {
			System.out
					.println("Error reading packet from server side! - ClassNotFound!");
			System.exit(-1);
		} catch (IOException e) {
			System.out
					.println("Error reading packet from server side! - IOException");
			System.exit(-1);
		}

		while (input != null && input.getIntent() != 999) {
			output = p.processInput(input);
			try {
				out.writeObject(output);
			} catch (IOException e) {
				System.out.println("Error sending packet on server side!");
				System.exit(-1);
			}

			if (output.getIntent() / 100 == 9)
				break;

			try {
				is.close();
				os.close();
				in.close();
				out.close();
				s.close();
			} catch (IOException e) {
				System.out.println("Error closing streams!");
				System.exit(-1);
			}

		}

	}
}
