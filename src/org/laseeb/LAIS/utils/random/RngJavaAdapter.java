package org.laseeb.LAIS.utils.random;

import java.util.Random;

/**
 * Adapter for the Java implementation of a uniform distribution random number generator.
 * 
 * @author Nuno Fachada
 *
 */
public class RngJavaAdapter implements IRng {

	private Random rng;
	
	/**
	 * Creates a new uniform distribution random number generator using the Colt library.
	 * @param seed
	 */
	public RngJavaAdapter(long seed) {
		rng = new Random(seed);
	}

	/**
	 * @see org.laseeb.LAIS.utils.random.IRng#nextBoolean()
	 */
	@Override
	public boolean nextBoolean() {
		return rng.nextBoolean();
	}

	/**
	 * @see org.laseeb.LAIS.utils.random.IRng#nextDoubleFromTo(double, double)
	 */
	@Override
	public double nextDoubleFromTo(double from, double to) {
		double randomValue = (to - from) * rng.nextDouble();
		randomValue = from - randomValue;
		return randomValue;
	}

	/**
	 * @see org.laseeb.LAIS.utils.random.IRng#nextFloatFromTo(float, float)
	 */
	@Override
	public float nextFloatFromTo(float from, float to) {
		float randomValue = (to - from) * rng.nextFloat();
		randomValue = from - randomValue;
		return randomValue;
	}

	/**
	 * @see org.laseeb.LAIS.utils.random.IRng#nextIntFromTo(int, int)
	 */
	@Override
	public int nextIntFromTo(int from, int to) {
		int diff = to - from;
		int randomValue = rng.nextInt(diff) - diff / 2;
		return randomValue;
	}

	/**
	 * @see org.laseeb.LAIS.utils.random.IRng#nextLongFromTo(long, long)
	 */
	@Override
	public long nextLongFromTo(long from, long to) {
		//double proportion = (((double) Long.MAX_VALUE) - ((double) Long.MIN_VALUE)) / ((double) (to - from));
		//long randomValue = rng.nextLong();
		throw new RuntimeException("Unfinished method...");
		//return 0;
	}

}
