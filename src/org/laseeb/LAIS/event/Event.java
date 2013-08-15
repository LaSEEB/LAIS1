package org.laseeb.LAIS.event;

import org.apache.log4j.Logger;
import org.laseeb.LAIS.LAISModel;
import org.laseeb.LAIS.utils.random.IRng;
import org.laseeb.LAIS.utils.random.RngManager;

import org.simpleframework.xml.Root;

import uchicago.src.sim.engine.BasicAction;

/**
 * This class represents schedulable simulation events in LAIS.
 * It extends repast's BasicAction class, which can be scheduled in the Repast Schedule, which
 * in turn is used for scheduling LAIS events.
 * 
 * @author Nuno Fachada
 *
 */
@Root(name="Event")
public abstract class Event extends BasicAction {

	/* Logger. */
	private static Logger logger = Logger.getLogger(Event.class);

	
	/**
	 * Initializes the event object with whichever information it requires from the model.
	 * @param model The LAISModel, master of all simulation objects.
	 * @throws EventException If an exception occurs while initializing the event.
	 */	
	public abstract void initialize(LAISModel model) throws EventException;
	
	/**
	 * Returns the random number generator associated with scripted events (which
	 * is the global one, for serial LAIS code).
	 * 
	 * @return The random number generator associated with scripted events.
	 */
	public static IRng getEventRng() {
		if (logger.isDebugEnabled()) {
			StackTraceElement[] steArray = Thread.currentThread().getStackTrace();
			logger.debug("EventRng requested by method '" + steArray[2].getMethodName() + "' in class '" + steArray[2].getClassName() + "'");
		}
		return RngManager.getInstance().getRng(null);
	}
	
	/**
	 * Returns a unique and deterministic ID based on client properties.
	 * This value will be used as a unique agent identifier.
	 * 
	 * @return A unique and deterministic ID based on client properties.
	 */
	
	public static int getUniqueID() {
		return RngManager.getInstance().getUniqueID();
	}
}
