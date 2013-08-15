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
