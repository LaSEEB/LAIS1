package org.laseeb.LAIS.agent;

/**
 * Exception thrown when errors occur in the agent context.
 * 
 * @author Nuno Fachada
 */
@SuppressWarnings("serial")
public class AgentException extends Exception {

	/**
	 * Instantiates an AgentException with a cause.
	 * 
	 * @param cause Original cause of this exception (if it exists).
	 */
	public AgentException(Throwable cause) {
		super(cause);
	}

	/**
	 * Instantiates an AgentException with a message and a cause.
	 * 
	 * @param message Information about what caused the exception.
	 * @param cause Original cause of this exception (if it exists).
	 */
	public AgentException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates an AgentException with a message.
	 * 
	 * @param message Information about what caused the exception.
	 */
	public AgentException(String message) {
		super(message);
	}

}
