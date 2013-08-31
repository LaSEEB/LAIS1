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
import org.laseeb.LAIS.agent.AgentException;
import org.laseeb.LAIS.space.Cell2D;
import org.laseeb.LAIS.substance.Substance;
import org.laseeb.LAIS.substance.SubstanceException;
import org.laseeb.LAIS.substance.SubstanceFamily;
import org.simpleframework.xml.Element;

/**
 * This condition checks for any substance in the surface of other agents which has functional
 * affinity greater than a given value with a given substance in the current agent surface.
 * 
 * @author Nuno Fachada
 */
public class SubConAffinInAgentCondition extends AgentCondition {
	
	/** 
	 * <strong>XML Element (0 &lt;= integer &lt;= 63)</strong>
	 * <p>
	 * Ending bit of affinity calculation. 
	 * <p> 
	 * <em>REQUIRED: YES.</em> 
	 * */
	@Element
	int endBit;
	
	/** 
	 * <strong>XML Element ({@link org.laseeb.LAIS.substance.SubstanceFamily} object reference)</strong>
	 * <p>
	 * If this field is used, the substance in local cell must belong 
	 * to the specified substance family. 
	 * <p> 
	 * <em>REQUIRED: NO.</em> 
	 * */
	@Element(required=false)
	SubstanceFamily family = null;
	
	/** 
	 * <strong>XML Element (integer &gt;= 0)</strong>
	 * <p>
	 * Message index where to pass an agent with the required superficial substance. If
	 * not used, no agent will be passed. 
	 * <p> 
	 * <em>REQUIRED: NO.</em> 
	 * */
	@Element(required=false)
	int indexAg = -1;
	
	/** 
	 * <strong>XML Element (integer &gt;= 0)</strong>
	 * <p>
	 * Message index of the calculated substance minimum match. If not used, this value 
	 * will not be passed to the actions.
	 * <p> 
	 * <em>REQUIRED: NO.</em> 
	 * */
	@Element(required=false)
	int indexMinMatch = -1;

	/** 
	 * <strong>XML Element (integer &gt;= 0)</strong>
	 * <p>
	 * Message index of agent selected by a previous condition. If not set
	 * this condition will investigate all agents in cell.
	 * <p> 
	 * <em>REQUIRED: NO.</em> 
	 * */
	@Element(required=false)
	Integer indexPrevAgent;

	/** 
	 * <strong>XML Element (integer &gt;= 0)</strong>
	 * <p>
	 * Message index of the substance detected in the agent which has affinity 
	 * with substance referenced by the <code>substance</code> element.
	 * <p> 
	 * <em>REQUIRED: NO.</em> 
	 * */
	@Element(required=false)
	int indexSub = -1;
	
	/** 
	 * <strong>XML Element (0 &lt;= float &lt;= 1)</strong>
	 * <p>
	 * Minimum match for condition to yield true. Match = affinity x percent.
	 * <p> 
	 * <em>REQUIRED: YES.</em> 
	 * */
	@Element
	float minMatch;
	
	/** 
	 * <strong>XML Element (0 &lt;= integer &lt;= 63)</strong>
	 * <p>
	 * Starting bit for affinity calculation. 
	 * <p> 
	 * <em>REQUIRED: YES.</em> 
	 * */
	@Element
	int startBit;
	
	/** 
	 * <strong>XML Element ({@link java.lang.String})</strong>
	 * <p>
	 * Reference of substance to which the affinity of other agents superficial 
	 * substances will be compared.
	 * <p> 
	 * <em>REQUIRED: YES.</em> 
	 * */
	@Element
	String substance;
	
	//TODO Should set a minimum local substance
	/**
	 * @see AgentCondition#evaluate(Agent, Cell2D, Object[])
	 */
	public boolean evaluate(Agent agent, Cell2D cell, Object[] message) throws ConditionException {
		/* Get substance to compare from agent. */
		Substance substanceToCompare;
		try {
			substanceToCompare = agent.getSubstanceByRef(substance).getSubstance(agent);
		} catch (SubstanceException se) {
			throw new ConditionException(this.getClass().getName() + " is unable to get substance with reference '" + substance + "' from agent '" + agent.getName() + "'.", se);
		} catch (AgentException ae) {
			throw new ConditionException(this.getClass().getName() + " is unable to get substance with reference '" + substance + "' from agent '" + agent.getName() + "'.", ae);
		}
		if (substanceToCompare == null) return false;
		/* Concentration of substance to compare in current agent. */
		float conSubToCompare = agent.getSubstanceCon(substanceToCompare);
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
		/* Cycle through agents. */
		while (iterAgents.hasNext()) {
			Substance currSub = null;
			float currMinMatch = 0.0f;
			Agent ag = (Agent) iterAgents.next();
			/* Cycle through substances of currently selected other agent. */
			Iterator<Substance> iterSub = ag.substanceIterator();
			while (iterSub.hasNext()) {
				/* Get next substance of currently selected other agent. */
				Substance sub = iterSub.next();
				/* Check if substance belong to family, if not check next substance. */
				if (family != null)
					if (!sub.getFamily().equals(family))
						continue;
				/* Get concentration of such substance in surface of currently selected other agent. */
				float conOtherAgentSub = ag.getSubstanceCon(sub);
				/* Get affinity of this agent's substance to compare with 
				 * substance of currently selected other agent. */
				float affinity;
				try {
					affinity = substanceToCompare.affinityWith(sub, startBit, endBit);
				} catch (SubstanceException se) {
					throw new ConditionException(this.getClass().getName() + " is unable to determine affinity between substances '" + substanceToCompare.getName() + "' and '" + sub.getName() + "' (start bit:" + startBit + ", end bit:" + endBit + ").", se);
				}
				/* Get percentage of substance in other agent that matches the substance to compare 
				 * in current agent. */
				float percentConMatch = Math.min(1.0f, conOtherAgentSub / conSubToCompare);
				/* Check if substance in other agent fits the minimum requirements in terms of 
				 * concentration and affinity in order for the condition to yield a positive match. */
				float minMatchTemp = percentConMatch * affinity;
				if (minMatchTemp > minMatch) {
					/* Check if match is higher than current match. */
					if (minMatchTemp > currMinMatch) {
						currSub = sub;
						currMinMatch = minMatchTemp;
					}
				}
			}
			/* If any substances were found in this agent, return the best. */
			if (currSub != null) {
				if (indexAg >= 0)
					message[indexAg] = ag;
				if (indexSub >= 0)
					message[indexSub] = currSub;
				if (indexMinMatch >= 0)
					message[indexMinMatch] = currMinMatch;
				return true;
			}
		}		
		return false;
	}
	

}
