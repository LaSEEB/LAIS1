package org.laseeb.LAIS.space;

import java.util.HashMap;
import java.util.Vector;

import org.laseeb.LAIS.substance.Substance;
import org.laseeb.LAIS.utils.random.RngManager;

/* Mock cell. */
public class MockCell extends Cell2D {
	public HashMap<Substance, Float> subConMap;
	public Vector<Cell2D> neighbors;
	int numSidesToTest;
	public MockCell(int numSidesToTest) {
		super(0, 0, null, null, null, null);
		subConMap = new HashMap<Substance, Float>();
		this.numSidesToTest = numSidesToTest;
	}
	public void initNeighbors() {
		neighbors = new Vector<Cell2D>(numSidesToTest);
		for (int i = 0; i < numSidesToTest; i++) {
			neighbors.add(i, new MockCell(numSidesToTest));
		}
	}
	public int getDistanceBetweenNeighbors(Cell2D cell1, Cell2D cell2) throws CellStepException {
		int direction;
		int indexCell1 = neighbors.indexOf(cell1);
		if (indexCell1 == -1)
			throw new CellStepException("Given cell (" + cell1.x + ", " + cell1.y 
					+ ") is not a neighbor of current cell (" + x + ", "+ y + ").");
		int indexCell2 = neighbors.indexOf(cell2);
		if (indexCell2 == -1)
			throw new CellStepException("Given cell (" + cell2.x + ", " + cell2.y 
					+ ") is not a neighbor of current cell (" + x + ", "+ y + ").");
			
		int distance = indexCell1 - indexCell2;
		direction = (int) Math.signum(distance);
		distance = Math.abs(distance);
		if (distance > neighbors.size()/2) distance = Math.abs(distance - neighbors.size()); 
		return direction * distance;
	}			
	public Cell2D getNeighbor(Cell2D cell, int proximity) throws CellStepException {
		/* Get number of neighbors. */
		int numNeighbors = neighbors.size();
		/* Get given cell location. */
		int indexGivenCell = neighbors.indexOf(cell);
		/* If given cell is not a valid neighbor, return null. */
		if (indexGivenCell == -1)
			throw new CellStepException("Given cell (" + cell.x + ", " + cell.y 
					+ ") is not a neighbor of current cell (" + x + ", "+ y + ").");
		/* Determine index of required neighbor cell. */
		int indexRequestedCell = normalizeNeighborIndex(indexGivenCell + proximity, numNeighbors);
		/* Return required cell. */
		return neighbors.get(indexRequestedCell);
	}
	public int getNumNeighbors() {return numSidesToTest;}
	public Cell2D getOppositeCell(Cell2D cell) throws CellStepException {
		/* Get distance between given cell and opposite. */
		int distance = numSidesToTest / 2;
		/* If number of neighbors is not pair, then there is no exact opposite neighbor. 
		 * Choose one randomly. */
		if (numSidesToTest % 2 != 0) {
			distance += RngManager.getInstance().getRng(this).nextIntFromTo(0, 1);
		}
		/* Determine non-null opposite cell. */
		Cell2D oppNeigh = null;
		do {
			oppNeigh = getNeighbor(cell, distance);
			if (oppNeigh == null) {
				distance++;
				distance = normalizeNeighborIndex(distance, numSidesToTest);
			}
		} while (oppNeigh == null);
		/* Return opposite cell. */
		return oppNeigh;
	}
	protected void stepSubstanceDiffEvap() {}
	public Vector<Cell2D> getNeighbors() {return neighbors;}
	public float getSubstanceCon(Substance sub) {
		if (subConMap.containsKey(sub)) {return subConMap.get(sub);} 
		else {return 0.0f;}
	}
	public boolean containsSubstance(Substance sub) {
		return subConMap.containsKey(sub);
	}
	
}
