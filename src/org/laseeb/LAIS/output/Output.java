package org.laseeb.LAIS.output;

import org.laseeb.LAIS.agent.AgentManager;
import org.laseeb.LAIS.substance.SubstanceManager;

/**
 * Interface which describes the methods that concrete output classes should implement. Concrete
 * implementations of this interface can output simulation results in whichever manner is appropriate,
 * such as to a file, to a graphical display, for the web, etc.
 * 
 * @author Nuno Fachada
 *
 */
public interface Output {
	
	/* Output title. */
	public final String substanceTitle = "Substance concentration";
	public final String agentTitle = "Number of agents";
	public final String familyTitle = "Substance family concentration";
	public final String familyDiversityTitle = "Family diversity";
	/* Time axis. */
	public final String time = "Time";
	/* Other axis. */
	public final String concentration = "Concentration";
	public final String numOfAgents = "Number of agents";
	public final String familyDiversityAxis = "Number of families";
	
	/**
	 * Add a family concentration source to the output.
	 * 
	 * @param family A substance family to track concentration of.
	 * @param subMan The substance manager.
	 * @throws OutputException If it's not possible to add a family concentration source to the output.
	 */
	public void addFamilyConcentration(String family, SubstanceManager subMan) throws OutputException;
	
	/**
	 * Add a family diversity (number of substances) source to the output.
	 * 
	 * @param family A substance family to track diversity of.
	 * @param subMan The substance manager.
	 * @throws OutputException If it's not possible to add family diversity source to the output.
	 */
	public void addFamilyDiversity(String family, SubstanceManager subMan) throws OutputException;

	/**
	 * Add a substance concentration source to the output.
	 * 
	 * @param sub A substance to track concentration of.
	 * @param subMan The substance manager.
	 * @throws OutputException If it's not possible to add substance source to the output.
	 */
	public void addSubstance(String sub, SubstanceManager subMan) throws OutputException;

	/**
	 * Add an agent numbers source to the output.
	 * 
	 * @param ag An agent to track numbers of.
	 * @param agMan The agent manager.
	 */
	public void addAgent(String ag, AgentManager agMan);
	
	/**
	 * Add an agent state numbers source to the output.
	 * 
	 * @param ag An agent to track numbers in a specific state
	 * @param stateType The state variable to keep track of.
	 * @param agMan The agent manager.
	 */
	public void addAgentState(String ag, String stateType, AgentManager agMan);
	
	/**
	 * Instructs the output to fetch information from the registered sources.
	 */
	public void step();

	/**
	 * Initialize output objects.
	 * @throws OutputException If not possible to initialize output object.
	 */
	public void initialize() throws OutputException;
	
	/**
	 * Terminate and destroy output objects.
	 */
	public void dispose();
	
}
