package org.laseeb.LAIS.event.subdeploy;

import java.awt.Point;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.laseeb.LAIS.LAISModel;
import org.laseeb.LAIS.event.Event;
import org.laseeb.LAIS.space.Cell2D;
import org.laseeb.LAIS.substance.SubstanceException;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Instances of this class represent substance deployment events. Deployment is performed 
 * having into account concentration and location constrains.
 * <p>
 * Concentration constrains determine the effective quantity of substance to be deployed, 
 * while location constrains determine the spatial location where the substance will be deployed. 
 * 
 * @author Nuno Fachada
 */
@Root
public class SubstanceDeploy extends Event {

	/** 
	 * <strong>XML Element ({@link java.lang.String})</strong>
	 * <p>
	 * The substance to deploy in the simulation.
	 * <p>
	 * <em>REQUIRED: YES</em> 
	 * */
	@Element
	String substance;
	
	/** 
	 * <strong>XML Element ({@link org.laseeb.LAIS.event.subdeploy.ConcentrationConstrain})</strong>
	 * <p>
	 * Substance concentration deployment constrain. If not given, a 
	 * {@link FixedConcentrationConstrain} with default values will be used.
	 * <p>
	 * <em>REQUIRED: NO</em> 
	 * */
	@Element(required=false)
	ConcentrationConstrain concentrationConstrain =
		new FixedConcentrationConstrain();
	
	/** 
	 * <strong>XML Element ({@link org.laseeb.LAIS.event.subdeploy.LocationConstrain})</strong>
	 * <p>
	 * Substance location deployment constrain. If not given, a 
	 * {@link RandomLocationConstrain} with default values will be used.
	 * <p>
	 * <em>REQUIRED: NO</em> 
	 * */
	@Element(required=false)
	LocationConstrain locationConstrain =
		new RandomLocationConstrain();

	/* LAIS model reference. */
	private LAISModel model;

	/* The logger. */
	private static Logger logger = Logger.getLogger(SubstanceDeploy.class);

	/**
	 * Initializes the SubstanceDeploy event object with information from the model. It also 
	 * initializes the respective constrains, which may also require information from the model.
	 * 
	 * @param model The LAISModel from where to extract the required information.
	 * @see org.laseeb.LAIS.event.Event#initialize(LAISModel)
	 */
	public void initialize(LAISModel model) {
		this.model = model;
		this.concentrationConstrain.initialize(model);
		this.locationConstrain.initialize(model);
	}

	/**
	 * Executes deployment event given the deployment constrains.
	 * @see uchicago.src.sim.engine.BasicAction#execute()
	 */
	public void execute() {
		/* Get constrains. */
		float concentration = concentrationConstrain.getConcentration();
		Map<Point, Float> weightMap = locationConstrain.getWeightMap();
		/* Determine full weight of the map. */
		Iterator<Point> pointIter = weightMap.keySet().iterator();
		float total = 0.0f;
		while (pointIter.hasNext()) {
			total += weightMap.get(pointIter.next());
		}
		/* Normalize and perform substance deployment. */
		if (total > 0.0f) {
			pointIter = weightMap.keySet().iterator();
			while (pointIter.hasNext()) {
				Point point = pointIter.next();
				float value = weightMap.get(point) / total;
				Cell2D cell = model.getSpace().getCell2DAt(point.x, point.y);
				/* Perform deployment. */
				try {
					cell.modifySubstanceCon(model.getSubstanceProvider().getSubstanceByName(substance), value * concentration);
				} catch (SubstanceException se) {
					model.getController().stopSim();
					logger.error(se.getMessage());
					return;
				}
			}
		}
	}
}
