package org.brunovandekerkhove.http;

import java.io.IOException;
import java.net.Socket;
import java.net.URI;

import org.brunovandekerkhove.utils.ClosedSocketException;
import org.brunovandekerkhove.utils.SocketUtils;

/**
 * A class of HTTP responses returned by a HTTP server.
 * 
 * @author 	Bruno Vandekerkhove
 * @version	1.0
 */
public class HTTPResponse extends HTTPMessage {

	/**
	 * Create a response by reading incoming data from a socket.
	 * 
	 * @param 	socket
	 *          	The socket to read from
	 * @throws 	IOException
	 * 			If an I/O error occurred.
	 * @throws	ClosedSocketException
	 * 			The given socket was closed while it was being read from. 
	 */
	public HTTPResponse(Socket socket) throws IOException, ClosedSocketException {
		this.header = new HTTPResponseHeader(socket);
		if (this.header.getHeaders().containsKey("Content-Length")) {
            int length = Integer.parseInt(this.header.getHeaders().get("Content-Length"));
            this.contents = SocketUtils.getBytes(socket, length);
        } else { // No Content-Length specified
        		this.contents = new byte[0];
        }
	}
	
	/**
	 * Initialize this new HTTP response with given HTTP version, status code, and response message.
	 * 
	 * @param 	version
	 * 			The HTTP version.
	 * @param 	status
	 * 			The status code for the response.
	 * @param 	message
	 * 			The message in the response.
	 */
	public HTTPResponse(HTTPVersion version, int status, String message) {
		this(version, status, message, null, null);
	}
	
	/**
	 * Initialize this new HTTP response with given HTTP version, status code, status message,
	 *  content and content type.
	 *  
	 * @param 	version
	 * 			The HTTP version for this response.
	 * @param 	status
	 * 			The status code in this response.
	 * @param 	message
	 * 			The status message in this response.
	 * @param 	contents
	 * 			The bytes in the contents of this response.
	 * @param 	contentType
	 * 			The content type of the contents.
	 */
	public HTTPResponse(HTTPVersion version, int status, String message, byte[] contents, String contentType) {
        header = new HTTPResponseHeader(message, status, version);
        if (contents == null)
        		this.contents = new byte[0];
        else  {
        		this.contents = contents;
        		header.addHeaderField("Content-Type", contentType);
        		header.addHeaderField("Content-Length", Integer.toString(contents.length));
        }
    }
	
	/**
     * Returns the status code for this HTTP message.
     */
    public int getStatus() {
    		return ((HTTPResponseHeader)this.header).status;
    }
    
    /**
     * Returns whether or not the document was moved.
     * 
     * @return True if and only if the status code for this response was 301 or 302.
     */
    public boolean isMoved() {
    		return (getStatus() == 301 || getStatus() == 302);
    }
    
    /**
     * Returns the moved to location, if any.
     * 
     * @return An URI representing the location header field.
     */
    public URI getLocation() {
    		try {
    			if (header.getHeaders().containsKey("Location")) {
    	            String location = header.getHeaderField("Location");
    	            URI uri = new URI(location);
    	            return uri;
    	        }
    		}
    		catch (Exception e) {}
    		return null;
    }

	@Override
	public String toString() {
		String string = this.header.toString() + "\r\n";
		String type = this.header.getHeaderField("Content-Type");
		if (type != null) {
			if (type.contains("text"))
				string += (this.contents == null ? "" : new String(this.contents));
			else
				string += "Type: \"" + type + "\"";
		}
		return string;
	}

}