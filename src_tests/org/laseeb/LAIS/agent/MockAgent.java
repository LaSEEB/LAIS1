package org.laseeb.LAIS.agent;

import java.util.HashMap;

import org.laseeb.LAIS.substance.MockSubstanceProxy;
import org.laseeb.LAIS.substance.Substance;
import org.laseeb.LAIS.substance.SubstanceProxy;

/* Mock agent class. */
public class MockAgent extends Agent {
	public MockAgent(int id) {
		super(id);
		// TODO Auto-generated constructor stub
	}
	/* Substance map. */
	public HashMap<String, Substance> subMap = new HashMap<String, Substance>();
	public SubstanceProxy getSubstanceByRef(String subRef) {
		return new MockSubstanceProxy(subMap.get(subRef));
	}
	public String getName() {
		return "MockAgent";
	}
}
