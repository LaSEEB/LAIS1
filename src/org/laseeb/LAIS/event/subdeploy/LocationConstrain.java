package org.laseeb.LAIS.event.subdeploy;

import java.util.HashMap;
import java.util.Map;
import java.awt.Point;

import org.laseeb.LAIS.LAISModel;
import org.simpleframework.xml.Root;

/**
 * Abstract class for substance deployment location constrains. 
 * Concrete classes define how to determine the specific location of substance deployment.
 * 
 * @author Nuno Fachada
 */
@Root
public abstract class LocationConstrain {
	
	/**
	 * Add weight to a point in the weight map.
	 * @param weightMap The map to which to add the weight in the given point.
	 * @param point The point where to add the weight.
	 * @param weight The weight to add to the given point.
	 */
	protected void addPointWeight(Map<Point, Float> weightMap, Point point, Float weight) {
		if (weightMap.containsKey(point)) {
			weightMap.put(point, weightMap.get(point) + weight);
		} else {
			weightMap.put(point, weight);
		}
	}
	
	/**
	 * Creates an empty location weight map.
	 * @return An empty location weight map.
	 */
	protected Map<Point, Float> createEmptyWeightMap() {
		return new HashMap<Point, Float>(); 
	}
	
	/**
	 * Get the weight map from the current constrain.
	 * @return The The weight map from the current constrain.
	 */
	public abstract Map<Point, Float> getWeightMap();
	
	/**
	 * Initialize constrain.
	 * @param model The simulation model.
	 */
	public void initialize(LAISModel model) {}

}
