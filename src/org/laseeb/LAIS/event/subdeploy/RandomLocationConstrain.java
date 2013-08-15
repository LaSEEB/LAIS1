package org.laseeb.LAIS.event.subdeploy;

import java.awt.Point;
import java.util.Map;

import org.laseeb.LAIS.LAISModel;
import org.laseeb.LAIS.event.Event;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Determines substance deployment in a random fashion.
 * @see LocationConstrain
 * @author Nuno Fachada
 */
@Root
public class RandomLocationConstrain extends LocationConstrain {

	/** 
	 * <strong>XML Element (integer)</strong>
	 * <p>
	 * Minimum number of cells where to deploy substance. If not given, default value is 1.
	 * <p>
	 * <em>REQUIRED: NO</em> 
	 * */
	@Element
	int minSpread = 1;
	
	/** 
	 * <strong>XML Element (integer)</strong>
	 * <p>
	 * Maximum number of cells where to deploy substance. If not given, default value is 1.
	 * <p>
	 * <em>REQUIRED: NO</em> 
	 * */
	@Element
	int maxSpread = 1;
	
	/* Horizontal size of simulation environment. */
	private int xSize;
	
	/* Vertical size of simulation environment. */
	private int ySize;
	
	/**
	 * Return a location weight map considering substance deployment in a random fashion and
	 * eventual decorated "next" constrain influences.
	 * @return Location weight map.
	 * @see LocationConstrain#getWeightMap()
	 */
	public Map<Point, Float> getWeightMap() {
		Map<Point, Float> weightMap = createEmptyWeightMap();
		int deFactoSpread = Event.getEventRng().nextIntFromTo(minSpread, maxSpread);
		for (int i = 0; i < deFactoSpread; i++) {
			Point point = new Point(
					Event.getEventRng().nextIntFromTo(0, xSize),
					Event.getEventRng().nextIntFromTo(0, ySize));
			float weight = 1.0f / deFactoSpread;
			addPointWeight(weightMap, point, weight);
		}
		return weightMap;
	}

	/**
	 * Initialize constrain. In this case only the size of the simulation environment is needed.
	 * @param model Simulation model.
	 * @see LocationConstrain#initialize(LAISModel)
	 */
	public void initialize(LAISModel model) {
		super.initialize(model);
		this.xSize = model.getSizeX();
		this.ySize = model.getSizeY();
	}

}
