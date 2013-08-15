package org.laseeb.LAIS.agent;

import java.util.HashMap;
import java.util.Collection;
import java.util.Iterator;

/**
 * An instance of this class contains the number of agents of a given type, 
 * separated by state type and values.
 * @author Nuno Fachada
 */
public class AgentNumbersMap {
	
	private HashMap<AgentStateMap, Integer> numbersByStatesMap;
	
	/**
	 * Creates an AgentNumbersMap with a single state map and quantity.
	 * @param asm State map.
	 * @param num Agent quantity.
	 */
	public AgentNumbersMap(AgentStateMap asm, int num) {
		numbersByStatesMap = new HashMap<AgentStateMap, Integer>();
		numbersByStatesMap.put(asm, num);
	}
	
	/**
	 * Returns an iterator for the state maps in this object.
	 * @return An iterator for the state maps in this object.
	 */
	public Iterator<AgentStateMap> agentStateMapIterator() {
		return numbersByStatesMap.keySet().iterator();
	}
		
	/**
	 * Returns the number of agents in the states indicated by the given state map.
	 * @param states The states the agents must be in to be taken into account.
	 * @return The number of agents which are in the given states.
	 */
	public int getNumbersByStates(AgentStateMap states) {
		/* If no state map is given, then the complete number of agents will be returned. */
		if (states == null)
			return getNumbers();
		/* A non-null state map is given, initialize return value to zero. */
		int numbers = 0;
		/* Iterate through all local state maps, and collect compatible numbers. */
		Iterator<AgentStateMap> iterMap = numbersByStatesMap.keySet().iterator();
		while (iterMap.hasNext()) {
			/* Get next state map. */
			AgentStateMap localMap = iterMap.next();
			/* Iterate through the state types in the given state map. */
			Iterator<String> iterStr = states.stateTypeIterator();
			boolean confirm = true;
			while (iterStr.hasNext()) {
				String stateType = iterStr.next();
				if (!localMap.containsStateType(stateType)) {
					confirm = false;
					break;
				} else if (!localMap.getState(stateType).equals(states.getState(stateType))) {
					confirm = false;
					break;
				}
			}
			if (confirm)
				numbers += numbersByStatesMap.get(localMap);
		}
		return numbers;	
	}
	
	/**
	 * Returns the total number of agents, independent of state.
	 * @return The total number of agents, independent of state.
	 */
	private int getNumbers() {
		int numbers = 0;
		Collection<Integer> numbersByStates = numbersByStatesMap.values();
		Iterator<Integer> iterNBS = numbersByStates.iterator();
		while (iterNBS.hasNext())
			numbers += iterNBS.next();
		return numbers;
	}
	
	/**
	 * Increments the number of agents in the given state. 
	 * @param asm The state map to increment number of agents.
	 */
	public void update(AgentStateMap asm) {
		Integer currNumber = numbersByStatesMap.get(asm);
		if (currNumber == null) currNumber = 0;
		currNumber++;
		numbersByStatesMap.put(asm, currNumber);
	}

}
