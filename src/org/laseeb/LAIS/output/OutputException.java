package org.laseeb.LAIS.output;

/**
 * Exception thrown when errors occur in the output context.
 * 
 * @author Nuno Fachada
 */
@SuppressWarnings("serial")
public class OutputException extends Exception {
	/**
	 * Instantiates an OutputException with a message and a cause.
	 * 
	 * @param message Information about what caused the exception.
	 * @param cause Original cause of this exception (if it exists).
	 */
	public OutputException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates an OutputException with a message.
	 * 
	 * @param message Information about what caused the exception.
	 */
	public OutputException(String message) {
		super(message);
	}

	/**
	 * Instantiates an OutputException with a cause.
	 * 
	 * @param cause Original cause of this exception (if it exists).
	 */
	public OutputException(Throwable cause) {
		super(cause);
	}

}
