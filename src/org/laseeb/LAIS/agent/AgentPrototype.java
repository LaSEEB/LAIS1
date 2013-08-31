package org.laseeb.LAIS.agent;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.Arrays;
import java.util.Set;

import org.apache.log4j.Logger;
import org.laseeb.LAIS.space.Cell2D;
import org.laseeb.LAIS.substance.SubstanceException;
import org.laseeb.LAIS.substance.SubstanceProxy;
import org.laseeb.LAIS.utils.random.IRng;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Validate;

import uchicago.src.sim.engine.CustomProbeable;

/**
 * An agent type is defined by its prototype; new agents deployed in the simulation
 * will be built using its prototype as a construction blueprint. 
 * 
 * @author Nuno Fachada
 */
@Root(name="agentPrototype")
public class AgentPrototype implements CustomProbeable, Comparable<AgentPrototype> {
	
	/** 
	 * <strong>XML ElementList (elements of type {@link org.laseeb.LAIS.agent.Gene})</strong>
	 * <p>
	 * A list of genes which compose the agent prototype genome. Should be used in the
	 * following format:
	 * <p>
	 * <code>
	 * &lt;genome&gt;<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;gene&gt;<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;...<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;/gene&gt;<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;gene&gt;<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;...<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;/gene&gt;<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;.<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;.<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;.<br/>
	 * &lt;/genome&gt;
	 * </code>
	 * <p>
	 * <em>REQUIRED: YES</em> 
	 * */
	@ElementList
	List<Gene> genome;
	
	/** 
	 * <strong>XML ElementMap (key: {@link java.lang.String}, value: {@link org.laseeb.LAIS.substance.SubstanceProxy})</strong>
	 * <p>
	 * The map of substance references for agents of this type. The substance (more
	 * specifically the {@link org.laseeb.LAIS.substance.SubstanceProxy})
	 * referenced by each reference can be altered at runtime; thus, the same reference 
	 * can point to different substances on agents with the prototype. Should be used 
	 * in the following format:
	 * <p>
	 * <code>
	 * &lt;refSubMap&gt;<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;entry&gt;<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;string&gt;substanceOneRef&lt;/string&gt;<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;SubstanceProxy&gt;<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;...<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/SubstanceProxy&gt;<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;/entry&gt;<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;entry&gt;<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;string&gt;substanceTwoRef&lt;/string&gt;<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;SubstanceProxy&gt;<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;...<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/SubstanceProxy&gt;<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;/entry&gt;<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;.<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;.<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;.<br/>
	 * &lt;/refSubMap&gt;
	 * </code>
	 * <p>
	 * <em>REQUIRED: YES</em> 
	 * */
	@ElementMap
	Map<String, SubstanceProxy> refSubMap;

	/** 
	 * <strong>XML ElementMap (key: {@link java.lang.String}, value: {@link java.lang.String})</strong>
	 * <p>
	 * The map of state types and state values allowed for the agents conforming to
	 * this prototype. Each agent state type can have several state values. If not given,
	 * agents conforming to this prototype will have no state variables.
	 * <p>
	 * Usage example:
	 * <p>
	 * <code>
	 * &lt;states&gt;<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;stateType type="Appetite"&gt;full,well feed,hungry,starving&lt;/stateType&gt;<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;stateType type="Attention Status"&gt;awake,drowsy,asleep&lt;/stateType&gt;<br/>
	 * &lt;/states&gt;
	 * </code>
	 * <p>
	 * <em>REQUIRED: NO</em> 
	 * */
	@ElementMap(required=false,entry="stateType",key="type",attribute=true)
	Map<String, String> states;
	
	/** 
	 * <strong>XML Element ({@link java.lang.String})</strong>
	 * <p>
	 * The RGB color of the agent. For example, if the user wants an agent to have the
	 * color red, the usage will be:
	 * <p>
	 * <code>
	 * &lt;color&gt;255,0,0&lt;/color&gt;
	 * </code>
	 * <p>
	 * <em>REQUIRED: YES</em> 
	 * */
	@Element
	String color;
	
	/* The name of the agent prototype. */
	private String name;

	/* Logger. */
	private static Logger logger = Logger.getLogger(AgentPrototype.class);

	/* The effectively used agent state types and state names, after XML parsing. */
	private Map<String, String[]> stateStates;
	
	/* Minimum allowed substance concentration; when the concentration of a substance is lower than this 
	 * value, it is removed from the respective context. */
	private float minConThreshold;

	/* The effectively used agent color. */
	//TODO Use a more versatile color / drawing feature.
	private Color colorObject;

	/**
	 * Static utility method for cloning agent genomes.
	 *  
	 * @param genome The genome to be cloned.
	 * @return The cloned genome.
	 * @throws CloneNotSupportedException If it's not possible to clone the genome.
	 */
	public static List<Gene> cloneGenome(List<Gene> genome, float mutationRate) throws CloneNotSupportedException {
		List<Gene> clonedGenome = new ArrayList<Gene>(genome.size());
		for (int i = 0; i < genome.size(); i++) {
			clonedGenome.add(i, genome.get(i).clone(mutationRate));
		}
		return clonedGenome;
	}
	
	/**
	 * Static utility method for cloning agent substance reference maps.
	 * 
	 * @param refSubMap Original reference substance map.
	 * @param mutationRate The substance mutation rate.
	 * @return The cloned substance reference map.
	 * @throws AgentException If not possible to clone agent substance reference map.
	 */
	public static Map<String, SubstanceProxy> cloneRefSubMap(
			Map<String, SubstanceProxy> refSubMap,
			float mutationRate,
			IRng rng) throws AgentException {
		Map<String, SubstanceProxy> clonedRefSubMap = new HashMap<String, SubstanceProxy>();
		Iterator<String> iterSubRef = refSubMap.keySet().iterator();
		while (iterSubRef.hasNext()) {
			String subRef = iterSubRef.next();
			try {
				clonedRefSubMap.put(subRef, refSubMap.get(subRef).clone(mutationRate, rng));
			} catch (SubstanceException se) {
				throw new AgentException(se);
			}
		}
		return clonedRefSubMap;
	}
	
	/**
	 * Auxiliary constructor, not expected to be used directly in code.
	 * Agent prototypes should in principle be created automatically 
	 * using the XML model.
	 * 
	 * @param name The name of the agent prototype.
	 */
	public AgentPrototype(String name) {
		this.name = name;
	}
	
	/**
	 * Public constructor used to create new prototypes from XML file.
	 */
	public AgentPrototype() {}
	
	/**
	 * Creates an agent based on this prototype.
	 * 
	 * @param mutationRate Mutation rate if agent is to be cloned with mutation from its prototype.
	 * @param id A unique agent ID.
	 * @param rng An instance of a random number generator.
	 * @return An agent based on this prototype.
	 * @throws CloneNotSupportedException  If it's not possible to create the agent.
	 * @throws AgentException When is not possible to create an agent.
	 */
	public Agent createAgent(float mutationRate, int id, IRng rng) throws CloneNotSupportedException, AgentException {
		Agent agent = new Agent(id);
		agent.setGenome(cloneGenome(genome, mutationRate));
		agent.setRefSubMap(cloneRefSubMap(refSubMap, mutationRate, rng));
		agent.setStateMap(new AgentStateMap());
		agent.setSupSubCon(new HashMap<String,Float>());
		agent.setPrototype(this);
		return agent;
	}
	
	/**
	 * Returns true if the given state type and name exist, returns false otherwise.
	 * @param stateType The state type to check the validity of.
	 * @param stateName The state name to check the validity of.
	 * @return True if the given state type and name exist, returns false otherwise.
	 */
	public boolean isValidState(String stateType, String stateName) {
		if (stateStates.containsKey(stateType)) {
			if (stateName == null) {
				/* TODO Check if a null state name should be allowed. */
				logger.warn("A null stateName was passed to AgentPrototype.isValidState() method.");
				return false;
			}
			int index = Arrays.binarySearch(stateStates.get(stateType), stateName);
			if (index >= 0)
				return true;
			else
				return false;
		} else {
			return false;
		}
	}
	
	/**
	 * Returns the color of this type of agent.
	 * 
	 * @return The color of this type of agent.
	 */
	public Color getColor() {
		return colorObject;
	}

//	/**
//	 * Returns the number of states for the given state type.
//	 *  
//	 * @param stateType The given state type.
//	 * @return The number of states for the given state type.
//	 */
//	public int getNumberOfStates(String stateType) {
//		return stateStates.get(stateType).length;
//	}
	
	/**
	 * Return the state names for the given state type.
	 * 
	 * @param stateType The given state type.
	 * @return The state names for the given state type.
	 */
	public String[] getStateNames(String stateType) {
		return stateStates.get(stateType);		
	}
	
	/**
	 * Returns the name of the agent.
	 * 
	 * @return The name of the agent.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name of the agent.
	 * 
	 * @param name The name of the agent.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/** 
	 * Returns the minimum concentration threshold.
	 * 
	 * @return The minimum concentration threshold.
	 */
	public float getMinConThreshold() {
		return minConThreshold;
	}

	/**
	 * Sets the minimum concentration threshold.
	 * 
	 * @param minConThreshold The minimum concentration threshold.
	 */
	public void setMinConThreshold(float minConThreshold) {
		this.minConThreshold = minConThreshold;
	}

	/**
	 * Validation method for the read XML information.
	 * 
	 * @see org.simpleframework.xml.load.Validate
	 */
	@Validate
	public void validate() {
		/* Validate color. */
		String[] rgb = this.color.split(",");
		this.colorObject = new Color(
				Integer.parseInt(rgb[0]), 
				Integer.parseInt(rgb[1]), 
				Integer.parseInt(rgb[2]));
		/* Validate states. */
		if (states != null) {
			stateStates = new HashMap<String, String[]>();
			Iterator<String> iterStr = states.keySet().iterator();
			while (iterStr.hasNext()) {
				String nextStateType = iterStr.next();
				String[] stateNames = states.get(nextStateType).split(",");
				Arrays.sort(stateNames);
				stateStates.put(nextStateType, stateNames);
			}
		}
	}

	/**
	 * @see uchicago.src.sim.engine.CustomProbeable
	 */
	public String[] getProbedProperties() {
		String properties[] = {"name", "color", "statesInfo"};
		return properties;
	}

	/**
	 * Implementation of the {@link java.lang.Comparable} interface.
	 * Two agent prototypes are distinguished by their name.
	 * 
	 * @see java.lang.Comparable
	 */
	public int compareTo(AgentPrototype otherPrototype) {
		return getName().compareTo(otherPrototype.getName());
	}

	/**
	 * Returns the name of the agent prototype (same as {@link #getName()}).
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getName();
	}
	
	/**
	 * Returns an array of strings describing the state types and values for this type of agent. 
	 * More specifically, each string in the array describes a state type and respective 
	 * values. If you consider a state type named <code>Vision<code> with possible values
	 * <code>Perfect</code>, <code>Average</code> and <code>Blind</code>, the returned string would be:
	 * <p>
	 * <code>Vision = [Perfect, Average, Blind]</code>
	 * 
	 * @return An array of strings describing the state types and values for this type of agent.
	 */
	public String[] getStatesInfo() {
		String statesArray[] = {""};
		if (states != null) {
			statesArray = new String[states.keySet().size()];
			Set<String> stateTypeSet = states.keySet();
			int i = 0;
			for (String stateType : stateTypeSet) {
				String stateValues[] = getStateNames(stateType);
				statesArray[0] = stateType + " = " + Arrays.toString(stateValues);
				i++;
			}
		} 
		return statesArray;
	}

}
