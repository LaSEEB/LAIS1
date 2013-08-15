package org.laseeb.LAIS.event.agdeploy;

import org.laseeb.LAIS.event.Event;
import org.simpleframework.xml.Element;

/**
 * Returns a random quantity of agents to deploy, up to a given maximum.
 * 
 * @see org.laseeb.LAIS.event.agdeploy.QuantityConstrain  
 * @author Nuno Fachada
 */
public class RandomQuantityConstrain extends QuantityConstrain {

	/** 
	 * <strong>XML Element (integer &gt; 0)</strong>
	 * <p>
	 * Maximum quantity of agents to deploy.
	 * <p>
	 * <em>REQUIRED: NO</em> 
	 * */
	@Element(required=false)
	int maxQuantity = 1;
	
	/**
	 * @see QuantityConstrain#getQuantity()
	 */
	public int getQuantity() {
		int quantityToReturn;
		quantityToReturn = maxQuantity;
		return Event.getEventRng().nextIntFromTo(0, quantityToReturn);
	}

}
