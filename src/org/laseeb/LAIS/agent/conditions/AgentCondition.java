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

import org.simpleframework.xml.Root;
import org.simpleframework.xml.Attribute;

/**
 * Agent conditions extend this abstract class. More specifically, agent conditions implement the
 * {@link #evaluate(Agent, Cell2D, Object[])} method.
 * 
 * @author Nuno Fachada
 */
@Root
public abstract class AgentCondition implements Cloneable {
	
	/** 
	 * <strong>XML Attribute (boolean)</strong>
	 * <p>
	 * This boolean will be XORed with the final condition decision. 
	 * Default value is <code>false</code> if not specified.
	 * <p> 
	 * <em>REQUIRED: NO.</em> 
	 * */
	@Attribute(required=false)
	boolean complement = false;
	
	/**
	 * Abstract agent condition.
	 * @param agent The agent in which the condition is incorporated.
	 * @param cell The cell where the agent is located.
	 * @param message Parameter list to be passed an action.
	 * @return True if this conditions condition verifies itself.
	 * @throws ConditionException If an error occurs during the evaluation of the condition.
	 */
	public abstract boolean evaluate(Agent agent, Cell2D cell, Object[] message) throws ConditionException;
		
	/**
	 * Returns a boolean which will be XORed with the final condition decision.
	 * @return A boolean which will be XORed with the final condition decision.
	 */
	public boolean getComplement() {
		return complement;
	}
	
	/**
	 * Clones this AgentCondition. This method may be overridden by concrete
	 * implementations of AgentCondition (for example if the mutation rate
	 * parameter is to have any influence on the cloning process).
	 * 
	 * @param mutationRate The mutation rate of the cloning process.
	 * @return A clone of this AgentCondition, possibly with some mutation.
	 * @throws CloneNotSupportedException If it's not possible to clone this condition.
	 */
	public AgentCondition clone(float mutationRate) throws CloneNotSupportedException {
		return (AgentCondition) this.clone();
	}

}
