package org.laseeb.LAIS;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

/**
 * An instance of this class contains information about what simulation data to track, i.e., to keep
 * a record of. 
 * <p>
 * This class is instantiated automatically by LAIS using the 
 * <strong>XML Data Track File</strong>. All the data necessary for instantiating this class is 
 * given within <code>&lt;LAISDataTrack&gt;</code> tags. More specifically, 
 * <code>&lt;LAISDataTrack&gt;</code> is the root tag for the <strong>XML Data Track File</strong>. 
 * 
 * @author Nuno Fachada
 *
 */
@Root
public class LAISDataTrack {
	
	/** 
	 * <strong>XML ElementList (List of {@link java.lang.String}s)</strong>
	 * <p>
	 * List containing the names of the substance families that should be tracked 
	 * regarding the total concentration of the respective substances.
	 * <p> 
	 * Each string must be an exact {@link org.laseeb.LAIS.substance.SubstanceFamily} 
	 * name, as declared in the <strong>XML Model File</strong>. Each name should be enclosed in a
	 * <code>&lt;family&gt;</code> tag; for example, in order to track two families,
	 * one named <code>OneSubstanceFamily</code>, the other named 
	 * <code>OtherSubstanceFamily</code>, the XML for this would be:
	 * <p>
	 * <code>
	 * &lt;familyConcentration&gt;<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;family&gt;OneSubstanceFamily&lt;&#47;family&gt;<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;family&gt;OtherSubstanceFamily&lt;&#47;family&gt;<br>
	 * &lt;&#47;familyConcentration&gt;
	 * </code> 
	 * <p>
	 * If not given, no substance family concentrations will be tracked.
	 * <p>
	 * <em>REQUIRED: NO</em> 
	 * */
	@ElementList(entry="family",required=false)
	List<String> familyConcentration = new ArrayList<String>();

	/** 
	 * <strong>XML ElementList (List of {@link java.lang.String}s)</strong>
	 * <p>
	 * List containing the names of the substance families that should be tracked 
	 * regarding the diversity of the respective substances.
	 * <p> 
	 * Each string must be an exact {@link org.laseeb.LAIS.substance.SubstanceFamily} 
	 * name, as declared in the <strong>XML Model File</strong>. Each name should be enclosed in a
	 * <code>&lt;family&gt;</code> tag; for example, in order to track two families,
	 * one named <code>OneSubstanceFamily</code>, the other named 
	 * <code>OtherSubstanceFamily</code>, the XML for this would be:
	 * <p>
	 * <code>
	 * &lt;familyDiversity&gt;<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;family&gt;OneSubstanceFamily&lt;&#47;family&gt;<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;family&gt;OtherSubstanceFamily&lt;&#47;family&gt;<br>
	 * &lt;&#47;familyDiversity&gt;
	 * </code> 
	 * <p>
	 * If not given, no substance family concentrations will be tracked.
	 * <p>
	 * <em>REQUIRED: NO</em> 
	 * */
	@ElementList(entry="family",required=false)
	List<String> familyDiversity = new ArrayList<String>();

	/** 
	 * <strong>XML ElementList (List of {@link java.lang.String}s)</strong>
	 * <p>
	 * List containing the names of the substances of which their concentration 
	 * should be tracked.
	 * <p> 
	 * Each string must be an exact {@link org.laseeb.LAIS.substance.Substance} 
	 * name, as declared in the <strong>XML Model File</strong>. Each name should be enclosed in a
	 * <code>&lt;substance&gt;</code> tag; for example, in order to track two substances,
	 * one named <code>SubstanceFirst</code>, the other named 
	 * <code>SubstanceLast</code>, the XML for this would be:
	 * <p>
	 * <code>
	 * &lt;substances&gt;<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;substance&gt;SubstanceFirst&lt;&#47;substance&gt;<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;substance&gt;SubstanceLast&lt;&#47;substance&gt;<br>
	 * &lt;&#47;substances&gt;
	 * </code> 
	 * <p>
	 * If not given, no substance family concentrations will be tracked.
	 * <p>
	 * <em>REQUIRED: NO</em> 
	 * */
	@ElementList(entry="substance",required=false)
	List<String> substances = new ArrayList<String>();
	
	/** 
	 * <strong>XML ElementList (List of {@link java.lang.String}s)</strong>
	 * <p>
	 * List containing the names of the agents types to be tracked.
	 * <p> 
	 * Each string must be the exact name given as an attribute in the
	 * <code>&lt;agentDefinition&gt;</code> tag, as declared in the <strong>XML Model File</strong> 
	 * (for an agent called <code>predator</code> you would have <code>&lt;agentDefinition 
	 * name="predator"&gt; ...</code>). 
	 * The names of agents to be tracked should be enclosed in a 
	 * <code>&lt;agent&gt;</code> tag; for example, in order to track two agent types,
	 * one named <code>predator</code>, the other named <code>prey</code>, 
	 * the XML for this would be:
	 * <p>
	 * <code>
	 * &lt;agents&gt;<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;agent&gt;predator&lt;&#47;agent&gt;<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;agent&gt;prey&lt;&#47;agent&gt;<br>
	 * &lt;&#47;agents&gt;
	 * </code> 
	 * <p>
	 * If not given, no agents types will be tracked.
	 * <p>
	 * <em>REQUIRED: NO</em> 
	 * */
	@ElementList(entry="agent",required=false)
	List<String> agents = new ArrayList<String>();

	/** 
	 * <strong>XML ElementMap (key: {@link java.lang.String}, value: {@link java.lang.String})</strong>
	 * <p>
	 * Map containing the names of agents and respective state types 
	 * that should be tracked. An agent can have different state types, and each 
	 * state type can have different values. 
	 * <p>
	 * Consider two agents types, <code>predator</code> and <code>prey</code>. For example, 
	 * the <code>predator</code> agents have one state type, <code>Appetite</code>, with
	 * four possible values: <code>full</code>, <code>well feed</code>, 
	 * <code>hungry</code> and <code>starving</code>. On the other hand, the
	 * <code>prey</code> type agents may have two state types, <code>Attention Status</code> and
	 * <code>Appetite</code>. The <code>Appetite</code> state type 
	 * takes the same values as in the case of the <code>predator</code> agents. The
	 * <code>Attention Status</code> can have three values: <code>awake</code>,
	 * <code>drowsy</code> and <code>asleep</code>. To follow the evolution of a state
	 * type just enclose the state type in a <code>&lt;agent name="(1)"&gt;</code>
	 * tag, where (1) is the exact agent name given as an attribute in the
	 * <code>&lt;agentDefinition&gt;</code> tag, as declared in the <strong>XML Model File</strong>. 
	 * For example, to follow the <code>Appetite</code> state type of the 
	 * <code>prey</code> agents: <code>&lt;agent name="prey"&gt;Appetite&lt;&#47;agent&gt;</code>.
	 * The state type string should be exactly the same as defined within the 
	 * <code>&lt;states&gt;</code> tag of the respective 
	 * {@link org.laseeb.LAIS.agent.AgentPrototype} definition in the <strong>XML Model File</strong>.
	 * <p> 
	 * To track all the state types of <code>predator</code> and <code>prey</code> agents, 
	 * the XML would be:
	 * <p>
	 * <code>
	 * &lt;agentStates&gt;<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;agent name="predator"&gt;Appetite&lt;&#47;agent&gt;<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;agent name="prey"&gt;Appetite,Attention Status&lt;&#47;agent&gt;<br>
	 * &lt;&#47;agentStates&gt;
	 * </code> 
	 * <p>
	 * If not given, no agent state types will be tracked.
	 * <p>
	 * <em>REQUIRED: NO</em> 
	 * */
	@ElementMap(entry="agent",attribute=true,key="name",required=false)
	Map<String, String> agentStates = new HashMap<String, String>();
	
	/**
	 * Returns an iterator for the names of the substance families that should be tracked regarding 
	 * the total concentration of the respective substances.
	 * @return An iterator for the names of the substance families that should be tracked regarding 
	 * the total concentration of the respective substances.
	 */
	public Iterator<String> getFamilyConcentrationIterator() {
		return familyConcentration.iterator();
	}
	
	/**
	 * Returns true if any family concentration is to be tracked, false otherwise.
	 * @return True if any family concentration is to be tracked, false otherwise.
	 */
	public boolean existFamilyConcentrationTrackers() {
		return !familyConcentration.isEmpty();
	}
	
	/**
	 * Returns an iterator for the names of the substance families that should be tracked regarding the 
	 * diversity of the respective substances.
	 * @return An iterator for the names of the substance families that should be tracked regarding the 
	 * diversity of the respective substances.
	 */
	public Iterator<String> getFamilyDiversityIterator() {
		return familyDiversity.iterator();
	}

	/**
	 * Returns true if any family diversity is to be tracked, false otherwise.
	 * @return True if any family diversity is to be tracked, false otherwise.
	 */
	public boolean existFamilyDiversityTrackers() {
		return !familyDiversity.isEmpty();
	}

	/**
	 * Returns an iterator for the names of the substances that should be tracked regarding their total 
	 * concentration.
	 * @return An iterator for the names of the substances that should be tracked regarding their total 
	 * concentration. 
	 */
	public Iterator<String> getSubstanceIterator() {
		return substances.iterator();
	}

	/**
	 * Returns true if any substance concentration is to be tracked, false otherwise.
	 * @return True if any substance concentration is to be tracked, false otherwise.
	 */
	public boolean existSubstanceTrackers() {
		return !substances.isEmpty();
	}

	/**
	 * Returns an iterator for the names of the agents that should be tracked regarding their total 
	 * number.
	 * @return An iterator for the names of the agents that should be tracked regarding their total 
	 * number.
	 */
	public Iterator<String> getAgentIterator() {
		return agents.iterator();
	}
	
	/**
	 * Returns true if any agent quantity is to be tracked, false otherwise.
	 * @return True if any agent quantity is to be tracked, false otherwise.
	 */
	public boolean existAgentTrackers() {
		return !agents.isEmpty();
	}

	/**
	 * Returns an iterator for the names of agents which have respective state types that should 
	 * be tracked.
	 * @return An iterator for the names of agents which have respective state types that should 
	 * be tracked.
	 */
	public Iterator<String> getAgentStatesIterator() {
		return agentStates.keySet().iterator();
	}
	
	/**
	 * Returns true if any agent state type is to be tracked, false otherwise.
	 * @return True if any agent state type is to be tracked, false otherwise.
	 */
	public boolean existAgentStateTrackers() {
		return !agentStates.isEmpty();
	}

	/**
	 * Returns state types to track for a given agent
	 * @param agent Agent to get state types to track.
	 * @return State types to track for given agent.
	 */
	public String[] getAgentState(String agent) {
		return agentStates.get(agent).split(",");
	}
}
