package org.laseeb.LAIS.agent.actions;

import org.laseeb.LAIS.agent.Agent;
import org.laseeb.LAIS.space.Cell2D;

/**
 * This action makes the agent commit suicide (cell apoptosis).
 * 
 * @author Nuno Fachada
 */
public class ApoptosisAction extends AgentAction {

	/**
	 * @see AgentAction#performAction(Agent, Cell2D, Object[])
	 */
	public void performAction(Agent agent, Cell2D cell, Object[] message) throws ActionException {
		cell.setAgentToDie(agent);
	}
	
}
