package org.laseeb.LAIS.event.agdeploy;

import java.awt.Point;

import org.laseeb.LAIS.LAISModel;
import org.laseeb.LAIS.event.Event;
import org.simpleframework.xml.Element;

/**
 * Location constrain in which agent deployment is performed within a specified
 * rectangle. Locations are by default given in linear fashion, i.e., if the
 * deployment rectangle is given by coordinates (3,3) and (6,6), locations are given
 * in the following order:
 * <p>
 * (3,3), (3,4), (3,5), (3,6), (4,3), ..., (6,5), (6,6)
 * <p>
 * Locations can be given randomly within the given rectangle if the user
 * specifies the <code>&lt;random&gt;</code> element tag with the value <code>true</code>. 
 * 
 * @author Nuno Fachada
 */
public class RectangularLocationConstrain extends LocationConstrain {

	/** 
	 * <strong>XML Element (integer > 0)</strong>
	 * <p>
	 * X-coordinate of upper-left corner of deployment rectangle.
	 * <p>
	 * <em>REQUIRED: YES</em> 
	 * */
	@Element
	int minX;

	/** 
	 * <strong>XML Element (integer > 0)</strong>
	 * <p>
	 * Y-coordinate of upper-left corner of deployment rectangle.
	 * <p>
	 * <em>REQUIRED: YES</em> 
	 * */
	@Element
	int minY;

	/** 
	 * <strong>XML Element (integer > 0)</strong>
	 * <p>
	 * X-coordinate of lower-right corner of deployment rectangle.
	 * <p>
	 * <em>REQUIRED: YES</em> 
	 * */
	@Element
	int maxX;

	/** 
	 * <strong>XML Element (integer > 0)</strong>
	 * <p>
	 * Y-coordinate of lower-right corner of deployment rectangle.
	 * <p>
	 * <em>REQUIRED: YES</em> 
	 * */
	@Element
	int maxY;

	/** 
	 * <strong>XML Element (boolean)</strong>
	 * <p>
	 * If set to <code>true</code>, deployment locations will be given randomly within the given 
	 * deployment rectangle. Default value is <code>false</code> (i.e., deployment locations
	 * will be given in a linear fashion).
	 * <p>
	 * <em>REQUIRED: NO</em> 
	 * */
	@Element(required=false)
	boolean random = false;

	/* Next deployment location. */
	private Point nextPoint;

	/**
	 * @see LocationConstrain#getNextCoord()
	 */
	public Point getNextCoord() {
		if (random) {
			nextPoint.x = Event.getEventRng().nextIntFromTo(minX, maxX);
			nextPoint.y = Event.getEventRng().nextIntFromTo(minY, maxY);
		} else {
			nextPoint.x += 1;
			if (nextPoint.x > maxX) {
				nextPoint.x = minX;
				nextPoint.y += 1;
				if (nextPoint.y > maxY)
					nextPoint.y = minY;
			}
		}
		return nextPoint;
	}
	
	/**
	 * @see LocationConstrain#initialize(LAISModel)
	 */
	public void initialize(LAISModel model) {
		super.initialize(model);
		if (!random)
			nextPoint = new Point(minX - 1, minY);
	}
	
}
