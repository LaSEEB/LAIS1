package org.laseeb.LAIS.event.agdeploy;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Returns a fixed quantity of agents to deploy.
 * 
 * @see org.laseeb.LAIS.event.agdeploy.QuantityConstrain  
 * @author Nuno Fachada
 */
@Root
public class FixedQuantityConstrain extends
		QuantityConstrain {
	
	/** 
	 * <strong>XML Element (integer > 0)</strong>
	 * <p>
	 * The quantity of agents to deploy.
	 * <p>
	 * <em>REQUIRED: YES</em> 
	 * */
	@Element
	int quantity;
	
	/**
	 * This is the fixed quantity constrain, as such it needs a constructor to
	 * create the constrain without XML indication.
	 * @param quantity The number of agents to deploy.
	 */
	FixedQuantityConstrain(int quantity) {
		this.quantity = quantity;
	}
	
	/* Required for XML serialization. */
	@SuppressWarnings("unused")
	private FixedQuantityConstrain() {}

	/**
	 * Returns a quantity of agents to deploy.
	 * @return A quantity of agents to deploy. 
	 */
	public int getQuantity() {
		return quantity;
	}
}
