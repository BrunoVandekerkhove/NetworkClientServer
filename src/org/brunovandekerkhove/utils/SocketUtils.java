package org.brunovandekerkhove.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * A class with utility methods for working with sockets.
 * 
 * @author 	Bruno Vandekerkhove
 * @version 1.0
 */
public class SocketUtils {

	/**
     * Write a string through the given socket.
     * 
     * @param	socket
     * 			The socket to write to.
     * @param 	string
     * 			The string that is to be written.
     * @throws 	IOException
     * 			An I/O error occurred.
     * @note		https://stackoverflow.com/questions/10673684/send-http-request-manually-via-socket
     */
    public static void writeString(Socket socket, String string) throws IOException {
    		DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
    		outputStream.writeBytes(string);
    }
    
    /**
     * Fetches bytes from the given socket until a new line is started.
     * 
     * @param	socket
     * 			The socket to read from.
     * @return 	A string representing a line that was just read.
     * @throws 	IOException
     * 			An I/O error occurred.
     * @throws	ClosedSocketException 
     * 			The socket was closed while it was being read from.
     */
    public static String nextLine(Socket socket) throws IOException, ClosedSocketException {
    		/*
    		BufferedReader input;
    		input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    		return input.readLine();
    		*/
    		InputStream inputStream = socket.getInputStream();
    		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int currentByte = 0;
        while (true) {
        		currentByte = inputStream.read();
            if (currentByte == -1)
                break;
            if (currentByte == '\r') {
            	inputStream.mark(2);
                currentByte = inputStream.read();
                if (currentByte == '\n')
                		break;
            }
            if (currentByte == '\n') 
            		break;
            byteArrayOutputStream.write(currentByte);
        }
        if (currentByte == -1 && byteArrayOutputStream.size() == 0)
        		throw new ClosedSocketException();
        return byteArrayOutputStream.toString("UTF-8");
    }
    
    /**
     * Read a given amount of bytes from the given socket.
     * 
     * @param	socket
     * 			The socket to read from.
     * @param 	length
     * 			The amount of bytes to be read from the socket.
     * @return	An array of n bytes read from this socket. Any byte that could not b
     * @throws 	IOException
     * 			An I/O error occurred 
     */
    public static byte[] getBytes(Socket socket, int length) throws IOException, ClosedSocketException {
        byte[] bytes = new byte[length];
        InputStream inputStream = socket.getInputStream();
        for (int i=0 ; i<length; ++i) {
        		byte newByte = (byte)(inputStream.read());
        		// Could be doing something when bytes become invalid (== -1)
        		// if (newByte == -1)
        			// break;
            bytes[i] = newByte;   
        }
        return bytes;
    }
	
}