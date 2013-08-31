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

import org.laseeb.LAIS.LAISModel;
import org.laseeb.LAIS.agent.AgentManager;
import org.laseeb.LAIS.substance.SubstanceManager;
import org.simpleframework.xml.Element;

import uchicago.src.sim.gui.Display2D;
import uchicago.src.sim.gui.Object2DDisplay;

/**
 * A space factory which provides objects for an square simulation space.
 * 
 * @author Nuno Fachada
 */
public class SquareSpaceFactory extends Abstract2DSpaceFactory {

	/** 
	 * <b>XML Element (boolean)</b>
	 * <p>
	 * Set this element to <code>true</code> if you want each cell to have eight
	 * neighbors (i.e., a Moore neighborhood). Default value is <code>false</code>,
	 * which specifies that each cell has four neighbors (Von Neumann neighborhood).
	 * <p> 
	 * <em>REQUIRED: NO</em> 
	 * */
	@Element(required=false)
	boolean moore = false;

	/** 
	 * <b>XML Element (boolean)</b>
	 * <p>
	 * Set this element to <code>true</code> if you want a toroidal simulation space.
	 * Default value is <code>false</code>.
	 * <p> 
	 * <em>REQUIRED: NO</em> 
	 * */
	@Element(required=false)
	boolean torus = false;

	/**
	 * Creates a cell for the square simulation space.
	 * 
	 * @see Abstract2DSpaceFactory#createCell(int, int, Abstract2DSpaceAdapter, SubstanceManager, AgentManager, LAISModel)
	 */
	@Override
	public Cell2D createCell(int numX, int numY,
			Abstract2DSpaceAdapter space, SubstanceManager substanceMan,
			AgentManager agentMan, LAISModel laisModel) {
		return new Cell2D(numX, numY, space, substanceMan, agentMan, laisModel);
	}

	/**
	 * Creates a display for the square simulation space.
	 * 
	 * @see Abstract2DSpaceFactory#createDisplay(Abstract2DSpaceAdapter)
	 */
	@Override
	public Display2D createDisplay(Abstract2DSpaceAdapter space) {
		return new Object2DDisplay(space.getRepastSpace());
	}

	/**
	 * Creates a square simulation space. If the <code>moore</code> field is set
	 * to <code>false</code> (the default), the simulation space will have a Von Neumann 
	 * neighborhood; if <code>moore</code> is set to <code>true</code>, then the
	 * simulation space will have a Moore neighborhood.
	 * 
	 * @see Abstract2DSpaceFactory#createSpace()
	 */
	@Override
	public Abstract2DSpaceAdapter createSpace() {
		if (moore)
			return new SquareMooreSpaceAdapter(x, y, torus);
		else
			return new SquareVonNeumannSpaceAdapter(x, y, torus);
	}

}
