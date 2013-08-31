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


package org.laseeb.LAIS.agent.conditions;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.laseeb.LAIS.agent.Agent;
import org.laseeb.LAIS.space.Cell2D;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementMap;

/**
 * This condition contains <i>n</i> walk conditions and a weight for each one.
 * Based on what is determined by the conditions and the respective weights,
 * this condition then determines the final destination for its agent. 
 * 
 * @author Nuno Fachada
 * @author Carlos Isidoro
 */
public class WeightedWalkCondition extends AgentCondition {

	/** 
	 * <strong>XML ElementMap (key: {@link AgentCondition}, value: float)</strong>
	 * <p>
	 * Map relating walk conditions to weights. Usage example: 
	 * <p> 
	 * <code>
	 * &lt;conditionMap&gt;<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;entry&gt;<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;walkCondition class="..."&gt;<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;...<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/walkCondition&gt;<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;weight>0.1&lt;/weight&gt;<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;/entry&gt;<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;entry&gt;<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;walkCondition class="..."&gt;<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;...<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;/walkCondition&gt;<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &lt;weight>0.9&lt;/weight&gt;<br/>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;/entry&gt;<br/>
	 * &lt;/conditionMap&gt;
	 * </code>
	 * <p>
	 * <em>REQUIRED: YES.</em>
	 * */
	@ElementMap(key="walkCondition",value="weight")
	HashMap<AgentCondition, Float> conditionMap;

	/** 
	 * <strong>XML Element (integer &gt;= 0)</strong>
	 * <p>
	 * The message index where to read and write movement decision. 
	 * <p> 
	 * <em>REQUIRED: YES.</em>
	 * */
	@Element
	int index;
	
	/* Existing angles (calculated only once, depends on the space). */
	private static volatile float angles[] = null;

	/* The logger. */
	private static Logger logger = Logger.getLogger(WeightedWalkCondition.class);
	
	
	/**
	 * @see AgentCondition#evaluate(Agent, Cell2D, Object[])
	 */
	public boolean evaluate(Agent agent, Cell2D cell, Object[] message)
			throws ConditionException {
		/* If angles not set, set them. */
		if (angles == null) {
			float angle = (float) (2*Math.PI / (cell.getNeighbors().size() + 1));
			angles = new float[cell.getNeighbors().size() + 1];
			for (int i = 0; i < cell.getNeighbors().size() + 1; i++) {
				angles[i] = i * angle;
			}
		}
		/* Set initial vector. */
		float angle = 0;
		float mag = 0;
		/* Cycle through all conditions. */
		for (AgentCondition ac : conditionMap.keySet()) {
			logger.debug("Adding vector with mag=" + mag + " and angle=" + Math.toDegrees(angle) + " with");
			/* Get destination suggested by condition. */
			int aDestination;
			if (ac.evaluate(agent, cell, message))
				aDestination = (Integer) message[index];
			else
				aDestination = -1;
			/* Get polar coordinates for this condition selection. */
			float currentMag = conditionMap.get(ac);
			float currentAng = angles[aDestination + 1];
			logger.debug("Vector with mag=" + currentMag + " and angle=" + Math.toDegrees(currentAng));
			/* Sum with previous angle and magnitude. */
			float magX = (float) (mag*Math.cos(angle) + currentMag*Math.cos(currentAng));
			float magY = (float) (mag*Math.sin(angle) + currentMag*Math.sin(currentAng));
			float tempAngle = (float) Math.atan(magY / magX);
			if (Float.isNaN(tempAngle)) {
				tempAngle = (float) Math.PI / 2;
			}
			mag = (float) Math.sqrt(Math.pow(magX, 2) + Math.pow(magY, 2));
			if (magX < 0)
				tempAngle = (float) (Math.PI + tempAngle);
			else if (tempAngle < 0)
				tempAngle = (float) (2*Math.PI + tempAngle);
			angle = tempAngle;
			logger.debug("Result is vector with mag=" + mag + " and angle=" + Math.toDegrees(angle));
		}
		logger.debug("Final is mag=" + mag + " and angle=" + Math.toDegrees(angle) + "\n------------------");

		/* Determine final destination (depends on angle). */
		int finalDestination = -1;
		for (int i = 0; i < angles.length; i++) {
			if (angle + cell.getRng().nextFloatFromTo(-0.1f, 0.1f) <= angles[i] + angles[1]/2) {
				finalDestination = i - 1;
				break;
			}
		}
		
		if (finalDestination >= 0) {
			/* Move to an adjacent cell. */
			message[index] = finalDestination;
			return true;
		} else {
			/* Don't move. */
			return false;
		}
	}

	/**
	 * Overrides {@link AgentCondition} because it is necessary to perform explicit cloning
	 * of encapsulated objects.
	 * 
	 * @param mutationRate The mutation rate of the cloning process.
	 * @return A clone of this AgentCondition, possibly with some mutation.
	 * @throws CloneNotSupportedException If it's not possible to clone this condition.
	 */
	public AgentCondition clone(float mutationRate) throws CloneNotSupportedException {
		WeightedWalkCondition clonedCondition = (WeightedWalkCondition) super.clone(mutationRate);
		clonedCondition.conditionMap = 
			new HashMap<AgentCondition, Float>(this.conditionMap.size());
		Iterator<AgentCondition> acIter = this.conditionMap.keySet().iterator();
		while (acIter.hasNext()) {
			AgentCondition ac = acIter.next();
			clonedCondition.conditionMap.put(
					ac.clone(mutationRate), 
					this.conditionMap.get(ac));
		}
		return clonedCondition;
	}

}
