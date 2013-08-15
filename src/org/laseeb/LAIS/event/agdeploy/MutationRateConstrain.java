package org.laseeb.LAIS.event.agdeploy;

import org.laseeb.LAIS.LAISModel;
import org.simpleframework.xml.Root;

/**
 * Abstract class of mutation rate constrains.
 * 
 * @author Nuno Fachada
 */
@Root
public abstract class MutationRateConstrain {

	/**
	 * Returns the mutation rate given by the constrain.
	 * @return The mutation rate given by the constrain.
	 */
	public abstract float getMutationRate();
	
	/**
	 * Initialize mutation constrain.
	 * 
	 * @param model The model where the constrain can obtain the necessary parameters.
	 */
	public void initialize(LAISModel model) {}
	
}
