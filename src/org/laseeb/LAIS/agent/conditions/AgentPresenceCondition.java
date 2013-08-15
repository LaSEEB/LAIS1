package org.laseeb.LAIS.agent.conditions;

import java.util.List;
import java.util.Set;

import org.laseeb.LAIS.agent.Agent;
import org.laseeb.LAIS.agent.AgentPrototype;
import org.laseeb.LAIS.agent.AgentView;
import org.laseeb.LAIS.space.Cell2D;
import org.simpleframework.xml.Element;

/**
 * Checks for the presence of an agent with a given prototype.
 * 
 * @author Nuno Fachada
 */
public class AgentPresenceCondition extends AgentCondition {

	/** 
	 * <strong>XML Element (integer > 0)</strong>
	 * <p>
	 * Message index for passing the first found agent with the given prototype. 
	 * <p> 
	 * <em>REQUIRED: NO.</em> 
	 * */
	@Element(required=false)
	int index = -1;
	
	/** 
	 * <strong>XML Element (reference to an AgentPrototype)</strong>
	 * <p>
	 * Agent prototype to check presence of. 
	 * <p> 
	 * <em>REQUIRED: YES.</em>
	 * */
	@Element
	AgentPrototype prototype;

	/**
	 * @see AgentCondition#evaluate(Agent, Cell2D, Object[])
	 */
	public boolean evaluate(Agent agent, Cell2D cell, Object[] message)
			throws ConditionException {
		
		/* The agent view. */
		AgentView typeView;
		/* Get the agent view by prototype in this cell. */
		if (!cell.isAgentViewUpdated(this.getClass().getName())) {
			/* View does not exist, create it. */
			typeView = new AgentView();
			/* Cycle through all agents in cell. */
			Set<Agent> agentsInCell = cell.getAgents();
			for (Agent ag : agentsInCell) {
				/* Add agent to view. */
				typeView.addAgent(ag.getPrototype(), ag);
			}
			/* Set updated view. */
			cell.setAgentView(this.getClass().getName(), typeView);
		} else {
			/* View exists, request it from cell. */
			typeView = cell.getAgentView(this.getClass().getName());
		}
		
		/* Get agents of given type. */
		List<Agent> agents = typeView.getAgents(prototype);
		/* If there are agents of the given type... */
		if (agents != null) {
			/* ...pass one of the agents randomly to the actions if index >= 0 and... */
			if (index >= 0) {
				int anAgentIndex = cell.getRng().nextIntFromTo(0, agents.size() - 1);
				message[index] = agents.get(anAgentIndex);
			}
			/* ...return true. */
			return true;
		}
		return false;
					
	}

}
