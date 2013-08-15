package org.laseeb.LAIS.event.subdeploy;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Suggests the deployment of a fixed substance concentration.
 * 
 * @author Nuno Fachada
 *
 */
@Root
public class FixedConcentrationConstrain extends ConcentrationConstrain {

	/** 
	 * <strong>XML Element (float)</strong>
	 * <p>
	 * Concentration of substance to deploy. If not given, default value is 1.0.
	 * <p>
	 * <em>REQUIRED: NO</em> 
	 * */
	@Element
	float concentration = 1.0f;
	
	/**
	 * Returns a fixed substance concentration.
	 * @return A fixed substance concentration.
	 */
	public float getConcentration() {
		return concentration;
	}
}
