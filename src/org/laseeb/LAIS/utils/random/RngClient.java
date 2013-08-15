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
