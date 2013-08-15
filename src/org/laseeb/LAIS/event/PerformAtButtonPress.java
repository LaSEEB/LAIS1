package org.laseeb.LAIS.event;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.laseeb.LAIS.LAISModel;
import org.laseeb.LAIS.utils.RepastAdapter;

import org.simpleframework.xml.Element;


/**
 * Perform simulation event at a button press.
 * @see org.laseeb.LAIS.event.ScriptingType
 * @author Nuno Fachada
 */
public class PerformAtButtonPress implements ScriptingType {
	
	/** 
	 * <strong>XML Element ({@link java.lang.String})</strong>
	 * <p>
	 * String to appear on the button. 
	 * <p>
	 * <em>REQUIRED: YES.</em>
	 * */
	@Element
	String button;	
	
	/** 
	 * <strong>XML Element ({@link org.laseeb.LAIS.event.Event})</strong>
	 * <p>
	 * {@link org.laseeb.LAIS.event.Event} to perform at button press. 
	 * <p>
	 * <em>REQUIRED: YES.</em>
	 * */
	Event myEvent;
	
	/**
	 * This method schedules an event which will occur when a button is pressed.
	 * @param model The model object, which contains the necessary objects to perform event scheduling.
	 * @param event The event to schedule.
	 * @see org.laseeb.LAIS.event.ScriptingType#scriptEvent(LAISModel, Event)
	 */
	public void scriptEvent(LAISModel model, Event event) {
		this.myEvent = event;
		/*If model is running in batch mode inform it's not possible to add a button. */
		if (model.getController().isBatch()) {
			System.err.println("Model is running in batch mode, " +
					"so it's not possible to add a '" + button + "' button!");
			return;
		}
		/* Create an action listener for the event. */
		ActionListener awtEvent = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				myEvent.execute();
			}
		};
		/* Add button. */
		RepastAdapter.addCustomButtonEvent(button, awtEvent, model.getModelManipulator().getPanel());
	}

}
