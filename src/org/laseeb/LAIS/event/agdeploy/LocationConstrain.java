package org.laseeb.LAIS.event.agdeploy;

import org.laseeb.LAIS.LAISModel;

import org.simpleframework.xml.Root;

import java.awt.Point;

/**
 * Abstract agent location deployment constrain class.
 * Specific location deployment strategies should extend this class. 
 * 
 * @author Nuno Fachada
 *
 */
@Root
public abstract class LocationConstrain {

	/**
	 * Returns next coordinate for agent deployment.
	 * @return Next coordinate for agent deployment.
	 */
	public abstract Point getNextCoord();

	/**
	 * Initialize location constrain.
	 * @param model Constrains get the necessary parameters from LAISModel, such as
	 * dimension of the simulation environment.
	 */
	public void initialize(LAISModel model) {
		/*
		 * There is no general initialization; implementations of this class
		 * can override this method if needed.
		 */
	}
	

}
