package org.laseeb.LAIS.event.agdeploy;

import org.laseeb.LAIS.LAISModel;
import org.simpleframework.xml.Root;

/**
 * Abstract agent quantity deployment constrain class.
 * 
 * @author Nuno Fachada
 */
@Root
public abstract class QuantityConstrain {

	/**
	 * Initializes the constrain object with whichever information it requires from the model.
	 * @param model The LAISModel, master of all simulation objects.
	 */	
	public void initialize(LAISModel model) {}
	
	/**
	 * Returns an indication of how many agents to deploy. If the constrain is the top or parent 
	 * constrain, then the result will not be a mere indication, but the exact quantity of agents
	 * to deploy.
	 * @return An indication or exact quantity of how many agents to deploy.
	 */
	public abstract int getQuantity();
	
}
