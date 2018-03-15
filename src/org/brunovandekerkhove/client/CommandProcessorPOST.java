package org.brunovandekerkhove.client;

import java.io.IOException;
import java.util.Scanner;

import org.brunovandekerkhove.http.HTTPCommand;
import org.brunovandekerkhove.http.HTTPRequest;
import org.brunovandekerkhove.http.HTTPSocket;
import org.brunovandekerkhove.http.HTTPVersion;

public class CommandProcessorPOST extends CommandProcessor {
	
	@Override
	public void process(HTTPCommand command, HTTPSocket socket, HTTPVersion version) throws IOException {

		// Create request
        HTTPRequest request = new HTTPRequest(command, version, "");
        request.header.addHeaderField("Host", command.getHost() + ":" + command.getPort());
        
        // Get contents
        System.out.println("Please enter the content to POST :");
        Scanner scanner = new Scanner(System.in);
        int emptyLines = 0;
        String content = "", currentLine;
        while (scanner.hasNextLine()) {
        		currentLine = scanner.nextLine();
            if (currentLine.length() < 1) {
            		emptyLines++;
            		if (emptyLines > 1) break;
            }
            else
            		emptyLines = 0;
            content += currentLine + "\n";
        }
        scanner.close();
        
        // Send request
        request.header.addHeaderField("Content-Length", Integer.toString(content.length()));
        request.contents = content.getBytes();
        sendRequest(request, socket);
        
	}

}