package org.laseeb.LAIS.agent.conditions;

import org.laseeb.LAIS.agent.Agent;
import org.laseeb.LAIS.space.Cell2D;

import org.simpleframework.xml.Element;

/**
 * This condition yields true in average for n = halflife iterations.
 * This rule will probably become obsolete by the development of a 
 * general-purpose numeric agent state approach.
 * 
 * @author Nuno Fachada
 * @deprecated This rule will probably become obsolete by the development of a 
 * general-purpose numeric agent state approach.
 */
@Deprecated
public class AgeCondition extends AgentCondition {

	/** 
	 * <strong>XML Element (integer > 0)</strong>
	 * <p>
	 * The agent's half-life.
	 * <p> 
	 * <em>REQUIRED: YES.</em> 
	 * */
	@Element
	int halflife;
	
	/**
	 * @see AgentCondition#evaluate(Agent, Cell2D, Object[])
	 */
	public boolean evaluate(Agent agent, Cell2D cell, Object[] message) throws ConditionException {
		double p = 1.0/halflife;		
		double doAction = cell.getRng().nextDoubleFromTo(0, 1);

		if (doAction < p) {
			return true;			
		} else {
			return false;
		}
	}

}
