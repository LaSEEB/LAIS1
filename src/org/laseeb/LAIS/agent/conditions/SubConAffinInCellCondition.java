package org.laseeb.LAIS.agent.conditions;

import java.util.Iterator;

import org.laseeb.LAIS.agent.Agent;
import org.laseeb.LAIS.agent.AgentException;
import org.laseeb.LAIS.space.Cell2D;
import org.laseeb.LAIS.substance.Substance;
import org.laseeb.LAIS.substance.SubstanceException;
import org.laseeb.LAIS.substance.SubstanceFamily;

import org.simpleframework.xml.Element;

/**
 * This condition checks for any substance on the local cell which has functional
 * affinity greater than a given value with a given substance in the current agent surface.
 * 
 * @author Nuno Fachada
 */
public class SubConAffinInCellCondition extends AgentCondition {
	
	/** 
	 * <strong>XML Element (0 &lt;= integer &lt;= 63)</strong>
	 * <p>
	 * Ending bit of affinity calculation. 
	 * <p> 
	 * <em>REQUIRED: YES.</em> 
	 * */
	@Element
	int endBit;
	
	/** 
	 * <strong>XML Element ({@link org.laseeb.LAIS.substance.SubstanceFamily} object reference)</strong>
	 * <p>
	 * If this field is used, the substance in local cell must belong 
	 * to the specified substance family. 
	 * <p> 
	 * <em>REQUIRED: NO.</em> 
	 * */
	@Element(required=false)
	SubstanceFamily family;

	/** 
	 * <strong>XML Element (integer &gt;= 0)</strong>
	 * <p>
	 * Message index where to pass the matching concentration. 
	 * <p> 
	 * <em>REQUIRED: NO.</em> 
	 * */
	@Element(required=false)
	int indexCon = -1;

	/** 
	 * <strong>XML Element (integer &gt;= 0)</strong>
	 * <p>
	 * Message index of substance detected in the agent which has affinity with 
	 * substance referenced by <code>substance</code> element. 
	 * <p> 
	 * <em>REQUIRED: NO.</em>
	 * */
	@Element(required=false)
	int indexSub = -1;
	
	/** 
	 * <strong>XML Element (0 &lt;= float &lt;= 1)</strong>
	 * <p>
	 * Minimum match for condition to yield true. Match = affinity x percent.
	 * <p> 
	 * <em>REQUIRED: YES.</em> 
	 * */
	@Element
	float minMatch;
	
	/** 
	 * <strong>XML Element (0 &lt;= integer &lt;= 63)</strong>
	 * <p>
	 * Starting bit for affinity calculation. 
	 * <p> 
	 * <em>REQUIRED: YES.</em>
	 * */
	@Element
	int startBit;
	
	/** 
	 * <strong>XML Element ({@link java.lang.String})</strong>
	 * <p>
	 * Reference of substance to which the affinity of local 
	 * substances will be compared.
	 * <p> 
	 * <em>REQUIRED: YES.</em> 
	 * */
	@Element
	String substance;
	
	/**
	 * @see AgentCondition#evaluate(Agent, Cell2D, Object[])
	 */
	public boolean evaluate(Agent agent, Cell2D cell, Object[] message) throws ConditionException {
		/* Get substance to compare from agent. */
		Substance substanceToCompare;
		try {
			substanceToCompare = agent.getSubstanceByRef(substance).getSubstance(agent);
		} catch (SubstanceException se) {
			throw new ConditionException(this.getClass().getName() + " is unable to get substance with reference '" + substance + "' from agent '" + agent.getName() + "'.", se);
		} catch (AgentException ae) {
			throw new ConditionException(this.getClass().getName() + " is unable to get substance with reference '" + substance + "' from agent '" + agent.getName() + "'.", ae);
		}
		if (substanceToCompare == null) return false;
		/* Concentration of substance to compare in current agent. */
		float conSubToCompare = agent.getSubstanceCon(substanceToCompare);
		/* Cycle through substances in current cell. */
		Iterator<Substance> iterSub = cell.substanceIterator();
		while (iterSub.hasNext()) {
			/* Get substance. */
			Substance sub = iterSub.next();
			/* Determine if substance is of the required family. */
			if (family != null)
				if (!sub.getFamily().equals(family))
					continue;
			/* Get concentration of substance in cell. */
			float conInCell = cell.getSubstanceCon(sub);
			/* Get affinity of this substances with this agent's substance to compare. */
			float affinity;
			try {
				affinity = substanceToCompare.affinityWith(sub, startBit, endBit);
			} catch (SubstanceException se) {
				throw new ConditionException(this.getClass().getName() + " is unable to determine affinity between substances '" + substanceToCompare.getName() + "' and '" + sub.getName() + "' (start bit:" + startBit + ", end bit:" + endBit + ").", se);
			}
			/* Get percentage of substance in cell that matches the substance to compare 
			 * in current agent. */
			float percentConMatch = Math.min(1.0f, conInCell / conSubToCompare);
			/* Check if substance in cell fits the minimum requirements in terms of 
			 * concentration and affinity in order for the condition to yield a positive match. */
			if (percentConMatch * affinity > minMatch) {
				if (indexSub >= 0)
					message[indexSub] = sub;
				if (indexCon >= 0)
					message[indexCon] = percentConMatch * affinity;
				return true;
			}
		}		
		return false;
	}
	

}
