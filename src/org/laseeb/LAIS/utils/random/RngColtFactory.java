package org.laseeb.LAIS.utils.random;

public class RngColtFactory implements IRngFactory {

	@Override
	public IRng createRng(long seed) {
		return new RngColtAdapter(seed);
	}

}
