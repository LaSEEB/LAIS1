package org.laseeb.LAIS.output;

import java.util.ArrayList;
import java.util.List;

import org.laseeb.LAIS.LAISModel;
import org.laseeb.LAIS.agent.AgentManager;
import org.laseeb.LAIS.agent.AgentStateMap;
import org.laseeb.LAIS.substance.Substance;
import org.laseeb.LAIS.substance.SubstanceException;
import org.laseeb.LAIS.substance.SubstanceFamily;
import org.laseeb.LAIS.substance.SubstanceManager;

import uchicago.src.sim.analysis.DataRecorder;

/**
 * Implementation of the Output interface for file output.
 * To be used in batch and/or no-GUI simulations.
 * 
 * @see org.laseeb.LAIS.output.Output
 * @author Nuno Fachada
 */
public class FileOutput implements Output {

	private DataRecorder familyDiversity;
	private DataRecorder substances;
	private DataRecorder families;
	private DataRecorder agents;
	private List<DataRecorder> allFiles;
	private LAISModel model;
	private FilenameManager fm;
	
	/**
	 * File output constructor; only requires the model as a parameter. 
	 * @param model The LAIS model.
	 */
	public FileOutput(LAISModel model) {
		this.model = model;
		/* Create a list of data recorders. */
		this.allFiles = new ArrayList<DataRecorder>();
		/* Create a filename manager*/
		this.fm = new FilenameManager(model.getOutputDir());
	}
	
	/**
	 * @see org.laseeb.LAIS.output.Output#addFamilyConcentration(String, SubstanceManager)
	 */
	public void addFamilyConcentration(String family, SubstanceManager subMan) throws OutputException {
		/* Family concentration uses the same graph that substance concentration. */
		if (families == null) {
			families = new DataRecorder(fm.buildFilename(familyTitle), 
					model, 
					familyTitle);
			allFiles.add(families);
		}
		SubstanceFamily subFamObject;
		try {
			subFamObject = subMan.getSubstanceFamilyByName(family);
		} catch (SubstanceException se) {
			throw new OutputException(se);
		}
		families.createNumericDataSource(family, 
				subMan.getFamilyConcentrationSource(subFamObject), 
				"getConcentration");		
	}

	/**
	 * @see org.laseeb.LAIS.output.Output#addFamilyDiversity(String, SubstanceManager)
	 */
	public void addFamilyDiversity(String family, SubstanceManager subMan) throws OutputException {
		if (familyDiversity == null) {
			familyDiversity = new DataRecorder(fm.buildFilename(familyDiversityTitle), 
					model, 
					familyDiversityTitle);
			allFiles.add(familyDiversity);
		}
		SubstanceFamily subFamObject;
		try {
			subFamObject = subMan.getSubstanceFamilyByName(family);
		} catch (SubstanceException se) {
			throw new OutputException(se);
		}
		familyDiversity.createNumericDataSource(family, 
				subMan.getFamilyDiversitySource(subFamObject), 
				"getDiversity");		
	}

	/**
	 * @see org.laseeb.LAIS.output.Output#addSubstance(String, SubstanceManager)
	 */
	public void addSubstance(String sub, SubstanceManager subMan) throws OutputException {
		if (substances == null) {
			substances = new DataRecorder(fm.buildFilename(substanceTitle), 
					model,
					substanceTitle);
			allFiles.add(substances);
		}
		Substance subObject;
		try {
			subObject = subMan.getSubstanceByName(sub);
		} catch (SubstanceException se) {
			throw new OutputException(se);
		}
		substances.createNumericDataSource(sub, 
				subMan.getSubstanceConcentrationSource(subObject), 
				"getConcentration");
	}

	/**
	 * @see org.laseeb.LAIS.output.Output#addAgent(String, AgentManager)
	 */
	public void addAgent(String ag, AgentManager agMan) {
		if (agents == null) {
			agents = new DataRecorder(fm.buildFilename(agentTitle), 
					model,
					agentTitle);
			allFiles.add(agents);
		}
		agents.createNumericDataSource(ag, 
				agMan.getAgentNumberSource(ag, null), 
				"getAgentNumbers");
	}

	/**
	 * @see org.laseeb.LAIS.output.Output#addAgentState(String, String, AgentManager)
	 */
	public void addAgentState(String ag, String stateType, AgentManager agMan) {
		DataRecorder agStates = new DataRecorder(
				fm.buildFilename(agentTitle + " - " + ag + " - " + stateType), 
				model,
				agentTitle + " - " + ag + " - " + stateType);
		for (String state : agMan.getStateNames(ag, stateType)) {
			agStates.createNumericDataSource(state,
				agMan.getAgentNumberSource(ag, new AgentStateMap(stateType, state)), 
				"getAgentNumbers");
		}
		allFiles.add(agStates);
	}

	/**
	 * @see org.laseeb.LAIS.output.Output#step()
	 */
	public void step() {
		for (DataRecorder dr : allFiles) {
			dr.record();
		}
	}

	/**
	 * @throws OutputException If its not possible to initialize this object.
	 * @see org.laseeb.LAIS.output.Output#initialize()
	 */
	public void initialize() throws OutputException {
		/* Create directory where to put the files. */
		if (!fm.createDir())
			throw new OutputException("Unable to create directory where to save simulation output files: '"+ fm.getDir() + "'.");		
	}

	/**
	 * @see org.laseeb.LAIS.output.Output#dispose()
	 */
	public void dispose() {
		families = null;
		familyDiversity = null;
		substances = null;
		agents = null;
		for (DataRecorder dr : allFiles) {
			dr.write();
		}
		allFiles.clear();
	}
	
}