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


package org.laseeb.LAIS.space;

import uchicago.src.sim.space.Object2DGrid;

/**
 * Abstract class for square simulation spaces. Concrete classes determine
 * the type
 * Encapsulates a Repast {@link uchicago.src.sim.space.Object2DGrid} object
 * to provide the required functionality. 
 * 
 * @author Nuno Fachada
 */
public abstract class SquareSpaceAdapter extends Abstract2DSpaceAdapter {

	/* Indicates if space is toroidal. */
	protected boolean torus;
	
	/**
	 * Protected constructor for the square space adapter. It is to be called
	 * by concrete square space adapter implementations.
	 * Parameters determine the simulation space dimension and
	 * whether the space will be toroidal or not.
	 * 
	 * @param x Horizontal dimension of the space.
	 * @param y Vertical dimension of the space.
	 * @param torus If true, simulation space will be toroidal.
	 */
	protected SquareSpaceAdapter(int x, int y, boolean torus) {
		this.torus = torus;
		space = new Object2DGrid(x, y);
	}
	
}
