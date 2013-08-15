package org.laseeb.LAIS.space;

import java.util.Vector;

import uchicago.src.sim.space.Discrete2DSpace;
import uchicago.src.sim.space.Object2DGrid;

/**
 * Simulation spaces should extend this abstract class, which is an adapter for
 * Repast {@link uchicago.src.sim.space.Object2DGrid} type objects (which it
 * encapsulates), translating LAIS  requests to 
 * {@link uchicago.src.sim.space.Object2DGrid} specific requests.
 * <p>
 * If the user wants to create a space which is not provided within Repast
 * {@link uchicago.src.sim.space.Object2DGrid} implementations, it is possible
 * to directly write the new space code in a subclass of this class, 
 * ignoring the encapsulated {@link uchicago.src.sim.space.Object2DGrid} object.
 * 
 * @author Nuno Fachada
 *
 */
public abstract class Abstract2DSpaceAdapter {
	
	/**
	 * The encapsulated Repast {@link uchicago.src.sim.space.Object2DGrid} space.
	 */
	protected Object2DGrid space;
	
	/**
	 * Returns a {@link java.util.Vector} containing the cell neighbors of 
	 * the given cell location. The vector will only contain the available
	 * neighbors, it will never contain <code>null</code>s.
	 * 
	 * @param x X position of the given cell.
	 * @param y Y position of the given cell.
	 * @return Cell neighbors of the given cell location.
	 */
	public abstract Vector<Cell2D> getNeighbors(int x, int y);
	
	/**
	 * Returns the encapsulated Repast space.
	 * 
	 * @return The encapsulated Repast space.
	 */
	Discrete2DSpace getRepastSpace() {
		return space;
	}

	/**
	 * Return the horizontal dimension of the simulation space.
	 * 
	 * @return The horizontal dimension of the simulation space.
	 */
	public int getSizeX() {
		return space.getSizeX();
	}

	/**
	 * Return the vertical dimension of the simulation space.
	 * 
	 * @return The vertical dimension of the simulation space.
	 */
	public int getSizeY() {
		return space.getSizeY();
	}
	
	/**
	 * Place a given cell in the given spatial location.
	 * 
	 * @param x Horizontal location where to place the given cell.
	 * @param y Vertical location where to place the given cell.
	 * @param cell The cell to place in the given spatial location.
	 */
	public void putCell2DAt(int x, int y, Cell2D cell) {
		space.putObjectAt(x, y, cell);
	}
	
	/**
	 * Return the cell placed in the given spatial location.
	 * @param x Horizontal location where to return the cell from.
	 * @param y Vertical location where to return the cell from.
	 * @return The cell placed in the given spatial location.
	 */
	public Cell2D getCell2DAt(int x, int y) {
//		try {
			return (Cell2D) space.getObjectAt(x, y);
//		} catch (Exception e) {
//			System.out.println("(" + x + "," + y + ")");
//			System.exit(-1);
//		}
//		return null;
	}
	
}
