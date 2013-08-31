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
import org.simpleframework.xml.Element;

/**
 * The agent walks to a neighborhood determined by the respective condition.
 * 
 * @author Nuno Fachada
 */
public class WalkAction extends AgentAction {
	
	/** 
	 * <strong>XML Element (integer >= 0)</strong>
	 * <p>
	 * Message index of the agent's destination.
	 * <p> 
	 * <em>REQUIRED: YES.</em>
	 * */
	@Element
	int index;
	
	/**
	 * @see AgentAction#performAction(Agent, Cell2D, Object[])
	 */
	public void performAction(Agent agent, Cell2D cell, Object[] message) throws ActionException {

		int destination = (Integer) message[index];
		if ((destination < 0) || (destination > cell.getNeighbors().size() - 1))
			throw new ActionException("Error in action '" + 
					this.getClass().getSimpleName() + 
					"': destination value is " + 
					destination + ", " +
					"but must be between 0 and " + (cell.getNeighbors().size() - 1) + ".");
		cell.setAgentToMoveOut(agent, (Cell2D) cell.getNeighbors().get(destination));
	}

}
