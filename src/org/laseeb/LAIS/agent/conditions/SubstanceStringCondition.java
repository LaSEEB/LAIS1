package org.laseeb.LAIS.agent.conditions;

import org.laseeb.LAIS.agent.Agent;
import org.laseeb.LAIS.agent.AgentException;
import org.laseeb.LAIS.space.Cell2D;
import org.laseeb.LAIS.substance.Substance;
import org.laseeb.LAIS.substance.SubstanceException;
import org.laseeb.LAIS.substance.SubstanceProxy;
import org.laseeb.LAIS.substance.SubstanceUtils;
import org.simpleframework.xml.Element;

/**
 * This condition creates a new string or appends a substring to an existing string, with the
 * name of a referenced substance, if the reference is valid. If the reference is not valid,
 * the condition will yield false. Useful condition for providing dynamic state names based 
 * on currently referenced substances.
 *   
 * @author Nuno Fachada
 *
 */
public class SubstanceStringCondition extends AgentCondition {

	/** 
	 * <strong>XML Element (integer &gt;= 0)</strong>
	 * <p>
	 * Message index of where to pass new string. 
	 * <p> 
	 * <em>REQUIRED: YES.</em>
	 * */
	@Element
	int indexSubstanceString;
	
	/** 
	 * <strong>XML Element (integer &gt;= 0)</strong>
	 * <p>
	 * Message index of where to get string passed by previous condition. If not given,
	 * this condition will pass the first string. 
	 * <p> 
	 * <em>REQUIRED: NO.</em>
	 * */
	@Element(required=false)
	int indexSubstanceStringPrev = -1;

	/** 
	 * <strong>XML Element ({@link java.lang.String})</strong>
	 * <p>
	 * Reference of substance.
	 * <p> 
	 * <em>REQUIRED: YES.</em> 
	 * */
	@Element
	String subRef;

	/**
	 * @see AgentCondition#evaluate(Agent, Cell2D, Object[])
	 */
	public boolean evaluate(Agent agent, Cell2D cell, Object[] message) throws ConditionException {
		Substance sub = null;
		SubstanceProxy sp;
		try {
			sp = agent.getSubstanceByRef(subRef);
		} catch (AgentException ae) {
			throw new ConditionException(this.getClass().getName() + " is unable to get substance with reference '" + subRef + "' from agent '" + agent.getName() + "'.", ae);
		}
		if (sp != null)
			try {
				sub = sp.getSubstance(agent);
			} catch (SubstanceException se) {
				throw new ConditionException(this.getClass().getName() + " is unable to get substance with reference '" + subRef + "' from agent '" + agent.getName() + "'.", se);
			}
		if (sub != null) {
			String substanceName = "";
			if (indexSubstanceStringPrev >= 0)
				substanceName = (String) message[indexSubstanceStringPrev];
			substanceName = substanceName + SubstanceUtils.subNameRemoveAppend(sub);
			message[indexSubstanceString] = substanceName;
			return true;
		} else {
			return false;
		}
	}

}
