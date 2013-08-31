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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import org.laseeb.LAIS.agent.Agent;
import org.laseeb.LAIS.space.Cell2D;
import org.laseeb.LAIS.substance.Substance;
import org.simpleframework.xml.Element;


/**
 * This condition verifies if there is a more of a given substance concentration 
 * in the surface of other local agents. 
 * 
 * @author Nuno Fachada
 */
public class SubConInAgentCondition extends AgentCondition {

	/** 
	 * <strong>XML Element (float &gt; 0)</strong>
	 * <p>
	 * Substance concentration to check for in agent.
	 * <p> 
	 * <em>REQUIRED: YES.</em> 
	 * */
	@Element
	float concentration;

	/** 
	 * <strong>XML Element (integer &gt;= 0)</strong>
	 * <p>
	 * Message index of where to pass the agent with more than a given 
	 * substance concentration on its surface. 
	 * <p> 
	 * <em>REQUIRED: NO.</em>
	 * */
	@Element(required=false)
	int indexAg = -1;
	
	/** 
	 * <strong>XML Element (integer &gt;= 0)</strong>
	 * <p>
	 * Message index of agent selected by a previous condition. If not set then
	 * this condition will investigate all agents in cell. 
	 * <p> 
	 * <em>REQUIRED: NO.</em>
	 * */
	@Element(required=false)
	Integer indexPrevAgent;

	/** 
	 * <strong>XML Element (a reference to a {@link org.laseeb.LAIS.substance.Substance})</strong>
	 * <p>
	 * Reference of substance to check presence of in the agent.
	 * <p> 
	 * <em>REQUIRED: YES.</em> 
	 * */
	@Element
	Substance substance;
	
	/**
	 * @see AgentCondition#evaluate(Agent, Cell2D, Object[])
	 */
	public boolean evaluate(Agent agent, Cell2D cell, Object[] message) throws ConditionException {
		
		/* List of agents that have the given superficial substance */
		ArrayList<Agent> agents = new ArrayList<Agent>();
		
		/* condition decision. */
		boolean decision = false;
		/* Get agent iterator. */
		Iterator<Agent> iterAgents;
		if (indexPrevAgent == null) {
			/* Iterate through all agents in cell. */
			iterAgents = cell.agentIterator();
		} else {
			/* Use only one agent, chosen in a previous condition. */
			Vector<Agent> oneAgentVector = new Vector<Agent>(1);
			oneAgentVector.add((Agent) message[indexPrevAgent]);
			iterAgents = oneAgentVector.iterator();
		}
		while (iterAgents.hasNext()) {
			Agent ag = iterAgents.next();
			if (ag.containsSubstance(substance)) {
				if (ag.getSubstanceCon(substance) > concentration) {
					/* If at least one agent contains more than the given substance concentration,
					 * the condition will yield true. */
					decision = true;
					/* The agent will be included for eventual selection and passed to the condition. */
					agents.add(ag);
				}
			}
		}		
		/* If there the decision is true, randomly select one of the agents to pass
		 * on the the action. */
		// TODO We can eventually make this random selection be biased towards the agents with more substance
		if (decision) {
			int whichAgent = cell.getRng().nextIntFromTo(0, agents.size() - 1);
			Agent ag = agents.get(whichAgent);
			if (indexAg >= 0)
				message[indexAg] = ag;			
		}
		return decision;
	}

}
