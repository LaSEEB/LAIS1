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


package org.laseeb.LAIS.space;

import java.awt.Color;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.laseeb.LAIS.LAISModel;
import org.laseeb.LAIS.agent.Agent;
import org.laseeb.LAIS.agent.AgentException;
import org.laseeb.LAIS.agent.AgentManager;
import org.laseeb.LAIS.agent.AgentView;
import org.laseeb.LAIS.agent.actions.ActionException;
import org.laseeb.LAIS.agent.conditions.ConditionException;
import org.laseeb.LAIS.substance.SubMergeRule;
import org.laseeb.LAIS.substance.Substance;
import org.laseeb.LAIS.substance.SubstanceContainer;
import org.laseeb.LAIS.substance.SubstanceManager;
import org.laseeb.LAIS.utils.random.IRng;
import org.laseeb.LAIS.utils.random.RngClient;
import org.laseeb.LAIS.utils.random.RngManager;
//import org.laseeb.LAIS.utils.QuickProfiler;

import uchicago.src.sim.engine.CustomProbeable;
import uchicago.src.sim.gui.DisplayConstants;
import uchicago.src.sim.gui.Drawable;
import uchicago.src.sim.gui.SimGraphics;

/**
 * Abstract class that represents cells in the simulation space.
 * Cells are regions in the simulation space. They are called cells because the simulation
 * space can be considered a Cellular Automata (CA).
 * 
 * @author Nuno Fachada
 *
 */
public class Cell2D implements SubstanceContainer, Drawable, CustomProbeable, RngClient  /*, DescriptorContainer*/  {

	/* Logger. */
	private static Logger logger = Logger.getLogger(Cell2D.class);
	/* Necessary agents sets; agentsInCell must be synchronized */
	protected Set<Agent> agentsInCell, agentsToDie, agentsToBorn, agentsToMoveIn;
	/* Agents to move out */
	protected Map<Agent, Cell2D> agentsToMoveOut;
	/* Agent views. */
	protected Map<String, AgentView> agentViews;
	protected Map<String, Double> agentViewsLastUpdate;
	/* The space where the cell is at */
	protected Abstract2DSpaceAdapter space;
	/* Cell2D coordinates */
	protected int x, y;
	/* Maps of global and local substance concentration. */
	protected Map<Substance, Float> localSubCon, localSubConFuture;
	/* Substance manager */
	protected SubstanceManager substanceMan;
	/* Agent manager */
	protected AgentManager agentMan;
	/* Number of agents produced. */
	protected int agentsProduced;
	/* The overlying AIS model */
	protected LAISModel model;

	/**
	 * Cell2D is an abstract cell in the 2D simulation environment.
	 * 
	 * @param x X coordinate (column) in which the cell is placed.
	 * @param y Y coordinate (row) in which the cell is placed.
	 * @param space Hexagonal space of which the cell is part of.
	 * @param substanceMan Object that determines the substance colors in the AIS Display.
	 * @param agentMan Agent manager.
	 * @param laisModel Reference to the LAIS Model.
	 */
	public Cell2D(int x, 
					int y, 
					Abstract2DSpaceAdapter space,
					SubstanceManager substanceMan,
					AgentManager agentMan,
					LAISModel laisModel) {
		agentsInCell = Collections.synchronizedSet(new TreeSet<Agent>());
		agentsToDie = new HashSet<Agent>();
		agentsToBorn = new HashSet<Agent>();
		agentsToMoveIn = new HashSet<Agent>();
		agentsToMoveOut = new HashMap<Agent, Cell2D>();
		agentViews = new HashMap<String, AgentView>();
		agentViewsLastUpdate = new HashMap<String, Double>();
		this.x = x;
		this.y = y;
		this.space = space;
		this.substanceMan = substanceMan;
		this.agentMan = agentMan;
		this.model = laisModel;
		this.agentsProduced = 0;
		
		this.localSubCon = new HashMap<Substance, Float>();
		this.localSubConFuture = new HashMap<Substance, Float>();
	}
	
	/**
	 * Sets an agent view.
	 * 
	 * @param viewName Name (reference) of the view.
	 * @param view The view object.
	 */
	public void setAgentView(String viewName, AgentView view) {
		agentViews.put(viewName, view);
		agentViewsLastUpdate.put(viewName, model.getController().getCurrentTime());
	}
	
	/**
	 * Returns an agent view.
	 * 
	 * @param viewName Name (reference) of the view.
	 * @return The requested view object.
	 */
	public AgentView getAgentView(String viewName) {
		return agentViews.get(viewName);
	}
	
	/**
	 * Returns true if given agent view is updated for the current simulation tick, false otherwise.
	 * 
	 * @param viewName viewName Name (reference) of the view.
	 * @return True if given agent view is updated for the current simulation tick, false otherwise.
	 */
	public boolean isAgentViewUpdated(String viewName) {
		if (agentViews.get(viewName) != null) {
			if (agentViewsLastUpdate.get(viewName) == model.getController().getCurrentTime())
				return true;
		}
		return false;
	}

	/**
	 * Add or remove a quantity of substance. Method to be primarily called by agents.
	 *  
	 * @param sub Substance to add or remove a quantity of.
	 * @param con Quantity of the given substance.
	 * @return Substance concentration after modification.
	 * @see org.laseeb.LAIS.substance.SubstanceContainer#modifySubstanceCon(Substance, Float)
	 */
	public synchronized float modifySubstanceCon(Substance sub, Float con) {
		float newCon = 0.0f;
		if (this.localSubConFuture.containsKey(sub)) {
			newCon = localSubConFuture.get(sub);				
		}
		newCon = newCon + con;
		if (newCon > this.getMinConThreshold()) {
			this.localSubConFuture.put(sub, newCon);
			return newCon;
		} else {
			this.localSubConFuture.remove(sub);
			return 0.0f;
		}
	}
	
	/**
	 * Update substance manager with substance concentration in this cell.
	 */
	public void updateSubstanceManager() {
		if (localSubCon.size() > 0)
			substanceMan.updateConcentrations(localSubCon);
	}

	/**
	 * Update agent manager regarding the agents currently in this cell.
	 */
	public void updateAgentManager() {
		if (!agentsInCell.isEmpty()) {
			agentMan.updateAgents(agentsInCell.iterator());
		}
	}
	
	/**
	 * Perform step one:
	 * <p> 1 - Agent actions;
	 * <p> 2 - Substance merging;
	 * <p> 3 - Set local substance concentration considering 1 and 2; 
	 * @throws CellStepException When an error occurs during a cell simulation step.
	 */
	public void stepOne() throws CellStepException {
		/* Clear views. */
		agentViews.clear();
		agentViewsLastUpdate.clear();
		/* Perform agent actions. */
		stepAgents();
		/* Perform substance merging. */
		stepSubstanceMerging();
		/* Update substance concentration references. */
		localSubCon = localSubConFuture;
		/* Update substance manager with substances from this cell. */
		updateSubstanceManager();
	}

	/**
	 * Perform step two:
	 * <p> 1 - Substance diffusion and evaporation;
	 * <p> 2 - Add newly created agents;
	 * <p> 3 - Add agents traveling from adjacent cells; 
	 */
	public void stepTwo() {
		/* Perform substance diffusion and evaporation. */
		stepSubstanceDiffEvap();
		/* Add newly born agents. */
		agentsInCell.addAll(agentsToBorn);
		agentsToBorn.clear();
		/* Add agents to move in. */
		agentsInCell.addAll(agentsToMoveIn);
		agentsToMoveIn.clear();
		/* Update agent manager with new agent numbers. */
		updateAgentManager();								
	}
	
	/**
	 * Perform substance diffusion / evaporation.
	 */
	protected void stepSubstanceDiffEvap() {
		/* Local substance concentration */
		float localConNextSub;
		/* Get an iterator that cycles through all neighbors in order to determine which substances
		 * are present in the neighborhood. 
		 * Previously we had an iterator which cycled through all the substances present
		 * in the simulation, which was too slow: 
		 * Iterator<Substance> gSubIterator = substanceMan.substanceIterator(); */
		Iterator<Cell2D> cellIter = neighborIterator();
		/* Create the data structure where to hold the substances. It is initialized with 
		 * the substances in the current cell. */
		HashSet<Substance> localNeighSubs = new HashSet<Substance>(localSubCon.keySet());
		/* Determine which substances are in the neighborhood. */
		while (cellIter.hasNext()) {
			Cell2D cell = (Cell2D) cellIter.next();
			localNeighSubs.addAll(cell.localSubCon.keySet());
		}
		/* Create future substance concentration. */
		localSubConFuture = new HashMap<Substance, Float>();
		/* Do substance diffusion. */
		for (Substance nextSub : localNeighSubs) {				
			/* Initialize current substance concentration to zero. */
			float neighborsTotalSubCon = 0;
			/* Obtain substance concentration in current cell. */
			if (localSubCon.containsKey(nextSub)) {
				localConNextSub = localSubCon.get(nextSub);
			} else {
				localConNextSub = 0;
			}				
			/* Obtain the neighborhood concentration of current substance. */
			Vector<Cell2D> neighbors = space.getNeighbors(x, y);
			for (Cell2D neighbor : neighbors) {
				float subConNeighbor;
				if (neighbor.containsSubstance(nextSub)) {
					subConNeighbor = neighbor.getSubstanceCon(nextSub);
				} else {
					subConNeighbor = 0;
				}
				neighborsTotalSubCon += subConNeighbor;
			}				
			/* Avoid wasting time if substance is not present. */
			if ((neighborsTotalSubCon != 0) || (localConNextSub != 0)) {
				/* Calculate adimensional coefficients. */
				float lKdif = nextSub.getKDif() * model.getDtDivDx2(),
					lKdeg = nextSub.getKDeg() * model.getDt();
				
				float currCon;
				if (localSubCon.containsKey(nextSub)) currCon = localSubCon.get(nextSub);
				else currCon = 0.0f;
				float difCon = (1 + lKdeg) * (currCon + lKdif * (neighborsTotalSubCon/neighbors.size() - currCon));
				
				/* Update local concentration of current substance. */
				if (difCon > model.getMinConThreshold())
					localSubConFuture.put(nextSub, difCon);
			}
		}			
	}
	
	/**
	 * Prompt agents to act.
	 * @throws CellStepException When an error occurs during the cell processing step.
	 */
	private void stepAgents() throws CellStepException {
		/* Cycle through all the agents in the cell. */
		Agent[] agents = new Agent[agentsInCell.size()];
		agents = agentsInCell.toArray(agents);
		for (Agent ag : agents) {
			/* Avoid that the agent plays with himself! */
			agentsInCell.remove(ag);
			try {
				ag.play(this);
			} catch (ConditionException ce) {
				throw new CellStepException(ce);
			} catch (ActionException ae) {
				throw new CellStepException(ae);
			} catch (AgentException ae) {
				throw new CellStepException(ae);
			}			
			agentsInCell.add(ag);
		}
		/* Remove dead agents */
		for (Agent ag : agentsToDie) {
			agentsInCell.remove(ag);
			agentsToMoveOut.remove(ag);
		}
		/* Remove agents to move out, and move them to destination cells */
		for (Agent ag : agentsToMoveOut.keySet()) {
			agentsInCell.remove(ag);
			Cell2D cellToTravel = agentsToMoveOut.get(ag);
			cellToTravel.setAgentToMoveIn(ag);
		}
		agentsToMoveOut.clear();
		agentsToDie.clear();
	}
	
	/**
	 * Perform substance merging.
	 */
	private void stepSubstanceMerging() {
		Vector<Substance> subs = new Vector<Substance>(localSubConFuture.keySet());
		while (!subs.isEmpty()) {
			/* Do this until there are no more substances to merge. */
			if (subs.get(0).isMergeable()) { 
				/* Only for substances that are mergeable. */
				for (int i = 1; i < subs.size(); i++) { 
					/* Check mergeability with other substances. */
					if(subs.get(i).isMergeable()) {
						/* If other substance is also mergeable, look for a merging rule. */
						Iterator<SubMergeRule> smrIter = substanceMan.mergeRuleIterator();
						while (smrIter.hasNext()) {
							SubMergeRule smr = smrIter.next();
							if (smr.isPair(subs.get(0), subs.get(i))) {
								/* There is a merging rule, let's apply it! */
								/* First determine concentration of substance to merge. */
								float conSub_0 = localSubConFuture.get(subs.get(0));
								float conSub_i = localSubConFuture.get(subs.get(i));
								Float conNewSub = localSubConFuture.get(smr.newSub);
								if (conNewSub == null) conNewSub = 0.0f; 
								float conToMerge = Math.min(conSub_0, conSub_i);
								conToMerge *= smr.affin;
								/* Very simple merging takes half of each substance to 
								 * create a new one. */
								localSubConFuture.put(subs.get(0), conSub_0 - conToMerge / 2);
								localSubConFuture.put(subs.get(i), conSub_i - conToMerge / 2);
								localSubConFuture.put(smr.newSub, conNewSub + conToMerge);
							}
						}
					}
				}
			}
			/* Remove reference substance from temporary vector. */
			subs.remove(0);
		}			
	}
	
	/**
	 * Adds an agent to this cell directly. Method used by agent deployment events. Not to be 
	 * used by agent actions; in that case use setAgentToMoveIn() instead.
	 * 
	 * @param ag An agent to add to this cell.
	 */
	public void addAgent(Agent ag) {
		agentsInCell.add(ag);
	}
	
	/**
	 * @see org.laseeb.LAIS.substance.SubstanceContainer#modifySubstanceCon(Substance, Float)
	 */
	public Iterator<Substance> substanceIterator() {
		return localSubCon.keySet().iterator();
	}
	
	/**
	 * Returns an iterator which cycles through all agents in this cell.
	 * @return An iterator which cycles through all agents in this cell.
	 */
	public Iterator<Agent> agentIterator() {
		return agentsInCell.iterator();
	}
	
	/**
	 * Obtain this cell's neighbors.
	 * @return Vector containing this cell's neighbors.
	 */
	public Vector<Cell2D> getNeighbors() {
		return space.getNeighbors(x, y);
	}
	
	/**
	 * Instruct that in the end of the current iteration, the specified agent must die.
	 * 
	 * @param ag Agent that is marked to die.
	 */
	public void setAgentToDie(Agent ag) {
		agentsToDie.add(ag);
	}
	
	/**
	 * Instruct that in the end of the current iteration, the specified agent will be inserted in
	 * this cell.
	 * 
	 * @param ag Agent that will be born in this cell in the end of the current iteration.
	 */
	public void setAgentToBorn(Agent ag) {
		agentsToBorn.add(ag);
	}
	
	/**
	 * Instruct that the specified agent should move to another, also specified, cell in the end 
	 * of the current iteration.
	 * 
	 * @param ag Agent that will move to another cell.
	 * @param travelToCell The cell where the agent will move to.
	 */
	public void setAgentToMoveOut(Agent ag, Cell2D travelToCell) {
		agentsToMoveOut.put(ag, travelToCell);
	}

	/**
	 * Instruct that the specified agent will move in to this cell in the end of the current iteration.
	 * Needs to be synchronized because this method can be called simultaneously by neighbor cells.
	 * 
	 * @param ag Agent that will move in to this cell.
	 */
	public synchronized void setAgentToMoveIn(Agent ag) {
		agentsToMoveIn.add(ag);
	}

	/**
	 * @see uchicago.src.sim.gui.Drawable#draw(SimGraphics)
	 */
	public void draw(SimGraphics g) {

		/* Fill cell with color from substances */
        if (localSubCon.size() > 0) {
//			Graphics2D g2d = g.getGraphics();
//	        Rectangle r = g2d.getClipBounds();
//	        int x = (int) r.getMinX();
//	        int y = (int) r.getMinY();
//	        g.setDrawingCoordinates(x + DisplayConstants.CELL_WIDTH / 10,
//	                                y + DisplayConstants.CELL_HEIGHT / 10, 
//	                                0);
	        g.setDrawingParameters(DisplayConstants.CELL_WIDTH,
                    DisplayConstants.CELL_HEIGHT,
                    0);
        	g.drawFastRoundRect(getBackgroundCellColor());
        }

        /* Insert agent presence */
		if (!agentsInCell.isEmpty()) {
	        //g.setDrawingCoordinates(x - DisplayConstants.CELL_WIDTH / 6,
              //      				y - DisplayConstants.CELL_HEIGHT / 6, 
                //    				0);
	        g.setDrawingParameters(DisplayConstants.CELL_WIDTH - 3,
                    DisplayConstants.CELL_HEIGHT - 3,
                    DisplayConstants.CELL_DEPTH - 3);
	        synchronized (agentsInCell) {
	        	/* We have to synchronize because calls to draw can be concurrent to
	        	 * changes in agentsInCell. */
	        	g.drawFastCircle(agentMan.getColor(agentsInCell.iterator().next().getPrototypeName()));
				/* It would be nicer for the drawing to take into account several agents, but 
				 * we would lose simulation efficiency. */
	        }
		} 
		
		/*else {
			g.drawFastRect(Color.WHITE);
			//g.drawStringInRoundRect(Color.ORANGE, Color.RED, "Zerou");			
		}*/
		
	}

	/**
	 * Return the horizontal coordinate of this cell.
	 * 
	 * @return The horizontal coordinate of this cell.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Return the vertical coordinate of this cell.
	 * 
	 * @return The vertical coordinate of this cell.
	 */
	public int getY() {
		return y;
	}
	
	/**
	 * Returns true if a given substance is present in cell.
	 * Returns false otherwise.
	 * 
	 * @param aisSub Substance to check presence of.
	 * @return True if substance is present, false otherwise.
	 * @see org.laseeb.LAIS.substance.SubstanceContainer#containsSubstance(Substance)
	 */
	public boolean containsSubstance(Substance aisSub) {
		return localSubCon.containsKey(aisSub);
	}
	
	/**
	 * Returns a given substance's concentration.
	 * @param sub Substance to obtain concentration of.
	 * @return Given substance's concentration.
	 * @see org.laseeb.LAIS.substance.SubstanceContainer#getSubstanceCon(Substance)
	 */
	public float getSubstanceCon(Substance sub) {
		if (containsSubstance(sub)) {
			return localSubCon.get(sub);
		} else {
			return 0.0f;
		}
	}

	/**
	 * Returns the minimum substance concentration threshold.
	 * 
	 * @see org.laseeb.LAIS.LAISModel#getMinConThreshold()
	 * @return The minimum substance concentration threshold.
	 */
	public float getMinConThreshold() {
		return model.getMinConThreshold();
	}
	
//	/* ********************************************************************** */
//	/* Methods regarding to the probed properties, to be called by Repast     */
//	/* ********************************************************************** */
//			
	/**
	 * Gets the names of the properties that are allowed to be probed. The property names returned 
	 * by this method should be reflect the accessor and mutator method names.
	 *  
	 * @see uchicago.src.sim.engine.CustomProbeable#getProbedProperties()
	 * @return An array of the property names.
	 */
	public String[] getProbedProperties() {
//		String[] probedProperties = {"x","y","localSubCon","numAgents","agents"};
//		return probedProperties;
		return null;
	}
	
	/**
	 * Returns a copy of this cell's substance concentration map.
	 * 
	 * @return A copy of this cell's substance concentration map
	 */
	public Map<Substance, Float> getSubConMap() {
		return new HashMap<Substance, Float>(this.localSubCon);
	}
	
	/**
	 * Returns a copy of this cell's agent set.
	 * 
	 * @return A copy of this cell's agent set.
	 */
	public Set<Agent> getAgents() {
		return new HashSet<Agent>(this.agentsInCell);
	}
	

	//TODO The algorithm that determines the cell color is far from perfect...
	/**
	 * Determine the background cell color, which is substance dependent.
	 * 
	 * @return The background cell color.
	 */
	private Color getBackgroundCellColor() {
		
		HashMap<Color, Double> colorImpact = new HashMap<Color, Double>();
		
		/* Determine which colors should be present, and the visual impact of each one */
		for (Substance sub : localSubCon.keySet()) {
			/* Determine relative substance concentration */
			double maxImpactCon = sub.getFamily().getSubMaxColorImpactCon();
			/* Normalize concentration */
			double relCon = localSubCon.get(sub) / maxImpactCon;
			/* And add it to the respective color impact */
			Color color = sub.getColor();
			Double impact = colorImpact.get(color);
			if (impact == null) impact = 0.0;
			Double newImpact = impact + relCon;
			colorImpact.put(color, newImpact);
		}
		
		/* Normalize color impact */
		for (Color color : colorImpact.keySet()) {
			double con = colorImpact.get(color);
			colorImpact.put(color, Math.min(1.0, con));
		}
		
		/* Determine the final color */
		int r = 0, g = 0, b = 0, a = 0;
		if (colorImpact.size() > 0) {
			for (Color color : colorImpact.keySet()) {
				r += (int) color.getRed() * colorImpact.get(color);
				g += (int) color.getGreen() * colorImpact.get(color);
				b += (int) color.getBlue() * colorImpact.get(color);
				a += (int) 255 * colorImpact.get(color);
			}
			r = r / colorImpact.size();
			g = g / colorImpact.size();
			b = b / colorImpact.size();
			a = a / colorImpact.size();
		}

		return new Color(r, g, b, a);
	}

	
	/**
	 * Returns a neighbor cell which is in the opposite direction to a given neighbor cell.
	 * <p> 
	 * This method works when the space topology considers all the neighbors 
	 * to be at the same distance from the current cell. For spatial topologies
	 * which don't consider this, the method should be overridden.
	 * 
	 * @param cell A cell which is a neighbor of the current cell.
	 * @return A neighbor cell which is in the opposite direction of the given neighbor cell.
	 * @throws CellStepException If the given cell is not neighbor of the current cell.
	 */
	public Cell2D getOppositeCell(Cell2D cell) throws CellStepException {
		/* Get number of neighbors. */
		int numNeighbors = space.getNeighbors(x, y).size();
		/* Get distance between given cell and opposite. */
		int distance = numNeighbors / 2;
		/* If number of neighbors is not pair, then there is no exact opposite neighbor. 
		 * Choose one randomly. */
		if (numNeighbors % 2 != 0) {
			distance += getRng().nextIntFromTo(0, 1);
		}
		/* Determine non-null opposite cell. */
		Cell2D oppNeigh = null;
		do {
			oppNeigh = getNeighbor(cell, distance);
			if (oppNeigh == null) {
				distance++;
				distance = normalizeNeighborIndex(distance, numNeighbors);
			}
		} while (oppNeigh == null);
		/* Return opposite cell. */
		return oppNeigh;
	}
	
	/**
	 * Method used to normalize cell indexes. For example, if a cell has six neighbors,
	 * which are mapped onto a vector with indexes ranging from 0 to 5, and the index 7 is given,
	 * it will be normalized to 2.
	 * 
	 * @param neighborIndex The index of the cell.
	 * @param numNeighbors The number of neighbors in the neighbor vector.
	 * @return The normalized cell index, which now is within the neighbor vector index range.
	 */
	protected int normalizeNeighborIndex(int neighborIndex, int numNeighbors) {
		if (neighborIndex < 0) neighborIndex = neighborIndex + numNeighbors;
		else if (neighborIndex >= numNeighbors) neighborIndex = neighborIndex - numNeighbors;
		return neighborIndex;
	}
	
	/**
	 * Returns a neighbor cell which is at a given distance from a given neighbor cell.
	 * <p> 
	 * This method works when the space topology considers all the neighbors 
	 * to be at the same distance from the current cell. For spatial topologies
	 * which don't consider this, the method should be overridden.
	 *
	 * @param cell A given neighbor cell.
	 * @param proximity The proximity of the given neighbor cell to the requested neighbor cell.
	 * @return The requested neighbor cell (which is at a given distance to a given cell).
	 * @throws CellStepException If the given cell is not neighbor of the current cell.
	 */
	public Cell2D getNeighbor(Cell2D cell, int proximity) throws CellStepException {
		/* Get neighbors. */
		Vector<Cell2D> neighbors = space.getNeighbors(x, y);
		/* Get number of neighbors. */
		int numNeighbors = neighbors.size();
		/* Get given cell location. */
		int indexGivenCell = neighbors.indexOf(cell);
		/* If given cell is not a valid neighbor, return null. */
		if (indexGivenCell == -1)
			throw new CellStepException("Given cell (" + cell.x + ", " + cell.y 
					+ ") is not a neighbor of current cell (" + x + ", "+ y + ").");
		/* Determine index of required neighbor cell. */
		int indexRequestedCell = normalizeNeighborIndex(indexGivenCell + proximity, numNeighbors);
		/* Return required cell. */
		return neighbors.get(indexRequestedCell);
	}
	
	
	/**
	 * Returns the distance between the two given neighbors relative to the current cell.
	 * <p> 
	 * This method works when the space topology considers all the neighbors 
	 * to be at the same distance from the current cell. For spatial topologies
	 * which don't consider this, the method should be overridden.
	 * 
	 * @param cell1 The first neighbor cell.
	 * @param cell2 The second neighbor cell.
	 * @return The distance between neighbors relative to the current cell.
	 * @throws CellStepException If at least one of the given cells is not neighbor of the current cell.
	 */
	public int getDistanceBetweenNeighbors(Cell2D cell1, Cell2D cell2) throws CellStepException {
		int direction;
		Vector<Cell2D> neighbors = space.getNeighbors(x, y);
		int indexCell1 = neighbors.indexOf(cell1);
		if (indexCell1 == -1)
			throw new CellStepException("Given cell (" + cell1.x + ", " + cell1.y 
					+ ") is not a neighbor of current cell (" + x + ", "+ y + ").");
		int indexCell2 = neighbors.indexOf(cell2);
		if (indexCell2 == -1)
			throw new CellStepException("Given cell (" + cell2.x + ", " + cell2.y 
					+ ") is not a neighbor of current cell (" + x + ", "+ y + ").");
			
		int distance = indexCell1 - indexCell2;
		direction = (int) Math.signum(distance);
		distance = Math.abs(distance);
		if (distance > neighbors.size()/2) distance = Math.abs(distance - neighbors.size()); 
		return direction * distance;
	}			
	
	/**
	 * Returns an iterator for this cell's neighbors.
	 * @return An iterator for this cell's neighbors.
	 */
	public Iterator<Cell2D> neighborIterator() {
		return new Iterator<Cell2D>() {
			/* Iterator private info. */
			private Iterator<?> neighIterator = getNeighbors().iterator();
			/* Returns true if there are more neighbors to return. */
			public boolean hasNext() {
				return neighIterator.hasNext();
			}
			/* Returns next neighbor. */
			public Cell2D next() {
				return (Cell2D) neighIterator.next();
			}
			/* We never remover anything... */
			public void remove() {
				/* Throw required exception in order to conform with Iterator interface. */
				throw new UnsupportedOperationException(
						"The remove() operation is not supported by the neighbor iterator!");
			}
		};
	}
	
	/**
	 * Returns the random number generator associated with this cell.
	 * 
	 * @return The random number generator associated with this cell.
	 */
	public IRng getRng() {
		return RngManager.getInstance().getRng(this);
	}

	/**
	 * Cells will use their (x, y) position to change the seed.
	 *  
	 * @param seed The global random number generator seed.
	 * @return The client specific random number generator seed.
	 * @see org.laseeb.LAIS.utils.random.RngClient#reSeed(long)
	 */
	@Override
	public long reSeed(long seed) {
		//long newSeed = Math.max(Math.abs(seed), Math.max(Math.abs(x), Math.abs(y)));
		//newSeed = ((long) Math.signum((double) seed) * seed + x) - y;
		String newSeedStr = seed + "-" + this.x + "-" + this.y;
		long newSeed = newSeedStr.hashCode();
		if (logger.isDebugEnabled())
			logger.debug("Seed (init: "+seed+") for cell (" + x + "," + y + "): " + newSeed);
		return newSeed;
	}

	/**
	 * Returns a unique and deterministic ID based on client properties.
	 * This value will be used as a unique agent identifier.
	 * 
	 * @return A unique and deterministic ID based on client properties.
	 * @see org.laseeb.LAIS.utils.random.RngClient#getUniqueID()
	 */
	@Override
	public int getUniqueID() {
		String str = this.model.getController().getCurrentTime() + "-" + this.x + "-" + this.y + "-" + this.agentsProduced;
		agentsProduced++;
		return str.hashCode();
	}


}