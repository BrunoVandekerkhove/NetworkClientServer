package org.brunovandekerkhove.http;

import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

import org.brunovandekerkhove.http.HTTPHeader;
import org.brunovandekerkhove.utils.ClosedSocketException;
import org.brunovandekerkhove.utils.SocketUtils;

/**
 * A class of HTTP request headers.
 * 
 * @author	Bruno Vandekerkhove
 * @version	1.0
 */
public class HTTPRequestHeader extends HTTPHeader {

	/**
	 * Initialize this new HTTP request header by reading from the given socket.
	 * 
	 * @param 	socket
	 * 			The socket to read from.
	 * @throws	ClosedSocketException 
     * 			The given socket was closed while it was being read from. 
	 */
	public HTTPRequestHeader(Socket socket) throws IOException, URISyntaxException, ClosedSocketException {
		super(socket);
		String host = "localhost";
		int port = 80;
		if (headers.containsKey("Host")) {
			host = headers.get("Host");
			if (host.contains(":")) {
				int index = host.indexOf(":");
				port = Integer.parseInt(host.substring(index+1));
				host = host.substring(0, index);
			}
		}
		URI currentURI = this.command.getURI();
		System.out.println(currentURI);
		URI newURI = new URI("http", null, host, port, currentURI.getPath(), null, null);
		this.command = new HTTPCommand(newURI, port, this.command.getType());
	}

	/**
	 * Initialize this new request header with given command, host and HTTP version.
	 * 
	 * @param 	command
	 * 			The command for this request header.
	 * @param 	host
	 * 			The host for this request header.
	 * @param 	version
	 * 			The HTTP version for this request header.
	 * @throws 	IOException
	 * 			An I/O exception occurred.
	 */
	public HTTPRequestHeader(HTTPCommand command, HTTPVersion version) throws IOException {
		super(version);
		this.command = command;
	}

	@Override
	protected void parseStatusLine(Socket socket) throws IOException, ClosedSocketException {

		// Get first line
		String statusLine = SocketUtils.nextLine(socket);
		int firstSpace = statusLine.indexOf(" ");
		String commandString = statusLine.substring(0, firstSpace);
		int secondSpace = statusLine.indexOf(" ", firstSpace + " ".length());
		String resourceString = statusLine.substring(firstSpace + " ".length(), secondSpace);
		String versionString = statusLine.substring(secondSpace + " ".length());

		// Parse line (can't really use splitting of string at spaces)
		this.version = HTTPVersion.versionForString(versionString);
		try {
			System.out.println(resourceString);
			this.command = new HTTPCommand(new URI("http", "localhost", resourceString, null), 80, commandString);
		} catch (URISyntaxException e) {
			System.out.println(e.getLocalizedMessage());
		}

	}

	@Override
	public String getStatusLine() {
		String path = getCommand().getURI().getPath();
		if (path == null || path.length() < 1)
			path = "/";
		return command.getType() + " " + path + " " + version.toString();
	}

	/**
	 * Get the command associated with this request header.
	 */
	public HTTPCommand getCommand() {
		return this.command;
	}

	/**
	 * The command associated with this request header.
	 */
	private HTTPCommand command;

	/**
	 * Returns whether or not this request header has a host header field.
	 * 
	 * @return	True if and only if this header has a host header field.
	 */
	public boolean hasHostField() {
		return headers.containsKey("Host");
	}

}