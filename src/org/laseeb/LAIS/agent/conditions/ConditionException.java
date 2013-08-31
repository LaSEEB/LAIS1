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
