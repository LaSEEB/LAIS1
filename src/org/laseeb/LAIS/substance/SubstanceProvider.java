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