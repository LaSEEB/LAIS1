package org.laseeb.LAIS.agent.conditions;

import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import org.laseeb.LAIS.agent.Agent;
import org.laseeb.LAIS.space.Cell2D;
import org.laseeb.LAIS.space.CellStepException;

import org.simpleframework.xml.Element;

/** 
 * This condition determines movement, taking inertia into account.
 * The operation mode is similar to {@link SubConNeighMovCondition},
 * but the relative probability of moving to a given destination <code>k</code>
 * is:
 * <p>
 * <code>relProb_k = 1-2*d(k)/N</code>
 * <p>
 * where
 * <ul> 
 * <li>
 * <code>d(k)</code> is the distance of destination <code>k</code> to destination 
 * <code>O</code>, which is opposite to where the agent came from in last tick 
 * (i.e., abstractly speaking, if agent came from west, destination <code>O</code> is
 * east).</li>
 * <li><code>N</code> is the total number of destinations.</li>
 * </ul>  
 * As such, the probability of moving to a destination <code>k</code> is:
 * <p>
 * <code>prob_k = relProb_k / sum(relProb_m, where m=1_to_N)</code>
 * <p>
 * The <code>inertia</code> parameter can balance these probabilities 
 * (see {@link #inertia}).
 * <p>
 * Remarks:
 * <ul>
 * <li>The relative probability of staying in the same place is <code>0.5</code>.</li>
 * <li>If agent was stopped in previous tick, movement will be random.</li>
 * </ul>
 * <p>
 * 
 * @author Nuno Fachada
 * 
 * */
public class InertialMovementCondition extends AgentCondition {

	/** 
	 * <strong>XML Element (integer &gt;= 0)</strong>
	 * <p>
	 * Message index to pass destination cell reference to walk action. 
	 * <p> 
	 * <em>REQUIRED: YES.</em> 
	 * */
	@Element 
	int index;
	
	/** 
	 * <strong>XML Element (0 &lt;= float &lt;= 1)</strong>
	 * <p>
	 * <code>inertia</code> is a value between 0 and 1 used for balancing the probability of 
	 * moving to a given destination. Basically, the following transformation is performed:
	 * <p>
	 * <code>
	 * new_relProb_k = (relProb_k / relProb_max) ^ [inertia / (1 - inertia)]  
	 * </code>
	 * <p>
	 * where <code>relProb_max</code> is the maximum <code>relProb</code> encountered in all
	 * cells in the neighborhood.
	 * <p>
	 * <b>Examples:</b>
	 * <p>
	 * <ul>
	 * <li>If <code>inertia</code> = 0.5 (the default value), its impact is ignored.</li>
	 * <li>If <code>inertia</code> = 1, the chosen destination will be destination
	 * <code>O</code>, opposite to where the agent came from.</li>
	 * <li>If <code>inertia</code> is zero, movement will be random.</li>
	 * </ul>
	 * <p> 
	 * <em>REQUIRED: NO.</em>
	 * */
	@Element(required=false)
	float inertia = 0.5f;

	/* Previous cell where the agent was situated */
	Cell2D previousCell = null;
	
	static int direction[] = {0,0,0,0,0,0,0,0};
	
	/**
	 * @see AgentCondition#evaluate(Agent, Cell2D, Object[])
	 */
	public boolean evaluate(Agent agent, Cell2D cell, Object[] message) throws ConditionException {

		/* Vector of neighbors. */
		Vector<Cell2D> neighs = cell.getNeighbors();

		/* If agent was stopped or inertia is zero, movement will be random. */
		if ((previousCell == null) || (inertia == 0) || (previousCell == cell)) {
			int destination = cell.getRng().nextIntFromTo(-1, neighs.size() - 1);
			if (destination == -1) {
				previousCell = null;
				return false;
			} else {
				previousCell = cell;
				message[index] = destination;
				direction[destination]++;
				return true;
			}
		}
		
		/* Determine map of sum of relative probabilities */
		Cell2D currDirectionCell;
		try {
			currDirectionCell = cell.getOppositeCell(previousCell);
		} catch (CellStepException cse) {
			throw new ConditionException(cse.getMessage());
		}
		Map<Integer, Float> cellMap = new TreeMap<Integer, Float>();
		for (int i = 0; i < neighs.size(); i++) {
			int d;
			try {
				d = Math.abs(cell.getDistanceBetweenNeighbors(currDirectionCell, neighs.get(i)));
			} catch (CellStepException cse) {
				throw new ConditionException(cse.getMessage());
			}
			float relProb = 1.0f - 2.0f * d / (neighs.size() + 1.0f);
			cellMap.put(i, relProb);
		}
		float relProbStop = 1.0f - neighs.size() / (neighs.size() + 1.0f); 
		cellMap.put(-1, relProbStop);
		/* Total non-normalized probability. */
		float totalProb = 0.0f;
		/* Apply inertia. */
		if (inertia == 1) {
			/* Special case: inertia = 1 */
			previousCell = cell;
			message[index] = neighs.indexOf(currDirectionCell);
			return true;			
		} else if (inertia == 0.5f) {
			/* Special case: inertia = 0.5 */
			for (Integer currCell : cellMap.keySet()) {
				totalProb += cellMap.get(currCell);
			}
		} else {
			/* General case */
			for (int i = -1; i < neighs.size(); i++) {
				float prob = (float) Math.pow(
						cellMap.get(i), 
						inertia / (1 - inertia)
						);
				cellMap.put(i, prob);
				totalProb += prob;
			}
		}

		/* Normalization is not performed to avoid wasting time */

		/* Determine where to go */
		float prob = cell.getRng().nextFloatFromTo(0.0f, totalProb);
		float densityProbDist = 0.0f;
		int destination = -1;
		for (int i = -1; i < neighs.size(); i++) {
			densityProbDist += cellMap.get(i);
			if (prob < densityProbDist) {
				destination = i;
				break;
			}
		}
		if (destination == -1) {
			previousCell = null;
			return false;
		} else {
			previousCell = cell;
			message[index] = destination;
			direction[destination]++;
			return true;
		}
	}	
}