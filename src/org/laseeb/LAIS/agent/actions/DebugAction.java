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


package org.laseeb.LAIS.agent.actions;

import org.apache.log4j.Logger;
import org.laseeb.LAIS.agent.Agent;
import org.laseeb.LAIS.space.Cell2D;
import org.simpleframework.xml.Element;

/**
 * Test action for debugging purposes.
 * Will only display messages if logger level is set to DEBUG (see log4j documentation).
 * 
 * @author Nuno Fachada
 */
public class DebugAction extends AgentAction {

	/** 
	 * <strong>XML Element (integer >= 0)</strong>
	 * <p>
	 * Message index of possible additional string to print. 
	 * <p> 
	 * <em>REQUIRED: NO.</em>
	 * */
	@Element(required=false)
	int index = -1;
	
	/** 
	 * <strong>XML Element (string)</strong>
	 * <p>
	 * String to print. 
	 * <p> 
	 * <em>REQUIRED: YES.</em> 
	 * */
	@Element
	String msg;

	/* The logger. */
	private static Logger logger = Logger.getLogger(DebugAction.class);
	
	/**
	 * @see AgentAction#performAction(Agent, Cell2D, Object[])
	 */
	public void performAction(Agent agent, Cell2D cell, Object[] message) throws ActionException {
		if (index >= 0)
			logger.debug("DebugActionMessage: " + msg + " / " + message[index]);
		else
			logger.debug("DebugActionMessage: " + msg);
	}

}
