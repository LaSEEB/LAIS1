package org.laseeb.LAIS.event.agdeploy;

import org.laseeb.LAIS.LAISModel;
import org.laseeb.LAIS.agent.AgentStateMap;
import org.laseeb.LAIS.agent.AgentWatcher;
import org.laseeb.LAIS.event.Event;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/** 
 * Constrain that yields a quantity depending on the currently existing 
 * agents in the simulation with given type and eventually state.
 * 
 * @author Nuno Fachada
 */
@Root
public class NumberDependentQuantityConstrain extends
		QuantityConstrain {

	/** 
	 * <strong>XML Element (integer &gt; 0)</strong>
	 * <p>
	 * Preferred quantity of agents of given type and state in the simulation. If 
	 * quantity of agents currently in simulation is lower than this value, then this
	 * constrain will yield a quantity of agents to deploy to approximate the overall
	 * quantity to this value.
	 * <p>
	 * <em>REQUIRED: YES</em> 
	 * */
	@Element
	int preferredQuantity;
	
	/** 
	 * <strong>XML Element (integer &gt; 0)</strong>
	 * <p>
	 * Maximum quantity of agents to deploy each time time constrain is invoked.
	 * If not given, this value will be equal to {@link java.lang.Integer#MAX_VALUE}.
	 * <p>
	 * <em>REQUIRED: NO</em> 
	 * */
	@Element(required=false)
	int maxQuantity = Integer.MAX_VALUE;
	
	/** 
	 * <strong>XML Element ({@link org.laseeb.LAIS.agent.AgentStateMap})</strong>
	 * <p>
	 * State map of agents to take into account currently in the simulation. If not
	 * given, agent state will be ignored.
	 * <p>
	 * <em>REQUIRED: NO</em> 
	 * */
	@Element(required=false)
	AgentStateMap agStateMap = new AgentStateMap();
	
	/** 
	 * <strong>XML Element ({@link java.lang.String})</strong>
	 * <p>
	 * Name of agent prototype as specified in the <strong>XML Model File</strong>.
	 * Only agents with this prototype will be accounted for when this constrain is
	 * invoked.
	 * <p>
	 * <em>REQUIRED: YES</em> 
	 * */
	@Element
	String agent;
	//TODO Make this not required; then all agents in the simulation will be accounted.
	
	/* Agent watcher object, obtained through the LAISModel. */
	private AgentWatcher agentWatcher;
	
	/**
	 * Initializes this constrain object with an AgentWatcher object, obtained through the model.
	 * @param model The LAISModel, master of all simulation objects.
	 * @see org.laseeb.LAIS.event.agdeploy.QuantityConstrain#initialize(LAISModel)
	 */	
	public void initialize(LAISModel model) {
		super.initialize(model);
		this.agentWatcher = model.getAgentWatcher();
	}

	/**
	 * Returns a quantity of agents to deploy based on the number of agents of given
	 * type and state (if agStateMap not null, otherwise agent state will be ignored).
	 * 
	 * @return A quantity of agents to deploy.
	 * @see org.laseeb.LAIS.event.agdeploy.QuantityConstrain#getQuantity()
	 */
	public int getQuantity() {
		int currentQuantity = agentWatcher.getNumbers(agent, agStateMap);
		int possibleToCreate = Math.min(Math.max(0, preferredQuantity - currentQuantity), maxQuantity);		
		return Event.getEventRng().nextIntFromTo(0, possibleToCreate);
	}
}
