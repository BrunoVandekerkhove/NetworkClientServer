package org.brunovandekerkhove.http;

/**
 * An enumeration of HTTP versions.
 */
public enum HTTPVersion {
	
	HTTP_10,
	HTTP_11;
	
	/**
	 * Get the HTTP version represented by the given string.
	 * 
	 * @param 	versionString
	 * 			The version string representing a HTTP version.
	 * @return	The HTTP version represented by the given string, or
	 * 			null if the string does not represent any HTTP version.
	 */
	public static HTTPVersion versionForString(String versionString) {
		if (versionString.equalsIgnoreCase("1.0")
			|| versionString.equalsIgnoreCase("http/1.0"))
			return HTTP_10;
		else if (versionString.equalsIgnoreCase("1.1")
			|| versionString.equalsIgnoreCase("http/1.1"))
			return HTTP_11;
		else return null;
	}
	
	/**
	 * Returns a textual representation of the given HTTP version.
	 * 
	 * @param 	version
	 * 			The version to create a textual representation of.
	 * @return 	A textual representation of the given HTTP version.
	 */
	public String toString() {
		switch (this) {
			case HTTP_10:
				return "HTTP/1.0";
			case HTTP_11:
				return "HTTP/1.1";
		}
		return "";
	}
	
}