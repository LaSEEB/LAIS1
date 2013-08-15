package org.laseeb.LAIS.substance;

import java.util.Iterator;

/**
 * Interface shared by objects which contain substances (e.g., 
 * {@link org.laseeb.LAIS.agent.Agent}, {@link org.laseeb.LAIS.space.Cell2D}).
 * 
 * @author Nuno Fachada
 */
public interface SubstanceContainer {
	
	/**
	 * Returns true if a given substance is present in container.
	 * Returns false otherwise.
	 * 
	 * @param aisSub Substance to check presence of.
	 * @return True if substance is present, false otherwise.
	 */
	public boolean containsSubstance(Substance aisSub);

	/**
	 * Add or remove a quantity of substance.
	 *  
	 * @param sub Substance to add or remove a quantity of.
	 * @param con Quantity of the given substance.
	 * @return Substance concentration after modification. 
	 */
	public float modifySubstanceCon(Substance sub, Float con);

	/** 
	 * Returns an iterator which cycles through the substances present on container.
	 * 
	 * @return An iterator which cycles through the substances present on the container.
	 */
	public Iterator<Substance> substanceIterator();
	
	/**
	 * Returns a given substance's concentration.
	 * @param sub Substance to obtain concentration of.
	 * @return Given substance's concentration.
	 */
	public float getSubstanceCon(Substance sub);

	
}
