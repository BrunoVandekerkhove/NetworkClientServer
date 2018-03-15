package org.brunovandekerkhove.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Utils {

	/**
	 * Converts the given input stream to a string.
	 * 
	 * @param 	in
	 * 			The input stream to convert.
	 * @return	Textual representation of the given input stream.
	 */
	public static String toString(InputStream in) {
		String newLine = System.getProperty("line.separator");
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder result = new StringBuilder();
		String line; boolean flag = false;
		try {
			while ((line = reader.readLine()) != null) {
				result.append(flag? newLine: "").append(line);
				flag = true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result.toString();
	}
	
}