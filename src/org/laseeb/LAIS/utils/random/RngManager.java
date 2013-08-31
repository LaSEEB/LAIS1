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

import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

/**
 * Manages the use of random number generators for clients implementing 
 * {@link RngClient}. In fact it implements {@link RngClient}, thus being a 
 * client of itself, in order to serve has a random number generator for
 * global serial LAIS code.
 *  
 * @author Nuno Fachada
 *
 */
public class RngManager implements RngClient {
	
	/* Logger. */
	private static Logger logger = Logger.getLogger(RngManager.class);
	/* Map of uniform distributions. */
	private ConcurrentHashMap<RngClient, IRng> rngMap = 
		new ConcurrentHashMap<RngClient, IRng>();
	/* For statistical and debugging purposes. */
	private static ConcurrentHashMap<RngClient, Integer> stats;
	
	static {
		if (logger.isDebugEnabled())
			stats = new ConcurrentHashMap<RngClient, Integer>();
	}
	
	/* Random seed. */
	private Long seed = null;
	/* Random number generator factory. */
	private IRngFactory rngFactory = null;
	/* Number of agents produced (required for unique ID). */
	private long agentsProduced = 0;
	
	/* Single instance. */
	private static RngManager instance = null;
	
	/* Private constructor, required in order for this class to be a singleton. */
	private RngManager() {}
	
	/**
	 * Get the single instance of the random number manager.
	 * 
	 * @return The single instance of the random number manager.
	 */
	public static RngManager getInstance() {
		if (instance == null) {
			instance = new RngManager();
		}
		return instance;
	}

	/**
	 * Gets the random number generator associated with the given client object. If it 
	 * doesn't exist, one is created.
	 * 
	 * @param client A client object, which has (or will have) an exclusive associated
	 * random number generator. 
	 * @return The random number generator associated with the given client object.
	 */
	public IRng getRng(RngClient client) {
		/* If no client is given, then assume myself as client. */
		if (client == null)
			client = this;
		/* Check if client already has an associated rng. */
		if (!rngMap.containsKey(client)) {
			/* Obtain effective seed. */
			long effectiveSeed = client.reSeed(seed);
			/* Map does not contain distribution, create it! */
			synchronized (seed) {
				rngMap.put(client, rngFactory.createRng(effectiveSeed));
			}
			if (logger.isDebugEnabled()) {
				stats.put(client, 1);
				logger.debug("New client of class '" + client.getClass().getSimpleName() + "' with seed=" + effectiveSeed + "; total clients: " + rngMap.size() + ".");
			}
			
		}
		if (logger.isDebugEnabled()) {
			String extraInfo = "";
			stats.put(client, stats.get(client) + 1);
			StackTraceElement[] steArray = Thread.currentThread().getStackTrace();
			if (client instanceof org.laseeb.LAIS.space.Cell2D)
				extraInfo = "(" + ((org.laseeb.LAIS.space.Cell2D) client).getX() + "," + ((org.laseeb.LAIS.space.Cell2D) client).getY() + ")";
			logger.debug("Request from '" + client.getClass().getSimpleName() + "' " + extraInfo + ", now with " + stats.get(client) + " requests.");
		}
		return rngMap.get(client);			
	}

	/**
	 * Clears all distributions.
	 */
	public void clear() {
		rngMap.clear();
	}

	/**
	 * Sets the random seed. This method should only be called from LAIS model.
	 * 
	 * @param newSeed Random seed to use in rng created from now on.
	 */
	public void setSeed(Long newSeed) {
		if (newSeed == null)
			this.seed = System.nanoTime();
		else
			this.seed = newSeed;
	}
	
	/**
	 * Sets the rng factory. This method should only be called from LAIS model.
	 * 
	 * @param rngFactory Rng factory to be used from now on.
	 */
	public void setRngFactory(IRngFactory rngFactory) {
		this.rngFactory = rngFactory;
	}

	/**
	 * The seed will not be modified in this case.
	 *  
	 * @param seed The global random number generator seed.
	 * @return The client specific random number generator seed.
	 * @see org.laseeb.LAIS.utils.random.RngClient#reSeed(long)
	 */
	public long reSeed(long seed) {
		return seed;
	}

	/**
	 * Returns a unique and deterministic ID based on client properties.
	 * This value will be used as a unique agent identifier.
	 * 
	 * @return A unique and deterministic ID based on client properties.
	 */
	@Override
	public int getUniqueID() {
		agentsProduced++;
		return (this.getClass().getSimpleName() + agentsProduced).hashCode();
	}

}
