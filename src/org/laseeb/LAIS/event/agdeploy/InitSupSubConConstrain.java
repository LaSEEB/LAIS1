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

import org.laseeb.LAIS.LAISModel;

import java.util.Map;

/**
 * Abstract initial superficial substance concentration in deployed agents constrain class.
 * 
 * @author Nuno Fachada
 *
 */
public abstract class InitSupSubConConstrain {
	
	/**
	 * Returns a map containing superficial substance concentrations of the agent
	 * to be deployed.
	 * @return A map containing superficial substance concentrations.
	 */
	public abstract Map<String, Float> getInitSubCon();
	
	/**
	 * Initializes the constrain object with whichever information it requires from the model.
	 * 
	 * @param model The LAISModel, master of all simulation objects.
	 */	
	public void initialize(LAISModel model) {}

}
