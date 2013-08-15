package org.laseeb.LAIS.agent.conditions;

import java.util.*;

import org.apache.log4j.Logger;
import org.laseeb.LAIS.agent.Agent;
import org.laseeb.LAIS.agent.AgentException;
import org.laseeb.LAIS.space.Cell2D;
import org.laseeb.LAIS.substance.Substance;
import org.laseeb.LAIS.substance.SubstanceException;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementMap;

/**
 * This condition allows the agent to move with greater probability to
 * a neighbor cell that contains given substances (in biology it
 * is said that such substance is a chemokine for the agent).
 * <p>
 * When instantiating this class, the user passes a substance list
 * and the chemotaxic power of each substance.
 * Thus, the probability of moving to a destination <code>k</code>
 * containing <code>n(k)</code> chemotaxic substances is directly
 * proportional to the following value:
 * <p>
 * <code>relProb_k = concentration(sub_1)*chemotaxic_power(sub_1) + ... 
 * + concentration(sub_n)*chemotaxic_power(sub_n)</code>
 * <p>
 * As such, considering <code>j</code> possible destinations, the probability 
 * of moving to a destination <code>k</code> is:
 * <p>
 * <code>prob_k = relProb_k / sum(relProb_m, where m=1_to_j)</code>
 * <p>
 * The <code>chemopower</code> parameter can balance these probabilities 
 * (see {@link SubConNeighMovCondition#chemopower}).
 * 
 * @author Nuno Fachada
 */
public class SubConNeighMovCondition extends AgentCondition {

	/** 
	 * <strong>XML Element (0 &lt;= float &lt;= 1)</strong>
	 * <p>
	 * <code>chemopower</code> is a value between 0 and 1 used for balancing the probability of 
	 * moving to a given destination. Basically, the following transformation is performed:
	 * <p>
	 * <code>
	 * new_relProb_k = (relProb_k / relProb_max) ^ [chemopower / (1 - chemopower)]  
	 * </code>
	 * <p>
	 * where <code>relProb_max</code> is the maximum <code>relProb</code> encountered in all
	 * cells in the neighborhood.
	 * <p>
	 * <b>Examples:</b>
	 * <p>
	 * <ul>
	 * <li>If <code>chemopower</code> = 0.5 (the default value), this probability of moving to a given
	 * destination depends only on the concentration and chemotaxic power of present substances, as
	 * discussed on the class description above.</li>
	 * <li>If <code>chemopower</code> = 1, the chosen destination will be the one with higher probability, i.e.,
	 * the probability of moving to said destination becomes 1, with all others becoming 0. If
	 * there are several destinations with equal higher probability, then these will have similar
	 * probability of being chosen.</li>
	 * <li>If <code>chemopower</code> is zero, all destinations containing chemotaxic substances have the same 
	 * probability of being chosen, independently of their concentration and chemotaxic power.</li>
	 * </ul>
	 * <p> 
	 * <em>REQUIRED: NO.</em>
	 * */
	@Element(required=false)
	float chemopower = 0.5f;
	
	/** 
	 * <strong>XML Element (integer &gt;= 0)</strong>
	 * <p>
	 * Message index where to pass the destination cell. 
	 * <p> 
	 * <em>REQUIRED: YES.</em>
	 * */
	@Element
	int index;

	/** 
	 * <strong>XML ElementMap (key: {@link java.lang.String}, value: float)</strong>
	 * <p>
	 * Map of chemotaxic substances and their relative chemotaxic power.
	 * <p> 
	 * <em>REQUIRED: YES.</em> 
	 * */
	@ElementMap(key="subRef",attribute=true)
	HashMap<String, Float> subMap;
	
	/* Logger. */
	private static Logger logger = Logger.getLogger(SubConNeighMovCondition.class);

	/**
	 * @see AgentCondition#evaluate(Agent, Cell2D, Object[])
	 */
	public boolean evaluate(Agent agent, Cell2D cell, Object[] message) throws ConditionException {
		
		
		/* Determine map of sum of weighted substances */
		HashMap<Integer, Float> cellPowerMap = new HashMap<Integer, Float>(),
			cellProbMap = new HashMap<Integer, Float>();
		Vector<Cell2D> neighs = cell.getNeighbors();
		/*for (int i = 0; i < neighs.size(); i++)
			cellPowerMap.put(i, 0.0f);
		cellPowerMap.put(-1, 0.0f);*/
		
		/* Determine the weighted chemotaxical power of substances in each destination */
		float maxPower = 0.0f;
		for (int i = -1; i < neighs.size(); i++) {
			Cell2D oneCell;
			if (i != -1)
				oneCell = neighs.get(i);
			else
				oneCell = cell;
			float totalPower = 0.0f;
			Iterator<String> iterSub = subMap.keySet().iterator();
			while (iterSub.hasNext()) {
				String subRef = iterSub.next();
				Substance sub;
				try {
					sub = agent.getSubstanceByRef(subRef).getSubstance(agent);
				} catch (SubstanceException se) {
					throw new ConditionException(this.getClass().getName() + " is unable to get substance with reference '" + subRef + "' from agent '" + agent.getName() + "'.", se);
				} catch (AgentException ae) {
					throw new ConditionException(this.getClass().getName() + " is unable to get substance with reference '" + subRef + "' from agent '" + agent.getName() + "'.", ae);
				}
				if (oneCell.containsSubstance(sub)) {
					totalPower += oneCell.getSubstanceCon(sub) * subMap.get(subRef);
				}
			}
			if (totalPower > maxPower) maxPower = totalPower;
			cellPowerMap.put(i, totalPower);
		}
		
		/* Apply chemopower */
		float totalProb = 0.0f;
		if ((chemopower == 0) || (maxPower == 0)) {
			/* Special case 1: chemopower = 0 */
			for (int i = -1; i < neighs.size(); i++) {
				if (cellPowerMap.get(i) > 0) {
					cellProbMap.put(i, 1.0f);
					totalProb += 1.0f;
				}
			}
			/* If no cell has any chemotaxic substance, make movement random. */
			//TODO Make this quicker, random walk and get out
			if (totalProb == 0) {
				for (int i = -1; i < neighs.size(); i++) {
					cellProbMap.put(i, 1.0f / cellPowerMap.keySet().size());
				}
				totalProb = 1.0f;				
			}
		} else if (chemopower == 1) {
			/* Special case 2: chemopower = 1 */
			//TODO Make this quicker, get the best and get out
			for (int i = -1; i < neighs.size(); i++) {
				if (cellPowerMap.get(i) == maxPower) {
					cellProbMap.put(i, 1.0f);
					totalProb += 1.0f;
				} else {
					cellProbMap.put(i, 0.0f);
				}
			}
			
		} else {
			/* General case */
			//TODO Case in which inertia = 0.5, avoid doing this!
			for (int i = -1; i < neighs.size(); i++) {
				float prob = (float) Math.pow(
						cellPowerMap.get(i) / maxPower, 
						chemopower / (1 - chemopower)
						);
				cellProbMap.put(i, prob);
				totalProb += prob;
			}
		}

		/* Normalization is not performed to avoid wasting time */

		/* Determine where to go */
		float prob = cell.getRng().nextFloatFromTo(0.0f, totalProb);
		if (logger.isDebugEnabled()) {
			StringBuilder sb = new StringBuilder("Total prob = " + totalProb + " with " + subMap.keySet().size() + " substances (maxPower=" + maxPower + "; ");
			for (Integer i : cellPowerMap.keySet())
				sb.append("[" + i + "]: " + cellPowerMap.get(i) + "; ");
			sb.append(")");
			logger.debug(sb.toString());
		}
		float densityProbDist = 0.0f;
		int destination = -1;
		for (int i = -1; i < neighs.size(); i++) {
			densityProbDist += cellProbMap.get(i);
			if (prob < densityProbDist) {
				destination = i;
				break;
			}
		}
		if (destination == -1) {
			return false;
		}
		else {
			message[index] = destination;
			return true;
		}
	}
}
