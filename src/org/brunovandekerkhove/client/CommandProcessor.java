package org.brunovandekerkhove.client;

import java.io.IOException;

import org.brunovandekerkhove.http.HTTPCommand;
import org.brunovandekerkhove.http.HTTPRequest;
import org.brunovandekerkhove.http.HTTPResponse;
import org.brunovandekerkhove.http.HTTPSocket;
import org.brunovandekerkhove.http.HTTPVersion;
import org.brunovandekerkhove.utils.ClosedSocketException;
import org.brunovandekerkhove.utils.SocketUtils;

/**
 * A class for processing HTTP commands.
 * 
 * @author 	Bruno Vandekerkhove
 * @version 	1.0
 */
public abstract class CommandProcessor {
	
	/**
	 * Initializes a new HTTP command processor for dealing with the given type of command.
	 * 
	 * @param	command
	 * 			A textual representation of the command that is to be processed.
	 * @param	version
	 * 			The HTTP version the processor will be used for.
	 * @return	An appropriate command processor for dealing with the given command,
	 * 			or null if the given command has no match.
	 */
	public static CommandProcessor processorForCommand(String command, HTTPVersion version) {
		if (version == null)
			return null;
		switch (command) {
		case "GET":
			return new CommandProcessorGET();
		case "HEAD":
			return new CommandProcessorHEAD();
		case "POST":
			return new CommandProcessorPOST();
		case "PUT":
			return (version == HTTPVersion.HTTP_10 ? null : new CommandProcessorPUT());
		default:
			return null;
		}
	}
	
	/**
	 * Process the given HTTP command.
	 * 
	 * @param 	command
	 * 			The command that is to be processed.
	 * @param	socket
	 * 			The socket that is to be used for processing the request.
	 * @param	version
	 * 			The HTTP version that is to be used.
	 * @throws	IOException 
	 * 			An I/O error occurred.
	 */
	public void process(HTTPCommand command, HTTPSocket socket, HTTPVersion version) throws IOException {
		
		// Make & send a request (a host header is added even when the HTTP version is 1.0,
		//	since many servers still expect it)
		HTTPRequest request = new HTTPRequest(command, version, "");
        request.header.addHeaderField("Host", command.getHost() + ":" + command.getPort());
        sendRequest(request, socket);
		
	}
	
	/**
	 * Send the given request using the given socket.
	 * 
	 * @param	request
	 * 			The request that is to be sent.
	 * @param 	socket
	 * 			The socket through with the request has to be sent.
	 * @throws 	IOException
	 * 			When an I/O error occurred.
	 */
	protected void sendRequest(HTTPRequest request, HTTPSocket socket) throws IOException {
		// System.out.println(request.toString());
		setSocket(socket);
		SocketUtils.writeString(socket, request.toString());
    }
	
	/**
	 * Get the response for this command processor, or null
	 *  if no command was processed.
	 *  
	 * @return 	The response to the command processed by this processor.
	 * @throws	IOException 
	 * 			An I/O error occurred.
	 */
	public HTTPResponse getResponse() throws IOException {
		if (this.socket == null)
			return null;
		try {
			HTTPResponse response = new HTTPResponse(this.socket);
			return response;
		}
		catch (ClosedSocketException e) {
			System.out.println("Closed socket!");
			return null;
		}
	}
	
	/**
	 * Set this command processor's request to the given one.
	 * 
	 * @param 	request
	 * 			The new request for this command processor.
	 */
	protected void setRequest(HTTPRequest request) {
		this.request = request;
	}
	
	/**
	 * The request associated with this command processor.
	 */
	protected HTTPRequest request;
	
	/**
	 * Set this command processor's socket to the given one.
	 * 
	 * @param 	socket
	 * 			The new socket for this command processor.
	 */
	protected void setSocket(HTTPSocket socket) {
		this.socket = socket;
	}
	
	/**
	 * The socket associated with this command processor.
	 */
	protected HTTPSocket socket;
	
}