/*   
 * This file is part of LAIS (LaSEEB Agent Interaction Simulator).
 * 
 * LAIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * LAIS is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with LAIS.  If not, see <http://www.gnu.org/licenses/>.
 */


package org.laseeb.LAIS.datasources;

import java.io.File;
import java.lang.reflect.Constructor;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.simpleframework.xml.Element;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * TODO To be totally reviewed.
 * 
 * @author Nuno Fachada
 *
 */
public class XMLElementDataSource extends DataSource {

	@Element
	String file;
	
	@Element
	String element;
	
	@Element
	String elementType;
	
	@Override
	public void initialize() throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		Document xmlDocument = null;
		NodeList elementList = null;
		builder = factory.newDocumentBuilder();
		xmlDocument = builder.parse(new File(file));
		Class<?> elementClass = null;
		elementClass = Class.forName(elementType);
		elementList = xmlDocument.getElementsByTagName(element);
		
		for (int i = 0; i < elementList.getLength(); i++) {
			Node node = elementList.item(i);
			String value = node.getTextContent();
			Constructor<?> constructor = null;
			Object obj = null;
			constructor = elementClass.getDeclaredConstructor(String.class);
			obj = constructor.newInstance(value);
			addObject(obj);
		}
	}

}
