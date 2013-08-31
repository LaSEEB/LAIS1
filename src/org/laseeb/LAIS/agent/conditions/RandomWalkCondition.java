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


package org.laseeb.LAIS.agent.conditions;

import org.laseeb.LAIS.agent.Agent;
import org.laseeb.LAIS.space.Cell2D;

import org.simpleframework.xml.Element;

/**
 * This condition determines a random direction for the agent.
 * <p>
 * The chosen direction will be passed to the actions.
 * @author Nuno Fachada
 */
public class RandomWalkCondition extends AgentCondition {
	
	/** 
	 * <strong>XML Element (integer &gt; 0)</strong>
	 * <p>
	 * Message index for destination cell reference. 
	 * <p> 
	 * <em>REQUIRED: YES.</em> 
	 * */
	@Element 
	int index;
	
	/**
	 * @see AgentCondition#evaluate(Agent, Cell2D, Object[])
	 */
	public boolean evaluate(Agent agent, Cell2D cell, Object[] message) throws ConditionException {
		
		int walk = cell.getRng().nextIntFromTo(0, cell.getNeighbors().size());
		if (walk != 0) {
			message[index] = walk - 1;
			return true;			
		} else {
			return false;
		}
	}
	
}
