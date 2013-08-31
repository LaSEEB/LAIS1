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

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * An agent view is used for obtaining groups of agents within a cell which conform 
 * to a specified key/parameter. It is useful when pieces of code repeatedly 
 * require the same agent view; if the view does not exist for the current simulation
 * tick, the piece of code creates it and stores it in the cell using the
 * {@link org.laseeb.LAIS.space.Cell2D#getAgentView(String)} method; subsequent calls to
 * the same piece of code in the same iteration will have the view available and obtain
 * the agents with the required parameters. 
 * 
 * @author Nuno Fachada
 */
public class AgentView {
	
	/* Map object supporting the agent view. */
	Map<Object, List<Agent>> agentView;
	
	/**
	 * Creates a new agent view.
	 */
	public AgentView() {
		agentView = new HashMap<Object, List<Agent>>();
	}
	
	/**
	 * Returns agents which conform to the specified key/parameter.
	 * 
	 * @param key Parameter to which the agents must conform in order to be returned
	 * by this method.
	 * @return Agents which conform to the specified key/parameter.
	 */
	public List<Agent> getAgents(Object key) {
		return agentView.get(key);
	}
	
	/**
	 * Adds an agent to this view.
	 * 
	 * @param key The key/parameter to which the given agent conforms to.
	 * @param agent The agent to be added to the current view.
	 */
	public void addAgent(Object key, Agent agent) {
		if (agentView.containsKey(key)) {
			agentView.get(key).add(agent);
		} else {
			List<Agent> agentList = new ArrayList<Agent>();
			agentList.add(agent);
			agentView.put(key, agentList);
		}
	}

}
