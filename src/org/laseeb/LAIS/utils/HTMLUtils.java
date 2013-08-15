package org.laseeb.LAIS.utils;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;

import uchicago.src.sim.engine.CustomProbeable;

/**
 * Utility methods for producing HTML output.
 * 
 * @author Nuno Fachada
 */
public class HTMLUtils {

	/* The ever present logger. */
	private static Logger logger = Logger.getLogger(HTMLUtils.class);
	
	/**
	 * Indentation constant. 
	 */
	public static final int INDENT = 2;
	public static final String spaceChar = "&nbsp;";
	
	/**
	 * Receives a {@link uchicago.src.sim.engine.CustomProbeable} object, and returns
	 * an HTML string describing its probeable properties.
	 * 
	 * @param cp The probeable object to extract HTML info of.
	 * @return An HTML string describing the object's probeable properties.
	 */
	public static String getHTMLPropertiesDescription(CustomProbeable cp) {
		/* Property value. */
		String value = null;
		/* Get probed properties. */
		String properties[] = cp.getProbedProperties();
		/* Create string builder for creating HTML string description of object properties. */
		StringBuilder htmlStringBuilder = new StringBuilder();
		/* Cycle through the probeable properties. */
		for (String property : properties) {
			/* First letter of property string to upper case. */
			property = property.substring(0, 1).toUpperCase() + property.substring(1);
			try {
				/* Get method which returns the property. */
				Method method = cp.getClass().getMethod("get" + property);
				/* Invoke method and get property value. */
				Object result = method.invoke(cp);
				if (result == null) {
					value = null;
				} else if (result.getClass().isArray()) {
					value = arrayToHTMLString((Object[]) result, 0);
				} else {
					value = result.toString();
				}
				value = value + "<br>";
			} catch (Exception e) {
				/* Oops... let's give a graceful response. */
				logger.debug(e.getClass().getName() + ": " + e.getMessage());
				value = ("N/A");
			}
			/* Build HTML key-value wrap and add it to final string. */
			htmlStringBuilder.append(getKeyValueHTMLWrap(property, value));
		}
		/* Return HTML string description. */
		return htmlStringBuilder.toString();
	}
	
	/**
	 * Receives two objects, a key and value pair, and wraps their 
	 * {@link java.lang.Object#toString()} output in HTML.
	 * Basically, this method returns an HTML representation of the 
	 * given key-value pair.
	 * 
	 * @param key The key object.
	 * @param value The value object.
	 * @return An HTML representation of the given key-value pair.
	 */
	public static String getKeyValueHTMLWrap(Object key, Object value) {
		if (key == null) key = "null";
		if (value == null) value = "null";
		return "<strong>" + key + "</strong><br>" + value + "<br>";
	}
	
	/**
	 * Converts an array to an HTML string. Works in a recursive fashion, i.e., if the array contains
	 * arrays, this method will format them accordingly, using a given indentation value.
	 *  
	 * @param objectArray The array to extract an HTML string of.
	 * @param indent Indentation.
	 * @return An HTML string reflecting the objects contained in the array.
	 */
	public static String arrayToHTMLString(Object[] objectArray, int indent) {
		String value = "";
		String indentString = "";
		/* Determine indentation. */
		for (int i = 0; i < indent; i++)
			indentString = indentString + spaceChar;
		/* Determine HTML string. */
		for (int i = 0; i < objectArray.length; i++) {
			Object obj = objectArray[i];
			if (obj.getClass().isArray()) {
				/* Call the method recursively if object is array. */
				value = value + arrayToHTMLString((Object[]) obj, indent + INDENT);
			} else {
				/* If object is not array, proceed as usual. */
				value = indentString + value + obj;
				if (i + 1 < objectArray.length)
					value = value + "<br>";
			}
		}
		return value;
	}
}