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


package org.laseeb.LAIS.event;

import org.simpleframework.xml.Root;
import org.laseeb.LAIS.LAISModel;

/**
 * Implementations of this interface are responsible for scripting simulation events.
 *  
 * @author Nuno Fachada
 */
@Root(name="ScriptingType")
public interface ScriptingType {

	/**
	 * Scripts the event accordingly to the concrete class.
	 * @param model The model object, which contains the necessary objects to perform event scripting.
	 * @param event The event to script.
	 */
	public void scriptEvent(LAISModel model, Event event);
	
}
