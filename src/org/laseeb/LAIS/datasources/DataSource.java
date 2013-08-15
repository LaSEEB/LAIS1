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
