package org.laseeb.LAIS.agent.conditions;

import org.apache.log4j.Logger;
import org.laseeb.LAIS.agent.Agent;
import org.laseeb.LAIS.space.Cell2D;
import org.simpleframework.xml.Element;

/**
 * Test condition for debugging purposes. Prints a message in the standard output.
 * <p>
 * The printed message can be passed to the actions.
 * 
 * @author Nuno Fachada
 */
public class DebugCondition extends AgentCondition {

	/** 
	 * <strong>XML Element (integer > 0)</strong>
	 * <p>
	 *  Message index for passing string object to actions. 
	 * <p> 
	 * <em>REQUIRED: NO.</em> 
	 * */
	@Element(required=false)
	int index = -1;
	
	/** 
	 * <strong>XML Element (string)</strong>
	 * <p>
	 * String to send to logger and to actions. 
	 * <p> 
	 * <em>REQUIRED: YES.</em> 
	 * */
	@Element
	String stringToPrint;
	
	/* The logger. */
	private static Logger logger = Logger.getLogger(DebugCondition.class);

	/**
	 * @see AgentCondition#evaluate(Agent, Cell2D, Object[])
	 */
	public boolean evaluate(Agent agent, Cell2D cell, Object[] message) throws ConditionException {
		logger.debug("DebugconditionMessage: " + stringToPrint);
		if (index >= 0)
			message[index] = stringToPrint;
		return true;
	}

}
