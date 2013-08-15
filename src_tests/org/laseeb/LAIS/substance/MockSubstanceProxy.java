package org.laseeb.LAIS.substance;

import org.laseeb.LAIS.agent.Agent;

/* Mock substance proxy. */
public class MockSubstanceProxy extends SubstanceProxy {
	private Substance sub;
	public MockSubstanceProxy(Substance sub) {
		this.sub = sub;
	}
	public Substance getSubstance(Agent agent) {
		return sub;
	}
}
