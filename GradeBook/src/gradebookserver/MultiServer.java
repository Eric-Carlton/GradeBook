package gradebookserver;

import java.net.*;
import java.io.*;

/**
 * Main server of GradeBook System, spawns off server threads for each client that wishes to connect 
 * @author Eric Carlton
 */
public class MultiServer {

	private static final int PORT = 4701;

	public static void main(String[] args) {

		ServerSocket s = null;

		boolean run = true;

		try {
			s = new ServerSocket(PORT);
			System.out.println("Connected on port " + PORT);
		} catch (IOException e) {
			System.err.println("Could not connect on port " + PORT);
			System.exit(-1);
		}

		while (run)
			try {				
				new MultiServerThread(s.accept()).start();
			} catch (IOException e) {
				System.err.println("Could not start new Server thread");
			}

		try {
			s.close();
		} catch (IOException e) {
			System.err.println("Could not close socket");
			System.exit(-1);
		}

	}

}
