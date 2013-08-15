package org.laseeb.LAIS.agent.actions;

import org.laseeb.LAIS.agent.Agent;
import org.laseeb.LAIS.space.Cell2D;

/**
 * This action is not currently used because substance degradation in agents is performed 
 * automatically.
 * 
 * @author Nuno Fachada
 */
public class DegradeSupSubAction extends AgentAction {

	/**
	 * @see AgentAction#performAction(Agent, Cell2D, Object[])
	 */
	public void performAction(Agent agent, Cell2D cell, Object[] message) throws ActionException {
		agent.degradeSupSub();
	}

}
