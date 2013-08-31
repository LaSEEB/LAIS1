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


package org.laseeb.LAIS.utils.random;

/**
 * Clients which use a random number generator must implement this interface.
 * 
 * @author Nuno Fachada
 *
 */
public interface RngClient {
	
	/**
	 * Receives the global random seed, and returns a new seed (deterministically
	 * obtained) based on client properties. This is required in order for all
	 * users of random number generators (rngs) to have rngs with different
	 * seeds; in order to repeat experiences with the same global seed, the
	 * client seeds must be deterministically obtained from the global seed.
	 *  
	 * @param seed The global random number generator seed.
	 * @return The client specific random number generator seed.
	 */
	public long reSeed(long seed);
	
	/**
	 * Returns a unique and deterministic ID based on client properties.
	 * This value will be used as a unique agent identifier.
	 * 
	 * @return A unique and deterministic ID based on client properties.
	 */
	public int getUniqueID();

}
