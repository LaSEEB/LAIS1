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

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

import org.laseeb.LAIS.agent.Agent;
import org.laseeb.LAIS.agent.AgentException;
import org.laseeb.LAIS.agent.AgentPrototype;
import org.laseeb.LAIS.agent.AgentStateMap;
import org.laseeb.LAIS.space.Cell2D;
import org.laseeb.LAIS.substance.SubstanceProxy;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementMap;

/**
 * This action makes the agent produce other agents (e.g. an infected cell producing viruses).
 * @author Nuno Fachada
 */
public class ProduceAgentAction extends AgentAction {

	
	/** 
	 * <strong>XML Element ({@link org.laseeb.LAIS.agent.AgentPrototype} reference.)</strong>
	 * <p>
	 * Type of agent to produce.
	 * <p> 
	 * <em>REQUIRED: YES.</em>
	 * */
	@Element
	AgentPrototype agentToProduce;
	
	/** 
	 * <strong>XML Element ({@link org.laseeb.LAIS.agent.AgentStateMap} object)</strong>
	 * <p>
	 * State map of newly created agents. If not given, an empty map will be created.
	 * <p> 
	 * <em>REQUIRED: NO.</em>
	 * */
	@Element(required=false)
	AgentStateMap agStateMap =
		new AgentStateMap();

	/** 
	 * <strong>XML Element (integer >= 0)</strong>
	 * <p>
	 * Message index containing a weight for controlling the effective number of agents
	 * to produce. If not referred, <code>numAgents</code> value will be used.
	 * <p> 
	 * <em>REQUIRED: NO.</em>
	 * */
	@Element(required=false)
	int indexWeight = -1;

	/** 
	 * <strong>XML Element (0 <= float <= 1)</strong>
	 * <p>
	 * Mutation rate of agents to produce.
	 * <p> 
	 * <em>REQUIRED: YES.</em>
	 * */
	@Element
	float mutRate;
	
	/** 
	 * <strong>XML Element (integer >= 0)</strong>
	 * <p>
	 * Number of agents to produce.
	 * <p> 
	 * <em>REQUIRED: YES.</em>
	 * */
	@Element
	int numAgents;
	
	/** 
	 * <strong>XML ElementMap (key: string, value: string)</strong>
	 * <p>
	 * Runtime substance references. If not given, no new 
	 * references will be added to agent.
	 * <p> 
	 * <em>REQUIRED: NO.</em>
	 * */
	@ElementMap(required=false,attribute=true,entry="subRef",key="ref")
	Map<String, String> runtimeSubRef = null;	

	/** 
	 * <strong>XML ElementMap (key: string, value: float)</strong>
	 * <p>
	 * Initial substance concentration map. If not given, new agents will have zero 
	 * surface substance concentration.
	 * <p> 
	 * <em>REQUIRED: NO.</em> 
	 * */
	@ElementMap(required=false,attribute=true,entry="substance",key="name")
	Map<String, Float> supSubConMap = new HashMap<String, Float>();

	/**
	 * @see AgentAction#performAction(Agent, Cell2D, Object[])
	 */
	public void performAction(Agent agent, Cell2D cell, Object[] message) throws ActionException {
		/* Determine weight. */
		float weight;
		if (indexWeight >= 0)
			weight = (Float) message[indexWeight];
		else
			weight = 1;
		/* Determine number of agents to produce. */
		int effectiveNumAgents = Math.round(numAgents * weight);
		/* Produce agents. */
		for (int i = 0; i < effectiveNumAgents; i++) {
			Agent newAgent;
			try {
				try {
					newAgent = agentToProduce.createAgent(
							mutRate,
							cell.getUniqueID(),
							cell.getRng());
				} catch (AgentException ae) {
					throw new ActionException(this.getClass().getSimpleName() + " is unable to produce agent '" + agentToProduce.getName() + "'.", ae);
				}
			} catch (CloneNotSupportedException cnse) {
				throw new ActionException("Could not produce a new agent!", cnse);
			}
			/* Set runtime references, if applicable. */
			if (runtimeSubRef != null) {
				/* Create a substance reference map. */
				Map<String, SubstanceProxy> refSubMap = new HashMap<String, SubstanceProxy>();
				/* Get an iterator. */
				Iterator<String> iterSubRef = runtimeSubRef.keySet().iterator();
				/* Set the new agent runtime substance references, which depend on the local 
				 * agent substance references. */
				while (iterSubRef.hasNext()) {
					String subRef = iterSubRef.next();
					String localAgentSubRef = runtimeSubRef.get(subRef);
					SubstanceProxy sp;
					try {
						sp = agent.getSubstanceByRef(localAgentSubRef);
					} catch (AgentException ae) {
						throw new ActionException(this.getClass().getName() + " is unable to get substance with reference '" + localAgentSubRef + "'.");
					}
					refSubMap.put(subRef, sp);
				}
				/* Perform required mutation. */
				try {
					refSubMap = AgentPrototype.cloneRefSubMap(refSubMap, mutRate, cell.getRng());
				} catch (AgentException ae) {
					throw new ActionException(ae);
				}
				/* Add new references to agent. */
				newAgent.addSubRefMap(refSubMap);
			}
			/* Set state map. */
			try {
				newAgent.setStateMap(agStateMap);
			} catch (AgentException ae) {
				throw new ActionException(this.getClass().getSimpleName() + "is unable to set state map in agent " + newAgent.getPrototypeName() + ".", ae);
			}
			/* Set initial substance concentration. */
			try {
				newAgent.setSupSubCon(supSubConMap);
			} catch (AgentException ae) {
				throw new ActionException(this.getClass().getSimpleName() + "is unable to set superficial substance concentration map in agent " + newAgent.getPrototypeName() + ".", ae);
			}
			/* Put agent in cell. */
			cell.setAgentToBorn(newAgent);
		}

	}

}
