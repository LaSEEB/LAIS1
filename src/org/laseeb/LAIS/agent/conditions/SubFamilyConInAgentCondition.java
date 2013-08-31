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

import java.util.Iterator;
import java.util.Vector;

import org.laseeb.LAIS.agent.Agent;
import org.laseeb.LAIS.space.Cell2D;
import org.laseeb.LAIS.substance.Substance;
import org.laseeb.LAIS.substance.SubstanceFamily;

import org.simpleframework.xml.Element;

/**
 * This condition checks for the presence of a given substance family on the surface of a 
 * nearby agent.
 * @author Nuno Fachada
 */
public class SubFamilyConInAgentCondition extends AgentCondition {

	/** 
	 * <strong>XML Element (integer &gt;= 0)</strong>
	 * <p>
	 * Message index where to pass agent in which the substance family was found. 
	 * <p> 
	 * <em>REQUIRED: NO.</em>
	 * */
	@Element(required=false)
	int indexAg = -1;
	
	/** 
	 * <strong>XML Element (integer &gt;= 0)</strong>
	 * <p>
	 * Message index where to pass substance concentration. 
	 * <p> 
	 * <em>REQUIRED: NO.</em> 
	 * */
	@Element(required=false)
	int indexCon = -1;
	
	/** 
	 * <strong>XML Element (integer &gt;= 0)</strong>
	 * <p>
	 * Message index of agent previously selected by other condition. If not set then
	 * this condition will investigate all agents in cell. 
	 * <p> 
	 * <em>REQUIRED: NO.</em> 
	 * */
	@Element(required=false)
	int indexPrevAgent = -1;

	/** 
	 * <strong>XML Element (integer &gt;= 0)</strong>
	 * <p>
	 * Message index of specific substance of family given by <code>subFamily</code> element. 
	 * <p> 
	 * <em>REQUIRED: NO.</em> 
	 * */
	@Element(required=false)
	int indexSub = -1;
	
	/** 
	 * <strong>XML Element (float &gt; 0)</strong>
	 * <p>
	 * Minimum quantity substance quantity for this rule to yield true.
	 * <p> 
	 * <em>REQUIRED: YES.</em>
	 * */
	@Element
	float minQuantity;

	/** 
	 * <strong>XML Element ({@link org.laseeb.LAIS.substance.SubstanceFamily} object reference)</strong>
	 * <p>
	 * The substance family to check presence of. 
	 * <p> 
	 * <em>REQUIRED: YES.</em>
	 * */
	@Element
	SubstanceFamily subFamily;
	
	/**
	 * @see AgentCondition#evaluate(Agent, Cell2D, Object[])
	 */
	public boolean evaluate(Agent agent, Cell2D cell, Object[] message) throws ConditionException {
		
		/* Get agent iterator. */
		Iterator<Agent> iterAgents;
		if (indexPrevAgent < 0) {
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
			Vector<Substance> possibleSubs = new Vector<Substance>();
			/* Cycle through substances in agent. */
			Iterator<Substance> iterSub = ag.substanceIterator();
			while (iterSub.hasNext()) {
				Substance sub = iterSub.next();
				if (sub.getFamily().equals(subFamily)) {
					if (ag.getSubstanceCon(sub) > minQuantity) {
						/* If substance is found, add to vector. */
						possibleSubs.add(sub);
					}
				}
			}
			/* If substance is found, pass message and return true; */
			if (possibleSubs.size() > 0) {
				/* Chose one randomly. */
				Substance sub = possibleSubs.get(cell.getRng().nextIntFromTo(0, possibleSubs.size() - 1));
				if (indexCon >= 0)
					message[indexCon] = ag.getSubstanceCon(sub);
				if (indexSub >= 0)
					message[indexSub] = sub;
				if (indexAg >= 0)
					message[indexAg] = ag;
				return true;
			}
		}
		/* Not found, return false. */
		return false;
	}
}
