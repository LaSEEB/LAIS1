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

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import org.laseeb.LAIS.LAISModel;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

/**
 * Returns a fixed superficial substance concentration map. 
 * 
 * @see org.laseeb.LAIS.event.agdeploy.InitSupSubConConstrain  
 * @author Nuno Fachada
 */
@Root
public class FixedInitSupSubConConstrain extends InitSupSubConConstrain {

	/** 
	 * <strong>XML ElementMap (key: {@link java.lang.String}, value: float)</strong>
	 * <p>
	 * Initial substance concentration map.
	 * <p>
	 * <em>REQUIRED: NO</em> 
	 * */
	@ElementMap(required=false,inline=true,attribute=true,entry="substance",key="name")
	Map<String, Float> supSubConMap = new HashMap<String, Float>();
	
	/**
	 * Returns an fixed indication (non-model dependent) of initial superficial substance 
	 * concentrations.
	 * @return An indication of the initial superficial substance concentration.
	 * @see org.laseeb.LAIS.event.agdeploy.InitSupSubConConstrain#getInitSubCon()
	 */
	public Map<String, Float> getInitSubCon() {
		/* Create an empty map. */
		Map<String, Float> finalSupSubConMap = new HashMap<String, Float>();
		/* Iterate through the local map, and add substance concentrations to the created map. */
		Iterator<String> iter = supSubConMap.keySet().iterator();
		while (iter.hasNext()) {
			float con = 0.0f;
			String str = iter.next();
			if (finalSupSubConMap.containsKey(str)) {
				con = finalSupSubConMap.get(str) + supSubConMap.get(str);
			} else {
				con = supSubConMap.get(str);
			}
			finalSupSubConMap.put(str, con);
		}
		/* Return the created map. */
		return finalSupSubConMap;
	}

	/**
	 * Because this implementation of InitSupSubConConstrain is model-independent, 
	 * it only performs super object initialization.
	 */
	public void initialize(LAISModel model) {
		super.initialize(model);
	}

}
