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


package org.laseeb.LAIS.agent;

import org.laseeb.LAIS.utils.random.IRng;

/**
 * Interface implemented by agent producing classes. 
 * It's purpose is to decouple agent deploying classes from the agent producing 
 * classes.
 *  
 * @author Nuno Fachada
 */
public interface AgentFactory {

	/**
	 * Agent factory method. Creates an agent with the given mutation rate.
	 * 
	 * @param agent The name of agent to create a clone of.
	 * @param mutationRate The mutation rate with which the agent will be cloned.
	 * @param id A unique agent ID.
	 * @param rng An instance of a random number generator.
	 * @return An instance of the requested agent.
	 * @throws CloneNotSupportedException If it's not possible to create the agent.
	 * @throws AgentException When it's not possible to create the agent.
	 */
	public Agent createAgent(String agent, float mutationRate, int id, IRng rng) throws CloneNotSupportedException, AgentException;
	
}
