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


package org.laseeb.LAIS.event.subdeploy;

import java.awt.Point;
import java.util.Map;
import java.util.Iterator;

import org.laseeb.LAIS.LAISModel;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Substance deployment location constrain which forces deployment on predefined 
 * rectangular areas of the simulation environment.
 * 
 * @see LocationConstrain
 * @author Nuno Fachada
 */
@Root
public class RectangularLocationConstrain extends LocationConstrain {

	/** 
	 * <strong>XML Element (integer)</strong>
	 * <p>
	 * First horizontal coordinate of deployment rectangle.
	 * <p>
	 * <em>REQUIRED: YES</em> 
	 * */
	@Element
	int x1;

	/** 
	 * <strong>XML Element (integer)</strong>
	 * <p>
	 * Second horizontal coordinate of deployment rectangle.
	 * <p>
	 * <em>REQUIRED: YES</em> 
	 * */
	@Element
	int x2;

	/** 
	 * <strong>XML Element (integer)</strong>
	 * <p>
	 * First vertical coordinate of deployment rectangle.
	 * <p>
	 * <em>REQUIRED: YES</em> 
	 * */
	@Element
	int y1;

	/** 
	 * <strong>XML Element (integer)</strong>
	 * <p>
	 * Second vertical coordinate of deployment rectangle.
	 * <p>
	 * <em>REQUIRED: YES</em> 
	 * */
	@Element
	int y2;
	
	/* Private weight map. */
	private Map<Point, Float> fixedMap;
	
	/**
	 * Return a location weight map considering substance deployment in a random fashion and
	 * eventual decorated "next" constrain influences.
	 * @return Location weight map.
	 * @see LocationConstrain#getWeightMap()
	 */
	public Map<Point, Float> getWeightMap() {
		Map<Point, Float> weightMap = createEmptyWeightMap();
		Iterator<Point> pointIter = fixedMap.keySet().iterator();
		while (pointIter.hasNext()) {
			Point point = pointIter.next();
			addPointWeight(weightMap, point, fixedMap.get(point));
		}
		return weightMap;
	}

	/**
	 * Initialize constrain. In this case we determine the fixed weight map indicating the
	 * rectangular deployment.
	 * @param model Simulation model.
	 * @see LocationConstrain#initialize(LAISModel)
	 */
	public void initialize(LAISModel model) {
		super.initialize(model);
		int lowerX = Math.min(this.x1, this.x2);
		int upperX = Math.max(this.x1, this.x2);
		int lowerY = Math.min(this.y1, this.y2);
		int upperY = Math.max(this.y1, this.y2);
		int dx = (upperX - lowerX) + 1;
		int dy = (upperY - lowerY) + 1;
		int rectArea = dx * dy;
		float weight = 1.0f / rectArea;
		fixedMap = createEmptyWeightMap();
		for (int i = lowerX; i <= upperX; i++) {
			for (int j = lowerY; j <= upperY; j++) {
				fixedMap.put(new Point(i, j), weight);
			}
		}	
	}

}
