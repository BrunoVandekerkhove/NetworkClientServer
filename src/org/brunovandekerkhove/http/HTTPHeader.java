package org.brunovandekerkhove.http;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.Map;

import org.brunovandekerkhove.utils.ClosedSocketException;
import org.brunovandekerkhove.utils.SocketUtils;

/**
 * A class of HTTP headers.
 * 
 * @author	Bruno Vandekerkhove
 * @version	1.0
 */
public abstract class HTTPHeader {
	
	/**
	 * Initialize this new HTTP header without any parameters.
	 */
	public HTTPHeader() {
		
	}
	
	/**
     * Initialize this new header with given HTTP version.
     * 
     * @param	version
     * 			The HTTP version to initialize this new HTTP header with.
     */
    public HTTPHeader(HTTPVersion version) {
        this.version = version;
    }
	
	/**
     * Initialize this new header by reading from the given socket.
     * 
     * @param	socket
     * 			The socket to initialize this new HTTP header with.
     * @throws 	IOException
     * 			An I/O error occurred.
	 * @throws	ClosedSocketException 
     * 			The given socket was closed while it was being read from.
     */
    public HTTPHeader(Socket socket) throws IOException, ClosedSocketException {
    		parseStatusLine(socket);
    		parseHeaders(socket);
    }
    
    /** 
     * Parse the status line by reading from the given socket.
     * 
     * @param	socket
     * 			The socket to read from.
     * @throws 	IOException
     * 			An I/O error occurred.
     */
    protected abstract void parseStatusLine(Socket socket) throws IOException, ClosedSocketException;
    
    /** 
     * Get the status line in this HTTP header.
     * 
     * @return A string representing the status line in this HTTP header.
     */
    public abstract String getStatusLine();
    
    /**
     * Read all the headers by reading from the given socket.
     * 
     * @param 	socket
     * 			The socket to read from.
     * @throws 	IOException
     * 			An I/O error occurred.
     * @throws	ClosedSocketException 
     * 			The given socket was closed while it was being read from.
     */
    private void parseHeaders(Socket socket) throws IOException, ClosedSocketException {
    	
    		// Pre-processing
    		StringBuilder stringBuilder = new StringBuilder(); // For building values
    		String currentLine, key = null;
    		boolean focus = true;
    		
    		// Fetch all key/value pairs
    		while (true) {
    			currentLine = SocketUtils.nextLine(socket);
    			if (currentLine == null || currentLine.length() == 0)
    				break;
    			else if (focus) {
    				int colonIndex = currentLine.indexOf(":");
    				key = currentLine.substring(0, colonIndex);
    				currentLine = currentLine.substring(colonIndex + ":".length());
    				focus = false;
    				stringBuilder = new StringBuilder();
    			}
    			stringBuilder.append(currentLine.trim());
    			if (!currentLine.endsWith(",")) {
    				this.addHeaderField(key, stringBuilder.toString());
    				focus = true;
    			}
    		}
        
    }
    
    /**
     * The version for this HTTP header.
     */
    public HTTPVersion version;
    
    /**
     * Add the given key/value pair to the hashmap kept by this header.
     * 
     * @param 	key
     * 			The key that is to be added.
     * @param 	value
     * 			The value that is to be associated with the given key.
     */
    public void addHeaderField(String key, String value) {
    		if (key != null)
    			this.headers.put(key, value);
    }
    
    /**
     * Get the value associated with the given key in this header.
     * 
     * @param 	key
     * 			The key whose value is desired.
     * @return	The value associated with the given key, or null if there is none.
     */
    public String getHeaderField(String key) {
    		return headers.get(key);
    }
    
    /**
     * Returns this header's hashmap with key-value pairs corresponding to the header fields.
     */
    public Map<String, String> getHeaders() {
    		return headers;
    }
    
    /**
     * A map representing key/value pairs for this HTTP header.
     */
    protected final Map<String, String> headers = new LinkedHashMap<String, String>();
    
    /**
     * Returns the textual representation of this header.
     *  The status line first, followed by all the headers.
     * 
     * @return A string representing this header.
     * @note https://stackoverflow.com/questions/10673684/send-http-request-manually-via-socket
     */
    public String toString() {
    		String string = getStatusLine() + "\r\n";
        for (String key : headers.keySet())
        		string += key + ": " + headers.get(key) + "\r\n";
        return string;
    }
	
}