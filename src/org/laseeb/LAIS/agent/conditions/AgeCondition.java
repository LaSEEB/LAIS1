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
 * This condition yields true in average for n = halflife iterations.
 * This rule will probably become obsolete by the development of a 
 * general-purpose numeric agent state approach.
 * 
 * @author Nuno Fachada
 * @deprecated This rule will probably become obsolete by the development of a 
 * general-purpose numeric agent state approach.
 */
@Deprecated
public class AgeCondition extends AgentCondition {

	/** 
	 * <strong>XML Element (integer > 0)</strong>
	 * <p>
	 * The agent's half-life.
	 * <p> 
	 * <em>REQUIRED: YES.</em> 
	 * */
	@Element
	int halflife;
	
	/**
	 * @see AgentCondition#evaluate(Agent, Cell2D, Object[])
	 */
	public boolean evaluate(Agent agent, Cell2D cell, Object[] message) throws ConditionException {
		double p = 1.0/halflife;		
		double doAction = cell.getRng().nextDoubleFromTo(0, 1);

		if (doAction < p) {
			return true;			
		} else {
			return false;
		}
	}

}
