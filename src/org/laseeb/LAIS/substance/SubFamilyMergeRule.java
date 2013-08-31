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

import org.apache.log4j.Logger;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * This class defines a rule for merging substances of pre-determined families.
 * @author Nuno Fachada
 */
@Root
public class SubFamilyMergeRule {

	/** 
	 * <strong>XML Element ({@link SubstanceFamily})</strong>
	 * <p>
	 * Family of substance one.
	 * <p>
	 * <em>REQUIRED: YES</em> 
	 * */
	@Element
	SubstanceFamily familyOne;

	/** 
	 * <strong>XML Element ({@link SubstanceFamily})</strong>
	 * <p>
	 * Family of substance two.
	 * <p>
	 * <em>REQUIRED: YES</em> 
	 * */
	@Element
	SubstanceFamily familyTwo;
	
	/** 
	 * <strong>XML Element ({@link SubstanceFamily})</strong>
	 * <p>
	 * Family of newly created substance.
	 * <p>
	 * <em>REQUIRED: YES</em> 
	 * */
	@Element
	SubstanceFamily newFamily;

	/** 
	 * <strong>XML Element (boolean)</strong>
	 * <p>
	 * If merging is dependent of a minimum affinity, set this to <code>true</code>
	 * (affinity between substances will have to be calculated, which is slower).
	 * Default value is <code>false</code>.
	 * <p>
	 * <em>REQUIRED: NO</em> 
	 * */
	@Element(required=false)
	boolean affinDependent = false;

	/** 
	 * <strong>XML Element (float)</strong>
	 * <p>
	 * Minimum affinity for merge to occur. If {@link #affinDependent} is 
	 * <code>false</code> (the default), then this value is considered 
	 * the affinity between substances for merging purposes.
	 * <p>
	 * <em>REQUIRED: YES</em> 
	 * */
	@Element
	float minAffinity;
	
	/** 
	 * <strong>XML Element (0 &gt;= integer &gt;= 63)</strong>
	 * <p>
	 * Start bit for affinity calculation, applicable if {@link #affinDependent} is 
	 * <code>true</code>. If not given, default value is 0.
	 * <p>
	 * <em>REQUIRED: NO</em> 
	 * */
	@Element(required=false)
	int fromBit = 0;
	
	/** 
	 * <strong>XML Element (0 &gt;= integer &gt;= 63)</strong>
	 * <p>
	 * End bit for affinity calculation, applicable if {@link #affinDependent} is 
	 * <code>true</code>. If not given, default value is 63.
	 * <p>
	 * <em>REQUIRED: NO</em> 
	 * */
	@Element
	int toBit = 63;
	
	/* Logger. */
	private static Logger logger = Logger.getLogger(SubFamilyMergeRule.class);
	
	/**
	 * Merge two substances, if possible.
	 * @param sub1 First substance to merge.
	 * @param sub2 Second substance to merge.
	 * @return Substance and affinity that corresponds to the merging of two other substances.
	 * @throws SubstanceException If not possible to merge substances.
	 */
	public SubMergeRule merge(Substance sub1, Substance sub2) throws SubstanceException {
		/* Determine if substances correspond to families. */
		if (((sub1.getFamily() == familyOne) && (sub2.getFamily() == familyTwo)) 
				|| ((sub1.getFamily() == familyTwo) && (sub2.getFamily() == familyOne))) {
			/* If so, make substance one always the same. */
			if (sub1.getFamily() != familyOne) {
				Substance aux = sub1;
				sub1 = sub2;
				sub2 = aux;
			}
		} else {
			return null;
		}
		/* Determine affinity between substances, if applicable. */
		float affinity;
		if (affinDependent) {
			affinity = sub1.affinityWith(sub2, fromBit, toBit);
		} else {
			affinity = minAffinity;
		}
		/* If affinity between substance is equal or bigger than the minimum required 
		 * affinity, then proceed with the merge. */
		if (affinity >= minAffinity) {
			float kDif = (sub1.getKDif() + sub2.getKDif()) / 2;
			float kDeg = (sub1.getKDeg() + sub2.getKDeg()) / 2;
			String name = SubstanceUtils.subNameRemoveAppend(sub1)
				+ " + " 
				+ SubstanceUtils.subNameRemoveAppend(sub2); 
			long bitId = SubstanceUtils.merge(sub1, sub2, fromBit, toBit);
			Substance newSub = new Substance(kDif, kDeg, name, bitId, newFamily);
			if (logger.isDebugEnabled())
				logger.debug("Created new substance " + newSub.getName() + " with affinity " + affinity + " from substances " + sub1.getName() + " and " + sub2.getName() + "!");
			return new SubMergeRule(sub1, sub2, newSub, affinity);
		} else {
			return null;
		}
	}
}

