package org.brunovandekerkhove.http;

/**
 * A class of HTTP messages (requests, responses, ...) to be sent over a network.
 * 
 * @author 	Bruno Vandekerkhove
 * @version 	1.0
 */
public class HTTPMessage {
    
    /**
	 * The HTTP header of this message.
	 */
    public HTTPHeader header;
	
    /**
     * Get this message's contents.
     */
    public byte[] getContents() {
    		return this.contents;
    }
    
    /**
     * Get a string representing this message's contents.
     */
    public String getContentString() {
    		return new String(this.contents);
    }
    
	/**
	 * The contents of this message.
	 */
	public byte[] contents;
	
}