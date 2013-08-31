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
 * Interface for uniform distribution adapters.
 * 
 * @author Nuno Fachada
 */
public interface IRng {
	
	/**
	 * Returns a uniformly distributed random <code>boolean</code>.
	 * @return A uniformly distributed random <code>boolean</code>.
	 */
	public boolean nextBoolean();
	
	/**
	 * Returns a uniformly distributed random number in the open interval 
	 * <code>(from,to)</code> (excluding <code>from</code> and <code>to</code>). 
	 * Pre conditions: <code>from &lt;= to</code>. 
	 * @param from Minimum value.
	 * @param to Maximum value.
	 * @return A uniformly distributed random number in the open interval 
	 * <code>(from,to)</code> (excluding <code>from</code> and <code>to</code>).
	 */
	public int nextIntFromTo(int from, int to);

	/**
	 * Returns a uniformly distributed random number in the open interval 
	 * <code>(from,to)</code> (excluding <code>from</code> and <code>to</code>). 
	 * Pre conditions: <code>from &lt;= to</code>. 
	 * @param from Minimum value.
	 * @param to Maximum value.
	 * @return A uniformly distributed random number in the open interval 
	 * <code>(from,to)</code> (excluding <code>from</code> and <code>to</code>).
	 */
	public long nextLongFromTo(long from, long to);

	/**
	 * Returns a uniformly distributed random number in the open interval 
	 * <code>(from,to)</code> (excluding <code>from</code> and <code>to</code>). 
	 * Pre conditions: <code>from &lt;= to</code>. 
	 * @param from Minimum value.
	 * @param to Maximum value.
	 * @return A uniformly distributed random number in the open interval 
	 * <code>(from,to)</code> (excluding <code>from</code> and <code>to</code>).
	 */	
	public float nextFloatFromTo(float from, float to);

	/**
	 * Returns a uniformly distributed random number in the open interval 
	 * <code>(from,to)</code> (excluding <code>from</code> and <code>to</code>). 
	 * Pre conditions: <code>from &lt;= to</code>. 
	 * @param from Minimum value.
	 * @param to Maximum value.
	 * @return A uniformly distributed random number in the open interval 
	 * <code>(from,to)</code> (excluding <code>from</code> and <code>to</code>).
	 */
	public double nextDoubleFromTo(double from, double to);
	
}
