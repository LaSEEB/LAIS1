package org.laseeb.LAIS.event.subdeploy;

import org.laseeb.LAIS.LAISModel;
import org.simpleframework.xml.Root;

/**
 * Abstract substance concentration deployment constrain class.
 * 
 * @author Nuno Fachada
 */
@Root
public abstract class ConcentrationConstrain {

	/**
	 * Initializes the constrain object with whichever information it requires from the model.
	 * @param model The LAISModel, master of all simulation objects.
	 */	
	public void initialize(LAISModel model) {}
	
	/**
	 * Returns an indication of how much substance to deploy. If the constrain is the top 
	 * or parent constrain, then the result will not be a mere indication, but the exact 
	 * concentration of substance to deploy.
	 * @return An indication of, or exact concentration of substance to deploy.
	 */
	public abstract float getConcentration();

}
