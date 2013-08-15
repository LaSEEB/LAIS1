package org.laseeb.LAIS.utils.random;

public class RngJavaFactory implements IRngFactory {

	@Override
	public IRng createRng(long seed) {
		return new RngJavaAdapter(seed);
	}

}
