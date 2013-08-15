package org.laseeb.LAIS.event;

import org.laseeb.LAIS.LAISModel;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import uchicago.src.sim.engine.Schedule;

/**
 * Perform simulation event at a specific simulation tick.
 * @see org.laseeb.LAIS.event.ScriptingType
 * @author Nuno Fachada
 */
@Root(name="AgentDeploy")
public class ScheduleAtTick implements ScriptingType {

	/** 
	 * <strong>XML Element (integer &gt;= 0)</strong>
	 * <p>
	 * Simulation tick when to perform event.
	 * <p>
	 * <em>REQUIRED: YES.</em>
	 * */
	@Element
	int tick;

	/** 
	 * Schedules simulation event at a specific simulation tick.
	 * @param model The model object, which contains the necessary objects to perform event scheduling.
	 * @param event The event to schedule.
	 * @see org.laseeb.LAIS.event.ScriptingType#scriptEvent(LAISModel, Event)
	 */
	public void scriptEvent(LAISModel model, Event event) {
		model.getSchedule().scheduleActionAt(tick, event, Schedule.LAST);
	}

}
