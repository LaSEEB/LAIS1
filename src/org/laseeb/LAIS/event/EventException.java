/*   
 * This file is part of LAIS (LaSEEB Agent Interaction Simulator).
 * 
 * LAIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * LAIS is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with LAIS.  If not, see <http://www.gnu.org/licenses/>.
 */


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
