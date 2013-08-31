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

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Suggests the deployment of a fixed substance concentration.
 * 
 * @author Nuno Fachada
 *
 */
@Root
public class FixedConcentrationConstrain extends ConcentrationConstrain {

	/** 
	 * <strong>XML Element (float)</strong>
	 * <p>
	 * Concentration of substance to deploy. If not given, default value is 1.0.
	 * <p>
	 * <em>REQUIRED: NO</em> 
	 * */
	@Element
	float concentration = 1.0f;
	
	/**
	 * Returns a fixed substance concentration.
	 * @return A fixed substance concentration.
	 */
	public float getConcentration() {
		return concentration;
	}
}
