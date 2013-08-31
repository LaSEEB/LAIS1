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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.laseeb.LAIS.substance.Substance;
import org.laseeb.LAIS.substance.SubstanceFamily;
import org.laseeb.LAIS.utils.ArrayListModel;
import org.laseeb.LAIS.utils.HTMLUtils;

/**
 * A window which displays information about a given map of substance concentrations.
 * 
 * @author Nuno Fachada
 */
@SuppressWarnings("serial")
public class SubstanceProbe extends JFrame {
	
	/* Label for indicating global substance family. */
	public static final String ALL_FAMILIES = "All families";
	
	/* Substance lists and respective models. */
	private JList subFamilyList, substanceList;
	private ArrayListModel subFamilyListModel, substanceListModel;
	
	/* Data maps. */
	private Map<SubstanceFamily, Float> famConMap;
	private Map<SubstanceFamily, Integer> famDivMap;
	private Map<SubstanceFamily, Set<Substance>> subByFamMap;
	
	/* Substance map. */
	private Map<Substance, Float> subConMap;
	
	/* Data text area. */
	private JEditorPane dataPane;
	
	/** Create the information window.
	 * 
	 * @param substanceConMap Substance concentration map to probe.
	 * @param title Title of the window.
	 */
	public SubstanceProbe(Map<Substance, Float> substanceConMap, String title) {
		
		/* Initialize frame. */
		super(title);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		/* Determine and keep information to display. */
		this.subConMap = substanceConMap;
		determineSupportMaps();
		
		/* Setup frame panel. */
		JPanel mainPanel = new JPanel(new GridBagLayout());
		
		/* Setup families label. */
		JLabel familiesLabel = new JLabel("<html><b>Families</b></html>");
		GridBagConstraints familiesLabelCons = new GridBagConstraints();
		familiesLabelCons.gridx = 0;
		familiesLabelCons.gridy = 0;
		familiesLabelCons.fill = GridBagConstraints.BOTH;
		mainPanel.add(familiesLabel, familiesLabelCons);
		
		/* Setup substances label. */
		JLabel substancesLabel = new JLabel("<html><b>Substances</b></html>");
		GridBagConstraints substancesLabelCons = new GridBagConstraints();
		substancesLabelCons.gridx = 1;
		substancesLabelCons.gridy = 0;
		substancesLabelCons.fill = GridBagConstraints.BOTH;
		mainPanel.add(substancesLabel, substancesLabelCons);
		
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
		

		/* Setup list models with all families, all substances. */
		subFamilyListModel = new ArrayListModel(subByFamMap.keySet());
		substanceListModel = new ArrayListModel(subConMap.keySet());
		
		/* Setup family list and model. */
		subFamilyList = new JList();
		subFamilyList.setModel(subFamilyListModel);
		ListSelectionListener sflListen = new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent lse) {				
				SubstanceFamily sf = (SubstanceFamily) subFamilyList.getSelectedValue();
				substanceListModel.setCollection(subByFamMap.get(sf));
				dataPane.setText(
						HTMLUtils.getKeyValueHTMLWrap("Number of substances", famDivMap.get(sf)) +
						HTMLUtils.getKeyValueHTMLWrap("Total substance concentration", famConMap.get(sf)) +						
						HTMLUtils.getHTMLPropertiesDescription(sf));
			}
		};
		subFamilyList.addListSelectionListener(sflListen);
		subFamilyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		subFamilyList.setLayoutOrientation(JList.VERTICAL);
		subFamilyList.setSelectedIndex(0);
		GridBagConstraints subFamilyListCons = new GridBagConstraints();
		subFamilyListCons.gridx = 0;
		subFamilyListCons.gridy = 1;
		subFamilyListCons.gridheight = GridBagConstraints.REMAINDER;
		subFamilyListCons.fill = GridBagConstraints.BOTH;
		subFamilyListCons.weightx = 1.0;
		subFamilyListCons.weighty = 1.0;
		mainPanel.add(new JScrollPane(subFamilyList), subFamilyListCons);
		
		/* Setup substance list. */
		substanceList = new JList();
		substanceList.setModel(substanceListModel);
		ListSelectionListener slListen = new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent lse) {				
				Substance sub = (Substance) substanceList.getSelectedValue();
				if (sub != null)
					dataPane.setText(
							HTMLUtils.getKeyValueHTMLWrap("Substance concentration", subConMap.get(sub)) +
							HTMLUtils.getHTMLPropertiesDescription(sub));
			}
		};
		substanceList.addListSelectionListener(slListen);
		substanceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		substanceList.setLayoutOrientation(JList.VERTICAL);
		subFamilyList.setSelectedIndex(0);
		GridBagConstraints substanceListCons = new GridBagConstraints();
		substanceListCons.gridx = 1;
		substanceListCons.gridy = 1;
		substanceListCons.gridheight = GridBagConstraints.REMAINDER;
		substanceListCons.fill = GridBagConstraints.BOTH;
		substanceListCons.weightx = 1.0;
		substanceListCons.weighty = 1.0;
		mainPanel.add(new JScrollPane(substanceList), substanceListCons);
				
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
		/* Total substance concentration. */
		Float totalSubCon = 0.0f;
		/* Initialize family maps. */
		famConMap = new TreeMap<SubstanceFamily, Float>();
		famDivMap = new TreeMap<SubstanceFamily, Integer>();
		subByFamMap = new TreeMap<SubstanceFamily, Set<Substance>>();
		/* Iterate through the substances and populate family maps. */
		Iterator<Substance> iterSub = subConMap.keySet().iterator();
		while (iterSub.hasNext()) {
			/* Get substance family. */
			Substance sub = iterSub.next();
			SubstanceFamily subFamily = sub.getFamily();
			/* Set/update family concentration. */
			float con = subConMap.get(sub);
			totalSubCon += con;
			if (famConMap.containsKey(subFamily))
				con += famConMap.get(subFamily);
			famConMap.put(subFamily, con);
			/* Set/update family diversity. */
			int div = 1;
			if (famDivMap.containsKey(subFamily))
				div += famDivMap.get(subFamily);
			famDivMap.put(subFamily, div);
			/* Set/update substance by family map.*/
			if (subByFamMap.containsKey(subFamily)) {
				Set<Substance> subSet = subByFamMap.get(subFamily);
				subSet.add(sub);
			} else {
				Set<Substance> subSet = new TreeSet<Substance>();
				subSet.add(sub);
				subByFamMap.put(subFamily, subSet);
			}
		}
		SubstanceFamily allFams = new SubstanceFamily("<html><em>" + ALL_FAMILIES + "</em></html>", 0.0f, null);
		famConMap.put(allFams, totalSubCon);
		famDivMap.put(allFams, subConMap.keySet().size());
		subByFamMap.put(allFams, subConMap.keySet());
	}
	
}
