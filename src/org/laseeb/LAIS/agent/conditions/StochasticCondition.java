package org.laseeb.LAIS.agent.conditions;

import org.laseeb.LAIS.agent.Agent;
import org.laseeb.LAIS.space.Cell2D;
import org.simpleframework.xml.Element;

/**
 * Simple condition which yields true with a given probability. If it yields true, it also returns
 * the random value (lower than the given probability).
 * <p>
 * TODO If indexFromConditions = probability = -1, throw a ConditionException.
 * <p>
 * @author Nuno Fachada
 *
 */
public class StochasticCondition extends AgentCondition {

	/** 
	 * <strong>XML Element (integer >= 0)</strong>
	 * <p>
	 * The message index of the probability defined by another condition. 
	 * <p> 
	 * <em>REQUIRED: NO.</em> 
	 * */
	@Element(required=false)
	int indexFromConditions = -1;

	/** 
	 * <strong>XML Element (integer &gt;= 0)</strong>
	 * <p>
	 * The index of the message where to put the random value, 
	 * in case the condition yields true. 
	 * <p> 
	 * <em>REQUIRED: NO.</em> 
	 * */
	@Element(required=false)
	int indexToActions = -1;

	/** 
	 * <strong>XML Element (0 &lt;= float &lt;= 1)</strong>
	 * <p>
	 * The probability of the condition yielding true. 
	 * <p> 
	 * <em>REQUIRED: NO.</em> 
	 * */
	@Element(required=false)
	Float probability = -1f;	
	
	/**
	 * @see AgentCondition#evaluate(Agent, Cell2D, Object[])
	 */
	public boolean evaluate(Agent agent, Cell2D cell, Object[] message) throws ConditionException {
		float realProbability;
		if (indexFromConditions >= 0)
			realProbability = (Float) message[indexFromConditions];
		else
			realProbability = probability;
		float p = cell.getRng().nextFloatFromTo(0, 1);
		if (indexToActions >= 0) 
			message[indexToActions] = p;
		if (p < realProbability) {
			return true;
		} else {
			return false;
		}
	}

}
