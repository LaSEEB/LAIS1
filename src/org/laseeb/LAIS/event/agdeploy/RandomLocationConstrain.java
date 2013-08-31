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

import java.awt.Point;

import org.laseeb.LAIS.LAISModel;
import org.laseeb.LAIS.event.Event;

/**
 * Location constrain in which agent deployment is performed in a random fashion.
 * 
 * @author Nuno Fachada
 */
public class RandomLocationConstrain extends LocationConstrain {
	
	private int maxX;
	private int maxY;

	/**
	 * @see LocationConstrain#getNextCoord()
	 */
	public Point getNextCoord() {
		return new Point(
				Event.getEventRng().nextIntFromTo(0, maxX - 1),
				Event.getEventRng().nextIntFromTo(0, maxY - 1));
	}
	

	/**
	 * Sets the minimum and maximum coordinates for agent deployment.
	 * @see LocationConstrain#initialize(LAISModel)
	 */
	public void initialize(LAISModel model) {
		super.initialize(model);
		this.maxX = model.getSizeX();
		this.maxY = model.getSizeY();
	}

}
