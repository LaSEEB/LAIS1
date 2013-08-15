package org.laseeb.LAIS.output;

import uchicago.src.sim.analysis.OpenSequenceGraph;
import uchicago.src.sim.analysis.OpenSeqStatistic;

import java.util.List;
import java.util.ArrayList;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import org.laseeb.LAIS.LAISModel;
import org.laseeb.LAIS.agent.AgentManager;
import org.laseeb.LAIS.agent.AgentStateMap;
import org.laseeb.LAIS.substance.Substance;
import org.laseeb.LAIS.substance.SubstanceException;
import org.laseeb.LAIS.substance.SubstanceFamily;
import org.laseeb.LAIS.substance.SubstanceManager;

/**
 * Implementation of the Output interface for graphical output.
 * 
 * @see org.laseeb.LAIS.output.Output
 * @author Nuno Fachada
 */
public class GraphicalOutput implements Output {

	private OpenSequenceGraph familyDiversity;
	private OpenSequenceGraph substances;
	private OpenSequenceGraph families;
	private OpenSequenceGraph agents;
	private List<OpenSequenceGraph> allGraphs;
	private LAISModel model;
	private FilenameManager fm;
	
	/**
	 * Graphical output constructor; only requires the model as a parameter. 
	 * @param lmodel The LAIS model.
	 */
	public GraphicalOutput(LAISModel lmodel) {
		this.model = lmodel;
		/* Create a filename manager*/
		this.fm = new FilenameManager(lmodel.getOutputDir());
		/* Create a list of charts. */
		this.allGraphs = new ArrayList<OpenSequenceGraph>();
		/* Add button to save graph info to files. */
		model.getModelManipulator().addButton("Save to file", new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				model.getController().pauseSim();
				if (!fm.createDir()) {
					System.err.println("Unable to create directory where to save simulation output files. No files will be saved.");
				} else {
					for (OpenSequenceGraph osg : allGraphs) {
						osg.writeToFile();
					}
				}
			}
		});
	}
	
	/**
	 * @see org.laseeb.LAIS.output.Output#addFamilyConcentration(String, SubstanceManager)
	 */
	public void addFamilyConcentration(String family, SubstanceManager subMan) throws OutputException {
		/* Family concentration uses the same graph that substance concentration. */
		if (families == null) {
			families = new OpenSequenceGraph(familyTitle, 
					model, 
					fm.buildFilename(familyTitle), 
					OpenSeqStatistic.CSV);
			families.setXRange(0, 200);
			families.setYRange(0, 200);
			families.setAxisTitles(time, concentration);
			allGraphs.add(families);
		}
		SubstanceFamily subFamObject;
		try {
			subFamObject = subMan.getSubstanceFamilyByName(family);
		} catch (SubstanceException se) {
			throw new OutputException(se);
		}
		families.createSequence(family, 
				subFamObject.getColor(), 
				subMan.getFamilyConcentrationSource(subFamObject), 
				"getConcentration");
		
	}

	/**
	 * @see org.laseeb.LAIS.output.Output#addFamilyDiversity(String, SubstanceManager)
	 */
	public void addFamilyDiversity(String family, SubstanceManager subMan) throws OutputException {
		if (familyDiversity == null) {
			familyDiversity = new OpenSequenceGraph(familyDiversityTitle, 
					model, 
					fm.buildFilename(familyDiversityTitle), 
					OpenSeqStatistic.CSV);
			familyDiversity.setXRange(0, 200);
			familyDiversity.setYRange(0, 20);
			familyDiversity.setAxisTitles(time, concentration);
			allGraphs.add(familyDiversity);
		}
		SubstanceFamily subFamObject;
		try {
			subFamObject = subMan.getSubstanceFamilyByName(family);
		} catch (SubstanceException se) {
			throw new OutputException(se);
		}
		familyDiversity.createSequence(subFamObject.getName(), 
				subFamObject.getColor(), 
				subMan.getFamilyDiversitySource(subFamObject), 
				"getDiversity");
		
	}

	/**
	 * @see org.laseeb.LAIS.output.Output#addSubstance(String, SubstanceManager)
	 */
	public void addSubstance(String sub, SubstanceManager subMan) throws OutputException {
		if (substances == null) {
			substances = new OpenSequenceGraph(substanceTitle, 
					model, 
					fm.buildFilename(substanceTitle), 
					OpenSeqStatistic.CSV);
			substances.setXRange(0, 200);
			substances.setYRange(0, 200);
			substances.setAxisTitles(time, concentration);
			allGraphs.add(substances);
		}
		Substance subObject;
		try {
			subObject = subMan.getSubstanceByName(sub);
		} catch (SubstanceException se) {
			throw new OutputException(se);
		}
		substances.createSequence(subObject.getName(), 
				subObject.getColor(), 
				subMan.getSubstanceConcentrationSource(subObject), 
				"getConcentration");
	}

	/**
	 * @see org.laseeb.LAIS.output.Output#addAgent(String, AgentManager)
	 */
	public void addAgent(String ag, AgentManager agMan) {
		if (agents == null) {
			agents = new OpenSequenceGraph(agentTitle, 
					model, 
					fm.buildFilename(agentTitle), 
					OpenSeqStatistic.CSV);
			agents.setXRange(0, 200);
			agents.setYRange(0, 20);
			agents.setAxisTitles(time, numOfAgents);
			allGraphs.add(agents);
		}
		agents.createSequence(ag, 
				agMan.getColor(ag), 
				agMan.getAgentNumberSource(ag, null), 
				"getAgentNumbers");
	}

	/**
	 * @see org.laseeb.LAIS.output.Output#addAgentState(String, String, AgentManager)
	 */
	public void addAgentState(String ag, String stateType, AgentManager agMan) {
		OpenSequenceGraph agStates = new OpenSequenceGraph(
				agentTitle + " : " + ag + " : " + stateType, 
				model, 
				fm.buildFilename(agentTitle + "_" + ag + "_" + stateType), 
				OpenSeqStatistic.CSV);
		agStates.setXRange(0, 200);
		agStates.setYRange(0, 20);
		agStates.setAxisTitles(time, numOfAgents);
		for (String state : agMan.getStateNames(ag, stateType)) {
			agStates.createSequence(state,
				agMan.getAgentNumberSource(ag, new AgentStateMap(stateType, state)), 
				"getAgentNumbers");
		}
		allGraphs.add(agStates);
	}

	/**
	 * @see org.laseeb.LAIS.output.Output#step()
	 */
	public void step() {
		for (OpenSequenceGraph osg : allGraphs) {
			osg.step();
		}
	}

	/**
	 * @see org.laseeb.LAIS.output.Output#initialize()
	 */
	public void initialize() {
		for (OpenSequenceGraph osg : allGraphs) {
			osg.display();
		}
	}

	/**
	 * @see org.laseeb.LAIS.output.Output#dispose()
	 */
	public void dispose() {
		families = null;
		familyDiversity = null;
		substances = null;
		agents = null;
		for (OpenSequenceGraph osg : allGraphs) {
			osg.dispose();
		}
		allGraphs.clear();
	}	
}
