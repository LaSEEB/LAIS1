package org.laseeb.LAIS.space;

import java.util.Vector;

import org.laseeb.LAIS.utils.RepastAdapter;

/**
 * Implementation of a square simulation space, where each cell has
 * eight neighbors (a Moore neighborhood). 
 *  
 * @author Nuno Fachada
 */
public class SquareMooreSpaceAdapter extends SquareSpaceAdapter {

	/**
	 * Public constructor which calls the super ({@link SquareSpaceAdapter}) 
	 * constructor. Parameters determine the simulation space dimension and
	 * whether the space will be toroidal or not.
	 * 
	 * @param x Horizontal dimension of the space.
	 * @param y Vertical dimension of the space.
	 * @param torus If true, simulation space will be toroidal.
	 */
	public SquareMooreSpaceAdapter(int x, int y, boolean torus) {
		super(x, y, torus);
	}
	
	/**
	 * Return the Moore neighborhood of the given cell location.
	 * @param x Horizontal location of the cell to get neighborhood of.
	 * @param y Vertical location of the cell to get neighborhood of.
	 * @return A {@link java.util.Vector} containing the Moore neighborhood 
	 * of the given cell location.
	 * @see org.laseeb.LAIS.utils.RepastAdapter#getMooreNeighbors(int, int, boolean, uchicago.src.sim.space.Object2DGrid)
	 */
	@Override
	public Vector<Cell2D> getNeighbors(int x, int y) {		
		return RepastAdapter.getMooreNeighbors(x, y, torus, space);
	}

}
