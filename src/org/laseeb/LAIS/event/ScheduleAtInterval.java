package org.laseeb.LAIS.event;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.laseeb.LAIS.LAISModel;

import uchicago.src.sim.engine.Schedule;

/**
 * Perform event at regular intervals.
 * @see org.laseeb.LAIS.event.ScriptingType
 * @author Nuno Fachada
 */
@Root(name="ScriptingType")
public class ScheduleAtInterval implements ScriptingType {
	
	/** 
	 * <strong>XML Element (integer &gt; 0)</strong>
	 * <p>
	 * The deployment interval.
	 * <p>
	 * <em>REQUIRED: YES.</em>
	 * */
	@Element
	int interval;
	
	/** 
	 * Schedules agent deployment at regular intervals. 
	 * @param model The model object, which contains the necessary objects to perform event scheduling.
	 * @param event The event to schedule.
	 * @see org.laseeb.LAIS.event.ScriptingType#scriptEvent(LAISModel, Event)
	 */
	public void scriptEvent(LAISModel model, Event event) {
		model.getSchedule().scheduleActionAtInterval(interval, event, Schedule.LAST);
	}
}
