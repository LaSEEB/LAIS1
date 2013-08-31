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

import org.laseeb.LAIS.agent.Agent;
import org.laseeb.LAIS.agent.AgentException;
import org.laseeb.LAIS.space.Cell2D;
import org.laseeb.LAIS.substance.Substance;
import org.laseeb.LAIS.substance.SubstanceContainer;
import org.laseeb.LAIS.substance.SubstanceException;
import org.simpleframework.xml.Element;

/** 
 * This action adds or removes a determined amount of the given substance 
 * from the current location or from the agent's surface. 
 * @author Nuno Fachada
 */
public class ProdConsSubAction extends AgentAction {

	/** 
	 * <strong>XML Element (float)</strong>
	 * <p>
	 * Base concentration to produce or consume (may be a negative value 
	 * in this last case). 
	 * <p> 
	 * <em>REQUIRED: YES.</em> 
	 * */
	@Element
	float conBase;
	
	/** 
	 * <strong>XML Element (float > 0)</strong>
	 * <p>
	 * Substance threshold, i.e., after action takes place, there can't be more (if <code>conBase</code> > 0)
	 * or less (if <code>conBase</code> < 0) concentration of the given substance present in the cell
	 * or agent. If not given, no threshold is set. 
	 * <p> 
	 * <em>REQUIRED: NO.</em> 
	 * */
	@Element(required=false)
	Float conThreshold;

	/** 
	 * <strong>XML Element (integer >= 0)</strong>
	 * <p>
	 * Message index of this action's impact. If not given, then conBase will be produced 
	 * or consumed. 
	 * <p> 
	 * <em>REQUIRED: NO.</em>
	 * */
	@Element(required=false)
	int indexCon = -1;
	
	/** 
	 * <strong>XML Element (integer >= 0)</strong>
	 * <p>
	 * Message index of the substance to be produced or consumed. If not given, a substance should be 
	 * defined in the <code>substance</code> element. 
	 * <p> 
	 * <em>REQUIRED: NO.</em>
	 * */
	@Element(required=false)
	Integer indexSub;

	/** 
	 * <strong>XML Element (boolean)</strong>
	 * <p>
	 * <code>localOrSuperficial</code> is <code>true</code> if substance is to be produced/consumed in current
	 * location, <code>false</code> if substance is to be produced/consumed in the agent surface. 
	 * <p> 
	 * <em>REQUIRED: YES.</em>
	 * */
	@Element
	boolean localOrSuperficial;
	
	/** 
	 * <strong>XML Element (a {@link java.lang.String}, which references 
	 * a {@link org.laseeb.LAIS.substance.Substance} instance)</strong>
	 * <p>
	 * Substance to be produced or consumed. If not given, substance should be passed in the message from the
	 * rules. 
	 * <p> 
	 * <em>REQUIRED: NO.</em>
	 * */
	@Element(required=false)
	String substance;
	
	/**
	 * @see AgentAction#performAction(Agent, Cell2D, Object[])
	 */
	public void performAction(Agent agent, Cell2D cell, Object[] message) throws ActionException {
		
		/* Get substance. */
		Substance sub;
		if (indexSub == null) {
			try {
				sub = agent.getSubstanceByRef(substance).getSubstance(agent);
			} catch (SubstanceException se) {
				throw new ActionException("Unable to get substance with reference '" + substance + "'.", se);
			} catch (AgentException ae) {
				throw new ActionException("Unable to get substance with reference '" + substance + "'.", ae);
			}
		} else {
			sub = (Substance) message[indexSub];
		}
		
		/* Get concentration to produce/consume. */
		float conToChange;
		if (indexCon >= 0) {
			conToChange = ((Float) message[indexCon]) * conBase;
		} else {
			conToChange = conBase;
		}
		
		/* Determine type of substance container. */
		SubstanceContainer container;
		if (localOrSuperficial)
			container = cell;
		else
			container = agent;
		
		/* Produce/consume substance in cell or agent. */
		if (conThreshold != null) {
			float eventualCon = container.getSubstanceCon(sub) + conToChange;
			if ((conBase > 0) && (eventualCon > conThreshold))
				conToChange = conThreshold - container.getSubstanceCon(sub);
			else if ((conBase < 0) && (eventualCon < conThreshold))
				conToChange = container.getSubstanceCon(sub) - conThreshold;
		}
		container.modifySubstanceCon(sub, conToChange);
	}
}
