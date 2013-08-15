package org.laseeb.LAIS.utils.random;


import org.apache.log4j.Logger;

import cern.jet.random.Uniform;
import cern.jet.random.engine.MersenneTwister;

/**
 * Adapter for the Colt library implementation of a uniform distribution random number generator.
 * 
 * @author Nuno Fachada
 *
 */
public class RngColtAdapter implements IRng {
	
	/* Logger. */
	private static Logger logger = Logger.getLogger(RngColtAdapter.class);
	
	/* Random number generator. */
	private Uniform rng;
	
	/**
	 * Creates a new uniform distribution random number generator using the Colt library.
	 * @param seed
	 */
	public RngColtAdapter(long seed) {
		rng = new Uniform(new MersenneTwister((int) seed));
	}

	/**
	 * @see org.laseeb.LAIS.utils.random.IRng#nextBoolean()
	 */
	@Override
	public boolean nextBoolean() {
		boolean bool = rng.nextBoolean();
		if (logger.isDebugEnabled()) debug(bool);
		return bool;
	}

	/**
	 * @see org.laseeb.LAIS.utils.random.IRng#nextDoubleFromTo()
	 */
	@Override
	public double nextDoubleFromTo(double from, double to) {
		double doub = rng.nextDoubleFromTo(from, to);
		if (logger.isDebugEnabled()) debug(doub);
		return doub;
	}

	/**
	 * @see org.laseeb.LAIS.utils.random.IRng#nextFloatFromTo()
	 */
	@Override
	public float nextFloatFromTo(float from, float to) {
		float flo = rng.nextFloatFromTo(from, to);
		if (logger.isDebugEnabled()) debug(flo);
		return flo;
	}

	/**
	 * @see org.laseeb.LAIS.utils.random.IRng#nextIntFromTo()
	 */
	@Override
	public int nextIntFromTo(int from, int to) {
		int i = rng.nextIntFromTo(from, to);
		if (logger.isDebugEnabled()) debug(i);
		return i;
	}

	/**
	 * @see org.laseeb.LAIS.utils.random.IRng#nextLongFromTo()
	 */
	@Override
	public long nextLongFromTo(long from, long to) {
		long lo = rng.nextLongFromTo(from, to);
		if (logger.isDebugEnabled()) debug(lo);
		return lo;
	}
	
	/* Debug method. */
	private void debug(Object obj) {
		StackTraceElement[] steArray = Thread.currentThread().getStackTrace();
		logger.debug("************ '" + steArray[2].getMethodName() + "' requested from " + steArray[3].getClassName() + ", returned value: '" + obj + "'");
		
	}

}
