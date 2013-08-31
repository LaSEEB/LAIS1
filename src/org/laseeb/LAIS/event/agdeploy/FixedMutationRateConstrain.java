/*   
 * This file is part of LAIS (LaSEEB Agent Interaction Simulator).
 * 
 * LAIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * LAIS is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with LAIS.  If not, see <http://www.gnu.org/licenses/>.
 */


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
