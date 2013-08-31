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


/**
 * 
 */
package org.laseeb.LAIS.agent.conditions;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.laseeb.LAIS.agent.MockAgent;
import org.laseeb.LAIS.space.Cell2D;
import org.laseeb.LAIS.space.MockCell;
import org.laseeb.LAIS.substance.Substance;

/**
 * Test for SubConNeighMovCondition class.
 * 
 * @author Nuno Fachada
 */
public class SubConNeighMovConditionTest {
	
	MockAgent agent;
	MockCell cell;
	int numSidesToTest = 6;
	int testTimes = 5000;
	int messageIndex = 0;
	

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		/* Create phony substances. */
		Substance sub1 = new Substance(0, 0, "sub1", 1, null);
		Substance sub2 = new Substance(0, 0, "sub2", 2, null);
		Substance sub3 = new Substance(0, 0, "sub3", 3, null);
		/* Create mock agent. */
		agent = new MockAgent(0);
		agent.subMap.put("sub1", sub1);
		agent.subMap.put("sub2", sub2);
		agent.subMap.put("sub3", sub3);
		/* Create mock cells. */
		cell = new MockCell(numSidesToTest);
		cell.initNeighbors();
		/* Interior cell will have same quantity of each substance. */
		cell.subConMap.put(sub1, 1.0f);
		cell.subConMap.put(sub2, 1.0f);
		cell.subConMap.put(sub3, 1.0f);
		((MockCell) cell.neighbors.get(0)).subConMap.put(sub1, 0.5f);
		((MockCell) cell.neighbors.get(2)).subConMap.put(sub2, 1.5f);
		((MockCell) cell.neighbors.get(3)).subConMap.put(sub2, 2.0f);
		((MockCell) cell.neighbors.get(4)).subConMap.put(sub3, 2.5f);
	}

	/**
	 * Test method for {@link org.laseeb.LAIS.agent.conditions.SubConNeighMovCondition#evaluate(org.laseeb.LAIS.agent.Agent, org.laseeb.LAIS.space.Cell2D, java.lang.Object[])}.
	 */
	@Test
	public final void testEvaluate() {
	/* TEST 1 - Equal weights for all substances. */
		SubConNeighMovCondition cond1 = new SubConNeighMovCondition();
		cond1.subMap = new HashMap<String, Float>();
		cond1.index = messageIndex;
		cond1.subMap.put("sub1", 1f);
		cond1.subMap.put("sub2", 1f);
		cond1.subMap.put("sub3", 1f);
		/* Perform test. */
		int result1[] = performTest(cond1, cell);
		int preferredNeighbor1 = getPreferredNeighbor(result1) - 1;
		/* Preferred neighbor should be -1. */
		assertEquals(preferredNeighbor1, -1);
		//System.out.println("Test 1 ok!");
	/* TEST 2 - Only sub2. */
		SubConNeighMovCondition cond2 = new SubConNeighMovCondition();
		cond2.subMap = new HashMap<String, Float>();
		cond2.index = messageIndex;
		cond2.subMap.put("sub2", 1f);
		/* Perform test. */
		int result2[] = performTest(cond2, cell);
		int preferredNeighbor2 = getPreferredNeighbor(result2) - 1;
		/* Preferred neighbor should be 3. */
		assertEquals(preferredNeighbor2, 3);
		//System.out.println("Test 2 ok!");
	/* TEST 3 - chemopower = 0. */
		SubConNeighMovCondition cond3 = new SubConNeighMovCondition();
		cond3.subMap = new HashMap<String, Float>();
		cond3.index = messageIndex;
		cond3.subMap.put("sub1", 0.01f);
		cond3.subMap.put("sub2", 1.00f);
		cond3.subMap.put("sub3", 0.10f);
		cond3.chemopower = 0.0f;
		/* Perform test. */
		int result3[] = performTest(cond3, cell);
		getPreferredNeighbor(result3);
		int coiso = testTimes / 5;
		/* All cells with substances should have been selected a similar number of times. */
		assertEquals(result3[0], coiso, testTimes / 50);
		assertEquals(result3[1], coiso, testTimes / 50);
		assertEquals(result3[3], coiso, testTimes / 50);
		assertEquals(result3[4], coiso, testTimes / 50);
		assertEquals(result3[5], coiso, testTimes / 50);
		//System.out.println("Test 3 ok!");
	/* TEST 4 - All subs, sub1 with extra weight. */
		SubConNeighMovCondition cond4 = new SubConNeighMovCondition();
		cond4.subMap = new HashMap<String, Float>();
		cond4.index = messageIndex;
		cond4.subMap.put("sub1", 1.0f);
		cond4.subMap.put("sub2", 0.1f);
		cond4.subMap.put("sub3", 0.1f);
		/* Perform test. */
		int result4[] = performTest(cond4, cell);
		int preferredNeighbor4 = getPreferredNeighbor(result4) - 1;
		/* Preferred neighbor should be -1. */
		assertEquals(preferredNeighbor4, -1);
		//System.out.println("Test 4 ok!");
	/* TEST 5 - All subs with similar (not equal) weight, chemopower = 1. */
		SubConNeighMovCondition cond5 = new SubConNeighMovCondition();
		cond5.subMap = new HashMap<String, Float>();
		cond5.index = messageIndex;
		cond5.subMap.put("sub1", 0.08f);
		cond5.subMap.put("sub2", 0.10f);
		cond5.subMap.put("sub3", 0.09f);
		cond5.chemopower = 1.0f;
		/* Perform test. Neighbor -1 must be chosen always. */
		int result5[] = performTest(cond5, cell);
		//int preferredNeighbor5 = getPreferredNeighbor(result5) - 1;
		for (int i = 0; i < result5.length; i++)
			if (i != 0)
				assertEquals(result5[i], 0);
		assertEquals(result5[0], testTimes);
		//System.out.println("Test 5 ok!");
	/* TEST 6 - chemopower = 0, no substances present. */
		SubConNeighMovCondition cond6 = new SubConNeighMovCondition();
		MockCell cellEmpty = new MockCell(numSidesToTest);
		cellEmpty.initNeighbors();
		cond6.subMap = new HashMap<String, Float>();
		cond6.index = messageIndex;
		cond6.subMap.put("sub1", 0.01f);
		cond6.subMap.put("sub2", 1.00f);
		cond6.chemopower = 0.0f;
		/* Perform test. */
		int result6[] = performTest(cond6, cellEmpty);
		getPreferredNeighbor(result6);
		coiso = testTimes / (numSidesToTest + 1);
		/* All cells with substances should have been selected a similar number of times. */
		for (int i = 0; i < result6.length; i++)
			assertEquals(result6[i], coiso, testTimes / 50);
		//System.out.println("Test 6 ok!");
	/* TEST 7 - chemopower = 1, same maxPower in two neighbors. */
		SubConNeighMovCondition cond7 = new SubConNeighMovCondition();
		MockCell cellNew = new MockCell(numSidesToTest);
		cellNew.initNeighbors();
		cellNew.subConMap.put(agent.subMap.get("sub1"), 1.0f);
		((MockCell) cellNew.neighbors.get(0)).subConMap.put(agent.subMap.get("sub1"), 1.0f);
		cond7.subMap = new HashMap<String, Float>();
		cond7.index = messageIndex;
		cond7.subMap.put("sub1", 1.0f);
		cond7.chemopower = 1.0f;
		/* Perform test. */
		int result7[] = performTest(cond7, cellNew);
		getPreferredNeighbor(result7);
		coiso = testTimes / 2;
		/* All cells with substances should have been selected a similar number of times. */
		assertEquals(result7[0], coiso, testTimes / 50);
		assertEquals(result7[1], coiso, testTimes / 50);
		//System.out.println("Test 7 ok!");
	}

	private int[] performTest(SubConNeighMovCondition cond, Cell2D currCell) {
		Object message[] = new Object[5];
		int testArray[] = new int[numSidesToTest + 1];
		Arrays.fill(testArray, 0);
		for (int i = 0; i < this.testTimes; i++) {
			int inc = 0;
			try {
				if (!cond.evaluate(agent, currCell, message)) {
					inc = 0;
				} else {
					inc = ((Integer) message[messageIndex]) + 1;
				}
			} catch (ConditionException e) {
				fail(e.getMessage());
			}
			testArray[inc]++;
		}
		return testArray;
	}
	
	private int getPreferredNeighbor(int result[]) {
		//System.out.println(Arrays.toString(result));
		int preferred = 0;
		int max = 0;
		for (int i = 0; i < result.length; i++) {
			if (result[i] > max) {
				max = result[i];
				preferred = i;
			}
		}
		return preferred;
	}

}
