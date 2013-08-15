package org.laseeb.LAIS.agent;

/**
 * Interface implemented by agent management classes. 
 * It's purpose is to decouple agent deploying classes which depend on the current
 * number of agents in the simulation from the agent management classes. 
 *  
 * @author Nuno Fachada
 */
public interface AgentWatcher {

	/**
	 * Returns the number of agents of given type currently present in the simulation.
	 * 
	 * @param agStr The type of agent to check number of.
	 * @param map The states the agent must be to be counted in.
	 * @return The number of agents of given type.
	 */
	public int getNumbers(String agStr, AgentStateMap map);
	
}
