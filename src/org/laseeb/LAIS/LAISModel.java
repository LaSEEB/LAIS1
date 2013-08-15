package org.laseeb.LAIS;

import java.util.*;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import uchicago.src.sim.engine.Schedule;
import uchicago.src.sim.engine.SimModelImpl;
import uchicago.src.sim.engine.BasicAction;
import uchicago.src.sim.gui.Display2D;
import uchicago.src.sim.gui.DisplayConstants;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.laseeb.LAIS.agent.AgentFactory;
import org.laseeb.LAIS.agent.AgentManager;
import org.laseeb.LAIS.agent.AgentWatcher;
import org.laseeb.LAIS.datasources.DataSource;
import org.laseeb.LAIS.event.Event;
import org.laseeb.LAIS.event.EventException;
import org.laseeb.LAIS.event.ScriptingType;
import org.laseeb.LAIS.gui.LAISDisplaySurface;
import org.laseeb.LAIS.gui.SubstanceProbe;
import org.laseeb.LAIS.output.FileOutput;
import org.laseeb.LAIS.output.GraphicalOutput;
import org.laseeb.LAIS.output.Output;
import org.laseeb.LAIS.output.OutputException;
import org.laseeb.LAIS.space.Abstract2DSpaceFactory;
import org.laseeb.LAIS.space.Cell2D;
import org.laseeb.LAIS.space.CellStepException;
import org.laseeb.LAIS.space.Abstract2DSpaceAdapter;
import org.laseeb.LAIS.substance.Substance;
import org.laseeb.LAIS.substance.SubstanceException;
import org.laseeb.LAIS.substance.SubstanceManager;
import org.laseeb.LAIS.substance.SubstanceProvider;
import org.laseeb.LAIS.utils.QuickProfiler;
import org.laseeb.LAIS.utils.random.IRngFactory;
import org.laseeb.LAIS.utils.random.RngColtFactory;
import org.laseeb.LAIS.utils.random.RngManager;

import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

/**
 * This class represents a LAIS model, which extends the 
 * Repast {@link uchicago.src.sim.engine.SimModelImpl} abstract class.
 * <p>
 * This class is instantiated automatically by LAIS using the <strong>XML Model File</strong>. 
 * All the data necessary for instantiating this class is given within <code>&lt;LAISModel&gt;</code> 
 * tags. More specifically, <code>&lt;LAISModel&gt;</code> is the root tag for the 
 * <strong>XML Model File</strong>.
 * <p>
 * The relationship between the various tool bar buttons and the actual execution of code is as follows.
 * <ul> 
 * <li>
 * When the setup button is clicked, the code in the <code>setup()</code> method is executed. 
 * </li>
 * <li>
 * When the initialize button is clicked, the code in <code>begin()</code> is executed. 
 * </li>
 * <li>
 * When the step button is clicked, the code in <code>begin()</code> is executed and any behavior 
 * scheduled for the next tick is executed. 
 * </li>
 * <li>
 * When the start button is clicked, <code>begin()</code> is executed, and the simulation enters a loop 
 * where any behavior scheduled for the next tick is executed, the tick count is incremented and the 
 * next schedule behavior is executed and so on until the user clicks the stop or pause button.
 * </li>
 * 
 * @see uchicago.src.sim.engine.SimModelImpl
 * @author Nuno Fachada
 *
 */
@Root
public class LAISModel extends SimModelImpl {
	
	/* ************************************************* */
	/* ************ XML instantiated fields ************ */
	/* ************************************************* */

	/** 
	 * <strong>XML Attribute (integer)</strong>
	 * <p>
	 * User specified random seed.
	 * <p> 
	 * <em>REQUIRED: NO</em>. 
	 * */
	@Attribute(required=false)
	Long rngSeed = null;
	
	/** 
	 * <strong>XML Attribute (integer)</strong>
	 * <p>
	 * Number of simulation threads. If not given the number of simulation threads
	 * will be equal to the number of available processors.
	 * <p> 
	 * <em>REQUIRED: NO</em> 
	 * */
	@Attribute(required=false)
	int numThreads = Runtime.getRuntime().availableProcessors();

	/** 
	 * <strong>XML ElementMap (key: {@link java.lang.String}, value: {@link org.laseeb.LAIS.datasources.DataSource})</strong>
	 * <p>
	 * Global read-only variables accessible to all simulation elements.
	 * <p> 
	 * <em>REQUIRED: NO</em> 
	 * */
	@ElementMap(required=false,attribute=true)
	HashMap<String, DataSource> globals = new HashMap<String, DataSource>();
	
	/** 
	 * <strong>XML Element ({@link org.laseeb.LAIS.space.Abstract2DSpaceFactory})</strong>
	 * <p>
	 * A space factory object, which will determine the topology and rules of
	 * the simulation space.
	 * <p> 
	 * <em>REQUIRED: YES</em> 
	 * */
	@Element
	Abstract2DSpaceFactory spaceFactory;
	
	/** 
	 * <strong>XML Element (float)</strong>
	 * <p>
	 * The simulation time constant (tau). If not given default value is 1.
	 * <p> 
	 * <em>REQUIRED: NO</em> 
	 * */
	@Element(required=false)
	float dt = 1.0f;
	
	/** 
	 * <strong>XML Element (float)</strong>
	 * <p>
	 * The simulation spatial constant. If not given default value is 1.
	 * <p> 
	 * <em>REQUIRED: NO</em> 
	 * */
	@Element(required=false)
	float dx2 = 1.0f;
	
	/** 
	 * <strong>XML Element (float)</strong>
	 * <p>
	 * Minimum allowed substance concentration; when the concentration of a 
	 * substance within a cell is lower than this value, it is removed 
	 * from the cell.
	 * <p> 
	 * <em>REQUIRED: YES</em> 
	 * */
	@Element
	float minConThreshold;
	
	/** 
	 * <strong>XML Element ({@link org.laseeb.LAIS.substance.SubstanceManager})</strong>
	 * <p>
	 * The substance manager, which defines and manages all substances and substance
	 * families in the simulation.
	 * <p> 
	 * <em>REQUIRED: YES</em> 
	 * */
	@Element
	SubstanceManager substanceMan;
	
	/** 
	 * <strong>XML Element ({@link org.laseeb.LAIS.agent.AgentManager})</strong>
	 * <p>
	 * The agent manager, which defines and manages all agents in the simulation.
	 * <p> 
	 * <em>REQUIRED: YES</em> 
	 * */
	@Element 
	AgentManager agentMan;
	
	/* ************************************************* */
	/* ************ Batch mode parameters ************** */
	/* ************************************************* */

	/* Simulation ticks for batch runs. */
	private long ticks;
	
	/* Interval for printing simulation ticks in batch mode. If this value is zero, 
	 * the tick information will never be printed. */
	private long tickIntervalPrint = 0;
	
	/* ************************************************* */
	/* **************** Internal fields **************** */
	/* ************************************************* */

	/* Logger. */
	private static Logger logger = Logger.getLogger(LAISModel.class);
	/* Name of the model. */
	private final String name = "LAIS Model";
	/* Model parameters which are changeable in the Repast GUI. */
	private final String[] initParams = {"numThreads","Dt","Dx2","MinConThreshold"};
	/* Directory where to output data. */
	private String outputDir = ".";	
	/* Simulation script (scheduled events). */
	private LAISScript simulationScript;
	/* Data tracking information. */
	private LAISDataTrack track;
	/* Output type object. */
	private Output output;
	/* A relationship between the spatial and time constants. */
	private float dtDivDx2;
	/* The simulation scheduler. */
	private Schedule schedule;
	/* Data structure representing the simulation space. */
	private Abstract2DSpaceAdapter space;
	/* The graphical simulation environment. */
	private LAISDisplaySurface dsurf;
	/* A list of the cells that compose the simulation environment. */
	private ArrayList<Cell2D> cellList;
	/* Thread cell supplier. */
	private CellSupplier cellSupplier;
	
	// TODO Allow user to select random factory
	/* Random number generator. */
	private IRngFactory rngFactory = new RngColtFactory();
	
	/* This class supplies CA cells to be processed by the existing threads. */
	private class CellSupplier {
		int currentX;
		int currentY;
		boolean hasNext;
		/* Restart cell for dealing them to threads. */
		void reInit() {
			currentX = 0;
			currentY = 0;
			hasNext = true;
		}
		/* Return how many cells remain to be processed. */
		int getRemainingCells() {
			return (space.getSizeX() - currentX) * (space.getSizeY() - currentY);
		}
		/* Return next cell in deck. */
		synchronized Cell2D getNextCell() {
			if (hasNext) {
				Cell2D nextCell = space.getCell2DAt(currentX, currentY);
				currentX++;
				if (currentX == space.getSizeX()) {
					currentX = 0;
					currentY++;
					if (currentY == space.getSizeY()) {
						hasNext = false;
					}
				}
				return nextCell;
			}
			return null;
		}
	}
	
	/**
	 * Initializes model constants.
	 */
	private LAISModel() {		
		try {
			DOMConfigurator.configure("logdefs.xml");
		} catch (Exception e) {
			System.err.println("Error loading logging definitions! No logging will be performed!");
			System.err.println(e.getMessage());
		}
		DisplayConstants.CELL_WIDTH = 12;
        DisplayConstants.CELL_HEIGHT = 12;
        DisplayConstants.CELL_DEPTH = 12;
        cellSupplier = new CellSupplier();
        
	}
	
	/**
	 * Initializes the model for the start of a run. Consequently, the three build methods are 
	 * called here, and any displays are displayed.  <code>begin()</code> is called whenever the 
	 * start button (or the step button if the run has not yet started) is clicked. In addition, 
	 * any objects needed that depend on parameter values are created here (NOT in setup).
	 */
	public void begin() {
		logger.info("Simulation begins! Number of threads: " + numThreads + ".");
		/* Initialize relationship between space and time constants. */
		dtDivDx2 = dt / dx2;
		
		/* Start/restart manager of random number generators. */
		RngManager.getInstance().clear();
		RngManager.getInstance().setSeed(rngSeed);
		RngManager.getInstance().setRngFactory(rngFactory);
		
		/* Build model, output and schedule. */
		buildModel();
		try {
			buildOutput();
		} catch (OutputException oe) {
			logger.error(oe.getMessage());
			getController().stopSim();
			return;
		}
		buildSchedule();
		/* Initialize the output. */
		try {
			output.initialize();
		} catch (OutputException oe) {
			logger.error(oe.getMessage());
			getController().stopSim();
			return;
		}
		/* Take information for step 0. */
		output.step();
		/* Initialize simulation display if simulation is in GUI mode. */
		if (getController().isGUI()) {
			dsurf.display();
			dsurf.setBackground(Color.WHITE);
		}
	}

	/** The <code>setup()</code> method should "tear down" the model in preparation for a call to 
	 * <code>begin()</code>. <code>setup()</code> is called when the model is loaded, either through 
	 * passing the model name as an argument to SimInit, or through the load model dialog, and more 
	 * frequently, whenever the setup button is clicked. Setup sets objects that are created over the 
	 * course of the run to null, and disposes of any DisplaySurfaces, graphs, and Schedules. 
	 * This helps preventing memory leaks and insure a clean startup (the call to <code>System.gc()</code> 
	 * helps too).
	 * <p>
	 * The initial model parameters are set to whatever defaults the user wants to see initially; 
	 * the Schedule is created here (i.e. <code>schedule = new Schedule(1);</code>), and the 
	 * DisplaySurface is created here as well (i.e. <code>displaySurface = 
	 * new DisplaySurface(this, "Heat Bugs Display");</code>).
	 * <p>
	 * Objects that rely on parameter values are NOT created or setup here. Any parameter manipulation 
	 * either through the user interface or through parameter files occurs after <code>setup()</code> 
	 * is called. In doing so, objects would be created using incorrect parameter values.
	 */
	public void setup() {

		/* Dispose of old objects. */
		cellList = null;
		space = null;
		schedule = null;
		if (output != null) output.dispose();
		output = null;
		
		if (dsurf != null) dsurf.dispose();
		dsurf = null;
		
		/* Clean eventually left over garbage. */
		System.gc();
		
		/* Reset and initialize agent and substances. */
		agentMan.resetNumbers();
		agentMan.setMinConThreshold(minConThreshold);
		substanceMan.resetAll();

		/* Creates the data output object - this can be delegated using the factory pattern. */
		if (getController().isGUI()) {
			/* Resets custom actions. */
			modelManipulator.init();
			/* Add button to print current substance list. */
			modelManipulator.addButton("List substances", new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					Iterator<Substance> iterSub = substanceMan.substanceIterator();
					Set<String> substances = new TreeSet<String>();
					while (iterSub.hasNext()) {
						Substance sub = iterSub.next();
						substances.add(sub.getName());
					}
					//LAIS.printMessage(substances.toArray(), LAIS.INFO_MESSAGE);
					new SubstanceProbe(substanceMan.getSubConMapCopy(), "Substances in simulation at tick " + getTickCount());
				}
			});
			/* Add button to print info about simulation. */
			modelManipulator.addButton("Tick info", new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					logger.info("Tick " + ((int) getController().getCurrentTime()) + ", " + cellSupplier.getRemainingCells() + " cells remain to process.");
				}
			});
			/* Create graphical output. */
			output = new GraphicalOutput(this);
			/* Creates and registers the display surface (simulation display). */
			dsurf = new LAISDisplaySurface(this, "LAIS Display");
			//registerDisplaySurface("LAIS Display", dsurf);			
			registerMediaProducer("LAIS Display", dsurf);			
		} else {
			output = new FileOutput(this);
		}

		/* Initializes the schedule. */
		schedule = new Schedule();		
	}

	/**
	 * <code>buildSchedule()</code> builds the Schedule that is responsible for altering the state 
	 * of the simulation. What this means is that the schedule is told what methods to call on which 
	 * objects and when.
	 */
	private void buildSchedule() {
		
		/* ******************************************************************************* */
		/* ***** Internal classes that represent fixed hard-coded simulation events. ***** */
		/* ******************************************************************************* */
		
		/**
		 * Performs an iteration in the simulation.
		 * @author Nuno Fachada
		 */
		class CellAction extends BasicAction {
			
			/* Flag indicating if simulation threads can perform a step through cells. */
			private boolean performStepOne;
			/* Flag indicating if simulation threads can perform a post-step through cells. */
			private boolean performStepTwo;
			/* Number of threads waiting to perform next action (step or post-step). When this 
			 * value equals the number of simulation threads, the execute() method in this class
			 * regains control of the simulation. */
			private int numWaiting;
			/* Always true until the simulation is stopped. When this flag is set to false, simulation 
			 * threads terminate execution. */
			private boolean simRunning;
			/* Simulation threads. */
			private Set<SimThread> simThreads;

			/* This class represents a simulation thread. */
			class SimThread extends Thread {
				/* The cell action object, used to synchronize and inform the simulation threads of 
				 * simulation status. */
				CellAction ca;
				/**
				 * Creates a simulation thread.
				 * @param ca The cell action object.
				 * @see java.lang.Thread
				 */
				public SimThread(CellAction ca) {
					this.ca = ca;
				}
				/** The code executed in each simulation step.
				 * @see java.lang.Thread#run()
				 */
				public void run() {
					int numCellsStep1 = 0, numCellsStep2 = 0;
					logger.info("Thread " + this.getName() + " started!");
					while (true) {
						/* Wait in order to perform step one. */
						synchronized(ca) {
							ca.notifyAll();
							while (!performStepOne) {
								try {
									ca.wait();
								} catch (InterruptedException ie) {}
							}
						}
						/* Check if simulation is over before proceeding. */
						if (!isSimRunning())
							break;
						try {
							numCellsStep1 = 0;
							Cell2D nextCell;
							while ((nextCell = cellSupplier.getNextCell()) != null) {
								nextCell.stepOne();
								numCellsStep1++;
							}
						} catch (CellStepException cse) {
							/* In case an exception occurs, stop simulation... */
							getController().stopSim();
							/* ...and log error. */
							logger.error("Error during cell step. Cause: " + cse.getMessage());
							if (logger.isTraceEnabled()) {
								StackTraceElement[] steArray = cse.getStackTrace();
								for (StackTraceElement ste : steArray)
									logger.trace(ste.toString());
							}
						}
						/* Inform main thread I'm done! */
						ca.incNumWaiting();
						synchronized(ca) {
							ca.notifyAll();
							/* Wait in order to perform step two. */
							while (!performStepTwo) {
								try {
									ca.wait();
								} catch (InterruptedException ie) {}
							}
						}
						/* Perform step two. */
						Cell2D nextCell;
						numCellsStep2 = 0;
						while ((nextCell = cellSupplier.getNextCell()) != null) {
							nextCell.stepTwo();
							numCellsStep2++;
						}
						/* Inform main thread I'm done! */
						ca.incNumWaiting();
					}
					logger.info("Thread " + this.getName() + " terminated!");
				}
			}
			
			/** The constructor for the cell action object. */
			public CellAction() {
				/* Create set of simulation threads. */
				simThreads = new HashSet<SimThread>();
				/* Set number of waiting threads. */
				this.numWaiting = 0;
				/* Set control booleans to false, so threads wait. */
				this.performStepOne = false;
				this.performStepTwo = false;
				this.simRunning = true;
				/* Create simulation threads. */
		    	for (int i = 0; i < getNumThreads(); i++) {
		    		SimThread st = new SimThread(this);
		    		simThreads.add(st);
		    		st.setName("LAIS SimThread number " + i);
		    		st.start();
		    	}
			}
			
			/**
			 * Increments the number of threads waiting to perform the next simulation action.
			 */
			public synchronized void incNumWaiting() {
				this.numWaiting++;
			}
			
			/**
			 * Terminates the simulation threads.
			 */
			public synchronized void cleanUp() {
				this.performStepOne = true;
				this.simRunning = false;
				this.notifyAll();
			}
			
			/**
			 * Indicates if simulation is still active, or if it has stopped.
			 * @return True if simulation has not yet ended.
			 */
			public boolean isSimRunning() {
				return this.simRunning;
			}
			
			/**
			 * Executes a full simulation iteration, controlling the simulation threads.
			 * @see uchicago.src.sim.engine.BasicAction#execute()
			 */
			public void execute() {
				if (isSimRunning()) {
					if (logger.isDebugEnabled()) {
						logger.debug("Started tick " + getController().getCurrentTime());
					}
					/* Reset global agent and substance concentration listeners. */
					agentMan.resetNumbers();
					substanceMan.resetConcentrations();
	
					/* Determine how to apply rules for newly created substances. */
					try {
						substanceMan.updateMergeRules();
					} catch (SubstanceException se) {
						logger.error(se.getMessage());
						getController().stopSim();
					}
	
					/* Reset the cell supplier. */
					cellSupplier.reInit();
					/* Synchronize threads to perform step and post-step */
					/* Inform threads to perform step. */
					this.numWaiting = 0;
					this.performStepOne = true;
					this.performStepTwo = false;
					synchronized(this) {
						this.notifyAll();
						/* Wait for threads to perform step. */
						while (this.numWaiting < getNumThreads()) {
							try {
								this.wait();
							} catch (InterruptedException ie) {}
						}
					}
					/* Reset the cell supplier. */
					cellSupplier.reInit();
					/* Inform threads to perform post-step. */
					this.numWaiting = 0;
					this.performStepOne = false;
					this.performStepTwo = true;
					/* Wait for threads to perform post-step. */
					synchronized(this) {
						this.notifyAll();
						while (this.numWaiting < getNumThreads()) {
							try {
								this.wait();
							} catch (InterruptedException ie) {}
						}
					}
				}
				if (logger.isDebugEnabled()) {
					logger.debug("Profiling results:\n" + QuickProfiler.getDurations());
					logger.debug("Finished tick " + getController().getCurrentTime());
				}
			}
		}		

		/**
		 * Updates the output.
		 * @author Nuno Fachada
		 */
		class UpdateOutputAction extends BasicAction {
			public void execute() {
				output.step();
			}
		}

		/**
		 * Updates the simulation display. Only to be used in GUI simulations.
		 * @author Nuno Fachada
		 */
		class UpdateDisplayAction extends BasicAction {
			public void execute() {
				dsurf.updateDisplay();
			}
		}
		
		/**
		 * Terminates the simulation threads in the end of the simulation run.
		 * @author Nuno Fachada
		 */
		class CleanUpAction extends BasicAction {
			private CellAction ca;
			public CleanUpAction(CellAction ca) {
				this.ca = ca;
			}
			public void execute() {
				ca.cleanUp();
			}
		}
		
		/**
		 * Terminates the simulation. Used in preprogrammed batch runs.
		 *  
		 * @author Nuno Fachada
		 */
		class StopAction extends BasicAction {
			public void execute() {
				getController().stopSim();
			}
		}		
		
		/* **************************************************** */
		/* ***** Schedule user-defined simulation events. ***** */
		/* **************************************************** */
		
		Iterator<ScriptingType> stIter = simulationScript.scriptedEventsIterator();
		while (stIter.hasNext()) {
			ScriptingType st = stIter.next();
			Event event = simulationScript.getScriptedEvent(st);
			try {
				event.initialize(this);
			} catch (EventException ee) {
				getController().stopSim();
				logger.error("Error initializing event. More information: " + ee.getMessage());
				return;
			}
			st.scriptEvent(this, event);
		}
		
		/* ******************************************************** */
		/* ***** Schedule fixed hard-coded simulation events. ***** */
		/* ******************************************************** */

		/* Cell Action. */
		CellAction ca = new CellAction();
		schedule.scheduleActionAtInterval(1, ca, Schedule.LAST);
		
		/* Update simulation display if in GUI mode. */
		if (this.getController().isGUI())
			schedule.scheduleActionAtInterval(1, new UpdateDisplayAction(), Schedule.LAST);
		/* Update output. */
		schedule.scheduleActionAtInterval(1, new UpdateOutputAction(), Schedule.LAST);
		
		/* If model is running in batch mode: */
		if (this.getController().isBatch()) {
			/* Make sure each run ends at the specified tick. */
			schedule.scheduleActionAt(ticks, new StopAction());
			/* If user wants to see simulation progress, print ticks from time to time. */
			if (tickIntervalPrint > 0) {
				schedule.scheduleActionAtInterval(tickIntervalPrint, new BasicAction() {
					public void execute() {
						System.out.println(
								"Tick " + 
								getController().getCurrentTime() + 
								" of " + ticks + 
								" (" + 
								((int) (100 * getController().getCurrentTime() / ticks)) + 
								"%)");
					}
				});
			}
		}
		
		/* Make sure simulation threads terminate in the end. */
		schedule.scheduleActionAtEnd(new CleanUpAction(ca));

	}

	/** 
	 * The <code>buildModel()</code> method is responsible for creating the parts of the simulation 
	 * that represent what is being modeled. The agents and their environment are created here, together 
	 * with data collection objects. 
	 */
	private void buildModel() {
		
		/* Create space */
		cellList = new ArrayList<Cell2D>();
		space = spaceFactory.createSpace();
		for (int i = 0; i < space.getSizeX(); i++) {
			for (int j = 0; j < space.getSizeY(); j++) {
				Cell2D cell = spaceFactory.createCell(i, j, space, substanceMan, agentMan, this);
				space.putCell2DAt(i, j, cell);
				cellList.add(cell);
			}
		}		
	}

	/**
	 * <code>buildOutput()</code> builds the parts of the simulation that have to do with outputting 
	 * the simulation data.
	 * 
	 * In the case of a GUI run, the actual creation of a DisplaySurface object should occur in 
	 * the <code>setup()</code> method however.
	 * @throws OutputException If it's not possible to build output.
	 */
	private void buildOutput() throws OutputException {
		/* Create family concentration tracking output. */
		if (track.existFamilyConcentrationTrackers()) {
			Iterator<String> iterator = track.getFamilyConcentrationIterator();
			while (iterator.hasNext()) {
				output.addFamilyConcentration(
						iterator.next(), 
						substanceMan);
			}
		}
		/* Create family diversity tracking output. */
		if (track.existFamilyDiversityTrackers()) {
			Iterator<String> iterator = track.getFamilyDiversityIterator();
			while (iterator.hasNext()) {
				output.addFamilyDiversity(
						iterator.next(), 
						substanceMan);
			}
		}
		/* Create substance tracking output. */
		if (track.existSubstanceTrackers()) {
			Iterator<String> iterator = track.getSubstanceIterator();
			while (iterator.hasNext()) {
				output.addSubstance(
						iterator.next(),
						substanceMan);
			}
		}
		/* Create agent tracking output. */
		if (track.existAgentTrackers()) {
			Iterator<String> iterator = track.getAgentIterator();
			while (iterator.hasNext()) {
				output.addAgent(
						iterator.next(),
						agentMan);
			}			
		}
		/* Create agent states tracking output. */
		if (track.existAgentStateTrackers()) {
			Iterator<String> iterator = track.getAgentStatesIterator();
			while (iterator.hasNext()) {
				String ag = iterator.next();
				String[] stateTypes = track.getAgentState(ag);
				for (String stateType : stateTypes)
					output.addAgentState(ag, stateType, agentMan);
			}						
		}
		/* Create simulation display if model is launched in GUI mode. */
		if (getController().isGUI()) {
			Display2D disp = spaceFactory.createDisplay(space);
			dsurf.addDisplayableProbeable(disp, "Cells");
		}
	}
	
	/**
	 * <code>getInitParam()</code> returns a String array of the initial model parameters that a user 
	 * wishes to be displayable and manipulable.
	 * 
	 * @return A String array of the initial model parameters that a user wishes to be displayable and 
	 * manipulable.
	 */
	public String[] getInitParam() {
		return initParams;
	}
	
	/**
	 * This returns the name of the model. This name will be displayed as the title of the tool bar.
	 * 
	 * @return The name of the model.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the schedule associated with the model.
	 * 
	 * @return Returns the the model's schedule variable.
	 */
	public Schedule getSchedule() {
		return schedule;
	}
	
	/**
	 * Returns the space associated with the model.
	 * 
	 * @return Returns the the model's space variable.
	 */
	public Abstract2DSpaceAdapter getSpace() {
		return this.space;
	}
	
	/**
	 * Returns the agent factory associated with the model.
	 * 
	 * @return Returns the the model's agent factory.
	 */
	public AgentFactory getAgentFactory() {
		return this.agentMan;
	}
	
	/**
	 * Returns the agent watcher associated with the model.
	 * 
	 * @return Returns the the model's agent watcher.
	 */
	public AgentWatcher getAgentWatcher() {
		return this.agentMan;
	}
	
	/**
	 * Returns the substance provider associated with the model.
	 * 
	 * @return Returns the the model's substance provider.
	 */
	public SubstanceProvider getSubstanceProvider() {
		return this.substanceMan;
	}

	/**
	 * Returns the relationship between the spatial and time constants.
	 * 
	 * @return The relationship between the spatial and time constants.
	 */
	public float getDtDivDx2() {
		return dtDivDx2;
	}
	
	/**
	 * Sets the simulation script.
	 * 
	 * @param simulationScript The simulation script.
	 */
	public void setSimulationScript(LAISScript simulationScript) {
		this.simulationScript = simulationScript;
	}
	
	/**
	 * Sets data tracking information, which will be displayed in graphs and/or recorded in
	 * a file.
	 * 
	 * @param track Data tracking information.
	 */
	public void setDataTrack(LAISDataTrack track) {
		this.track = track;
	}
		
	/**
	 * Returns the x dimension of the simulation environment.
	 * 
	 * @return The x dimension of the simulation environment.
	 */
	public int getSizeX() {
		return space.getSizeX();
	}

	/**
	 * Returns the y dimension of the simulation environment.
	 * 
	 * @return The y dimension of the simulation environment.
	 */
	public int getSizeY() {
		return space.getSizeY();
	}

	/* ***************************************************** */ 
	/* Getters and Setters for user modifiable parameters.   */ 
	/* ***************************************************** */
	
	/** 
	 * Returns the spatial constant.
	 * 
	 * @return The spatial constant.
	 */
	public float getDx2() {
		return dx2;
	}

	/** 
	 * Returns the time constant (tau).
	 * 
	 * @return The time constant (tau).
	 */
	public float getDt() {
		return dt;
	}

	/**
	 * Sets the spatial constant.
	 * 
	 * @param dx2 The spatial constant.
	 */
	public void setDx2(float dx2) {
		this.dx2 = dx2;
		dtDivDx2 = dt / dx2;
	}

	/**
	 * Sets the time constant (tau).
	 * 
	 * @param dt The time constant (tau).
	 */
	public void setDt(float dt) {
		this.dt = dt;
		dtDivDx2 = dt / dx2;
	}
	
	/**
	 * Sets the number of simulation ticks for batch mode simulations.
	 * 
	 * @param ticks The number of ticks to set.
	 */
	public void setTicks(long ticks) {
		this.ticks = ticks;
	}
	
	/**
	 * Sets the interval for printing simulation ticks in batch mode.
	 * If this value is zero, the tick information will never be printed.
	 * 
	 * @param tickIntervalPrint The number of ticks to set.
	 */
	public void setTickIntervalPrint(long tickIntervalPrint) {
		this.tickIntervalPrint = tickIntervalPrint;
	}

	/**
	 * Returns the folder where the output data will be saved.
	 * 
	 * @return The folder where the output data will be saved.
	 */
	public String getOutputDir() {
		return this.outputDir;
	}

	/**
	 * Sets the folder where the output data will be saved.
	 * 
	 * @param outputDir Folder where the output data will be saved.
	 */
	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}
	
	/** 
	 * Returns the minimum concentration threshold.
	 * 
	 * @return The minimum concentration threshold.
	 */
	public float getMinConThreshold() {
		return minConThreshold;
	}

	/**
	 * Sets the minimum concentration threshold.
	 * 
	 * @param minConThreshold The minimum concentration threshold.
	 */
	public void setMinConThreshold(float minConThreshold) {
		this.minConThreshold = minConThreshold;
	}

	/**
	 * Returns the number of simulation threads.
	 * 
	 * @return the numThreads
	 */
	public int getNumThreads() {
		return numThreads;
	}

	/**
	 * Sets the number of simulation threads.
	 * 
	 * @param numThreads the numThreads to set
	 */
	public void setNumThreads(int numThreads) {
		this.numThreads = numThreads;
	}
	
//	/**
//	 * Returns the factory for random number generators.
//	 * 
//	 * @return The factory for random number generators.
//	 */
//	public IRngFactory getRngFactory() {
//		return this.rngFactory;
//	}
//	
//	/**
//	 * Sets the factory for random number generators.
//	 * 
//	 * @param rngFactory The factory for random number generators.
//	 */
//	public void setRngFactory(IRngFactory rngFactory) {
//		this.rngFactory = rngFactory;
//	}
//	
//	/**
//	 * Factory method which creates and returns a new random number generator.
//	 * Delegates this creation to the defined factory class.
//	 * 
//	 * @return A new random number generator.
//	 */
//	public IRng createRng() {
//		if (rngSeed != null) {
//			return rngFactory.createRng(rngSeed);
//		} else {
//			return rngFactory.createRng(System.nanoTime());
//		}
//	}
}
