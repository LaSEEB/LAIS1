package org.laseeb.LAIS.event.agdeploy;

import org.laseeb.LAIS.LAISModel;

import java.util.Map;

/**
 * Abstract initial superficial substance concentration in deployed agents constrain class.
 * 
 * @author Nuno Fachada
 *
 */
public abstract class InitSupSubConConstrain {
	
	/**
	 * Returns a map containing superficial substance concentrations of the agent
	 * to be deployed.
	 * @return A map containing superficial substance concentrations.
	 */
	public abstract Map<String, Float> getInitSubCon();
	
	/**
	 * Initializes the constrain object with whichever information it requires from the model.
	 * 
	 * @param model The LAISModel, master of all simulation objects.
	 */	
	public void initialize(LAISModel model) {}

}
