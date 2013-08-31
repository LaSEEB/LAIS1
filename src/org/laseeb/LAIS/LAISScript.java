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


package org.laseeb.LAIS;

import java.util.LinkedHashMap;
import java.util.Iterator;

import org.laseeb.LAIS.event.Event;
import org.laseeb.LAIS.event.ScriptingType;

import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;


/**
 * An instance of this class contains scripted simulation events.
 * Scripted events can occur at predetermined moments or when the user decides
 * (e.g., by pressing a button).
 * <p>
 * This class is instantiated automatically by LAIS using the 
 * <strong>XML Script File</strong>. All the data necessary for instantiating this class is 
 * given within <code>&lt;LAISScript&gt;</code> tags. More specifically, 
 * <code>&lt;LAISScript&gt;</code> is the root tag for the <strong>XML Script File</strong>. 
 * 
 * @author Nuno Fachada
 */
@Root
public class LAISScript {

	/** 
	 * <strong>XML ElementMap (key: {@link org.laseeb.LAIS.event.ScriptingType}, 
	 * value: {@link org.laseeb.LAIS.event.Event})</strong>
	 * <p>
	 * The event map, which associates each event to a scripting type. Because this is the
	 * only XML initialized field in this class, the map is describe inline in the XML; as such,
	 * the general form of XML for describing this field is as follows:
	 * <p>
	 * <code>
	 * ...<br>
	 * &lt;scriptedEvent&gt;<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;ScriptingType class="(a1)"&gt;<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;...<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;/ScriptingType&gt;<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;Event class="(b1)"&gt;<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;...<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;/Event&gt;<br>
	 * &lt;/scriptedEvent&gt;<br>
	 * &lt;scriptedEvent&gt;<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;ScriptingType class="(a2)"&gt;<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;...<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;/ScriptingType&gt;<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;Event class="(b2)"&gt;<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;...<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;/Event&gt;<br>
	 * &lt;/scriptedEvent&gt;<br>
	 * ...
	 * </code>
	 * <p>
	 * Replace (a1) and (a2) for {@link org.laseeb.LAIS.event.ScriptingType} implementations and 
	 * (b1) and (b2) for {@link org.laseeb.LAIS.event.Event} implementations.
	 * <p> 
	 * <em>REQUIRED: NO</em> 
	 * */
	@ElementMap(entry="scriptedEvent",required=false,inline=true)
	LinkedHashMap<ScriptingType, Event> scriptedEvents;
	/* A linked hash map is used to maintain event ordering has determined 
	 * in script file. */
	
	/**
	 * Returns an iterator to the scripting types of the events.
	 * @return An iterator to the scripting types of the events.
	 */
	public Iterator<ScriptingType> scriptedEventsIterator() {
		if (scriptedEvents != null)
			return scriptedEvents.keySet().iterator();
		else
			return new Iterator<ScriptingType>() {
				public boolean hasNext() {return false;}
				public ScriptingType next() {return null;}
				public void remove() {}
			};
	}
	
	/**
	 * Returns the event associated with the given scripting type.
	 * @param st A given scripting type.
	 * @return The event associated with the given scripting type.
	 */
	public Event getScriptedEvent(ScriptingType st) {
		return scriptedEvents.get(st);
	}

}
