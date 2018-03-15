package org.brunovandekerkhove.client;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.brunovandekerkhove.http.HTTPCommand;
import org.brunovandekerkhove.http.HTTPRequest;
import org.brunovandekerkhove.http.HTTPSocket;
import org.brunovandekerkhove.http.HTTPVersion;
import org.brunovandekerkhove.utils.LocalFileManager;

/**
 * A class of command processors for processing HTTP GET requests.
 * 
 * @author 	Bruno Vandekerkhove
 * @version	1.0
 */
public class CommandProcessorGET extends CommandProcessor {

	@Override
	public void process(HTTPCommand command, HTTPSocket socket, HTTPVersion version) throws IOException {
		
		// Set up new request adding an 'If-Modified-Since' header to prevent redundant data
		HTTPRequest request = new HTTPRequest(command, version, "");
        Date modifiedDate = LocalFileManager.getDefaultManager().getLastModifiedDate(command.getURI());
        if (modifiedDate != null) {
            DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT")); // Greenwich Mean Time
            request.header.addHeaderField("If-Modified-Since", dateFormat.format(modifiedDate));
        }
        
        // Add host to header and send the request
        request.header.addHeaderField("Host", command.getHost() + ":" + command.getPort());
        sendRequest(request, socket);
        
    }
	
}
