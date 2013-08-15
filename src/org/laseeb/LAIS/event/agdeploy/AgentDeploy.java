package org.laseeb.LAIS.event.agdeploy;

import java.awt.Point;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.laseeb.LAIS.LAISModel;
import org.laseeb.LAIS.agent.Agent;
import org.laseeb.LAIS.agent.AgentException;
import org.laseeb.LAIS.agent.AgentPrototype;
import org.laseeb.LAIS.agent.AgentStateMap;
import org.laseeb.LAIS.event.Event;
import org.laseeb.LAIS.event.EventException;
import org.laseeb.LAIS.space.Cell2D;
import org.laseeb.LAIS.substance.Substance;
import org.laseeb.LAIS.substance.SubstanceException;
import org.laseeb.LAIS.substance.SubstanceProxy;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementMap;

/**
 * Instances of this class represent agent deployment events. Deployment is performed having into
 * account different constrains for different agent characteristics and different deployment
 * details.
 * <p>
 * Current constrains:
 * <ul>
 * <li>Mutation rate (default: zero mutation).</li>
 * <li>Initial superficial substance concentration (default: no superficial substance concentration
 * at deployment time).</li>
 * <li>Quantity constrains, which determine the effective number of agents to be deployed 
 * (default: deploy one agent).</li>
 * <li>Location constrains, which determine the spatial location where the agents will be deployed
 * (default: random deployment).</li> 
 * </ul> 
 * 
 * @author Nuno Fachada
 */
public class AgentDeploy extends Event {

	/** 
	 * <strong>XML Element ({@link java.lang.String})</strong>
	 * <p>
	 * Type of agent to deploy in the simulation. This string must correspond to the exact name 
	 * given as an attribute in the <code>&lt;agentDefinition&gt;</code> tag, as declared in the 
	 * <strong>XML Model File</strong>. For example, to deploy agents declared with name 
	 * <code>prey</code> (i.e., <code>&lt;agentDefinition name="prey"&gt;...</code>), 
	 * one would use the following XML: 
	 * <p>
	 * <code>&lt;agentPrototype&gt;prey&lt;&#47;agentPrototype&gt;</code>
	 * <p>
	 * <em>REQUIRED: YES</em> 
	 * */
	@Element
	String agentPrototype;

	/** 
	 * <strong>XML Element ({@link org.laseeb.LAIS.agent.AgentStateMap})</strong>
	 * <p>
	 * The agent's state map. If not given, an empty map will be created.
	 * <p>
	 * <em>REQUIRED: NO</em> 
	 * */
	@Element(required=false)
	AgentStateMap agStateMap =
		new AgentStateMap();
	
	/** 
	 * <strong>XML ElementMap (key: {@link java.lang.String}, value: {@link java.lang.String})</strong>
	 * <p>
	 * Runtime substance references. If not given, no new references will be added to agent.
	 * <p>
	 * <em>REQUIRED: NO</em> 
	 * */
	@ElementMap(required=false,attribute=true,entry="subRef",key="ref")
	Map<String, String> runtimeSubRef = null;	

	/** 
	 * <strong>XML Element ({@link org.laseeb.LAIS.event.agdeploy.MutationRateConstrain})</strong>
	 * <p>
	 * Deployment mutation rate constrain(s). If not given, the default constrain will
	 * provide a fixed mutation of zero.
	 * <p>
	 * <em>REQUIRED: NO</em> 
	 * */
	@Element(required=false)
	MutationRateConstrain mutRateConstrain = 
		new FixedMutationRateConstrain(0);
	
	/** 
	 * <b>XML Element ({@link org.laseeb.LAIS.event.agdeploy.InitSupSubConConstrain})</b>
	 * <p>
	 * Initial substance concentration constrain(s). If not given, the default constrain will provide
	 * an empty map.
	 * <p>
	 * <em>REQUIRED: NO</em> 
	 * */
	@Element(required=false)
	InitSupSubConConstrain iscConstrain =
		new FixedInitSupSubConConstrain();
	
	/** 
	 * <strong>XML Element ({@link org.laseeb.LAIS.event.agdeploy.QuantityConstrain})</strong>
	 * <p>
	 * Agent quantity deployment constrain(s). If not given, the default constrain will 
	 * determine the deployment of one agent.
	 * <p>
	 * <em>REQUIRED: NO</em> 
	 * */
	@Element(required=false)
	QuantityConstrain quantityConstrain = 
		new FixedQuantityConstrain(1);
	
	/** 
	 * <strong>XML Element ({@link org.laseeb.LAIS.event.agdeploy.LocationConstrain})</strong>
	 * <p>
	 * Agent location deployment constrain(s). If not given, the default constrain will 
	 * produce random deployment.
	 * <p>
	 * <em>REQUIRED: NO</em> 
	 * */
	@Element(required=false)
	LocationConstrain locationConstrain = 
		new RandomLocationConstrain();

	/* LAIS model reference. */
	private LAISModel model;
	/* Runtime substance reference map. */
	private Map<String, SubstanceProxy> runtimeSubRefMap = null;	

	/* The logger. */
	private static Logger logger = Logger.getLogger(AgentDeploy.class);

	
	/**
	 * Initializes the AgentDeploy event object with information from the model, more specifically,
	 * with the simulation space and agent factory. It also initializes the respective constrains,
	 * which may also require information from the model.
	 * 
	 * @param model The LAISModel from where to extract the required information.
	 * @throws EventException If an exception occurs while initializing the event.
	 * @see org.laseeb.LAIS.event.Event#initialize(LAISModel)
	 */
	public void initialize(LAISModel model) throws EventException {
		this.model = model;
		if (runtimeSubRef != null) {
			runtimeSubRefMap = new HashMap<String, SubstanceProxy>();
			Iterator<String> iterSubRef = runtimeSubRef.keySet().iterator();
			while (iterSubRef.hasNext()) {
				String subRef = iterSubRef.next();
				String subName = runtimeSubRef.get(subRef);
				Substance sub;
				try {
					sub = model.getSubstanceProvider().getSubstanceByName(subName);
				} catch (SubstanceException se) {
					throw new EventException(se);
				}
				runtimeSubRefMap.put(subRef, new SubstanceProxy(sub));
			}
		}
		this.mutRateConstrain.initialize(model);
		this.iscConstrain.initialize(model);
		this.quantityConstrain.initialize(model);
		this.locationConstrain.initialize(model);
	}
	
	/**
	 * Executes deployment event given the deployment constrains.
	 * @see uchicago.src.sim.engine.BasicAction#execute()
	 */
	public void execute() {
		/* Get quantity of agents to deploy. */
		int quantity = quantityConstrain.getQuantity();
		/* Deploy agents. */
		for (int i = 0; i < quantity; i++) {
			/* Get location for next agent. */
			Point nextCoord = locationConstrain.getNextCoord();
			Cell2D cell = model.getSpace().getCell2DAt(nextCoord.x, nextCoord.y);
			/* Get mutation rate. */
			float mutRate = mutRateConstrain.getMutationRate();
			/* Create agent. */
			Agent newAgent;
			try {
				newAgent = model.getAgentFactory().createAgent(
						agentPrototype, 
						mutRate,
						Event.getUniqueID(),
						Event.getEventRng());
			} catch (CloneNotSupportedException cnse) {
				model.getController().stopSim();
				logger.error("Could not deploy agent of type '" + agentPrototype + "'. " + cnse.getMessage());
				return;
			} catch (AgentException ae) {
				model.getController().stopSim();
				logger.error("Could not deploy agent of type '" + agentPrototype + "'.", ae);
				return;
			}
			/* Set runtime substance references. */
			if (runtimeSubRefMap != null) {
				try {
					newAgent.addSubRefMap( 
						AgentPrototype.cloneRefSubMap(
								runtimeSubRefMap, 
								mutRate,
								Event.getEventRng()));
				} catch (AgentException ae) {
					model.getController().stopSim();
					logger.error(ae.getMessage());
					return;
				}
			}
			/* Set initial superficial substance concentration. */
			try {
				newAgent.setSupSubCon(iscConstrain.getInitSubCon());
			} catch (AgentException ae) {
				model.getController().stopSim();
				logger.error(this.getClass().getName() + " is unable to set superficial substance concentration map in agent '" + newAgent.getName() + "'.");
				return;
			}
			/* Set state map. */
			try {
				newAgent.setStateMap(agStateMap);
			} catch (AgentException ae) {
				model.getController().stopSim();
				logger.error("Could not set state map in agent '" + agentPrototype + "'.");
				return;
			}
			/* Add agent to chosen cell. */
			cell.addAgent(newAgent);
		}
	}
}
