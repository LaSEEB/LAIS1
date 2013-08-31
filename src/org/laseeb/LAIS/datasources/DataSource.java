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


package org.laseeb.LAIS.datasources;

import java.util.ArrayList;

/**
 * TODO To be totally reviewed.
 * 
 * @author Nuno Fachada
 *
 */
public abstract class DataSource {
	
	private ArrayList<Object> data = new ArrayList<Object>(); 
	
	protected void addObject(Object obj) {
		data.add(obj);
	}
	
	public abstract void initialize() throws Exception;
	
	public Object getDataAtTick(int tick) {
		if (tick >= data.size()) {
			return data.get(tick % data.size());
		} else {
			return data.get(tick);
		}
	}

}
