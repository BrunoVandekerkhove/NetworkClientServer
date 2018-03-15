package org.brunovandekerkhove.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.util.Date;

public class LocalFileManager {

	/**
	 * The path to which web pages can be saved.
	 */
	private static final String LOCAL_PATH = System.getProperty("user.home") + "/NetworkClient/";

	/** 
	 * A private Constructor prevents any other
	 *	class from instantiating.
	 */
	private LocalFileManager() { }

	/**
	 * Get the singleton instance.
	 * 
	 * @return The sole instantiation of this class.
	 */
	public static LocalFileManager getDefaultManager( ) {
		return defaultManager;
	}
	
	/**
	 * The singleton instance for this class.
	 */
	private static LocalFileManager defaultManager = new LocalFileManager();

	/**
	 * Get the last modified date for the local file representing the given URI.
	 * 
	 * @param 	uri
	 * 			The URI of the file that is locally saved and whose last modified date
	 * 			is requested.
	 * @return	The last modified date of the local file for the given URI,
	 * 			or null if no such file exists.
	 */
	public Date getLastModifiedDate(URI uri) {
		File file = new File(localPathForURI(uri));
        if (!file.exists())
        		return null;
        return new Date(file.lastModified());
	}
	
	/**
	 * Get the last modified date for the local file represented by the given path.
	 * 
	 * @param 	path
	 * 			The path of the file that is locally saved and whose last modified date
	 * 			is requested.
	 * @return	The last modified date of the file at the given file,
	 * 			or null if no such file exists.
	 */
	public Date getLastModifiedDate(String path) {
		File file = new File(path);
        if (!file.exists() || file.isDirectory())
        		return null;
        return new Date(file.lastModified());
	}
	
	/**
	 * Get the local path for the given URI.
	 * 
	 * @param 	uri
	 * 			The URI whose local path is desired.
	 * @return	The local path for the given URI.
	 */
	public static String localPathForURI(URI uri) {
        String host = uri.getHost();
        String resource = uri.getPath();
        if (resource.equals("/")) 
        		resource = "/index.html";
        return LOCAL_PATH + host + resource;
    }
	
	/**
	 * Saves the given bytes to the given path. 
	 * 
	 * @param 	subPath
	 * 			The path of the output file that is to be written to.
	 * 			This should be relative to LOCAL_PATH.
	 * @param 	content
	 * 			The bytes to write.
	 * @throws 	IOException 
	 * 			An I/O error occurred.
	 */
	public void saveLocally(String subPath, byte[] content) throws IOException {
		File saveFile = new File(LOCAL_PATH + subPath);
		saveFile.getParentFile().mkdirs();
		saveFile.createNewFile(); // Does nothing if file exists
		FileOutputStream fileOutput = new FileOutputStream(saveFile, false);
		fileOutput.write(content);
		fileOutput.close();
	}

	/**
	 * Saves the given string to the given path. 
	 * 
	 * @param 	subPath
	 * 			The path of the output file that is to be written to.
	 * 			This should be relative to LOCAL_PATH.
	 * @param 	content
	 * 			The string to write.
	 * @throws 	IOException 
	 * 			An I/O error occurred.
	 */
	public void saveLocally(String subPath, String content) throws IOException {
		File saveFile = new File(LOCAL_PATH + subPath);
		saveFile.getParentFile().mkdirs();
		saveFile.createNewFile(); // Does nothing if file exists
		PrintWriter out = new PrintWriter(saveFile);
		out.print(content);
		out.close();
	}
	
}