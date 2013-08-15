package org.laseeb.LAIS.agent.conditions;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.Before;
import org.junit.Test;
import org.laseeb.LAIS.space.Cell2D;
import org.laseeb.LAIS.space.MockCell;

/**
 * Tests {@link org.laseeb.LAIS.agent.conditions.InertialMovementCondition} class.
 * 
 * @author Nuno Fachada
 */
public class InertialMovementConditionTest {

	/* Range of number of neighbors to test. */
	int minNeigh = 4;
	int maxNeigh = 8;
	/* Number of walks per test. */
	int numWalksPerTest = 100000;
	/* Logger. */
	private static Logger logger;
	
	@Before
	public void setUp() {
		try {
			DOMConfigurator.configure("logdefs.xml");
		} catch (Exception e) {
			System.err.println("Error loading logging definitions! No logging will be performed!");
			System.err.println(e.getMessage());
		}
		logger = Logger.getLogger(InertialMovementConditionTest.class);
	}
	
	/**
	 * Test the evaluate() method.
	 */
	@Test
	public final void testEvaluate() {
		/*
		 * TEST 1 - Higher values of inertia should have less deviations.
		 */
		/* Test for neighbors between minNeigh and maxNeigh: */
		for (int numNeighs = minNeigh; numNeighs <= maxNeigh; numNeighs++) {
			/* Create a inertial movement condition for testing purposes. */
			InertialMovementCondition imc = new InertialMovementCondition();
			/* Create and initialize necessary objects. */
			imc.index = 0;
			Object message[] = new Object[1];
			MockCell cell = new MockCell(numNeighs);
			cell.initNeighbors();
			TreeMap<Float, Integer> stats = new TreeMap<Float, Integer>();
			/* Test for several values of inertia between 0 and 1. */
			for (int j = 0; j <= 10; j++) {
				/* k will represent inertia. */
				float k = ((float) j) / ((float) 10); 
				stats.put(k, 0);
				imc.inertia = k;
				int lastDirection = -1;
				/* Perform "numWalkPerTest" tests for current value of k. */
				for (int i = 0; i < numWalksPerTest; i++) {
					try {
						/* Get a direction. */
						if (imc.evaluate(null, cell, message)) {
							Integer direction = (Integer) message[0];
							if (lastDirection != -1) {
								/* Agent was moving, determine deviation. */
								Cell2D destinationCell = cell.neighbors.get(direction);
								Cell2D originCell = cell.getOppositeCell(destinationCell);
								imc.previousCell = originCell;
								/* Store deviation in stats. */
								stats.put(k, stats.get(k) + Math.abs(cell.getDistanceBetweenNeighbors(destinationCell, cell.neighbors.get(lastDirection))));
							} else {
								/* If agent was previously stopped, then there is no deviation. */
								imc.previousCell = null;
							}
							lastDirection = direction;
						} else {
							/* If agent stopped the deviation is half the number of neighbors... */
							if (lastDirection != -1) {
								/* ...if agent was previously moving! */
								stats.put(k, stats.get(k) + numNeighs / 2);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						logger.info(k);
						logger.info(i);
						fail(e.getMessage());
					}
				}
			}
			/* Check if values are as expected (i.e., less deviations with higher inertia. */
			int previousDeviations = Integer.MAX_VALUE;
			float previousInertia = 0;
			for (Float inertia : stats.keySet()) {
				if (stats.get(inertia) > previousDeviations)
					fail("Inertia=" + inertia + " has more deviations than Inertia=" + previousInertia + " ("+ stats.get(inertia) + ">" + previousDeviations +")! [numNeighs:" + numNeighs + "]");
				previousInertia = inertia;
				previousDeviations = stats.get(inertia);
			}
		}
		/*
		 * TEST 2 - Initial direction (when stopped) should be random.
		 */
		/* Test for neighbors between minNeigh and maxNeigh: */
		for (int numNeighs = minNeigh; numNeighs <= maxNeigh; numNeighs++) {
			/* Create a inertial movement condition for testing purposes. */
			InertialMovementCondition imc = new InertialMovementCondition();
			/* Create and initialize necessary objects. */
			imc.index = 0;
			Object message[] = new Object[1];
			MockCell cell = new MockCell(numNeighs);
			cell.initNeighbors();
			TreeMap<Integer, Integer> stats;
			/* Test for several values of inertia between 0 and 1. */
			for (int j = 0; j <= 10; j++) {
				/* k will represent inertia. */
				float k = ((float) j) / ((float) 10); 
				imc.inertia = k;
				stats = new TreeMap<Integer, Integer>();
				for (int i = -1; i < numNeighs; i++)
					stats.put(i, 0);
				/* Perform "numWalkPerTest" tests for current value of k. */
				for (int i = 0; i < numWalksPerTest; i++) {
					try {
						Integer direction;
						if (imc.evaluate(null, cell, message)) {
							direction = (Integer) message[0];
						} else {
							direction = -1;
						}
						stats.put(direction, stats.get(direction) + 1);
					} catch (ConditionException e) {
						e.printStackTrace();
						logger.info(k);
						logger.info(i);
						fail(e.getMessage());
					}
					imc.previousCell = null;
				}
				/* Check if first directions are in fact random. */
				/* Calculate mean. */
				float mean = 0;
				for (Integer d : stats.keySet()) {
					mean += stats.get(d);
				}
				mean = mean / (float) stats.size();
				/* Calculate standard deviation. */
				float std = 0;
				for (Integer d : stats.keySet()) {
					std += (float) Math.pow(stats.get(d) - mean, 2);
				}
				std = (float) Math.sqrt(std/(float) stats.size());
				/* If std. dev. is higher than 2% of the mean, the test fails. */
				if (std > mean * 0.02f)
					fail("Std. is higher than 2% of the mean (" + std + " > 2% of " + mean +") when numNeighs=" + numNeighs + ", inertia=" + k);
			}
		}
		/*
		 * TEST 3 - Preferred direction of n instances: directions should be evenly
		 * preferred.
		 */
		/* Test for neighbors between minNeigh and maxNeigh: */
		for (int numNeighs = minNeigh; numNeighs <= maxNeigh; numNeighs++) {
			/* Test for several values of inertia between 0 and 1. */
			for (int j = 0; j <= 10; j++) {
				/* k will represent inertia. */
				float k = ((float) j) / ((float) 10); 
				/* Create a series of inertial movement conditions and cells. */
				ArrayList<InertialMovementCondition> imcList = new ArrayList<InertialMovementCondition>();
				ArrayList<MockCell> cellList = new ArrayList<MockCell>();
				for (int i = 0; i < 10000; i++) {
					InertialMovementCondition imc = new InertialMovementCondition();
					imc.index = 0;
					imc.inertia = k;
					imcList.add(imc);
					MockCell cell = new MockCell(numNeighs);
					cell.initNeighbors();
					cellList.add(cell);					
				}
				/* Create and initialize necessary objects. */
				Object message[] = new Object[1];
				TreeMap<Integer, Integer> stats;
				stats = new TreeMap<Integer, Integer>();
				for (int i = 0; i < numNeighs; i++)
					stats.put(i, 0);
				/* Perform "numWalkPerTest" tests for current value of k. */
				for (int i = 0; i < numWalksPerTest/1000; i++) {
					/* Perform walk on imc's in list. */
					for (int n = 0; n < imcList.size(); n++) {
						try {
							if (imcList.get(n).evaluate(null, cellList.get(n), message)) {
								int direction = (Integer) message[0];
								stats.put(direction, stats.get(direction) + 1);	
								Cell2D destinationCell = cellList.get(n).neighbors.get(direction);
								Cell2D originCell = cellList.get(n).getOppositeCell(destinationCell);
								imcList.get(n).previousCell = originCell;
							}
						} catch (Exception e) {
							e.printStackTrace();
							logger.info(k);
							logger.info(i);
							fail(e.getMessage());
						}
					}
				}
				/* See if preferred directions are well distributed. */
				/* Calculate mean. */
				float mean = 0;
				for (Integer d : stats.keySet()) {
					mean += stats.get(d);
				}
				mean = mean / (float) stats.size();
				/* Calculate standard deviation. */
				float std = 0;
				for (Integer d : stats.keySet()) {
					//logger.info("direction: " + d + ", preferred: " + stats.get(d) + ", inertia: " + k + ", neighs: " + numNeighs);
					std += (float) Math.pow(stats.get(d) - mean, 2);
				}
				std = (float) Math.sqrt(std/(float) stats.size());
				/* If std. dev. is higher than 2% of the mean, the test fails. */
				if (std > mean * 0.05f)
					fail("Std. is higher than 5% of the mean (" + std + " > 5% of " + mean +") when numNeighs=" + numNeighs + ", inertia=" + k);
				//logger.info("std: " + std + ", mean : " + mean + " with numNeighs=" + numNeighs + ", inertia=" + k);
			}
		}
	}
}
