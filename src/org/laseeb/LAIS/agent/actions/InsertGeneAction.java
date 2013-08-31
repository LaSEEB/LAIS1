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
import org.laseeb.LAIS.agent.Gene;
import org.laseeb.LAIS.space.Cell2D;
import org.simpleframework.xml.Element;

/**
 * Insert a gene in another agent genome.
 *  
 * @author Nuno Fachada
 */
public class InsertGeneAction extends AgentAction {

	/** 
	 * <strong>XML Element (a {@link org.laseeb.LAIS.agent.Gene} object or reference)</strong>
	 * <p>
	 * Gene to splice. 
	 * <p> 
	 * <em>REQUIRED: YES.</em>
	 * */
	@Element
	Gene gene;
	
	/** 
	 * <strong>XML Element (integer >= 0)</strong>
	 * <p>
	 * Message index of agent where the gene will be spliced. 
	 * <p> 
	 * <em>REQUIRED: YES.</em> 
	 * */
	@Element
	int indexAg;
	
	/** 
	 * <strong>XML Element (0 >= float >= 1)</strong>
	 * <p>
	 * Mutation rate of inserted gene. 
	 * <p> 
	 * <em>REQUIRED: NO.</em>
	 * */
	@Element(required=false)
	float mutationRate;

	/**
	 * @see AgentAction#performAction(Agent, Cell2D, Object[])
	 */
	public void performAction(Agent agent, Cell2D cell, Object[] message) throws ActionException {
		/* Get agent where to insert gene. */
		Agent otherAgent = (Agent) message[indexAg];
		/* Insert gene. */
		try {
			otherAgent.insertGene(gene, mutationRate);
		} catch (CloneNotSupportedException cnse) {
			throw new ActionException("Could not clone gene.", cnse);
		}		
	}

}
