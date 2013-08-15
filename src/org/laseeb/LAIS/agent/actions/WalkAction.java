package org.laseeb.LAIS.agent.actions;

import org.laseeb.LAIS.agent.Agent;
import org.laseeb.LAIS.space.Cell2D;
import org.simpleframework.xml.Element;

/**
 * The agent walks to a neighborhood determined by the respective condition.
 * 
 * @author Nuno Fachada
 */
public class WalkAction extends AgentAction {
	
	/** 
	 * <strong>XML Element (integer >= 0)</strong>
	 * <p>
	 * Message index of the agent's destination.
	 * <p> 
	 * <em>REQUIRED: YES.</em>
	 * */
	@Element
	int index;
	
	/**
	 * @see AgentAction#performAction(Agent, Cell2D, Object[])
	 */
	public void performAction(Agent agent, Cell2D cell, Object[] message) throws ActionException {

		int destination = (Integer) message[index];
		if ((destination < 0) || (destination > cell.getNeighbors().size() - 1))
			throw new ActionException("Error in action '" + 
					this.getClass().getSimpleName() + 
					"': destination value is " + 
					destination + ", " +
					"but must be between 0 and " + (cell.getNeighbors().size() - 1) + ".");
		cell.setAgentToMoveOut(agent, (Cell2D) cell.getNeighbors().get(destination));
	}

}
