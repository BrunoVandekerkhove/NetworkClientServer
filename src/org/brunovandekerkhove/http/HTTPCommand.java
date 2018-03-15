package org.brunovandekerkhove.http;

import java.net.URI;

/**
 * A class representing HTTP commands, having a command type (represented by a string), a uri 
 * 	and a port number.
 */
public class HTTPCommand {
	
	/**
	 * Initialize this new HTTP command with given URI, port number and command type.
	 * 	
	 * @param 	uri
	 * 			The URI this command should be initialised with.
	 * @param	port
	 * 			The port number this command should be initialised with.
	 * @param 	type
	 * 			The command type this command should be initialised with.
	 */
	public HTTPCommand(URI uri, int port, String type) {
		setType(type);
		setURI(uri);
		setPort(port);
	}
	
	/**
	 * Returns the URI for this HTTP command.
	 */
	public URI getURI() {
		return this.uri;
	}
	
	/**
	 * Set the URI for this HTTP command.
	 * 
	 * @param 	URI
	 * 			The new URI for this HTTP command.
	 */
	public void setURI(URI uri) {
		this.uri = uri;
	}
	
	/**
	 * Returns the host associated with this HTTP command.
	 * 
	 * @return The host name of the URI associated with this command.
	 */
	public String getHost() {
		return this.uri.getHost();
	}
	
	/**
	 * The URI for this HTTP command.
	 */
	private URI uri;
	
	/**
	 * Returns the port for this HTTP command.
	 */
	public int getPort() {
		return this.port;
	}
	
	/**
	 * Set the port for this HTTP command.
	 * 
	 * @param 	port
	 * 			The new port for this HTTP command.
	 */
	public void setPort(int port) {
		this.port = port;
	}
	
	/**
	 * The port for this HTTP command.
	 */
	private int port;
	
	/**
	 * Returns this HTTP command's type.
	 */
	public String getType() {
		return this.type;
	}
	
	/**
	 * Set this HTTP command's type to the given one.
	 * 
	 * @param 	type
	 * 			The new type for this HTTP command.
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * The type of this HTTP command.
	 */
	private String type;
	
}