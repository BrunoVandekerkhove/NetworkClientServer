package org.brunovandekerkhove.client;

import java.io.IOException;

import org.brunovandekerkhove.http.HTTPResponse;
import org.brunovandekerkhove.utils.ClosedSocketException;

public class CommandProcessorHEAD extends CommandProcessor {

	@Override
	public HTTPResponse getResponse() throws IOException {
		try {
			HTTPResponse response = new HTTPResponse(this.socket); // A 'HEAD' request does not have a body
			response.contents = new byte[0];
			return response;
		}
		catch (ClosedSocketException e) {
			return null;
		}
	}
	
}