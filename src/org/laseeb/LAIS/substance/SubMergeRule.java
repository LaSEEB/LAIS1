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

