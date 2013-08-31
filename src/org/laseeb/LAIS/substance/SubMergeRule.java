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

/**
 * This class contains information of a substance already created by merging two 
 * other substances.
 * @author Nuno Fachada
 */
public class SubMergeRule {
	
	/** First substance of pair. */
	public final Substance sub1;
	/** Second substance of pair. */
	public final Substance sub2;
	/** Substance created due to merging of substances in pair. */
	public final Substance newSub;
	/** Affinity between substances in pair. */
	public final float affin;
	
	/**
	 * Keep record of merged substances.
	 * 
	 * @param sub1 One of the substances in pair.
	 * @param sub2 Other of the substances in pair.
	 * @param newSub Substance created due to merging of substances in pair.
	 * @param affin Affinity between substances in pair.
	 */
	public SubMergeRule(Substance sub1, Substance sub2, Substance newSub, float affin) {
		this.sub1 = sub1;
		this.sub2 = sub2;
		this.newSub = newSub;
		this.affin = affin;
	}
	
	/** 
	 * Determines if a given substance pair corresponds to this pair.
	 * 
	 * @param sub1 One of the substances in pair.
	 * @param sub2 Other of the substances in pair.
	 * @return True if pair is the same, false otherwise.
	 */
	public boolean isPair(Substance sub1, Substance sub2) {
		if (
				(this.sub1.equals(sub1) && this.sub2.equals(sub2))
				|| 
				(this.sub2.equals(sub1) && this.sub1.equals(sub2))
			)
			return true;
		else
			return false;
	}
	
	/**
	 * Returns a string description of this substance merging rule.
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Merge Rule: ["
			+ sub1.getName() + "]+["
			+ sub2.getName() + "] "
			+ "with affinity " + affin;
	}
}

