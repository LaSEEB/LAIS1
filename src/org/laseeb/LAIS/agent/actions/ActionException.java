package org.laseeb.LAIS.agent.actions;

/**
 * Exception thrown by {@link AgentAction} objects to report problems such as
 * invalid parameters or incoherent context.
 * 
 * @author Nuno Fachada
 */
@SuppressWarnings("serial")
public class ActionException extends Exception {
	
	/**
	 * Instantiates an ActionException with a message and a cause.
	 * 
	 * @param message Information about what caused the exception.
	 * @param cause Original cause of this exception (if it exists).
	 */
	public ActionException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates an ActionException with a message.
	 * 
	 * @param message Information about what caused the exception.
	 */
	public ActionException(String message) {
		super(message);
	}

	/**
	 * Instantiates an ActionException with a cause.
	 * 
	 * @param cause Original cause of this exception (if it exists).
	 */
	public ActionException(Throwable cause) {
		super(cause);
	}
}
