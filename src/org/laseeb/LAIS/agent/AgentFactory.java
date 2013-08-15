package org.laseeb.LAIS.agent;

import org.laseeb.LAIS.utils.random.IRng;

/**
 * Interface implemented by agent producing classes. 
 * It's purpose is to decouple agent deploying classes from the agent producing 
 * classes.
 *  
 * @author Nuno Fachada
 */
public interface AgentFactory {

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
	 */
	public Agent createAgent(String agent, float mutationRate, int id, IRng rng) throws CloneNotSupportedException, AgentException;
	
}
