package org.brunovandekerkhove.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A class of HTTP servers for responding to HTTP requests. The servers are multi-threaded.
 * 
 * @author	Bruno Vandekerkhove
 * @version	1.0
 * @note The servers are multi-threaded by using thread pools.
 * A thread pool is a group of pre-instantiated, idle threads which stand ready to be given work. 
 * 	These are preferred over instantiating new threads for each task when there is a large number of 
 * 	short tasks to be done rather than a small number of long ones. This prevents having to incur the 
 * 	overhead of creating a thread a large number of times.
 */
public class ServerHTTP {

	/**
	 * Initialize this server with given port number.
	 * 	The server immediately start listening to incoming connections,
	 * 	dealing with each of them on a separate thread.
	 * 
	 * @param 	port
	 * 			The port for this new server.
	 * @throws 	IOException
	 * 			An I/O error occurred.
	 */
	public ServerHTTP(int port) throws IOException {
		
		if (port < 0)
			throw new IllegalArgumentException("Invalid port number.");
		this.port = port;
		
		// Accept incoming connections, create thread for each one of them,
		//	while listening to more incoming connections
		// As stated in the course multi-threading can be done with thread pools
		// https://docs.oracle.com/javase/tutorial/essential/concurrency/pools.html
		// https://softwareengineering.stackexchange.com/questions/173575/what-is-a-thread-pool
		ServerSocket socket = new ServerSocket(getPort());
		try {
            ExecutorService executor = Executors.newCachedThreadPool();
            while (true) {
                // Accept the incoming connection
                Socket incomingSocket = socket.accept();
                Runnable connectionHandler = new ConnectionHandler(incomingSocket);
                executor.execute(connectionHandler); // Execute thread (could be in thread pool)
            }
        } finally {
            socket.close();
        }
		
	}
	
	/**
	 * Returns the port this server listens on.
	 */
	public int getPort() {
		return this.port;
	}
	
	/**
	 * The port this server listens on.
	 */
	private int port;
	
	/**
	 * The entry point for the server.
	 * 
	 * @param 	args
	 * 			Input arguments.
	 */
	public static void main(String[] args) throws IOException {
		
		// Read input
		boolean success = true;
		if (args.length < 1) {
			System.out.println("Invalid arguments. Format should be <port>.");
			success = false;
		}
		else {
			try {
				int port = Integer.parseInt(args[0]);
				@SuppressWarnings("unused")
				ServerHTTP server = new ServerHTTP(port);
			}
			catch (Exception e) {
				System.out.println("Failed to set up server.");
				System.out.println(e.getLocalizedMessage());
				success = false;
			}
		}
        
		// Error occurred
		if (!success)
			System.exit(1);
		
    }
	
}