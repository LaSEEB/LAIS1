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


package org.laseeb.LAIS.agent;

import java.util.ArrayList;
import java.util.Iterator;

import org.laseeb.LAIS.agent.actions.AgentAction;
import org.laseeb.LAIS.agent.conditions.AgentCondition;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Validate;

/**
 * This class represents one gene of the agent's genome. A gene contains a list of conditions and a list of 
 * actions. The list of actions will only be executed if all the conditions in the list of conditions verify.
 * Conditions can pass parameters to actions, in order to further control their impact.
 * 
 * Genomes are cloneable. The cloning of conditions and actions that comprise the 
 * genome is their responsibility.
 * 
 *   
 * @author Nuno Fachada
 *
 */
@Root(name="gene")
public class Gene implements Cloneable {
	
	/** 
	 * <strong>XML ElementList (elements of type {@link org.laseeb.LAIS.agent.conditions.AgentCondition})</strong>
	 * <p>
	 * A list of conditions which must yield true in order to execute this 
	 * gene's action list. Usage example:
	 * <p>
	 * <code>
	 * &lt;conditions&gt;<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;AgentCondition class="..."&gt;<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;...<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;/AgentCondition&gt;<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;AgentCondition class="..."&gt;<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;...<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;/AgentCondition&gt;<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;.<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;.<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;.<br/>
	 * &lt;/conditions&gt;
	 * </code>
	 * <p>
	 * <em>REQUIRED: YES</em> 
	 * */
	@ElementList
	ArrayList<AgentCondition> conditions;

	/** 
	 * <strong>XML ElementList (elements of type {@link org.laseeb.LAIS.agent.actions.AgentAction})</strong>
	 * <p>
	 * A list of actions to be executed if all conditions in this gene's list of conditions 
	 * yield true. Usage example:
	 * <p>
	 * <code>
	 * &lt;actions&gt;<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;AgentAction class="..."&gt;<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;...<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;/AgentAction&gt;<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;AgentAction class="..."&gt;<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;...<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;/AgentAction&gt;<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;.<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;.<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;.<br/>
	 * &lt;/actions&gt;
	 * </code>
	 * <p>
	 * <em>REQUIRED: YES</em> 
	 * */
	@ElementList
	ArrayList<AgentAction> actions;
	
	/**
	 * Returns an iterator that iterates through the conditions of this gene.
	 * 
	 * @return Iterator that iterates through the conditions of this gene.
	 */
	public Iterator<AgentCondition> getConditionIterator() {
		return conditions.iterator();
	}

	/**
	 * Returns an iterator that iterates through the actions of this gene.
	 * 
	 * @return Iterator that iterates through the actions of this gene.
	 */
	public Iterator<AgentAction> getActionIterator() {
		return actions.iterator();
	}

	/**
	 * Clones this gene. Delegates cloning of conditions and actions to themselves.
	 * 
	 * @return A clone of this gene.
	 * @throws CloneNotSupportedException If it's not possible to clone the gene.
	 */
	public Gene clone(float mutationRate) throws CloneNotSupportedException {
		Gene geneClone = new Gene();
		geneClone.conditions = new ArrayList<AgentCondition>(conditions.size());
		geneClone.actions = new ArrayList<AgentAction>(actions.size());
		/* Clone conditions */
		for (int i = 0; i < conditions.size(); i++) {
			geneClone.conditions.add(i, conditions.get(i).clone(mutationRate));
		}
		/* Clone actions */
		for (int i = 0; i < actions.size(); i++) {
			geneClone.actions.add(i, actions.get(i).clone(mutationRate));
		}
		return geneClone;
	}

	@SuppressWarnings("unused")
	@Validate
	private void validate() {
		if (conditions == null)
			conditions = new ArrayList<AgentCondition>();
		if (actions == null)
			actions = new ArrayList<AgentAction>();
	}

}
