package org.brunovandekerkhove.http;

import java.io.IOException;
import java.net.Socket;

/**
 * A class of HTTP sockets. These ones keep the host.
 * 
 * @author 	Bruno Vandekerkhove
 * @version	1.0
 */
public class HTTPSocket extends Socket {

    /**
     * Initialize this new HTTP socket with given host name and port.
     * 
     * @param 	host
     * 			The host name for this new socket.
     * @param 	port
     * 			The port number for this new socket.
     * @throws 	IOException
     * 			If an I/O error occurred.
     */
    public HTTPSocket(String host, int port) throws IOException {
        super(host, port);
        this.host = host;
    }
	
    /**
     * Returns the host name associated with this socket.
     */
    public String getHost() {
    		return this.host;
    }
    
    /**
     * The host name associated with this socket.
     */
    String host;
    
}