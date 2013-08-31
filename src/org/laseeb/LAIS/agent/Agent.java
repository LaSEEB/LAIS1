/*   
 * This file is part of LAIS (LaSEEB Agent Interaction Simulator).
 * 
 * LAIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * LAIS is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with LAIS.  If not, see <http://www.gnu.org/licenses/>.
 */


package org.laseeb.LAIS.agent;

import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Set;

import org.laseeb.LAIS.agent.actions.ActionException;
import org.laseeb.LAIS.agent.actions.AgentAction;
import org.laseeb.LAIS.agent.conditions.*;
import org.laseeb.LAIS.space.Cell2D;
import org.laseeb.LAIS.substance.Substance;
import org.laseeb.LAIS.substance.SubstanceContainer;
import org.laseeb.LAIS.substance.SubstanceException;
import org.laseeb.LAIS.substance.SubstanceProxy;
import org.laseeb.LAIS.utils.random.IRng;
//import org.laseeb.LAIS.utils.QuickProfiler;

import uchicago.src.sim.engine.CustomProbeable;

/**
 * This class represents the agents in the simulation. The <i>de facto</i> behavior of agents 
 * is determined by their <code>genome</code>.
 * 
 * @author Nuno Fachada
 *
 */
public class Agent implements Cloneable, Comparable<Agent>, SubstanceContainer, CustomProbeable {
	
	/* The agent's genome. */	
	private List<Gene> genome;
	
	/* The agent's substance reference map. When cloned an agent can mutate some of it's 
	 * primary substances, however, these references remain the same. */
	private Map<String, SubstanceProxy> refSubMap;
	
	/* Substances that are present in the agent's surface. */
	private Map<Substance, Float> supSubConMap;

	/* The agent state types and their values. */
	private AgentStateMap stateMap;
	
	/* States to set next time the play method is executed. */
	private AgentStateMap nextStateMap = new AgentStateMap();
	
	/* The constant prototype features of this agent. */
	private AgentPrototype prototype;

	/* The agent's hash code, required in order to maintain ordering within spatial blocks. */
	private int hashCode;
	
	/**
	 * Public constructor. Requires a unique agent ID.
	 * 
	 * @param id Unique agent ID.
	 */
	public Agent(int id) {
		this.hashCode = id;
	}
	
	/** 
	 * This method returns a clone of the agent, possibly with superficial substance mutations.
	 * 
	 * @param mutationRate The probability of substance mutation in this agent.
	 * @param id A unique agent ID. 
	 * @param rng An instance of a random number generator.
	 * @return A clone of the agent, possibly with superficial substance mutations. 
	 * @throws CloneNotSupportedException If it's not possible to clone the agent.
	 * @throws AgentException When the cloning of the agent is not possible.
	 * */
	public Agent clone(float mutationRate, int id, IRng rng) throws CloneNotSupportedException, AgentException {
		/* Clone agent (shallow copy). */
		Agent agent = null;
		agent = (Agent) super.clone();
		agent.hashCode = id;
		/* Clone genome. */
		agent.genome = AgentPrototype.cloneGenome(genome, mutationRate);
		/* Clone state (specific changes to the state are specified in the cloning gene). */
		if (stateMap != null) {
			agent.stateMap = stateMap.clone();
		}
		/* Next state map is empty. */
		agent.nextStateMap = new AgentStateMap();
		/* Clone substance references. */
		agent.refSubMap = AgentPrototype.cloneRefSubMap(refSubMap, mutationRate, rng);
		/* Clone superficial substances - for now we basically "copy" the superficial substance 
		 * concentration of the original agent. It's not an exact copy because substance 
		 * references are taken into account, so if some substance has mutated during cloning,
		 * such change is considered. */
		//TODO Should be decided by conditions/actions?
		agent.supSubConMap = new HashMap<Substance, Float>();
		Iterator<String> iterSubRef = refSubMap.keySet().iterator();
		while (iterSubRef.hasNext()) {
			String subRef = iterSubRef.next();
			/* Obtain concentration of original substance in original agent. */
			Float subCon = this.supSubConMap.get(this.getSubstanceByRef(subRef));
			/* Add same concentration of cloned substance in cloned agent. */
			if (subCon != null) {
				try {
					agent.supSubConMap.put(
							agent.getSubstanceByRef(subRef).getSubstance(agent), 
							subCon);
				} catch (SubstanceException se) {
					throw new AgentException(se);
				}
			}
		}
		/* Return new clone */
		return agent;
	}
	
	/**
	 * This method is called in order to prompt the agent to act.
	 * 
	 * @param cell The cell where the agent is currently situated. The cell corresponds to the 
	 * surrounding environment, which can be inspected by the agent.
	 * @throws ConditionException When an error occurs while evaluating a condition.
	 * @throws ActionException When an error occurs while executing an action.
	 * @throws AgentException When an error occurs during the agents performance.
	 */
	public synchronized void play(Cell2D cell) throws ConditionException, ActionException, AgentException {
		/* Set next states. */
		Iterator<String> stateTypeIter = nextStateMap.stateTypeIterator();
		while (stateTypeIter.hasNext()) {
			String stateType = stateTypeIter.next();
			String stateValue = nextStateMap.getState(stateType);
			setState(stateType, stateValue);
		}
		nextStateMap.clear();
		/* Run through the genetic code */
		boolean conditions;
		Iterator<Gene> geneIter = genome.iterator();
		while (geneIter.hasNext()) {
			/* Get gene */
			Gene gene = geneIter.next();
			/* An iterator for each condition */
			Iterator<AgentCondition> condIter = gene.getConditionIterator();
			/* Analyze conditions */
			conditions = true;
			Object[] conditionActionMessage = new Object[5]; //TODO Automate this value - set as a gene attribute!
			while ((conditions) && (condIter.hasNext())){
				AgentCondition condition = condIter.next();
				//QuickProfiler.start(condition.getClass().getSimpleName());
				conditions = condition.evaluate(this, cell, conditionActionMessage) ^ condition.getComplement();
				//QuickProfiler.end(condition.getClass().getSimpleName());
			};
			/* If all conditions in gene are true, perform actions */
			if (conditions) {
				/* Get actions to the corresponding conditions */
				Iterator<AgentAction> actionIter = gene.getActionIterator();
				/* Perform actions */
				while (actionIter.hasNext()) {
					actionIter.next().performAction(this, cell, conditionActionMessage);
				}
			}
		}
		/* Degrade superficial substance concentration. */
		degradeSupSub();//TODO Make this an option (automatic or not)
	}
	
	/**
	 * Returns this agent's prototype.
	 * 
	 * @return The agent's prototype.
	 */
	public AgentPrototype getPrototype() {
		return prototype;
	}
	
	/**
	 * Indicates if a determined substance is present on the agent's surface.
	 * 
	 * @param sub Substance to check presence of.
	 * @return True if substance is present, false otherwise.
	 * @see org.laseeb.LAIS.substance.SubstanceContainer#containsSubstance(Substance)
	 */
	public boolean containsSubstance(Substance sub) {
		return supSubConMap.containsKey(sub);
	}
	
	/**
	 * Inserts a gene in this agent genome.
	 * 
	 * @param gene The gene to be inserted in this agent's genome.
	 * @throws CloneNotSupportedException 
	 */
	public void insertGene(Gene gene, float mutationRate) throws CloneNotSupportedException {
		genome.add(gene.clone(mutationRate));
	}
	
	/**
	 * Returns the agent's state types and values.
	 * @return The agent's state types and values.
	 */
	public AgentStateMap getStatesMap() {
		return stateMap;
	}
	
	/**
	 * Returns the concentration of a given substance on the agent's surface.
	 * 
	 * @param sub Substance to get concentration of.
	 * @return Concentration of given substance.
	 * @see org.laseeb.LAIS.substance.SubstanceContainer#getSubstanceCon(Substance)
	 */
	public float getSubstanceCon(Substance sub) {
		if (containsSubstance(sub))
			return supSubConMap.get(sub);
		else
			return 0.0f;
	}

	/** 
	 * Returns an iterator which cycles through the substances present on the agent's surface.
	 * 
	 * @return An iterator which cycles through the substances present on the agent's surface.
	 * @see org.laseeb.LAIS.substance.SubstanceContainer#modifySubstanceCon(Substance, Float)
	 */
	public Iterator<Substance> substanceIterator() {
		return supSubConMap.keySet().iterator();
	}
	
	/**
	 * Modify (add/subtract) given substance concentration on the agent's surface.
	 * 
	 * @param sub Substance to modify concentration of.
	 * @param con Concentration to add (if positive) or subtract (if negative).
	 * @return Substance concentration after modification.
	 * @see org.laseeb.LAIS.substance.SubstanceContainer#modifySubstanceCon(Substance, Float)
	 */
	public float modifySubstanceCon(Substance sub, Float con) {
		float newCon = 0.0f;
		if (supSubConMap.containsKey(sub))
			newCon = getSubstanceCon(sub);
		newCon += con;
		if (newCon < prototype.getMinConThreshold())
			supSubConMap.put(sub, 0.0f);
		else
			supSubConMap.put(sub, newCon);
		return getSubstanceCon(sub);
	}

	/**
	 * Same as {@link #getName()}.
	 * 
	 * @see #getName()
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return getName();
	}

	/**
	 * Returns the name of the agent prototype.
	 * 
	 * @return The name of the agent prototype.
	 */
	public String getPrototypeName() {
		return prototype.getName();
	}
	
	/**
	 * Returns the name of the agent.
	 * 
	 * @return The name of the agent.
	 */
	public String getName() {
		return getPrototypeName() + " {" + Integer.toHexString(hashCode()) + "}"; 
	}

	/**
	 * Returns the agent state correspondent to a given state key.
	 * 
	 * @param stateKey The state key.
	 * @return The state corresponding to the given state key.
	 */
	public String getState(String stateKey) {
		return stateMap.getState(stateKey);
	}
	
	/**
	 * Sets the agent state correspondent to a given state type.
	 * 
	 * @param stateType The state type.
	 * @param stateValue The state to set, corresponding to the given state key.
	 * @throws AgentException If the state key-value pair is not valid, according to the prototype.
	 */
	public synchronized void setState(String stateType, String stateValue) throws AgentException {
		if (prototype.isValidState(stateType, stateValue))
			stateMap.setState(stateType, stateValue);
		else {
			throw new AgentException("Invalid state type - state value pair ("
					+ stateType + ", " + stateValue + ")"
					+ " for agent " + prototype.getName() + "!  (setState method)");
		}
	}
	
	/**
	 * Determines the agent state correspondent to a given state type, but only sets this
	 * change the next time the agents {link {@link Agent#play(Cell2D)} method is executed.
	 * 
	 * @param stateType The state type.
	 * @param stateValue The state to set, corresponding to the given state key.
	 * @throws AgentException If the state key-value pair is not valid, according to the prototype.
	 */
	public synchronized void setNextState(String stateType, String stateValue) throws AgentException {
		if (prototype.isValidState(stateType, stateValue))
			nextStateMap.setState(stateType, stateValue);
		else {
			throw new AgentException("Invalid state type - state value pair ("
					+ stateType + ", " + stateValue + ")"
					+ " for agent " + prototype.getName() + "! (setNextState method)");
		}
	}

	/**
	 * Sets the state types and values for the agent.
	 * @param asm State types and values.
	 * @throws AgentException When is not possible to set the state map.
	 */
	public void setStateMap(AgentStateMap asm) throws AgentException {
		stateMap = new AgentStateMap();
		Iterator<String> iterStateType = asm.stateTypeIterator();
		while (iterStateType.hasNext()) {
			String stateType = iterStateType.next();
			setState(stateType, asm.getState(stateType));
		}
	}

	/**
	 * Sets this agent's genome. 
	 * Declared friendly, i.e., only classes in the same package can use this method.
	 * 
	 * @param genome The agent's genome.
	 */
	void setGenome(List<Gene> genome) {
		this.genome = genome;
	}
	
	/**
	 * Sets this agent's prototype. 
	 * Declared friendly, i.e., only classes in the same package can use this method.
	 * 
	 * @param prototype The agent's prototype.
	 */
	void setPrototype(AgentPrototype prototype) {
		this.prototype = prototype;
	}
	
	/**
	 * Sets this agent's substance reference map. 
	 * Declared friendly, i.e., only classes in the same package can use this method.
	 * 
	 * @param refSubMap The agent's substance reference map.
	 */
	void setRefSubMap(Map<String, SubstanceProxy> refSubMap) {
		this.refSubMap = refSubMap;
	}
	
	/**
	 * Returns a superficial substance given a reference.
	 * @param ref The substance immutable string reference.
	 * @return The substance proxy to which the given string corresponds.
	 * @throws AgentException When the substance reference is unknown. 
	 */
	public SubstanceProxy getSubstanceByRef(String ref) throws AgentException {
		if (refSubMap.containsKey(ref))
			return refSubMap.get(ref);
		else
			throw new AgentException("The substance referenced by '" + ref + "' is unknown to agent '" + prototype.getName() + "'!");
	}
	
	/**
	 * Sets an immutable substance reference for the agent.
	 * 
	 * @param ref The substance reference (a string).
	 * @param sp The substance proxy that is referenced.
	 */
	public void setSubRef(String ref, SubstanceProxy sp) {
		refSubMap.put(ref, sp);
	}
	
	/**
	 * Adds immutable substance references, replacing pre-existing references.
	 * 
	 * @param newRefSubMap The substance reference map.
	 */
	public void addSubRefMap(Map<String, SubstanceProxy> newRefSubMap) {
		refSubMap.putAll(newRefSubMap);
	}

	/**
	 * Sets the superficial substance concentration map.
	 * 
	 * @param subMap A map containing textual references for substances and respective substance
	 * concentration. 
	 * @throws AgentException When its not possible to get substance by reference.
	 */
	public void setSupSubCon(Map<String, Float> subMap) throws AgentException {
		supSubConMap = new HashMap<Substance, Float>();
		Iterator<String> iterStr = subMap.keySet().iterator();
		while (iterStr.hasNext()) {
			String strSub = iterStr.next();
			SubstanceProxy sp = refSubMap.get(strSub);
			try {
				supSubConMap.put(sp.getSubstance(this), subMap.get(strSub));
			} catch (SubstanceException se) {
				throw new AgentException(se);
			}
		}
	}
	
	/**
	 * Perform evaporation of the agent's superficial substances, according to the
	 * defined evaporation rate.
	 */
	public void degradeSupSub() {
		Iterator<Substance> iterSub = supSubConMap.keySet().iterator();
		while (iterSub.hasNext()) {
			Substance sub = iterSub.next();
			float con = supSubConMap.get(sub);
			con = con + con * sub.getKDeg();
			if (con > prototype.getMinConThreshold())
				supSubConMap.put(sub, con);
			else
				iterSub.remove();
		}
	}

	/**
	 * @see uchicago.src.sim.engine.CustomProbeable
	 */
	public String[] getProbedProperties() {
		String properties[] = {"name","statesInfo","substanceConcentrationsInfo","substanceReferencesInfo"};
		return properties;
	}
	
	/**
	 * @see AgentStateMap#getStatesInfo()
	 */
	public String[] getStatesInfo() {
		return stateMap.getStatesInfo();
	}
	
	/**
	 * Returns an array of strings where each string describes a substance superficially present on the
	 * agent and the respective concentration.
	 * 
	 * @return An array of string where each string describes a substance superficially present on the
	 * agent and the respective concentration.
	 */
	public String[] getSubstanceConcentrationsInfo() {
		String supSubConInfo[] = {""};
		if (supSubConMap.size() > 0) {
			supSubConInfo = new String[supSubConMap.size()];
			int i = 0;
			Set<Substance> supSubs = supSubConMap.keySet();
			for (Substance supSub : supSubs) {
				supSubConInfo[i] = supSub.getName() + " = " + supSubConMap.get(supSub); 
				i++;
			}
		}
		return supSubConInfo;
	}

	/**
	 * Returns an array of strings where each string describes a substance reference and the effective
	 * substance referenced by the respective reference.
	 * 
	 * @return An array of strings where each string describes a substance reference and the effective
	 * substance referenced by the respective reference.
	 */
	public String[] getSubstanceReferencesInfo() {
		String refSubInfo[] = {""};
		if (refSubMap != null) {
			if (refSubMap.size() > 0) {
				refSubInfo = new String[refSubMap.size()];
				int i = 0;
				Set<String> subRefs = refSubMap.keySet();
				for (String subRef : subRefs) {
					try {
						refSubInfo[i] = subRef + " = " + refSubMap.get(subRef).getSubstance(this).getName();
					} catch (SubstanceException se) {
						refSubInfo[i] = subRef + " = " + se.getMessage();
					} catch (NullPointerException npe) {
						refSubInfo[i] = subRef + " = " + "null";
					}
					i++;
				}
			}
		}
		return refSubInfo;
	}
	
	/**
	 * Returns the agent's hash code.
	 * @return The agent's hash code.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return hashCode;
	}

	/**
	 * @see java.lang.Comparable#compareTo(Object)
	 */
	@Override
	public int compareTo(Agent otherAgent) {
		return hashCode - otherAgent.hashCode;
	}
}
