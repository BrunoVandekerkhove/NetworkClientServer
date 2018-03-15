package org.brunovandekerkhove.client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.brunovandekerkhove.http.HTTPCommand;
import org.brunovandekerkhove.http.HTTPResponse;
import org.brunovandekerkhove.http.HTTPSocket;
import org.brunovandekerkhove.http.HTTPVersion;

/**
 * A class of HTTP 1.0 clients for communicating with a server and parsing the response.
 *  One socket is used for each host/port combo.
 * 
 * @author 	Bruno Vandekerkhove
 * @version 	1.0
 */
public class ClientHTTP11 extends ClientHTTP {

	/**
	 * Returns the HTTP version for this client.
	 */
	public HTTPVersion getVersion() {
		return HTTPVersion.HTTP_11;
	}
	
	/**
	 * Get the socket for the given host name and port number.
	 *  This method allows for recycling sockets when the host and port
	 *  of an URI are the same.
	 * 
	 * @param 	host
	 * 			The host name for the socket.
	 * @param 	port
	 * 			The port number for the socket.
	 * @return	Any available socket matching the given host name and port number,
	 * 			or a newly created one if necessary.
	 */
	private HTTPSocket socketFor(String host, int port) {
		
		// Consider available sockets
		for (HTTPSocket socket : sockets) {
			if (socket.getPort() == port && socket.getHost().equalsIgnoreCase(host)) {
				if (socket.isClosed()) {
					sockets.remove(socket);
					break;
				}
				return socket;
			}
		}
		
		// No available socket, create one
		HTTPSocket newSocket;
		try {
			newSocket = new HTTPSocket(host, port);
			sockets.add(newSocket);
			return newSocket;
		} catch (IOException e) {
			System.out.println("An error occurred while opening a new socket (host = " + host + ", port = " + port + ")");
		}
		
		// No new socket could be created
		return null;
		
	}
	
	/**
	 * A list of all available sockets for this client.
	 */
	private ArrayList<HTTPSocket> sockets = new ArrayList<HTTPSocket>();

	@Override
	public List<HTTPResponse> process(List<HTTPCommand> commands) throws UnknownHostException, IOException {
		
		// Let the commands be processed by the appropriate processors
		//	Note that sockets are recycled here (HTTP version 1.1)
		ArrayList<CommandProcessor> processors = new ArrayList<CommandProcessor>();
		for (HTTPCommand command : commands)  {
			CommandProcessor processor = CommandProcessor.processorForCommand(command.getType(), getVersion());
			HTTPSocket socket = socketFor(command.getHost(), command.getPort());
			processor.process(command, socket, getVersion());
			processors.add(processor);
		}
			
		// Fetch all responses and return them
		ArrayList<HTTPResponse> responses = new ArrayList<HTTPResponse>();
		for (CommandProcessor processor : processors)
			responses.add(processor.getResponse());
	    return responses;
		
	}
	
}