package org.laseeb.LAIS.agent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;

import org.simpleframework.xml.Root;
import org.simpleframework.xml.ElementMap;

/**
 * Represents the agent states.
 * Agents can have several types of states, referred as "state type".
 * Each "state type" can have different values, i.e., different "states".
 * So, the agent states are the values of each state type.
 * 
 * @author Nuno Fachada
 */
@Root
public class AgentStateMap {

	/** 
	 * <strong>XML ElementMap (key: {@link java.lang.String}, value: {@link java.lang.String})</strong>
	 * <p>
	 * A map of agent state types and values. For example, if a given agent has two state
	 * types named <code>Attention Status</code> and <code>Appetite</code>, with possible 
	 * values (<code>awake</code>, <code>drowsy</code>, <code>asleep</code>) and
	 * (<code>full</code>, <code>well feed</code>, <code>hungry</code>, 
	 * <code>starving</code>), respectively; then, a possible usage of this element map
	 * would be:  
	 * <p>
	 * <code>
	 * &lt;entry&gt;<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;string&gt;Attention Status&lt;/string&gt;<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;string&gt;awake&lt;/string&gt;<br/>
	 * &lt;/entry&gt;<br/>
	 * &lt;entry&gt;<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;string&gt;Appetite&lt;/string&gt;<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;string&gt;hungry&lt;/string&gt;<br/>
	 * &lt;/entry&gt;
	 * </code>
	 * <p>
	 * <em>REQUIRED: NO</em> 
	 * */
	@ElementMap(inline=true,required=false,key="stateType",attribute=true)
	Map<String, String> map;
	
	/**
	 * Creates a new empty agent state map.
	 */
	public AgentStateMap() {
		map = new HashMap<String, String>();
	}
	
	/**
	 * Creates a new agent state map with a single state type and value.
	 * @param stateType The only state type in this agent state map.
	 * @param state The state of the state type.
	 */
	public AgentStateMap(String stateType, String state) {
		this.map = new HashMap<String, String>();
		this.map.put(stateType, state);
	}
	
	/**
	 * Clones the agent state map.
	 * @return An equal (but not the same) state map.
	 */
	public AgentStateMap clone() {
		AgentStateMap asm = new AgentStateMap();
		asm.map = new HashMap<String, String>();
		Iterator<String> iterStr = map.keySet().iterator();
		while (iterStr.hasNext()) {
			String str = iterStr.next();
			asm.map.put(str, new String(map.get(str)));
		}
		return asm;
	}
	
	/**
	 * Returns the state associated with the given state type.
	 * @param stateType A state type to get the state of.
	 * @return The state for a given state type.
	 */
	public String getState(String stateType) {
		return map.get(stateType);
	}
	
	/**
	 * Sets the agent state for the given state type.
	 * @param stateType A given state type.
	 * @param state The state to set.
	 */
	public void setState(String stateType, String state) {
		map.put(stateType, state);
	}
	
	/**
	 * Returns an iterator for the state types in this state map.
	 * @return An iterator for the state types in this state map.
	 */
	public Iterator<String> stateTypeIterator() {
		return map.keySet().iterator();
	}
	
	/**
	 * Returns true if this state map contains the given state type, false otherwise.
	 * @param stateType The given state type to check presence of.
	 * @return True if this state map contains the given state type, false otherwise.
	 */
	public boolean containsStateType(String stateType) {
		return map.containsKey(stateType);
	}
	
	/**
	 * Clears the state map.
	 */
	public void clear() {
		map.clear();
	}
	
	/**
	 * Used to differentiate between agent state map contents.
	 * @return The hash code of this state map.
	 * @see java.lang.Object#hashCode() 
	 */
	public int hashCode() {
		return map.hashCode();
	}
	
	/**
	 * Compares a given state map with the current state map.
	 * @param obj A state map to compare with the current state map.
	 &gt;* @return True if state map contains same state types and states, false otherwise.
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		return map.equals(((AgentStateMap) obj).map);
	}
	
	/**
	 * Returns an array of strings describing state types and values of this state map.
	 * Each string in the array is a state type - state value mapping.
	 * 
	 * @return An array of strings describing state types and values of this state map.
	 */
	public String[] getStatesInfo() {
		String states[] = {""};
		if (map.size() > 0) {
			states = new String[map.size()];
			Object stateTypes[] = map.keySet().toArray();
			int i = 0;
			for (Object stateType : stateTypes) {
				states[i] = stateType + " = " + getState((String) stateType);
				i++;
			}
		}
		return states;		
	}

}
