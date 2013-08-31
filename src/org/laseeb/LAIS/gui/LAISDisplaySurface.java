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