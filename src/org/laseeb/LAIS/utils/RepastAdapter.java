package org.laseeb.LAIS.utils;

import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JButton;

import org.laseeb.LAIS.LAISModel;
import org.laseeb.LAIS.space.Cell2D;

import uchicago.src.sim.space.Object2DGrid;

/**
 * This class provides utility methods required to adapt LAISModel code to Repast code.
 * 
 * @author Nuno Fachada
 */
public class RepastAdapter {

	/**
	 * Add a button to the repast custom actions panel.
	 * <p>
	 * This method is required because it's not possible to directly add buttons after 
	 * the {@link LAISModel#setup()} method has been called by the Repast model 
	 * {@link uchicago.src.sim.engine.Controller}.
	 * <p>
	 * As such, this method opens the possibility to add buttons at simulation runtime, or more
	 * specifically, after simulation {@link LAISModel#setup()} has been performed.
	 * 
	 * @param buttonText The text associated with the button.
	 * @param al The action which will be associated with the press of the button.
	 * @param repastWidgetPanel The repast custom action panel.
	 */
	public static void addCustomButtonEvent(String buttonText, 
			ActionListener al, 
			JPanel repastWidgetPanel) {
		/* Create a button. */
		JButton myButton = new JButton(buttonText);
		/* Associate event with button. */
		myButton.addActionListener(al);
		/* Get button panel. */
		JPanel repastButtonPanel = (JPanel) repastWidgetPanel.getComponent(1);
		JPanel repastButtonSubPanel = (JPanel) repastButtonPanel.getComponent(0);
		/* Get number of buttons currently in panel. */
		int numOfButtons = repastButtonSubPanel.getComponentCount();
		/* Create layout constrains according to uchicago.src.sim.engine.ModelManipulator.getPanel(),
		 * but with the necessary changes to accommodate the new button. */
		GridBagConstraints c = new GridBagConstraints ();
		c.gridx = 0;
		c.gridy = numOfButtons;
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.fill = GridBagConstraints.BOTH;
		c.gridheight = 1;
		/* Add button. */
		repastButtonSubPanel.add(myButton, c);
		repastButtonPanel.validate();
	}

	/**
	 * Returns a {@link java.util.Vector} containing {@link org.laseeb.LAIS.space.Cell2D}s
	 * which are Moore neighbors of the cell located at the given (x,y) location. Cell at (x,y)
	 * is not returned.
	 * <p>
	 * Neighbors are given in the following index order:
	 * <p>
	 * 0: N, 1: NE, 2: E, 3: SE, 4: S, 5: SW, 6: W, 7: NW
	 * <p>
	 * This method exists because LAIS architecture requires that spaces return 
	 * cell neighbors sequentially in clockwise order. 
	 * Repast classes {@link uchicago.src.sim.space.MooreNeighborhooder} does not follow 
	 * this rule.   
	 *    
	 * @param x The horizontal location of the cell to get neighbors of.
	 * @param y The vertical location of the cell to get neighbors of.
	 * @param torus True if space is toroidal, false otherwise.
	 * @param space Simulation space.
	 * @return The Moore neighbors of the given cell location in the given space.
	 */
	public static Vector<Cell2D> getMooreNeighbors(int x, int y, boolean torus, Object2DGrid space) {
		/* Initialize neighbor vector. */
		Vector<Cell2D> neighs = new Vector<Cell2D>(8);
		/* Add North. */
		try {
			neighs.add((Cell2D) space.getObjectAt(x, y - 1));
		} catch (IndexOutOfBoundsException ioobe) {
			if (torus) {
				neighs.add((Cell2D) space.getObjectAt(x, space.getSizeY() - 1));
			}
		}
		/* Add Northeast. */
		try {
			neighs.add((Cell2D) space.getObjectAt(x + 1, y - 1));
		} catch (IndexOutOfBoundsException ioobe) {
			if (torus) {
				int effectiveX = x + 1;
				int effectiveY = y - 1;
				if (space.getSizeX() == x + 1)
					effectiveX = 0;
				if (y == 0)
					effectiveY = space.getSizeY() - 1; 
				neighs.add((Cell2D) space.getObjectAt(effectiveX, effectiveY));
			}
		}
		/* Add East. */
		try {
			neighs.add((Cell2D) space.getObjectAt(x + 1, y));
		} catch (IndexOutOfBoundsException ioobe) {
			if (torus) {
				neighs.add((Cell2D) space.getObjectAt(0, y));
			}
		}
		/* Add Southeast. */
		try {
			neighs.add((Cell2D) space.getObjectAt(x + 1, y + 1));
		} catch (IndexOutOfBoundsException ioobe) {
			if (torus) {
				int effectiveX = x + 1;
				int effectiveY = y + 1;
				if (space.getSizeX() == x + 1)
					effectiveX = 0;
				if (space.getSizeY() == y + 1)
					effectiveY = 0;					
				neighs.add((Cell2D) space.getObjectAt(effectiveX, effectiveY));
			}
		}
		/* Add South. */
		try {
			neighs.add((Cell2D) space.getObjectAt(x, y + 1));
		} catch (IndexOutOfBoundsException ioobe) {
			if (torus) {
				neighs.add((Cell2D) space.getObjectAt(x, 0));					
			}
		}
		/* Add Southwest. */
		try {
			neighs.add((Cell2D) space.getObjectAt(x - 1, y + 1));
		} catch (IndexOutOfBoundsException ioobe) {
			if (torus) {
				int effectiveX = x - 1;
				int effectiveY = y + 1;
				if (x == 0)
					effectiveX = space.getSizeX() - 1;
				if (space.getSizeY() == y + 1)
					effectiveY = 0;					
				neighs.add((Cell2D) space.getObjectAt(effectiveX, effectiveY));
			}
		}
		/* Add West. */
		try {
			neighs.add((Cell2D) space.getObjectAt(x - 1, y));
		} catch (IndexOutOfBoundsException ioobe) {
			if (torus) {
				neighs.add((Cell2D) space.getObjectAt(space.getSizeX() - 1, y));
			}
		}
		/* Add Northwest. */
		try {
			neighs.add((Cell2D) space.getObjectAt(x - 1, y - 1));
		} catch (IndexOutOfBoundsException ioobe) {
			if (torus) {
				int effectiveX = x - 1;
				int effectiveY = y - 1;
				if (x == 0)
					effectiveX = space.getSizeX() - 1;
				if (y == 0)
					effectiveY = space.getSizeY() - 1;					
				neighs.add((Cell2D) space.getObjectAt(effectiveX, effectiveY));
			}
		}
		/* Return neighbor vector. */
		return neighs;
	}

	/**
	 * Returns a {@link java.util.Vector} containing {@link org.laseeb.LAIS.space.Cell2D}s
	 * which are Von Neumann neighbors of the cell located at the given (x,y) location. 
	 * Cell at (x,y) is not returned.
	 * <p>
	 * Neighbors are given in the following index order:
	 * <p>
	 * 0: N, 1: E, 2: S, 3: W
	 * <p>
	 * This method exists because LAIS architecture requires that spaces return 
	 * cell neighbors sequentially in clockwise order. 
	 * Repast classes {@link uchicago.src.sim.space.VNNeighborhooder} does not follow 
	 * this rule.   
	 *    
	 * @param x The horizontal location of the cell to get neighbors of.
	 * @param y The vertical location of the cell to get neighbors of.
	 * @param torus True if space is toroidal, false otherwise.
	 * @param space Simulation space.
	 * @return The Von Neumann neighbors of the given cell location in the given space.
	 */
	public static Vector<Cell2D> getVonNeumannNeighbors(int x, int y, boolean torus, Object2DGrid space) {
		/* Initialize neighbor vector. */
		Vector<Cell2D> neighs = new Vector<Cell2D>(4);
		/* Add North. */
		try {
			neighs.add((Cell2D) space.getObjectAt(x, y - 1));
		} catch (IndexOutOfBoundsException ioobe) {
			if (torus) {
				neighs.add((Cell2D) space.getObjectAt(x, space.getSizeY() - 1));
			}
		}
		/* Add East. */
		try {
			neighs.add((Cell2D) space.getObjectAt(x + 1, y));
		} catch (IndexOutOfBoundsException ioobe) {
			if (torus) {
				neighs.add((Cell2D) space.getObjectAt(0, y));
			}
		}
		/* Add South. */
		try {
			neighs.add((Cell2D) space.getObjectAt(x, y + 1));
		} catch (IndexOutOfBoundsException ioobe) {
			if (torus) {
				neighs.add((Cell2D) space.getObjectAt(x, 0));					
			}
		}
		/* Add West. */
		try {
			neighs.add((Cell2D) space.getObjectAt(x - 1, y));
		} catch (IndexOutOfBoundsException ioobe) {
			if (torus) {
				neighs.add((Cell2D) space.getObjectAt(space.getSizeX() - 1, y));
			}
		}
		/* Return neighbor vector. */
		return neighs;		
	}
		
}
