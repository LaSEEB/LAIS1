package org.laseeb.LAIS.agent.conditions;

/**
 * Exception thrown by {@link AgentCondition} objects to report problems such as
 * invalid parameters or incoherent context.
 * 
 * @author Nuno Fachada
 */
@SuppressWarnings("serial")
public class ConditionException extends Exception {

	/**
	 * Instantiates a ConditionException with a message and a cause.
	 * 
	 * @param message Information about what caused the exception.
	 * @param cause Original cause of this exception (if it exists).
	 */
	public ConditionException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a ConditionException with a messages.
	 * 
	 * @param message Information about what caused the exception.
	 */
	public ConditionException(String message) {
		super(message);
	}

}
