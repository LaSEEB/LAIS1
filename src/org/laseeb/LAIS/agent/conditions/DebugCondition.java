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

import org.apache.log4j.Logger;
import org.laseeb.LAIS.agent.Agent;
import org.laseeb.LAIS.space.Cell2D;
import org.simpleframework.xml.Element;

/**
 * Test condition for debugging purposes. Prints a message in the standard output.
 * <p>
 * The printed message can be passed to the actions.
 * 
 * @author Nuno Fachada
 */
public class DebugCondition extends AgentCondition {

	/** 
	 * <strong>XML Element (integer > 0)</strong>
	 * <p>
	 *  Message index for passing string object to actions. 
	 * <p> 
	 * <em>REQUIRED: NO.</em> 
	 * */
	@Element(required=false)
	int index = -1;
	
	/** 
	 * <strong>XML Element (string)</strong>
	 * <p>
	 * String to send to logger and to actions. 
	 * <p> 
	 * <em>REQUIRED: YES.</em> 
	 * */
	@Element
	String stringToPrint;
	
	/* The logger. */
	private static Logger logger = Logger.getLogger(DebugCondition.class);

	/**
	 * @see AgentCondition#evaluate(Agent, Cell2D, Object[])
	 */
	public boolean evaluate(Agent agent, Cell2D cell, Object[] message) throws ConditionException {
		logger.debug("DebugconditionMessage: " + stringToPrint);
		if (index >= 0)
			message[index] = stringToPrint;
		return true;
	}

}
