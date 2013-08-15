package org.laseeb.LAIS.space;

import org.laseeb.LAIS.LAISModel;
import org.laseeb.LAIS.agent.AgentManager;
import org.laseeb.LAIS.substance.SubstanceManager;
import org.simpleframework.xml.Element;

import uchicago.src.sim.gui.Display2D;
import uchicago.src.sim.gui.Object2DHexaDisplay;

/**
 * A space factory which provides objects for an hexagonal simulation space.
 * 
 * @author Nuno Fachada
 */
public class HexSpaceFactory extends Abstract2DSpaceFactory {

	/** 
	 * <b>XML Element (boolean)</b>
	 * <p>
	 * Set this element to <code>true</code> if you want a toroidal simulation space.
	 * Default value is <code>false</code>.
	 * <p> 
	 * <em>REQUIRED: NO</em> 
	 * */
	@Element(required=false)
	boolean torus = false;

	/**
	 * Creates a cell for the hexagonal simulation space.
	 * 
	 * @see Abstract2DSpaceFactory#createCell(int, int, Abstract2DSpaceAdapter, SubstanceManager, AgentManager, LAISModel)
	 */
	@Override
	public Cell2D createCell(int numX, int numY, Abstract2DSpaceAdapter space,
			SubstanceManager substanceMan, AgentManager agentMan,
			LAISModel laisModel) {
		return new Cell2D(numX, numY, space, substanceMan, agentMan, laisModel);
	}

	/**
	 * Creates a display for the hexagonal simulation space.
	 * 
	 * @see Abstract2DSpaceFactory#createDisplay(Abstract2DSpaceAdapter)
	 */
	@Override
	public Display2D createDisplay(Abstract2DSpaceAdapter space) {
		Object2DHexaDisplay disp = new Object2DHexaDisplay(space.getRepastSpace());
		disp.setFramed(false);
		//disp.setFrameColor(Color.BLACK);
		return disp;
	}

	/**
	 * Creates an hexagonal simulation space.
	 * 
	 * @see Abstract2DSpaceFactory#createSpace()
	 */
	@Override
	public Abstract2DSpaceAdapter createSpace() {
		return new HexSpaceAdapter(x, y, torus);
	}

}
