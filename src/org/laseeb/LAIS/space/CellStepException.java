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
