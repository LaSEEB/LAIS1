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

import org.simpleframework.xml.Root;

import java.awt.Point;

/**
 * Abstract agent location deployment constrain class.
 * Specific location deployment strategies should extend this class. 
 * 
 * @author Nuno Fachada
 *
 */
@Root
public abstract class LocationConstrain {

	/**
	 * Returns next coordinate for agent deployment.
	 * @return Next coordinate for agent deployment.
	 */
	public abstract Point getNextCoord();

	/**
	 * Initialize location constrain.
	 * @param model Constrains get the necessary parameters from LAISModel, such as
	 * dimension of the simulation environment.
	 */
	public void initialize(LAISModel model) {
		/*
		 * There is no general initialization; implementations of this class
		 * can override this method if needed.
		 */
	}
	

}
