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


package org.laseeb.LAIS.agent.actions;

import org.laseeb.LAIS.agent.Agent;
import org.laseeb.LAIS.space.Cell2D;
import org.simpleframework.xml.Root;

@Root
/**
 * The agent action interface. All agent action classes implement this interface.
 * 
 * @author Nuno Fachada
 */
public abstract class AgentAction implements Cloneable {
	
	/**
	 * Perform the respective agent action.
	 * 
	 * @param agent The agent that is going to perform the action.
	 * @param cell The cell where the agent is currently located.
	 * @param message Message passed by conditions.
	 * @throws ActionException If an error occurs during the execution of the action.
	 */
	public abstract void performAction(Agent agent, Cell2D cell, Object[] message) throws ActionException;
	
	/**
	 * Clones this AgentAction. This method may be overridden by concrete
	 * implementations of AgentAction (for example if the mutation rate
	 * parameter is to have any influence on the cloning process).
	 * 
	 * @param mutationRate The mutation rate of the cloning process.
	 * @return A clone of this AgentAction, possibly with some mutation.
	 * @throws CloneNotSupportedException If it's not possible to clone action.
	 */
	public AgentAction clone(float mutationRate) throws CloneNotSupportedException {
		return (AgentAction) this.clone();
	}
	
}
