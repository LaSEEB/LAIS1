package org.laseeb.LAIS.space;

/**
 * Exception thrown when problems occur during the execution of a 
 * {@link org.laseeb.LAIS.space.Cell2D} simulation step. 
 * 
 * @author Nuno Fachada
 */
@SuppressWarnings("serial")
public class CellStepException extends Exception {

	/**
	 * Instantiates exception with a message.
	 * 
	 * @param message Information about what caused the exception.
	 */
	public CellStepException(String message) {
		super(message);
	}
	
	/**
	 * Instantiates exception with a message and a cause.
	 * 
	 * @param message Information about what caused the exception.
	 * @param cause Original cause of this exception (if it exists).
	 */
	public CellStepException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates exception with a cause.
	 * 
	 * @param cause Original cause of this exception.
	 */
	public CellStepException(Throwable cause) {
		super(cause);
	}
	
	
}
