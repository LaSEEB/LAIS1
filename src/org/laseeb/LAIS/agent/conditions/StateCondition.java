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
import org.simpleframework.xml.ElementArray;

/**
 * This condition yields true if the agent is in  a given state or states.
 * @author Nuno Fachada
 */
public class StateCondition extends AgentCondition {
	
	/** 
	 * <strong>XML Element ({@link java.lang.String})</strong>
	 * <p>
	 * State type to check for. 
	 * <p> 
	 * <em>REQUIRED: YES.</em> 
	 * */
	@Element
	String stateType;

	/** 
	 * <strong>XML ElementArray (array of {@link java.lang.String}s)</strong>
	 * <p>
	 * Array of state values to check for. 
	 * <p> 
	 * <em>REQUIRED: YES.</em>
	 * */
	@ElementArray
	String[] stateValues;

	/**
	 * @see AgentCondition#evaluate(Agent, Cell2D, Object[])
	 */
	public boolean evaluate(Agent agent, Cell2D cell, Object[] message) throws ConditionException {
		for (String str : stateValues) {
			//if (str.compareTo(agent.getState(stateType)) == 0)
			if (str.equals(agent.getState(stateType)))
				return true;
		}
		return false;
	}

}
