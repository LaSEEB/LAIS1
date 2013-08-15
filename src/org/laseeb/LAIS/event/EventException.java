package org.laseeb.LAIS.event;

/**
 * Exception thrown when problems occur during the execution of user defined events
 * (i.e., during scripted events). 
 * <p>
 * This exception extends {@link java.lang.RuntimeException} because Repast
 * scheduling code would have to be altered in order to accommodate the more
 * responsible {@link java.lang.Exception}. This way, 
 * {@link org.laseeb.LAIS.LAISModel} will have to blindly catch this exception.
 * 
 * @author Nuno Fachada
 */
@SuppressWarnings("serial")
public class EventException extends Exception {

	/**
	 * Instantiates exception with a message.
	 * 
	 * @param message Information about what caused the exception.
	 */
	public EventException(String message) {
		super(message);
	}
	
	/**
	 * Instantiates exception with a message and a cause.
	 * 
	 * @param message Information about what caused the exception.
	 * @param cause Original cause of this exception (if it exists).
	 */
	public EventException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates exception with a cause.
	 * 
	 * @param cause Original cause of this exception (if it exists).
	 */
	public EventException(Throwable cause) {
		super(cause);
	}
}
