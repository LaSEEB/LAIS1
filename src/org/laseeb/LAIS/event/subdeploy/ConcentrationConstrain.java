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


package org.laseeb.LAIS.event.subdeploy;

import org.laseeb.LAIS.LAISModel;
import org.simpleframework.xml.Root;

/**
 * Abstract substance concentration deployment constrain class.
 * 
 * @author Nuno Fachada
 */
@Root
public abstract class ConcentrationConstrain {

	/**
	 * Initializes the constrain object with whichever information it requires from the model.
	 * @param model The LAISModel, master of all simulation objects.
	 */	
	public void initialize(LAISModel model) {}
	
	/**
	 * Returns an indication of how much substance to deploy. If the constrain is the top 
	 * or parent constrain, then the result will not be a mere indication, but the exact 
	 * concentration of substance to deploy.
	 * @return An indication of, or exact concentration of substance to deploy.
	 */
	public abstract float getConcentration();

}
