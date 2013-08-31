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
import org.laseeb.LAIS.substance.Substance;
import org.laseeb.LAIS.substance.SubstanceProxy;
import org.simpleframework.xml.Element;

/**
 * Sets a substance reference in the agent in question (default) or in a given agent.
 * <p>
 * A substance can be set by:
 * <p>
 * 1 - Passing a substance in the message (possibly in the future allow passing a substance proxy).
 * <p>
 * 2 - Passing a substance reference which points to an already referenced substance proxy within the 
 * agent in question.
 * 
 * @author Nuno Fachada
 *
 */
public class SetSubRefAction extends AgentAction {

	/** 
	 * <strong>XML Element (integer >= 0)</strong>
	 * <p>
	 * Message index of agent to set the substance reference. If not given, substance 
	 * reference will be set in current agent.
	 * <p> 
	 * <em>REQUIRED: NO.</em> 
	 * */
	@Element(required=false)
	int indexAg = -1;
	
	/** 
	 * <strong>XML Element (integer >= 0)</strong>
	 * <p>
	 * Message index of substance to set reference of.
	 * <p> 
	 * <em>REQUIRED: NO.</em> 
	 * */
	@Element(required=false)
	int indexSub = -1;
	
	/** 
	 * <strong>XML Element (string)</strong>
	 * <p>
	 * Reference of new substance to set.
	 * <p> 
	 * <em>REQUIRED: NO.</em> 
	 * */
	@Element(required=false)
	String newSubRef = null;
	
	/** 
	 * <strong>XML Element (string)</strong>
	 * <p>
	 * The reference to the substance.
	 * <p> 
	 * </em>REQUIRED: YES.<em> 
	 * */
	@Element
	String subRef;
	
	/**
	 * @see AgentAction#performAction(Agent, Cell2D, Object[])
	 */
	public void performAction(Agent agent, Cell2D cell, Object[] message) throws ActionException {
		Agent agentOfInterest;
		SubstanceProxy subProxyToSet;
		/* If an agent is passed in the message, then change state of given agent. 
		 * Otherwise, change substance reference in self. */
		if (indexAg >= 0)
			agentOfInterest = (Agent) message[indexAg];
		else
			agentOfInterest = agent;
		/* If substance is passed in the message, use it, else use substance set
		 * in action. */
		if (indexSub >= 0)
			subProxyToSet = new SubstanceProxy((Substance) message[indexSub]);
		else
			try {
				subProxyToSet = agent.getSubstanceByRef(newSubRef);
			} catch (AgentException ae) {
				throw new ActionException(this.getClass().getSimpleName() + "is unable to set substance with reference " + newSubRef + " in agent '" + agent.getName() + "'.", ae);
			}
		/* Set reference. */
		agentOfInterest.setSubRef(subRef, subProxyToSet);
	}

}
