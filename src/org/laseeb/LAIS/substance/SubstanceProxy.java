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


package org.laseeb.LAIS.substance;

import org.laseeb.LAIS.agent.Agent;
import org.laseeb.LAIS.agent.AgentException;
import org.laseeb.LAIS.utils.random.IRng;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Substance proxies represent substances in the {@link Agent} genome, i.e., in
 * agent conditions ({@link org.laseeb.LAIS.agent.conditions.AgentCondition}) and 
 * actions ({@link org.laseeb.LAIS.agent.actions.AgentAction}).
 * This allows for an agent to alter its perception of substance during simulation runtime,
 * and allows for substance mutation when agent cloning occurs.
 * <p>
 * Substance proxies can also represent other substance proxies, as well as a composition of
 * two substances, possibly yet to be merged (i.e., a future substance).
 * 
 * @author Nuno Fachada
 */
@Root
public class SubstanceProxy implements Cloneable {

	/** 
	 * <strong>XML Element ({@link java.lang.String})</strong>
	 * <p>
	 * The reference to the first substance.
	 * <p>
	 * <em>REQUIRED: NO</em> 
	 * */
	@Element(required=false)
	String subRef1;
	
	/** 
	 * <strong>XML Element ({@link java.lang.String})</strong>
	 * <p>
	 * The reference to the second substance (if final substance is result of
	 * substance merging).
	 * <p>
	 * <em>REQUIRED: NO</em> 
	 * */
	@Element(required=false)
	String subRef2;
	
	/** 
	 * <strong>XML Element ({@link Substance})</strong>
	 * <p>
	 * Substance represented by the proxy.
	 * <p>
	 * <em>REQUIRED: NO</em> 
	 * */
	@Element(required=false)
	Substance substance;
	
	/** 
	 * <strong>XML Element ({@link SubFamilyMergeRule})</strong>
	 * <p>
	 * Family merge rule.
	 * <p>
	 * <em>REQUIRED: NO</em> 
	 * */
	@Element(required=false)
	SubFamilyMergeRule familyMergeRule;
	
	/* Effective substance referenced by subRef1. */
	private Substance sub1;
	/* Effective substance referenced by subRef2. */
	private Substance sub2;
	/* Substance merge rule. */
	private SubMergeRule smr;
	
	/**
	 * Constructor used by the XML serialization framework.
	 */
	public SubstanceProxy() {}
	
	/**
	 * Creates a new substance proxy.
	 * 
	 * @param substance Substance represented by the proxy.
	 */
	public SubstanceProxy(Substance substance) {
		this.substance = substance;
	}
	
	/**
	 * Clone a substance proxy.
	 * Substance(s) represented by this proxy are also cloned with a given
	 * mutation rate.
	 * @param mutationRate Substance mutation rate.
	 * @param rng An instance of a random number generator.
	 * @return A cloned substance proxy.
	 * @throws SubstanceException If not possible to clone substance proxy.
	 */
	public SubstanceProxy clone(float mutationRate, IRng rng) throws SubstanceException {
		/* Create clone. */
		SubstanceProxy spNew = null;
		try {
			spNew = (SubstanceProxy) super.clone();
		} catch (CloneNotSupportedException cnse) {
			throw new SubstanceException("Error in SubstanceProxy cloning!", cnse);
		}
		/* Perform mutation of represented substance, if any. */
		if (familyMergeRule == null) {
			if (substance != null) {
				spNew.substance = substance.cloneWithMutation(mutationRate, rng);
			}
		}
		return spNew;
	}
	
	/**
	 * Returns the substance represented by this proxy.
	 * @param agent The agent which provides a context for this substance proxy. 
	 * @return The substance represented by this proxy.
	 * @throws SubstanceException When not possible to get the substance represented by this proxy.
	 */
	public Substance getSubstance(Agent agent) throws SubstanceException {
		/* In case of a simple substance. */
		if (familyMergeRule == null) {
			if (substance == null)
				throw new SubstanceException("Substance referenced by '" + subRef1 + "' is *null* in agent '" + agent.getName() + "'!");
			return substance;
		}
		
		try {
			/* In case of a composition of two substances. */
			if ((sub1 != null) && (sub2 != null)) {
				if (sub1.equals(agent.getSubstanceByRef(subRef1).getSubstance(agent)) 
						&& sub2.equals(agent.getSubstanceByRef(subRef2).getSubstance(agent))) {
					/* Substance already defined.*/
					return smr.newSub;
				}
			}
			/* If we are here, substance is not yet defined or original substances have changed. */
			sub1 = agent.getSubstanceByRef(subRef1).getSubstance(agent);
			sub2 = agent.getSubstanceByRef(subRef2).getSubstance(agent);
		} catch (AgentException ae) {
			throw new SubstanceException(this.getClass().getName() + 
					" could not get substance by reference from agent '" +
					agent + "'.", ae);
		}
		smr = familyMergeRule.merge(sub1, sub2);
		if (smr == null)
			return null;
		else
			substance = smr.newSub;
		return substance;
	}
	
	/**
	 * Sets the substance represented by this substance proxy.
	 * @param substance
	 */
	public void setSubstance(Substance substance) {
		this.substance = substance;
	}
	
}
