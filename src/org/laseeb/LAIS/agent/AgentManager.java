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


package org.laseeb.LAIS.agent;

import java.util.*;
import java.awt.Color;

//import org.laseeb.LAIS.event.AgentDeploy;
import org.laseeb.LAIS.utils.random.IRng;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Validate;

/**
 * This class manages agents in the simulation, more specifically:
 * <ul>
 * <li>Maintains a list of agent prototypes used to create new agents.</li>
 * <li>Keeps record of the number of agents currently in the simulation.</li>
 * </ul>
 * 
 * @author Nuno Fachada
 *
 */
@Root
public class AgentManager implements AgentFactory, AgentWatcher {

	/** 
	 * <strong>XML ElementMap (key: {@link java.lang.String}, value: {@link org.laseeb.LAIS.agent.AgentPrototype})</strong>
	 * <p>
	 * A map of agent prototype definitions, where the key is the agent 
	 * prototype name, and the value is the agent prototype. Should be used in the
	 * following format:
	 * <p>
	 * <code>
	 * &lt;agentDefinition name="nameOfAgentOne"&gt;<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;agentPrototype&gt;<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;...<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;/agentPrototype&gt;<br/>
	 * &lt;/agentDefinition&gt;<br/>
	 * &lt;agentDefinition name="nameOfAgentTwo"&gt;<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;agentPrototype&gt;<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;...<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;/agentPrototype&gt;<br/>
	 * &lt;/agentDefinition&gt;<br/>
	 * .<br/>
	 * .<br/>
	 * .
	 * </code>
	 * <p>
	 * <em>REQUIRED: NO</em> 
	 * */
	@ElementMap(entry="agentDefinition",inline=true,key="name",attribute=true,required=false)
	LinkedHashMap<String, AgentPrototype> agentMap = new LinkedHashMap<String, AgentPrototype>();
	
	/* Runtime agent numbers by state, updated externally. */
	private HashMap<String, AgentNumbersMap> agentNumbersByState;
	
	/**
	 * Basic initialization.
	 */
	public AgentManager() {
		agentNumbersByState = new HashMap<String, AgentNumbersMap>();
	}
	
	/**
	 * This internal class is a data source regarding the number of agents in the simulation for 
	 * data export classes (charts, sequences, output to file...).
	 *  
	 * @author Nuno Fachada
	 */
	public class AgentNumberSource {
		
		/* The agent name. */
		private String myAgentName;
		/* States map. */
		private AgentStateMap statesMap;
		
		/**
		 * The constructor assigns the internal variables as references to AgentManager variables.
		 *  
		 * @param agName Reference to the agent name.
		 */
		public AgentNumberSource(String agName, AgentStateMap statesMap) {
			this.myAgentName = agName;
			this.statesMap = statesMap;
		}
		
		/**
		 * Returns the number of agent with name myAgentName (this class internal variable) in the simulation.
		 * 
		 * @return The number of agent with name myAgentName (this class internal variable) in the simulation.
		 */
		public int getAgentNumbers() {
			return getNumbers(this.myAgentName, this.statesMap);
		}		
	}
	
	
	/**
	 * Returns an iterator that sweeps the agent prototypes.
	 * 
	 * @return An iterator that sweeps the agent prototypes.
	 */
	public Iterator<AgentPrototype> prototypeIterator() {
		return new Iterator<AgentPrototype>() {
			Iterator<AgentPrototype> iterAgentData = agentMap.values().iterator();
			public AgentPrototype next() {
				return iterAgentData.next();
			}
			public void remove() {}
			public boolean hasNext() {
				return iterAgentData.hasNext();
			}
		};
	}
	
	/**
	 * Returns the number of agents of given type currently present in the simulation.
	 * 
	 * @param agStr The type of agent to check number of.
	 * @param map The states the agent must be to be counted in.
	 * @return The number of agents of given type.
	 * @see org.laseeb.LAIS.agent.AgentWatcher#getNumbers(String, AgentStateMap)
	 */
	public int getNumbers(String agStr, AgentStateMap map) {
		AgentNumbersMap agMap = agentNumbersByState.get(agStr);
		if (agMap == null)
			return 0;
		else
			return agMap.getNumbersByStates(map);
	}
	
	/**
	 * Agent factory method. Creates an agent with the given mutation rate.
	 * 
	 * @param agent The name of agent to create a clone of.
	 * @param mutationRate The mutation rate with which the agent will be cloned.
	 * @param id A unique agent ID.
	 * @param rng An instance of a random number generator.
	 * @return An instance of the requested agent.
	 * @throws CloneNotSupportedException If it's not possible to create the agent.
	 * @throws AgentException When it's not possible to create the agent.
	 * @see org.laseeb.LAIS.agent.AgentFactory#createAgent(String, float, IRng)
	 */
	public Agent createAgent(String agent, float mutationRate, int id, IRng rng) throws CloneNotSupportedException, AgentException {
		Agent ag = agentMap.get(agent).createAgent(mutationRate, id, rng);
		return ag;
	}
	
	/**
	 * Returns a data source regarding the number of agents in the simulation.
	 * 
	 * @param agName The name of the agents that the source should check for.
	 * @param statesMap Only count the agent if it is in these specified states. 
	 * @return A data source regarding the number of agents in the simulation.
	 */
	public AgentNumberSource getAgentNumberSource(String agName, AgentStateMap statesMap) {
		return new AgentNumberSource(agName, statesMap);
	}
	
	/**
	 * Returns an array of state names for a given state type of a given agent.
	 * @param ag Agent to obtain the state names for a given state type.
	 * @param stateType The state type to get the state names of.
	 * @return An array of state names for a given state type of a given agent.
	 */
	public String[] getStateNames(String ag, String stateType) {
		return agentMap.get(ag).getStateNames(stateType);		
	}
	
	/**
	 * Return the color of a given agent.
	 * 
	 * @param ag The agent to return the color of.
	 * @return The color of a given agent.
	 */
	public Color getColor(String ag) {
		return agentMap.get(ag).getColor();
	}

	/**
	 * Reset number of agents by state.
	 */
	public void resetNumbers() {
		agentNumbersByState.clear();
	}
	
	/**
	 * Sets the minimum substance concentration threshold for the agents superficial substances. 
	 * @param minConThreshold
	 */
	public void setMinConThreshold(float minConThreshold) {
		Iterator<AgentPrototype> agProtIter= prototypeIterator();
		while (agProtIter.hasNext()) {
			AgentPrototype ap = agProtIter.next();
			ap.setMinConThreshold(minConThreshold);
		}
	}
	
	/**
	 * Update agent numbers.
	 * @param iterAgents
	 */
	public synchronized void updateAgents(Iterator<Agent> iterAgents) {
		while (iterAgents.hasNext()) {
			Agent ag = iterAgents.next();
			if (agentNumbersByState.containsKey(ag.getPrototypeName())) {
				AgentNumbersMap am = agentNumbersByState.get(ag.getPrototypeName());
				am.update(ag.getStatesMap());
			} else {
				AgentNumbersMap am = new AgentNumbersMap(ag.getStatesMap().clone(), 1);
				agentNumbersByState.put(ag.getPrototypeName(), am);
			}
		}
	}
	
	/**
	 * This method initializes the agent names.
	 */
	@Validate
	public void validate() {
		Iterator<String> iterAgName = agentMap.keySet().iterator();
		while (iterAgName.hasNext()) {
			String agName = iterAgName.next();
			AgentPrototype ap = agentMap.get(agName);
			ap.setName(agName);
		}
	}
	
}
