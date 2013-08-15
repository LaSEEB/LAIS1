package org.laseeb.LAIS.event;

import org.simpleframework.xml.Root;
import org.laseeb.LAIS.LAISModel;

/**
 * Implementations of this interface are responsible for scripting simulation events.
 *  
 * @author Nuno Fachada
 */
@Root(name="ScriptingType")
public interface ScriptingType {

	/**
	 * Scripts the event accordingly to the concrete class.
	 * @param model The model object, which contains the necessary objects to perform event scripting.
	 * @param event The event to script.
	 */
	public void scriptEvent(LAISModel model, Event event);
	
}
