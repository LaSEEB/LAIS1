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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.laseeb.LAIS.agent.Agent;
import org.laseeb.LAIS.agent.AgentPrototype;
import org.laseeb.LAIS.utils.ArrayListModel;
import org.laseeb.LAIS.utils.HTMLUtils;

/**
 * A window which displays information about a given set of agents.
 * 
 * @author Nuno Fachada
 */
@SuppressWarnings("serial")
public class AgentProbe extends JFrame {

	/* Label for indicating all agents. */
	public static final String ALL_AGENTS = "All agents";

	/* Substance lists and respective models. */
	private JList prototypeList, agentList;
	private ArrayListModel prototypeListModel, agentListModel;
	
	/* Data maps. */
	private Map<AgentPrototype, Set<Agent>> agentsByPrototypeMap;
	
	/* Agent set. */
	private Set<Agent> agentSet;
	
	/* Data text area. */
	private JEditorPane dataPane;
	
	/** Create the information window.
	 * 
	 * @param originalAgentSet The agent set to probe.
	 * @param title The title of the window.
	 */
	public AgentProbe(Set<Agent> originalAgentSet, String title) {
		
		/* Initialize frame. */
		super(title);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		/* Determine and keep information to display. */
		this.agentSet = originalAgentSet;
		determineSupportMaps();
		
		/* Setup frame panel. */
		JPanel mainPanel = new JPanel(new GridBagLayout());
		
		/* Setup prototypes label. */
		JLabel prototypesLabel = new JLabel("<html><b>Prototypes</b></html>");
		GridBagConstraints prototypesLabelCons = new GridBagConstraints();
		prototypesLabelCons.gridx = 0;
		prototypesLabelCons.gridy = 0;
		prototypesLabelCons.fill = GridBagConstraints.BOTH;
		mainPanel.add(prototypesLabel, prototypesLabelCons);
		
		/* Setup agents label. */
		JLabel agentsLabel = new JLabel("<html><b>Agents</b></html>");
		GridBagConstraints agentsLabelCons = new GridBagConstraints();
		agentsLabelCons.gridx = 1;
		agentsLabelCons.gridy = 0;
		agentsLabelCons.fill = GridBagConstraints.BOTH;
		mainPanel.add(agentsLabel, agentsLabelCons);
		
		/* Setup data label. */
		JLabel dataLabel = new JLabel("<html><b>Data</b></html>");
		GridBagConstraints dataLabelCons = new GridBagConstraints();
		dataLabelCons.gridx = 2;
		dataLabelCons.gridy = 0;
		dataLabelCons.fill = GridBagConstraints.BOTH;
		mainPanel.add(dataLabel, dataLabelCons);

		/* Setup data pane. */
		dataPane = new JEditorPane();
		dataPane.setEditable(false);
		dataPane.setContentType("text/html");
		GridBagConstraints dataPaneCons = new GridBagConstraints();
		dataPaneCons.gridx = 2;
		dataPaneCons.gridy = 1;
		dataPaneCons.gridheight = GridBagConstraints.REMAINDER;
		dataPaneCons.fill = GridBagConstraints.BOTH;
		dataPaneCons.weightx = 1.0;
		dataPaneCons.weighty = 1.0;
		mainPanel.add(new JScrollPane(dataPane), dataPaneCons);
		

		/* Setup list models with all families, all agents. */
		prototypeListModel = new ArrayListModel(agentsByPrototypeMap.keySet());
		agentListModel = new ArrayListModel(agentSet);
		
		/* Setup family list and model. */
		prototypeList = new JList();
		prototypeList.setModel(prototypeListModel);
		ListSelectionListener plListen = new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent lse) {				
				AgentPrototype ap = (AgentPrototype) prototypeList.getSelectedValue();
				agentListModel.setCollection(agentsByPrototypeMap.get(ap));
				dataPane.setText(
						HTMLUtils.getKeyValueHTMLWrap("Number of agents", agentsByPrototypeMap.get(ap).size()) +
						HTMLUtils.getHTMLPropertiesDescription(ap));
			}
		};
		prototypeList.addListSelectionListener(plListen);
		prototypeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		prototypeList.setLayoutOrientation(JList.VERTICAL);
		prototypeList.setSelectedIndex(0);
		GridBagConstraints prototypeListCons = new GridBagConstraints();
		prototypeListCons.gridx = 0;
		prototypeListCons.gridy = 1;
		prototypeListCons.gridheight = GridBagConstraints.REMAINDER;
		prototypeListCons.fill = GridBagConstraints.BOTH;
		prototypeListCons.weightx = 1.0;
		prototypeListCons.weighty = 1.0;
		mainPanel.add(new JScrollPane(prototypeList), prototypeListCons);
		
		/* Setup agent list. */
		agentList = new JList();
		agentList.setModel(agentListModel);
		ListSelectionListener alListen = new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent lse) {				
				Agent ag = (Agent) agentList.getSelectedValue();
				if (ag != null)
					dataPane.setText(HTMLUtils.getHTMLPropertiesDescription(ag));
			}
		};
		agentList.addListSelectionListener(alListen);
		agentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		agentList.setLayoutOrientation(JList.VERTICAL);
		prototypeList.setSelectedIndex(0);
		GridBagConstraints agentListCons = new GridBagConstraints();
		agentListCons.gridx = 1;
		agentListCons.gridy = 1;
		agentListCons.gridheight = GridBagConstraints.REMAINDER;
		agentListCons.fill = GridBagConstraints.BOTH;
		agentListCons.weightx = 1.0;
		agentListCons.weighty = 1.0;
		mainPanel.add(new JScrollPane(agentList), agentListCons);
				
		/* Add main panel to frame. */
		add(mainPanel);
		
		/* Show frame. */
		pack();
		setVisible(true);		
	}	

	/*
	 * Obtain different views on the data for use in the probe.
	 */
	private void determineSupportMaps() {
		/* Initialize prototype map. */
		agentsByPrototypeMap = new TreeMap<AgentPrototype, Set<Agent>>();
		/* Iterate through the agents and populate prototype map. */
		Iterator<Agent> iterAg = agentSet.iterator();
		while (iterAg.hasNext()) {
			/* Get agent prototype. */
			Agent ag = iterAg.next();
			AgentPrototype prototype = ag.getPrototype();
			/* Update prototype map. */
			if (agentsByPrototypeMap.containsKey(prototype)) {
				agentsByPrototypeMap.get(prototype).add(ag);
			} else {
				Set<Agent> agents = new HashSet<Agent>();
				agents.add(ag);
				agentsByPrototypeMap.put(prototype, agents);
			}
		}
		AgentPrototype allAgents = new AgentPrototype("<html><em>" + ALL_AGENTS + "</em></html>");
		agentsByPrototypeMap.put(allAgents, agentSet);
	}

}
