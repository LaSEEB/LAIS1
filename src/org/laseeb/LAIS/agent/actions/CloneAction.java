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

import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

import org.laseeb.LAIS.agent.Agent;
import org.laseeb.LAIS.agent.AgentException;
import org.laseeb.LAIS.space.Cell2D;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementMap;

/**
 * This action clones the respective agent.
 * 
 * @author Nuno Fachada
 */
public class CloneAction extends AgentAction {
	
	/** 
	 * <strong>XML ElementMap (key: {@link java.lang.String}, value: {@link java.lang.String})</strong>
	 * <p>
	 * The states of the clones. 
	 * <p> 
	 * <em>REQUIRED: YES.</em> 
	 * */
	@ElementMap
	Map<String, String> cloneStateMap = new HashMap<String, String>();

	/** 
	 * <strong>XML Element (0 >= float >= 1)</strong>
	 * <p>
	 * Mutation rate. 
	 * <p> 
	 * <em>REQUIRED: YES.</em>
	 * */
	@Element
	float mutRate;
	
	/** 
	 * <strong>XML Element (integer > 0)</strong>
	 * <p>
	 * Number of clones to produce. 
	 * <p> 
	 * <em>REQUIRED: YES.</em> 
	 * */
	@Element
	int numClones;
	

	/**
	 * @see AgentAction#performAction(Agent, Cell2D, Object[])
	 */
	public void performAction(Agent agent, Cell2D cell, Object[] message) throws ActionException {
		
		for (int i = 0; i < numClones; i++) {
			Agent clonedAgent;
			try {
				try {
					clonedAgent = agent.clone(
							mutRate, 
							cell.getUniqueID(), 
							cell.getRng());
				} catch (AgentException ae) {
					throw new ActionException(this.getClass().getName() + " is unable to clone agent '" + agent.getPrototypeName() + "'.", ae);
				}
			} catch (CloneNotSupportedException cnse) {
				throw new ActionException(this.getClass().getName() + " is unable to clone agent!", cnse);
			}
			Iterator<String> iterStates = cloneStateMap.keySet().iterator();
			while (iterStates.hasNext()) {
				String stateName = iterStates.next();
				try {
					clonedAgent.setState(stateName, cloneStateMap.get(stateName));
				} catch (AgentException ae) {
					throw new ActionException(this.getClass().getName() + " is unable to set state.", ae);
				}
			}
			cell.setAgentToBorn(clonedAgent);
		}
	}
}
