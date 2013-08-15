package org.laseeb.LAIS.substance;

/**
 * Exception thrown when errors occur in the substance context.
 * 
 * @author Nuno Fachada
 */
@SuppressWarnings("serial")
public class SubstanceException extends Exception {
	/**
	 * Instantiates a SubstanceException with a message and a cause.
	 * 
	 * @param message Information about what caused the exception.
	 * @param cause Original cause of this exception (if it exists).
	 */
	public SubstanceException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a SubstanceException with a message.
	 * 
	 * @param message Information about what caused the exception.
	 */
	public SubstanceException(String message) {
		super(message);
	}

}
