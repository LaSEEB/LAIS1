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

/**
 * Simulation space factories should extend this abstract class.
 * Subclasses of this class provide the simulator with necessary objects 
 * (the space, its cells and its display) for the space type they represent.
 * 
 * @author Nuno Fachada
 *
 */
public abstract class Abstract2DSpaceFactory {
	
	/** 
	 * <b>XML Element (integer >= 1)</b>
	 * <p>
	 * Horizontal dimension of simulation space.
	 * <p> 
	 * <em>REQUIRED: YES</em> 
	 * */
	@Element
	int x;

	/** 
	 * <b>XML Element (integer >= 1)</b>
	 * <p>
	 * Vertical dimension of simulation space.
	 * <p> 
	 * <em>REQUIRED: YES</em> 
	 * */
	@Element
	int y;

	/**
	 * Returns a new simulation space of dimensions (x,y).
	 * 
	 * @return A new simulation space of dimensions (x,y).
	 */
	public abstract Abstract2DSpaceAdapter createSpace();
	
	/**
	 * Returns a new simulation display for the given simulation space.
	 * 
	 * @return A new simulation display for the given simulation space.
	 */
	public abstract Display2D createDisplay(Abstract2DSpaceAdapter space);
	
	/**
	 * Returns a new cell, which corresponds to a region in the simulation space.
	 * 
	 * @return A new cell, which corresponds to a region in the simulation space.
	 */
	public abstract Cell2D createCell(int numX,
			int numY,
			Abstract2DSpaceAdapter space,
			SubstanceManager substanceMan,
			AgentManager agentMan,
			LAISModel laisModel);
	
}
