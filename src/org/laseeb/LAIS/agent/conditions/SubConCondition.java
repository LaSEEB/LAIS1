package org.laseeb.LAIS.agent.conditions;

import org.laseeb.LAIS.agent.Agent;
import org.laseeb.LAIS.agent.AgentException;
import org.laseeb.LAIS.space.Cell2D;
import org.laseeb.LAIS.substance.Substance;
import org.laseeb.LAIS.substance.SubstanceContainer;
import org.laseeb.LAIS.substance.SubstanceException;
import org.simpleframework.xml.Element;

/**
 * This condition checks for the presence of a given substance in a substance container
 * (either the cell where the agent is located in, or the agent itself).
 * @author Nuno Fachada
 */
public class SubConCondition extends AgentCondition {

	/** 
	 * <strong>XML Element (boolean)</strong>
	 * <p>
	 * <code>localOrSuperficial</code> is <code>true</code> if substance is to be observed in current
	 * location, <code>false</code> if substance is to be observed in the agent surface. Default value
	 * is <code>true</code> (i.e., substance is to be observed in current location).
	 * <p> 
	 * <em>REQUIRED: NO.</em>
	 * */
	@Element(required=false)
	boolean localOrSuperficial = true;

	/** 
	 * <strong>XML Element (integer &gt;= 0)</strong>
	 * <p>
	 * Message index where to pass effective substance concentration. 
	 * <p> 
	 * <em>REQUIRED: NO.</em> 
	 * */
	@Element(required=false)
	int index = -1;
	
	/** 
	 * <strong>XML Element (float &gt; 0)</strong>
	 * <p>
	 * Minimum quantity substance quantity for this rule to yield true.
	 * <p> 
	 * <em>REQUIRED: YES.</em>
	 * */
	@Element
	float minQuantity;

	/** 
	 * <strong>XML Element ({@link java.lang.String})</strong>
	 * <p>
	 * Reference of substance to check presence of.
	 * <p> 
	 * <em>REQUIRED: YES.</em> 
	 * */
	@Element
	String substance;
	
	/**
	 * @see AgentCondition#evaluate(Agent, Cell2D, Object[])
	 */
	public boolean evaluate(Agent agent, Cell2D cell, Object[] message) throws ConditionException {
		/* Determine type of substance container. */
		SubstanceContainer container;
		if (localOrSuperficial)
			container = cell;
		else
			container = agent;
		/* Perform action. */
		Substance sub;
		try {
			sub = agent.getSubstanceByRef(substance).getSubstance(agent);
		} catch (SubstanceException se) {
			throw new ConditionException(this.getClass().getName() + " is unable to get substance with reference '" + substance + "' from agent '" + agent.getName() + "'.", se);
		} catch (AgentException ae) {
			throw new ConditionException(this.getClass().getName() + " is unable to get substance with reference '" + substance + "' from agent '" + agent.getName() + "'.", ae);
		}
		Float con = container.getSubstanceCon(sub);
		if (con == null) con = 0.0f;
		if (index >= 0) message[index] = con;
		if (con >= minQuantity) 
			return true;
		else 
			return false;
		
	}


}
