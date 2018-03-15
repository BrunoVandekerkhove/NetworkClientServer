package org.brunovandekerkhove.server;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;

import org.brunovandekerkhove.http.HTTPRequest;
import org.brunovandekerkhove.http.HTTPResponse;
import org.brunovandekerkhove.http.HTTPVersion;
import org.brunovandekerkhove.utils.LocalFileManager;

/**
 * A class of handlers for managing connections to the server.
 *  The handlers are meant to be run on a separate thread.
 * 
 * @author 	Bruno Vandekerkhove
 * @version	1.0
 */
public class ConnectionHandler implements Runnable {

	/**
	 * Initialize this new connection handler with given socket.
	 * 
	 * @param 	socket
	 * 			The socket to initialize this connection handler with.
	 */
	public ConnectionHandler(Socket socket) {
		this.socket = socket;
	}

	/**
	 * Registers the socket for this connection handler.
	 */
	private Socket socket;

	@Override
	public void run() {

		try {

			DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
			while (!socket.isClosed()) {

				HTTPResponse response = null;
				HTTPVersion version = HTTPVersion.HTTP_10;
				boolean requestsClose = false;
				
				try { // Get request and generate response (default = error)					
					HTTPRequest request = new HTTPRequest(socket);
					version = request.header.version;
					response = generateResponse(request);
					requestsClose = request.requestsClose();
				} catch (Exception e) { // Could, for example, be error writing locally (PUT/POST)
					response = new HTTPResponse(version, 500, "Server Error");
				}
				
				// Send the response and close the connection if appropriate (HTTP v. 1.0)
				if (!socket.isClosed()) {
					if (response == null) // Response was not generated => error
						response = new HTTPResponse(version, 500, "Server Error");				
					try { // Try writing response to output stream of socket
						outputStream.writeBytes(response.header.toString() + "\r\n");
						outputStream.write(response.contents);
						if (version == HTTPVersion.HTTP_10 || requestsClose)
							socket.close();
					}
					catch (IOException e) { // Error writing to socket
						socket.close();
						System.out.println(e.getLocalizedMessage());
					}
				}
				
			}

		} catch (IOException e) {
			System.out.println(e.getLocalizedMessage());
		}

	}

	/**
	 * Generate a server response for the given HTTP request.
	 * 
	 * @param 	request
	 * 			The request to respond to.
	 * @return	An appropriate HTTP response for the given HTTP request.
	 * @throws	IOException
	 * 			If an error occurred while reading from a stream (eg. when reading files).
	 */
	private HTTPResponse generateResponse(HTTPRequest request) throws IOException {
		HTTPResponse response = null;
		HTTPVersion requestVersion = request.header.version;
		if (requestVersion == HTTPVersion.HTTP_11 
			&& !request.hasHostField()) { // Check if a host has been specified (only mandatory in HTTP/1.1)
			response = new HTTPResponse(requestVersion, 400, "Bad Request"); 
		} else {
			String subPath = request.getURI().getPath();
			if (subPath.equalsIgnoreCase("/"))
				subPath = "/index.html";
			String localPath = "resources" + subPath;
			switch (request.getCommandType()) { // Generate appropriate response to request
			case "HEAD":
			case "GET":
		        Date localDate = LocalFileManager.getDefaultManager().getLastModifiedDate(localPath);
		        if (localDate != null) { // Local date can be null when the file doesn't exist
		            Date ifModifiedSinceDate = request.getIfModifiedSinceDate();
		            if (ifModifiedSinceDate != null && localDate.before(ifModifiedSinceDate))
		            		response = new HTTPResponse(requestVersion, 304, "Not Modified");
		            else {
		            		byte[] contents = Files.readAllBytes(Paths.get(localPath));
		            		response = new HTTPResponse(requestVersion, 200, "OK", contents, getContentType(localPath));
		            }
		        } else {
		        		byte[] contents = Files.readAllBytes(Paths.get(PATH_404));
		            response = new HTTPResponse(requestVersion, 404, "Not Found", contents, getContentType(PATH_404));
		        }
		        if (request.getCommandType().equalsIgnoreCase("head"))
		        		response.contents = new byte[0]; // Only the heading is necessary
				break;
			case "POST":
			case "PUT":
				String inputContent = request.getContentString();
		        try {
		            File file = new File(localPath);
		            if (!file.exists()) {
		                file.createNewFile();
		                response = new HTTPResponse(requestVersion, 201, "Created");
		            }
		            else
		            		response = new HTTPResponse(requestVersion, 204, "No Content");
		            FileWriter fileWritter = new FileWriter(file, request.getCommandType().equalsIgnoreCase("put"));
		            BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
		            bufferWritter.write(inputContent);
		            bufferWritter.close();
		        }
		        catch (IOException e){
		            response = new HTTPResponse(requestVersion, 400, "Bad Request");
		        }
				break;
			default: // http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.5.2
				response = new HTTPResponse(requestVersion, 501, "Not Implemented");
				break;
			}
		}
		return response;
	}
	
	/**
	 * Get the MIME type for the file at the given path.
	 * 
	 * @param 	path
	 * 			The path of the file whose content type is desired.
	 * @return 	The content type matching the given extension, or an empty string
	 * 			if none matches.
	 * @note		https://stackoverflow.com/questions/23714383/what-are-all-the-possible-values-for-http-content-type-header
	 */
	public String getContentType(String path) {
		try {
			String MIME = Files.probeContentType(Paths.get(path));
			return MIME;
		}
		catch (Exception e) {
			return "";
		}
    }
	
	/**
	 * The path of the 404 html file.
	 */
	private static final String PATH_404 = "resources/404.html";

}