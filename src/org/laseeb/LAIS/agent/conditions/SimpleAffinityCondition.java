package org.laseeb.LAIS.agent.conditions;

import org.laseeb.LAIS.agent.Agent;
import org.laseeb.LAIS.agent.AgentException;
import org.laseeb.LAIS.space.Cell2D;
import org.laseeb.LAIS.substance.Substance;
import org.laseeb.LAIS.substance.SubstanceException;
import org.simpleframework.xml.Element;

/**
 * This condition yields true if the affinity between two of the substances referenced by
 * the agent is higher than a given threshold. There are no requirements for substance
 * to be present in either the local cell or the agent surface.
 * <p>
 * The determined affinity can be passed to the actions.
 * 
 * @author Nuno Fachada
 *
 */
public class SimpleAffinityCondition extends AgentCondition {

	/** 
	 * <strong>XML Element (0 &lt;= integer &lt;= 63)</strong>
	 * <p>
	 * End bit for affinity calculation. 
	 * <p> 
	 * <em>REQUIRED: YES.</em> 
	 * */
	@Element
	int endBit;

	/** 
	 * <strong>XML Element (integer &gt;= 0)</strong>
	 * <p>
	 * Message index for passing a value (float) indicating effective affinity. 
	 * <p> 
	 * <em>REQUIRED: NO.</em> 
	 * */
	@Element(required=false)
	int index = -1;

	/** 
	 * <strong>XML Element (0 &lt;= float &lt;= 1)</strong>
	 * <p>
	 * Minimum affinity for this rule to yield true. 
	 * <p> 
	 * <em>REQUIRED: YES.</em> 
	 * */
	@Element
	float minAffin;

	/** 
	 * <strong>XML Element (0 &lt;= integer &lt;= 63)</strong>
	 * <p>
	 * Start bit for affinity calculation. 
	 * <p> 
	 * <em>REQUIRED: YES.</em>
	 * */
	@Element
	int startBit;

	/** 
	 * <strong>XML Element ({@link java.lang.String})</strong>
	 * <p>
	 * Reference of the first substance to compare. 
	 * <p> 
	 * <em>REQUIRED: YES.</em> 
	 * */
	@Element
	String substance1;

	/** 
	 * <strong>XML Element ({@link java.lang.String})</strong>
	 * <p>
	 * Reference of the second substance to compare. 
	 * <p> 
	 * <em>REQUIRED: YES.</em>
	 * */
	@Element
	String substance2;
	
	/**
	 * @see AgentCondition#evaluate(Agent, Cell2D, Object[])
	 */
	public boolean evaluate(Agent agent, Cell2D cell, Object[] message) throws ConditionException {
		Substance sub1;
		try {
			sub1 = agent.getSubstanceByRef(substance1).getSubstance(agent);
		} catch (SubstanceException se) {
			throw new ConditionException(this.getClass().getName() + " is unable to get substance with reference '" + substance1 + "' from agent '" + agent.getName() + "'.", se);
		} catch (AgentException ae) {
			throw new ConditionException(this.getClass().getName() + " is unable to get substance with reference '" + substance1 + "' from agent '" + agent.getName() + "'.", ae);
		}
		Substance sub2;
		try {
			sub2 = agent.getSubstanceByRef(substance2).getSubstance(agent);
		} catch (SubstanceException se) {
			throw new ConditionException(this.getClass().getName() + " is unable to get substance with reference '" + substance1 + "' from agent '" + agent.getName() + "'.", se);
		} catch (AgentException ae) {
			throw new ConditionException(this.getClass().getName() + " is unable to get substance with reference '" + substance1 + "' from agent '" + agent.getName() + "'.", ae);
		}
		float affin;
		try {
			affin = sub1.affinityWith(sub2, startBit, endBit);
		} catch (SubstanceException se) {
			throw new ConditionException(this.getClass().getName() + " is unable to determine affinity between substances '" + sub1.getName() + "' and '" + sub2.getName() + "' (start bit:" + startBit + ", end bit:" + endBit + ").", se);
		}
		if (affin > minAffin) {
			if (index >= 0)
				message[index] = affin;
				return true;
		} else {
			return false;
		}
	}

}
