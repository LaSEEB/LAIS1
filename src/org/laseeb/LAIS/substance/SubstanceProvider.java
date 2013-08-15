package org.laseeb.LAIS.substance;

import java.util.Iterator;

/**
 * Interface shared by classes which provide substance information.
 * 
 * @author Nuno Fachada
 *
 */
public interface SubstanceProvider {
	
	/**
	 * Return substance by name.
	 * 
	 * @param name Name of substance.
	 * @return Substance with given name.
	 * @throws SubstanceException If there is no substance with the given name.
	 */
	public Substance getSubstanceByName(String name) throws SubstanceException;
	
	/**
	 * Returns an iterator which cycles through the substances which the provider provides.
	 * 
	 * @return An iterator which cycles through the substances which the provider provides.
	 */
	public Iterator<Substance> substanceIterator();

}