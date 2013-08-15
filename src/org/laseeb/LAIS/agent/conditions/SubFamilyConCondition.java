package org.laseeb.LAIS.agent.conditions;

import org.laseeb.LAIS.agent.Agent;
import org.laseeb.LAIS.space.Cell2D;
import org.laseeb.LAIS.substance.Substance;
import org.laseeb.LAIS.substance.SubstanceContainer;
import org.laseeb.LAIS.substance.SubstanceFamily;
import org.simpleframework.xml.Element;

/**
 * This condition checks for the presence of a given substance family in the cell where the 
 * agent is situated.
 * @author Nuno Fachada
 */
public class SubFamilyConCondition extends AgentCondition {

	/** 
	 * <strong>XML Element (integer &gt;= 0)</strong>
	 * <p>
	 * Message index where to pass substance concentration. 
	 * <p> 
	 * <em>REQUIRED: NO.</em>
	 * */
	@Element(required=false)
	int indexCon = -1;
	
	/** 
	 * <strong>XML Element (integer &gt;= 0)</strong>
	 * <p>
	 * Message index where to pass specific substance within family given in 
	 * <code>subFamily</code> element. 
	 * <p> 
	 * <em>REQUIRED: NO.</em>
	 * */
	@Element(required=false)
	int indexSub = -1;
	
	/** 
	 * <strong>XML Element (boolean)</strong>
	 * <p>
	 * <code>localOrSuperficial</code> is <code>true</code> if substance is to be observed 
	 * in current location, <code>false</code> if substance is to be observed in the agent 
	 * surface. Default value is <code>true</code> (i.e., substance is to be observed in 
	 * current location).
	 * <p> 
	 * <em>REQUIRED: NO.</em>
	 * */
	@Element(required=false)
	boolean localOrSuperficial = true;

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
	 * <strong>XML Element ({@link org.laseeb.LAIS.substance.SubstanceFamily} object reference)</strong>
	 * <p>
	 * The substance family to check presence of. 
	 * <p> 
	 * <em>REQUIRED: YES.</em> 
	 * */
	@Element
	SubstanceFamily subFamily;
	
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
		java.util.Iterator<Substance> iterSub = container.substanceIterator();
		while (iterSub.hasNext()) {
			Substance sub = iterSub.next();
			if (sub.getFamily().equals(subFamily)) {
				if (container.getSubstanceCon(sub) > minQuantity) {
					if (indexSub >= 0)
						message[indexSub] = sub;
					if (indexCon >= 0)
						message[indexCon] = container.getSubstanceCon(sub);
					return true;
				}
			}
		}
		return false;
	}

}
