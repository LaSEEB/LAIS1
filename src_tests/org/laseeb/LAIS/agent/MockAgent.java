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
