package org.laseeb.LAIS.space;

import java.util.Vector;

import uchicago.src.sim.space.Object2DHexagonalGrid;
import uchicago.src.sim.space.Object2DHexagonalTorus;

/**
 * Provides an hexagonal simulation space.
 * Encapsulates a Repast {@link uchicago.src.sim.space.Object2DHexagonalGrid} object
 * to provide the required functionality. If the space is to be toroidal,
 * the encapsulated Repast space object will in fact be a 
 * {@link uchicago.src.sim.space.Object2DHexagonalTorus}, which extends
 * {@link uchicago.src.sim.space.Object2DHexagonalGrid}).
 * 
 * @author Nuno Fachada
 */
public class HexSpaceAdapter extends Abstract2DSpaceAdapter {

	/**
	 * Public constructor for the hexagonal space adapter. 
	 * Parameters determine the simulation space dimension and
	 * whether the space will be toroidal or not.
	 * 
	 * @param x Horizontal dimension of the space.
	 * @param y Vertical dimension of the space.
	 * @param torus If true, simulation space will be toroidal.
	 */
	public HexSpaceAdapter(int x, int y, boolean torus) {
		if (torus)
			this.space = new Object2DHexagonalTorus(x, y);
		else
			this.space = new Object2DHexagonalGrid(x, y);
	}
	
	/**
	 * Returns the neighbor cells of the give cell. In this case, the neighbors 
	 * consist of the six adjacent cells.
	 * 
	 * @param x The horizontal location of the given cell.
	 * @param y The vertical location of the given cell.
	 * @return A vector of {@link Cell2D} cells, neighbors of the given cell.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Vector<Cell2D> getNeighbors(int x, int y) {
		return ((Object2DHexagonalGrid) space).getNeighbors(x, y, false);
	}

}
