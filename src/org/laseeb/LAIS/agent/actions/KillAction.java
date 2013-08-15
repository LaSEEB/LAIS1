package org.laseeb.LAIS.agent.actions;

import org.laseeb.LAIS.agent.Agent;
import org.laseeb.LAIS.space.Cell2D;
import org.simpleframework.xml.Element;

/**
 * This action makes an agent kill another agent. 
 * @author Nuno Fachada
 */
public class KillAction extends AgentAction {

	/** 
	 * <strong>XML Element (integer >= 0)</strong>
	 * <p>
	 * Message index of agent to kill. 
	 * <p> 
	 * <em>REQUIRED: YES.</em> 
	 * */
	@Element
	int index;

	/**
	 * @see AgentAction#performAction(Agent, Cell2D, Object[])
	 */
	public void performAction(Agent agent, Cell2D cell, Object[] message) throws ActionException {
		/* Get agent to kill */
		Agent agToKill = (Agent) message[index];
		/* Kill the agent */
		cell.setAgentToDie(agToKill);
	}

}
