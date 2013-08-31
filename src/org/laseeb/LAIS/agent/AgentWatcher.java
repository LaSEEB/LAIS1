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

/**
 * Interface implemented by agent management classes. 
 * It's purpose is to decouple agent deploying classes which depend on the current
 * number of agents in the simulation from the agent management classes. 
 *  
 * @author Nuno Fachada
 */
public interface AgentWatcher {

	/**
	 * Returns the number of agents of given type currently present in the simulation.
	 * 
	 * @param agStr The type of agent to check number of.
	 * @param map The states the agent must be to be counted in.
	 * @return The number of agents of given type.
	 */
	public int getNumbers(String agStr, AgentStateMap map);
	
}
