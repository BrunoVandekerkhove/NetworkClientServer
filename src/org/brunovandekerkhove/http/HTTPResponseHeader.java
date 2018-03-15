package org.brunovandekerkhove.http;

import java.io.IOException;
import java.net.Socket;

import org.brunovandekerkhove.utils.ClosedSocketException;
import org.brunovandekerkhove.utils.SocketUtils;

/**
 * A class representing the headers of a response
 * Contains both the first line of a response as the other headers.
 */
public class HTTPResponseHeader extends HTTPHeader {
    
	/**
	 * Initialize this new HTTP response header with given HTTP socket.
	 * 
	 * @param 	socket
	 * 			The HTTP socket to initialize with.
	 * @throws	IOException 
	 * 			An I/O error occurred.
	 * @throws	ClosedSocketException 
     * 			The given socket was closed while it was being read from.
	 */
	public HTTPResponseHeader(Socket socket) throws IOException, ClosedSocketException {
		super(socket);
	}
	
	/**
	 * Initialize this new HTTP response header with given message, status and HTTP version.
	 * 
	 * @param 	message
	 * 			The message for this new HTTP response header.
	 * @param 	status
	 * 			The status for this new HTTP response header.
	 * @param 	version
	 * 			The version for this new HTTP response header.
	 */
    public HTTPResponseHeader(String message, int status, HTTPVersion version) {
        super(version);
        this.message = message;
        this.status = status;
    }
    
    @Override
    protected void parseStatusLine(Socket socket) throws IOException, ClosedSocketException {
    		
    		// Get status line and get its parts
    		String statusLine = SocketUtils.nextLine(socket);
        int firstSpace = statusLine.indexOf(" ");
        if (firstSpace > 0) {
        	
        		String version = statusLine.substring(0, firstSpace);
            int secondSpace = statusLine.indexOf(" ", firstSpace + " ".length());
            
            if (secondSpace > 0) {
            	
            		String statusCodeString = statusLine.substring(firstSpace + " ".length(), secondSpace);
                String statusMessageString = statusLine.substring(secondSpace + " ".length());
                
                // Save the status line parts (can't really use splitting of string at spaces)
                this.version = HTTPVersion.versionForString(version);
                this.status = Integer.parseInt(statusCodeString);
                this.message = statusMessageString;
                
            }
            
        }
        
    }
    
    @Override
    public String getStatusLine() {
        return this.version + " " + this.status + " " + this.message;
    }
    
    /**
     * The message for this response header.
     */
    public String message;
    
    /**
     * The status for this reponse header.
     */
    public int status;
    
}