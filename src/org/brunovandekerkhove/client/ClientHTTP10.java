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
 *  A new connection is made for each command.
 * 
 * @author 	Bruno Vandekerkhove
 * @version 	1.0
 */
public class ClientHTTP10 extends ClientHTTP {
	
	/**
	 * Returns the HTTP version for this client.
	 */
	public HTTPVersion getVersion() {
		return HTTPVersion.HTTP_10;
	}

	@Override
	public List<HTTPResponse> process(List<HTTPCommand> commands) throws UnknownHostException, IOException {
		ArrayList<HTTPResponse> responses = new ArrayList<HTTPResponse>();
		for (HTTPCommand command : commands) { // Process request one by one
			CommandProcessor processor = CommandProcessor.processorForCommand(command.getType(), getVersion());
			if (processor == null) // Invalid command or version mismatch
				continue;
			HTTPSocket socket = new HTTPSocket(command.getHost(), command.getPort());
			processor.process(command, socket, getVersion());
			responses.add(processor.getResponse());
			socket.close();
		}
		return responses;
	}
	
}