package org.brunovandekerkhove.utils;

import java.io.IOException;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * A class for parsing HTML content.
 *  
 * @author	Bruno Vandekerkhove
 * @version	1.0
 */
public class HTMLParser {
	
	/**
	 * Initialize a new HTML parser.
	 * 
	 * @param	html
	 * 			The HTML that is to be parsed.
	 * @param	baseURI
	 * 			The URI the HTML came from.
	 */
	public HTMLParser(String html, String baseURI) {
		document = Jsoup.parse(html, baseURI);
	}
	
	/**
	 * The HTML document resulting from parsing the given HTML string.
	 */
	private Document document;
    
	/**
	 * Finds the images linked to within the given HTML snippet.
	 * 
	 * @param 	paths
	 * 			A list for adding the image paths to.
	 */
	public void findImages(List<String> paths) 
			throws IllegalAccessException, IOException, IllegalAccessException {
		Elements imageElements = document.getElementsByTag("img");
		for (Element element : imageElements)
			paths.add(element.attr("abs:src"));
    }
	
	/**
	 * Finds the <link> resources linked in the given HTML snippet.
	 *  (CSS, favico, ...)
	 * 
	 * @param 	paths
	 * 			A list for adding the resource paths to.
	 */
	public void findLinkResources(List<String> paths) 
			throws IllegalAccessException, IOException {
		Elements linkElements = document.getElementsByTag("link");
		for (Element element : linkElements)
			paths.add(element.attr("abs:href"));
    }
    
	/**
	 * Finds the scripts linked in the given HTML snippet.
	 *  (JavaScript!)
	 * 
	 * @param 	paths
	 * 			A list for adding the script paths to.
	 */
    public void findScripts(List<String> paths) 
    		throws IllegalAccessException, IOException {
    		Elements scriptElements = document.getElementsByTag("script");
		for (Element element : scriptElements)
			paths.add(element.attr("abs:src"));
    }

}