package org.laseeb.LAIS.event.agdeploy;

import org.laseeb.LAIS.LAISModel;
import org.simpleframework.xml.Element;

/**
 * This constrain provides a fixed mutation rate.
 * 
 * @author Nuno Fachada
 */
public class FixedMutationRateConstrain extends MutationRateConstrain {

	/** 
	 * <strong>XML Element (float)</strong>
	 * <p>
	 * A fixed mutation rate.
	 * <p>
	 * <em>REQUIRED: YES</em> 
	 * */
	@Element
	float mutationRate;
	
	/**
	 * This is the default mutation rate constrain, as such it needs a constructor to
	 * create the constrain without XML indication.
	 * @param mutationRate The fixed mutation rate.
	 */
	FixedMutationRateConstrain(int mutationRate) {
		this.mutationRate = mutationRate;
	}
	
	/* Required by XML serialization framework. */
	@SuppressWarnings("unused")
	private FixedMutationRateConstrain() {}
	
	/**
	 * @see MutationRateConstrain#getMutationRate()
	 */
	public float getMutationRate() {
		return mutationRate;
	}

	/**
	 * @see MutationRateConstrain#initialize(LAISModel)
	 */
	public void initialize(LAISModel model) {
		super.initialize(model);
	}

}
