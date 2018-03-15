package org.brunovandekerkhove.client;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.brunovandekerkhove.http.HTTPCommand;
import org.brunovandekerkhove.http.HTTPResponse;
import org.brunovandekerkhove.http.HTTPVersion;
import org.brunovandekerkhove.utils.HTMLParser;
import org.brunovandekerkhove.utils.LocalFileManager;

/**
 * A class of HTTP clients for communicating with a server and parsing the response.
 * 
 * @author 	Bruno Vandekerkhove
 * @version 	1.0
 */
public abstract class ClientHTTP {
	
	/**
	 * Create and initialize a new client with given HTTP version.
	 * 
	 * @param 	version
	 * 			The HTTP version ascribed to.
	 * @return	An initialized HTTP client ascribing to the given HTTP version.
	 */
	public static ClientHTTP initializeClient(HTTPVersion version) {
		if (version == null)
			return null; // null type given
		switch (version) {
			case HTTP_10:
				return new ClientHTTP10();
			case HTTP_11:
				return new ClientHTTP11();
			default:
				return null; // No valid type given
		}
	}
	
	/**
	 * Process the given HTTP command.
	 * 
	 * @param	command
	 * 			The HTTP command that is to be processed.
	 */
	public HTTPResponse process(HTTPCommand command) throws UnknownHostException, IOException {
		List<HTTPCommand> commands = new ArrayList<HTTPCommand>();
		commands.add(command);
		List<HTTPResponse> responses = process(commands);
		if (responses.size() > 0)
			return responses.get(0);
		return null;
	}
	
	/**
	 * Process the given HTTP commands.
	 * 
	 * @param 	commands 
	 * 			A list of HTTP commands to be processed by this client.
	 */
	public abstract List<HTTPResponse> process(List<HTTPCommand> commands) throws UnknownHostException, IOException;
	
	/**
	 * Returns the HTTP version for this client.
	 */
	public abstract HTTPVersion getVersion();
	
	/**
	 * The entry point for this client.
	 * 
	 * @param 	args
	 * 			Input arguments.
	 */
	public static void main(String[] args) {

		// Get arguments
		boolean success = true;
		if (args.length < 3) {
			System.out.println("Invalid arguments (format should be '<Command> <URI> <Port>').");
			success = false;
		}
		else {
			
			try {

				// Read in command, host name and port number
				String command = args[0];
				URI uri = new URI(args[1]);
				if (uri.getScheme() == null || !uri.getScheme().equalsIgnoreCase("http"))
					uri = new URI("http://" + args[1]);
				int port = Integer.parseInt(args[2]);
				
				// Get HTTP version (default is 1.1)
				ClientHTTP client;
				HTTPVersion httpVersion = null;
				if (args.length > 3)
					httpVersion = HTTPVersion.versionForString(args[3]);
				if (httpVersion == null)
					httpVersion = HTTPVersion.HTTP_11;
				client = ClientHTTP.initializeClient(httpVersion);
				
				// Process command
				if (client != null ) {
					
					// Process the first command
					HTTPResponse response = client.process(new HTTPCommand(uri, port, command));
					if (response != null) {
						System.out.println(response);
						if (command.equalsIgnoreCase("GET")) {
							
							String savePath = uri.getPath();
							if (savePath == null || savePath.length() == 0)
								savePath = "/index.html";
							LocalFileManager.getDefaultManager().saveLocally(savePath, response.getContents());
							
							// Check if the document was moved, fetch it if possible
							if (response.isMoved()) {
								uri = response.getLocation();
					            response = client.process(new HTTPCommand(uri, port, "GET"));
					            	System.out.println(response);
					            	savePath = uri.getPath();
								if (savePath == null || savePath.length() == 0)
									savePath = "/index.html";
								LocalFileManager.getDefaultManager().saveLocally(savePath, response.getContents());
							}
							
							// Parse HTML and fetch images & scripts (CSS + JS + icon)
							if (response != null 
								&& response.getContents() != null
								&& response.getContents().length != 0) {
								try {
									
									// Parse HTML
									String html = new String(response.getContents());
									HTMLParser parser = new HTMLParser(html, uri.toString());
									ArrayList<String> paths = new ArrayList<String>();
									parser.findImages(paths);
									parser.findLinkResources(paths);
									parser.findScripts(paths);
									
									// Get all resources and save them to disk
							        List<HTTPCommand> commands = new ArrayList<>();
							        for (int i=0 ; i<paths.size() ; i++) {
							        		String path = paths.get(i);
							        		try {
							        			URI resourceURI = new URI(path);
							        			if (!resourceURI.getHost().equalsIgnoreCase(uri.getHost()) 
							        				|| resourceURI.getScheme().equalsIgnoreCase("https"))
								        			continue;
								            commands.add(new HTTPCommand(resourceURI, port, "GET"));
							        		}
							        		catch (Exception exception) {} // When a resource path is invalid
							        }
							        List<HTTPResponse> responses = client.process(commands);
							        for (int i=0 ; i<responses.size() ; i++) {
							        		HTTPResponse resourceResponse  = responses.get(i);
							        		savePath = commands.get(i).getURI().getPath();
							        		LocalFileManager.getDefaultManager().saveLocally(savePath, resourceResponse.getContents());
							        }
									
								} catch (IllegalAccessException e) {}
							}
							
						}
						
					}
					
				}
				else {
					System.out.println("HTTP client couldn't be created.");
					success = false;
				}
				
			}
			catch (NumberFormatException e) {
				System.out.println("Invalid port number.");
				success = false;
			}
			catch (URISyntaxException e) {
				System.out.println("Invalid input URI.");
				success = false;
			}
			catch (IOException e) {
				System.out.println("An I/O error occurred while processing the HTTP command.");
				System.out.println(e.getLocalizedMessage());
				success = false;
			}

		}

		if (!success)
			System.exit(1);
		System.exit(0);

	}

}
