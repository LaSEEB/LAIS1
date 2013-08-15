package org.laseeb.LAIS.gui;

import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.event.MouseInputAdapter;

import org.laseeb.LAIS.space.Cell2D;

import uchicago.src.sim.engine.SimModel;
import uchicago.src.sim.gui.DisplaySurface;
import uchicago.src.sim.gui.Probeable;

/**
 * This class replaces Repast's {@link uchicago.src.sim.gui.DisplaySurface} class in
 * order perform a different treatment of mouse events, namely for probing purposes.
 * 
 * @author Nuno Fachada
 */
@SuppressWarnings("serial")
public class LAISDisplaySurface extends DisplaySurface {
	
	/* Our very own mouse event adapter. */
	private MouseInputAdapter ldsMouseAdapter = new MouseInputAdapter() {

		@SuppressWarnings("unchecked")
		public void mouseClicked(MouseEvent evt) {
			int x = evt.getX();
			int y = evt.getY();

			for (int i = 0; i < probeables.size(); i++) {
				Probeable p = (Probeable) probeables.get(i);
				ArrayList list = p.getObjectsAt(x, y);
				for (int j = 0; j < list.size(); j++) {
					Object o = list.get(j);
					if (o != null) {
						Cell2D cell = (Cell2D) o;
						new SubstanceProbe(cell.getSubConMap(), 
								"Substances in cell (" 
								+ cell.getX() + "," + cell.getY() + ")"
								+ " at tick " + model.getTickCount());
						new AgentProbe(cell.getAgents(), 
								"Agents in cell (" 
								+ cell.getX() + "," + cell.getY() + ")"
								+ " at tick " + model.getTickCount());
					}
				}
			}
		}
	};
	
	/**
	 * Creates a specialized display surface for LAIS. This display surface extends
	 * Repast's {@link uchicago.src.sim.gui.DisplaySurface}, replacing the mouse event
	 * handler in order to perform our very own custom probing with the 
	 * {@link org.laseeb.LAIS.gui.SubstanceProbe} singleton class.
	 * 
	 * @param model The LAISModel instance.
	 * @param name The name of the display surface.
	 */
	public LAISDisplaySurface(SimModel model, String name) {
		super(model, name);
		removeMouseListener(dsMouseAdapter);
		removeMouseMotionListener(dsMouseAdapter);
		addMouseListener(ldsMouseAdapter);
		addMouseMotionListener(ldsMouseAdapter);
		
	}
}