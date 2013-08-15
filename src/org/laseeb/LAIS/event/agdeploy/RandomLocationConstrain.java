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
