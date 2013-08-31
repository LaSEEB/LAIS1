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


package org.laseeb.LAIS.event.agdeploy;

import org.laseeb.LAIS.event.Event;
import org.simpleframework.xml.Element;

/**
 * Returns a random quantity of agents to deploy, up to a given maximum.
 * 
 * @see org.laseeb.LAIS.event.agdeploy.QuantityConstrain  
 * @author Nuno Fachada
 */
public class RandomQuantityConstrain extends QuantityConstrain {

	/** 
	 * <strong>XML Element (integer &gt; 0)</strong>
	 * <p>
	 * Maximum quantity of agents to deploy.
	 * <p>
	 * <em>REQUIRED: NO</em> 
	 * */
	@Element(required=false)
	int maxQuantity = 1;
	
	/**
	 * @see QuantityConstrain#getQuantity()
	 */
	public int getQuantity() {
		int quantityToReturn;
		quantityToReturn = maxQuantity;
		return Event.getEventRng().nextIntFromTo(0, quantityToReturn);
	}

}
