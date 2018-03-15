package org.brunovandekerkhove.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.brunovandekerkhove.utils.ClosedSocketException;

/**
 * A class of HTTP requests, with a header and contents.
 * 
 * @author 	Bruno Vandekerkhove
 * @version 	1.0
 */
public class HTTPRequest extends HTTPMessage {
    
	/**
	 * Initializes this HTTP request with the given socket.
	 * 
	 * @param 	socket
	 * 			The socket to read from when generating this HTTP request.
	 * @throws 	IOException 
	 * 			An I/O error occurred.
	 * @throws	ClosedSocketException 
     * 			The given socket was closed while it was being read from.
	 */
	public HTTPRequest(Socket socket) throws IOException, URISyntaxException, ClosedSocketException {
		this.header = new HTTPRequestHeader(socket);
		if (this.header.getHeaders().containsKey("Content-Length")) {
            int length = Integer.parseInt(this.header.getHeaders().get("Content-Length"));
            this.contents = new byte[length];
            InputStream inputStream = socket.getInputStream();
            for (int i = 0; i < length; ++i)
            		this.contents[i] = (byte)(inputStream.read());        
        } else { // No Content-Length specified
        		this.contents = new byte[0];
        }
	}
	
    /**
     * Initialize this new request with given command, host, HTTP version and contents.
     * 
     * @param 	command 
     * 			The HTTP command for this request.
     * @param 	version 
     * 			The HTTP version that is to be used.
     * @param 	contents 
     * 			The textual contents to be sent.
     * @throws	IOException 
     * 			An I/O error occurred.
     */
    public HTTPRequest(HTTPCommand command, HTTPVersion version, String contents) throws IOException {
    		if (contents == null)
    			throw new IllegalArgumentException("Null request contents.");
        this.header = new HTTPRequestHeader(command, version);
        this.contents = contents.getBytes();
    }
    
    /**
     * Initialize this new request with given command, host, HTTP version and contents.
     * 
     * @param 	command 
     * 			The HTTP command for this request.
     * @param 	version 
     * 			The HTTP version that is to be used.
     * @param 	contents 
     * 			The contents to be sent.
     * @throws	IOException 
     * 			An I/O error occurred.
     */
    public HTTPRequest(HTTPCommand command, HTTPVersion version, byte[] contents) throws IOException {
    		this(command, version, new String(contents));
    }
    
    /**
     * Returns the type of the command for this request.
     * 
     * @return The type of the command (GET, POST, ...) for this HTTP request.
     */
    public String getCommandType() {
    		return ((HTTPRequestHeader)this.header).getCommand().getType();
    }
    
    /**
     * Returns the URI for this request.
     * 
     * @return The URI for this HTTP request.
     */
    public URI getURI() {
    		return ((HTTPRequestHeader)this.header).getCommand().getURI();
    }
    
    /**
     * Returns a textual representation of this HTTP request.
     * 
     * @return 	The contents of this HTTP request, represented as a string.
     */
    public String toString() {
    		return this.header.toString() + "\r\n" + new String(this.contents);
    }
    
    /**
     * Returns whether or not this request has a host header field.
     * 
     * @return	True if and only if this request specifies a host in its header.
     */
    public boolean hasHostField() {
    		return ((HTTPRequestHeader)this.header).hasHostField();
    }
    
    /**
     * Returns whether or not this request asks for closing the connection.
     * 
     * @return True if and only if this request's header has 'close' in its 'Connection' header field.
     * @note http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html
     */
    public boolean requestsClose() {
    		String connectionField = ((HTTPRequestHeader)this.header).getHeaderField("Connection");
    		return (connectionField != null && connectionField.equalsIgnoreCase("close"));
    }
    
    /**
	 * Returns the If-Modified-Since field, formatted as a date.
	 * 
	 * @return The if-modified-since field as a date, or null if 
	 * 			there is no such field.
	 */
	public Date getIfModifiedSinceDate() {
		String headerField = header.getHeaderField("If-Modified-Since");
		if (headerField != null) {
			try {
				DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
				dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
				return dateFormat.parse(headerField);
			} catch (ParseException e) {System.out.println(e.getLocalizedMessage());}
		}
		return null;
	}
	
}