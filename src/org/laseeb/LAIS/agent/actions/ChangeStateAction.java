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
import org.laseeb.LAIS.agent.AgentException;
import org.laseeb.LAIS.space.Cell2D;
import org.simpleframework.xml.Element;

/**
 * Changes a specified agent state type to a specified state value.
 * 
 * @author Nuno Fachada
 */
public class ChangeStateAction extends AgentAction {
	
	/** 
	 * <strong>XML Element (integer >= 0)</strong>
	 * <p>
	 * Message index referencing the agent to change state. 
	 * If not given, state will change in this agent.
	 * <p> 
	 * <em>REQUIRED: NO.</em> 
	 * */
	@Element(required=false)
	int indexAg = -1;
	
	/** 
	 * <strong>XML Element (integer >= 0)</strong>
	 * <p>
	 * Index of dynamic state value. 
	 * <p>
	 * <em>REQUIRED: NO.</em> 
	 * */
	@Element(required=false)
	int indexStateValue = -1;	
	
	/** 
	 * <strong>XML Element ({@link java.lang.String})</strong>
	 * <p>
	 * Specified agent state type. 
	 * <p>
	 * <em>REQUIRED: YES.</em>
	 * */
	@Element
	String stateType;
	
	/** 
	 * <strong>XML Element ({@link java.lang.String})</strong>
	 * <p>
	 * Specified agent state value. 
	 * <p>
	 * <em>REQUIRED: NO.</em>
	 * */
	@Element(required=false)
	String stateValue = null;	

	/**
	 * @see AgentAction#performAction(Agent, Cell2D, Object[])
	 */
	public void performAction(Agent agent, Cell2D cell, Object[] message) throws ActionException {
		/* Determine state value to set. */
		String effectiveStateValue;
		if (indexStateValue >= 0)
			/* If a dynamic state value is given, then use it has the effective state
			 * value to set... */
			effectiveStateValue = (String) message[indexStateValue];
		else
			/* ...otherwise use static state value as the effective state value to set. */  
			effectiveStateValue = stateValue;
		/* Determine agent to change state. */
		Agent agentOfInterest;
		if (indexAg >= 0)
			/* If an agent is passed in the message, then change state of that agent... */
			agentOfInterest = (Agent) message[indexAg];
		else
			/* ...otherwise change state of this agent. */
			agentOfInterest = agent;
		/* Change state value of state type in given agent. */
		if (!agentOfInterest.getPrototype().isValidState(stateType, effectiveStateValue))
			throw new ActionException("Tried to change state '" + stateType + "' of agent of type '" 
				+  agent.getPrototypeName() + "' to invalid state '" + effectiveStateValue + "'");
		try {
			agentOfInterest.setNextState(stateType, effectiveStateValue);
		} catch (AgentException ae) {
			throw new ActionException(ae);
		}
	}
}
