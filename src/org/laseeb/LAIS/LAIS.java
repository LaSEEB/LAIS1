package org.laseeb.LAIS;

import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.CycleStrategy;

import uchicago.src.sim.engine.SimInit;
import uchicago.src.sim.parameter.ParameterReader;

/**
 * Class containing the main method for running LAIS. It handles command-line argument
 * parsing, loading of simulation modules (model, script and data track), application
 * setup and application launch.
 * <p>
 * The three
 * 
 * @author Nuno Fachada
 */
public class LAIS {
	
	/* Indicates if a GUI will be shown. */
	private static boolean isGui;
	
	/** Default simulation ticks in batch mode. */
	final int DEFAULT_TICKS = 2000;
	
	/* Properties file. */
	private final String PROPERTIES_FILE = "LAIS.properties";
	
	/** Constant for specifying information messages. */
	public final static int INFO_MESSAGE = 0;
	/** Constant for specifying error messages. */
	public final static int ERROR_MESSAGE = 1;
	
	/* LAIS properties. */
	private Properties laisProperties;
	
	/* Model module. */
	private LAISModel model = null;
	
	/* Scripting module. */
	private LAISScript script = null;
	
	/* Data track module. */
	private LAISDataTrack dataTrack = null;

	/**
	 * Public constructor for this class. Handles command line argument parsing,
	 * application setup and application launch.
	 * <p>
	 * If no arguments are passed, loads GUI launcher for selecting XML modules to load in model.
	 * <p>
	 * If 3 arguments are passed, loads GUI launcher with the indicated XML modules pre-loaded.
	 * <p>
	 * If 4 or 5 arguments are passed, LAIS automatically launches in batch mode.
	 * 
	 * @param args Command line arguments: [model.xml script.xml data.xml [param.txt] [SIMTICKS]]
	 */
	public LAIS(String[] args) {
		/* If more than 3 arguments are passed, then run in text mode. */
		if (args.length > 3)
			isGui = false;
		/* Load properties. */
		laisProperties = new Properties();
		try {
			laisProperties.load(new FileReader(PROPERTIES_FILE));
		} catch (Exception e) {
			printMessage("Problems opening properties file '" + PROPERTIES_FILE + "'! A new file will be created.", LAIS.ERROR_MESSAGE);
			setDefaultProperties();
		}
		/* Check command line arguments. */
		if (args.length == 0) {
			/* Load previously used XML modules. */
			loadLastUsed();
			/* Display GUI launcher. */
			if (isGui) {
				guiLauncher();
			} else {
				System.err.println("Can't launch GUI, please run in batch mode.\n");
				printCommandLineOptions();
			}
		} else if (args.length == 3) {
			/* Load model module. */
			loadModel(args[0], true);
			/* Load scripting and event module. */
			loadScript(args[1], true);
			/* Load data tracking module. */
			loadDataTrack(args[2], true);
			/* Display GUI launcher. */
			if (isGui) {
				guiLauncher();
			} else {
				printMessage("Can't launch GUI, please run in batch mode.\n", LAIS.ERROR_MESSAGE);
				printCommandLineOptions();
			}
		} else if ((args.length >= 4) && (args.length <=5)) {
			/* Load model module. */
			loadModel(args[0], true);
			/* Load scripting and event module. */
			loadScript(args[1], true);
			/* Load data tracking module. */
			loadDataTrack(args[2], true);
			/* Set model max ticks. */
			if (args.length == 5) {
				long ticks = DEFAULT_TICKS;
				try {
					ticks = Long.parseLong(args[4]);
				} catch (NumberFormatException nfe) {
					printMessage("'" + args[4] + "'" + " is not a valid integer.", LAIS.ERROR_MESSAGE);
					System.exit(-1);
				}
				model.setTicks(ticks);
			} else {
				model.setTicks(DEFAULT_TICKS);
			}
			/* Launch LAIS in batch mode. */
			launch(true, args[3]);
		} else {
			printCommandLineOptions();
		}
		/* Save properties. */
		try {
			laisProperties.store(new FileWriter(PROPERTIES_FILE), "LAIS properties");
		} catch (IOException ioe) {
			printMessage("Unable to save properties!", LAIS.ERROR_MESSAGE);
			System.exit(-1);
		}
	}
	
	/* Print command-line options. */
	private void printCommandLineOptions() {
		printMessage("Usage: java " 
				+ LAISModel.class.getName() 
				+ " [model.xml script.xml data.xml [param.txt] [SIMTICKS]]\n"
				+ " * model.xml - Simulation model (agents, substances, etc).\n"
				+ " * script.xml - Simulation script.\n"
				+ " * datatrack.xml - Data to track.\n"
				+ " * param.txt - Batch mode parameter file.\n"
				+ " * SIMTICKS - Simulation ticks for batch mode (default: " 
				+ this.DEFAULT_TICKS + ").\n",
				LAIS.INFO_MESSAGE);
	}
	
	/* Launch LAIS/Repast. */
	private void launch(boolean batch, String batchParametersFile) {
		/* Cannot launch LAIS if any of the modules is null. */
		if ((model == null) || (script == null) || (dataTrack == null)) {
			printMessage("Please load model, scripting and data " +
					"tracking XML modules before launching LAIS!", LAIS.ERROR_MESSAGE);
			return;
		}
		/* Set script and data tracker in model. */
		model.setSimulationScript(script);
		model.setDataTrack(dataTrack);
		/* Initialize Repast. */
		final SimInit init = new SimInit();
		/* Finalizing variables for use in inner class. */
		final String bpf = batchParametersFile;
		final boolean b = batch;
		/* Launch Repast with LAIS. */
		SwingUtilities.invokeLater(
				new Runnable() {
					public void run() {
						init.loadModel(model, bpf, b);
					}
				}
		);
	}

	/**
	 * Show a message to the user.
	 * @param msg Message to show.
	 * @param msgType Message type.
	 */
	public static void printMessage(Object msg, int msgType) {
		if (!isGui) {
			/* GUI not supported, use command line message. */
			if (msgType == LAIS.INFO_MESSAGE)
				System.out.println(msg);
			else if (msgType == LAIS.ERROR_MESSAGE)
				System.err.println(msg);
		} else {
			/* Display message within a GUI dialog box. */
			int guiMsgType = JOptionPane.INFORMATION_MESSAGE;
			if (msgType == LAIS.INFO_MESSAGE)
				guiMsgType = JOptionPane.INFORMATION_MESSAGE;
			else if (msgType == LAIS.ERROR_MESSAGE)
				guiMsgType = JOptionPane.ERROR_MESSAGE;
			if (msg.getClass().isArray()) {
				JTextArea msgTextArea = new JTextArea();
				msgTextArea.setEditable(false);
				msgTextArea.setRows(20);
				msgTextArea.setColumns(20);
				for (Object obj : (Object[]) msg)
					msgTextArea.append(obj + "\n");
				msg = new JScrollPane(msgTextArea);
			}
			JOptionPane.showMessageDialog(
					null, 
					msg, 
					"LAIS", 
					guiMsgType);
		}
	}
	
	/* Loads last used XML modules. */
	private void loadLastUsed() {
		/* Load model. */
		String modelFile = laisProperties.getProperty("lastModelFile");
		if (modelFile.length() > 0) {
			if (!loadModel(modelFile, false))
				laisProperties.setProperty("lastModelFile", "");
		}
		/* Load script. */
		String scriptFile = laisProperties.getProperty("lastScriptFile");
		if (scriptFile.length() > 0)
			if (!loadScript(scriptFile, false))
				laisProperties.setProperty("lastScriptFile", "");
		/* Load data track. */
		String dataTrackFile = laisProperties.getProperty("lastDataTrackFile");
		if (dataTrackFile.length() > 0)
			if (!loadDataTrack(dataTrackFile, false))
				laisProperties.setProperty("lastDataTrackFile", "");
		/* Check save data directory. */
		String saveFolder = laisProperties.getProperty("lastSaveFolder");
		if (!(new File(saveFolder)).exists())
			laisProperties.setProperty("lastSaveFolder", "");
		/* Load batch parameter file. */
		String batchParamFile = laisProperties.getProperty("lastBatchParameterFile");
		if (batchParamFile.length() > 0)
			if (!loadBatchParameters(batchParamFile, false))
				laisProperties.setProperty("lastBatchParameterFile", "");
	}
	
	/* Loads model module. */
	private boolean loadModel(String modelFilename, boolean verbose) {
		File modelFile = new File(modelFilename);
		if (modelFile.exists()) {
			Serializer serializer = new Persister(new CycleStrategy("id","reference"));
			try {
				model = serializer.read(LAISModel.class, modelFile);
				return true;
			} catch (Exception e) {
				model = null;
				printMessage("Not a valid model XML file. Reason: " + e.getMessage(), LAIS.ERROR_MESSAGE);
			}
		} else {
			if (verbose)
				printMessage("Model file not found!", LAIS.ERROR_MESSAGE);
		}
		return false;
	}
	
	/* Loads scripting module. */
	private boolean loadScript(String scriptFilename, boolean verbose) {
		File scriptFile = new File(scriptFilename);
		if (scriptFile.exists()) {
			Serializer serializer = new Persister(new CycleStrategy("id","reference"));
			try {
				script = serializer.read(LAISScript.class, scriptFile);
				return true;
			} catch (Exception e) {
				script = null;
				printMessage("Not a valid script XML file. Reason: " + e.getMessage(), LAIS.ERROR_MESSAGE);
			}
		} else {
			if (verbose)
				printMessage("Script file not found!", LAIS.ERROR_MESSAGE);
		}
		return false;
	}
	
	/* Loads data track module. */
	private boolean loadDataTrack(String dataTrackFilename, boolean verbose) {
		File dataTrackFile = new File(dataTrackFilename);
		if (dataTrackFile.exists()) {
			Serializer serializer = new Persister(new CycleStrategy("id","reference"));
			try {
				dataTrack = serializer.read(LAISDataTrack.class, dataTrackFile);
				return true;
			} catch (Exception e) {
				dataTrack = null;
				printMessage("Not a valid data track XML file. Reason: " + e.getMessage(), LAIS.ERROR_MESSAGE);
			}
		} else {
			if (verbose)
				printMessage("Data track file not found!", LAIS.ERROR_MESSAGE);
		}
		return false;
	}
	
	/* Loads the batch parameters file. */
	private boolean loadBatchParameters(String batchParamFilename, boolean verbose) {
		if ((new File(batchParamFilename).exists())) {
			try {
				new ParameterReader(batchParamFilename);
				return true;
			} catch (Exception e) {
				printMessage("Not a valid batch parameter file. Reason: " + e.getMessage(), LAIS.ERROR_MESSAGE);
			}
		} else {
			if (verbose)
				printMessage("Batch parameter file not found!", LAIS.ERROR_MESSAGE);
		}
		return false;
	}

	/* Sets the default properties. */
	private void setDefaultProperties() {
		laisProperties.setProperty("lastModelFile", "");
		laisProperties.setProperty("lastScriptFile", "");
		laisProperties.setProperty("lastDataTrackFile", "");
		laisProperties.setProperty("lastRunIsBatch", "false");
		laisProperties.setProperty("lastBatchParameterFile", "");
		laisProperties.setProperty("lastBatchTicks", Integer.toString(DEFAULT_TICKS));
		laisProperties.setProperty("lastFolder", "");
	}

	/* Displays the GUI launcher. */
	private void guiLauncher() {
		/* Define an event handler class. */
		class GuiLauncherActionListener implements ActionListener {
			/* Internal references to Swing objects. */
			JTextField jtfModel, jtfScript, jtfDataTrack, jtfFolder, jtfBatchParameterFile;
			JCheckBox jcbBatch;
			JButton jbModel, jbModelClear, jbScript, jbScriptClear, jbDataTrack, jbDataTrackClear, jbFolder, jbFolderClear, jbBatch, jbBatchClear;
			JSpinner jsBatchMaxTicks, jsBatchIntervalTickPrint;
			JFileChooser jfc;
			FileFilter xmlFileFilter;
			/* Constructor. */
			GuiLauncherActionListener(JTextField jtfModel, JTextField jtfScript, JTextField jtfDataTrack, JTextField jtfFolder, JTextField jtfBatchParameterFile, JSpinner jsBatchMaxTicks, JSpinner jsBatchIntervalTickPrint, JCheckBox jcbBatch, JButton jbModel, JButton jbModelClear, JButton jbScript, JButton jbScriptClear, JButton jbDataTrack, JButton jbDataTrackClear, JButton jbFolder, JButton jbFolderClear, JButton jbBatch, JButton jbBatchClear, JFileChooser jfc) {
				this.jtfModel = jtfModel;
				this.jtfScript = jtfScript;
				this.jtfDataTrack = jtfDataTrack;
				this.jtfFolder = jtfFolder;
				this.jtfBatchParameterFile = jtfBatchParameterFile;
				this.jsBatchMaxTicks = jsBatchMaxTicks;
				this.jsBatchIntervalTickPrint = jsBatchIntervalTickPrint;
				this.jcbBatch = jcbBatch;
				this.jbModel = jbModel;
				this.jbModelClear = jbModelClear;
				this.jbScript = jbScript;
				this.jbScriptClear = jbScriptClear;
				this.jbDataTrack = jbDataTrack;
				this.jbDataTrackClear = jbDataTrackClear;
				this.jbFolder = jbFolder;
				this.jbFolderClear = jbFolderClear;
				this.jbBatch = jbBatch;
				this.jbBatchClear = jbBatchClear;
				this.jfc = jfc;
				this.xmlFileFilter = new FileNameExtensionFilter("XML files","xml");
			}
			/* Action handler method. */
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == jbModel) {
					/* Model load button clicked. */
					jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
					jfc.setFileFilter(xmlFileFilter);
					if (jfc.showOpenDialog(jbModel) == JFileChooser.APPROVE_OPTION) {
						String filename = jfc.getSelectedFile().getAbsolutePath();
						if (loadModel(filename, false)) {
							jtfModel.setText(filename);
						} else {
							jtfModel.setText("");
						}
					}
				} else if (e.getSource() == jbModelClear) {
					jtfModel.setText("");
				} else if (e.getSource() == jbScript) {
					/* Script load button clicked. */
					jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
					jfc.setFileFilter(xmlFileFilter);
					if (jfc.showOpenDialog(jbScript) == JFileChooser.APPROVE_OPTION) {
						String filename = jfc.getSelectedFile().getAbsolutePath();
						if (loadScript(filename, false)) {
							jtfScript.setText(filename);
						} else {
							jtfScript.setText("");
						}
					}
				} else if (e.getSource() == jbScriptClear) {
					jtfScript.setText("");
				} else if (e.getSource() == jbDataTrack) {
					/* Data track load button clicked. */
					jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
					jfc.setFileFilter(xmlFileFilter);
					if (jfc.showOpenDialog(jbDataTrack) == JFileChooser.APPROVE_OPTION) {
						String filename = jfc.getSelectedFile().getAbsolutePath();
						if (loadDataTrack(filename, false)) {
							jtfDataTrack.setText(filename);
						} else {
							jtfDataTrack.setText("");		
						}
					}
				} else if (e.getSource() == jbDataTrackClear) {
					jtfDataTrack.setText("");
				} else if (e.getSource() == jbFolder) {
					/* Data output folder button clicked. */
					jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					jfc.setAcceptAllFileFilterUsed(true);
					if (jfc.showOpenDialog(jbBatch) == JFileChooser.APPROVE_OPTION) {
						jtfFolder.setText(jfc.getSelectedFile().getAbsolutePath());
					}
				} else if (e.getSource() == jbFolderClear) {
					jtfFolder.setText("");
				} else if (e.getSource() == jbBatch) {
					/* Batch file parameter button was clicked. */
					jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
					jfc.setAcceptAllFileFilterUsed(true);
					if (jfc.showOpenDialog(jbBatch) == JFileChooser.APPROVE_OPTION) {
						String filename = jfc.getSelectedFile().getAbsolutePath();
						if (loadBatchParameters(filename, true)) {
							jtfBatchParameterFile.setText(filename);
						} else {
							jtfBatchParameterFile.setText("");
						}
					}
				} else if (e.getSource() == jbBatchClear) {
					jtfBatchParameterFile.setText("");
				} else if (e.getSource() == jcbBatch) {
					/* Batch check box was clicked. */
					if (jcbBatch.isSelected()) {
						jbBatch.setEnabled(true);
						jbBatchClear.setEnabled(true);
						jsBatchMaxTicks.setEnabled(true);
						jsBatchIntervalTickPrint.setEnabled(true);
					} else {
						jbBatch.setEnabled(false);
						jbBatchClear.setEnabled(false);
						jsBatchMaxTicks.setEnabled(false);
						jsBatchIntervalTickPrint.setEnabled(false);
					}
				}	
			}
		};
		/* Icons. */
		ImageIcon deleteIcon = new ImageIcon(this.getClass().getResource("/uchicago/src/sim/images/Delete.gif"));
		ImageIcon loadIcon = new ImageIcon(this.getClass().getResource("/uchicago/src/sim/images/Open24.gif"));
		/* Text box size. */
		int textBoxSize = 30;
		/* Swing object stack. */
		Object[] message = new Object[7];
		/* Set file chooser. */
		JFileChooser jfc = new JFileChooser();
		jfc.setCurrentDirectory(new File(laisProperties.getProperty("lastFolder")));
		/* Model components. */
		JTextField jtfModel = new JTextField(
				laisProperties.getProperty("lastModelFile"), textBoxSize);
		jtfModel.setEditable(false);
		JButton jbModel = new JButton(loadIcon);
		JButton jbModelClear = new JButton(deleteIcon);
		JPanel modelPanel = new JPanel();
		modelPanel.add(jtfModel);
		modelPanel.add(jbModel);
		modelPanel.add(jbModelClear);
		modelPanel.setBorder(BorderFactory.createTitledBorder("Model"));
		message[0] = modelPanel;
		/* Script panel. */
		JTextField jtfScript = new JTextField(
				laisProperties.getProperty("lastScriptFile"), textBoxSize);
		jtfScript.setEditable(false);
		JButton jbScript = new JButton(loadIcon);
		JButton jbScriptClear = new JButton(deleteIcon);
		JPanel scriptPanel = new JPanel();
		scriptPanel.add(jtfScript);
		scriptPanel.add(jbScript);
		scriptPanel.add(jbScriptClear);
		scriptPanel.setBorder(BorderFactory.createTitledBorder("Script"));
		message[1] = scriptPanel;
		/* Data track panel. */
		JTextField jtfDataTrack = new JTextField(
				laisProperties.getProperty("lastDataTrackFile"), textBoxSize);
		jtfDataTrack.setEditable(false);
		JButton jbDataTrack = new JButton(loadIcon);
		JButton jbDataTrackClear = new JButton(deleteIcon);
		JPanel dataTrackPanel = new JPanel();
		dataTrackPanel.add(jtfDataTrack);
		dataTrackPanel.add(jbDataTrack);
		dataTrackPanel.add(jbDataTrackClear);
		dataTrackPanel.setBorder(BorderFactory.createTitledBorder("Data track"));
		message[2] = dataTrackPanel;
		/* Folder where to save simulation data. */
		JTextField jtfFolder = new JTextField(
				laisProperties.getProperty("lastSaveFolder"), textBoxSize);
		jtfFolder.setEditable(false);
		JButton jbFolder = new JButton(loadIcon);
		JButton jbFolderClear = new JButton(deleteIcon);
		JPanel folderPanel = new JPanel();
		folderPanel.add(jtfFolder);
		folderPanel.add(jbFolder);
		folderPanel.add(jbFolderClear);
		folderPanel.setBorder(BorderFactory.createTitledBorder("Data output folder"));
		message[3] = folderPanel;
		/* Batch run checkbox. */
		JCheckBox jcbBatch = new JCheckBox("Enable batch mode");
		jcbBatch.setSelected(Boolean.parseBoolean(laisProperties.getProperty("lastRunIsBatch")));
		message[4] = jcbBatch;
		/* Batch run file panel. */
		JTextField jtfBatch = new JTextField(
				laisProperties.getProperty("lastBatchParameterFile"), textBoxSize);
		jtfBatch.setEditable(false);
		JButton jbBatch = new JButton(loadIcon);
		JButton jbBatchClear = new JButton(deleteIcon);
		jbBatch.setEnabled(Boolean.parseBoolean(laisProperties.getProperty("lastRunIsBatch")));
		jbBatchClear.setEnabled(Boolean.parseBoolean(laisProperties.getProperty("lastRunIsBatch")));
		JPanel batchPanel1 = new JPanel();
		batchPanel1.add(jtfBatch);
		batchPanel1.add(jbBatch);
		batchPanel1.add(jbBatchClear);
		batchPanel1.setBorder(BorderFactory.createTitledBorder("Batch run parameters"));
		message[5] = batchPanel1;
		/* Batch spinner - max tick setter. */
		JSpinner jsBatchMaxTick = 
			new JSpinner(new SpinnerNumberModel(
					Long.parseLong(laisProperties.getProperty("lastBatchTicks")),
					0,
					Long.MAX_VALUE,
					1)
			);
		jsBatchMaxTick.setEnabled(Boolean.parseBoolean(laisProperties.getProperty("lastRunIsBatch")));
		JPanel batchPanel2 = new JPanel();
		batchPanel2.add(jsBatchMaxTick);
		batchPanel2.setBorder(BorderFactory.createTitledBorder("Maximum batch run ticks"));
		/* Batch spinner - interval tick print setter. */
		JSpinner jsBatchIntervalTickPrint = 
			new JSpinner(new SpinnerNumberModel(
					Long.parseLong(laisProperties.getProperty("lastBatchIntervalTickPrint")),
					0,
					Long.MAX_VALUE,
					1)
			);
		jsBatchIntervalTickPrint.setEnabled(Boolean.parseBoolean(laisProperties.getProperty("lastRunIsBatch")));
		JPanel batchPanel3 = new JPanel();
		batchPanel3.add(jsBatchIntervalTickPrint);
		batchPanel3.setBorder(BorderFactory.createTitledBorder("Tick notification interval"));
		/* Join spinners. */
		JPanel batchPanel4 = new JPanel();
		batchPanel4.add(batchPanel2);
		batchPanel4.add(batchPanel3);
		message[6] = batchPanel4;
		/* Set action listener. */
		ActionListener al = new GuiLauncherActionListener(jtfModel, jtfScript, jtfDataTrack, jtfFolder, jtfBatch, jsBatchMaxTick, jsBatchIntervalTickPrint, jcbBatch, jbModel, jbModelClear, jbScript, jbScriptClear, jbDataTrack, jbDataTrackClear, jbFolder, jbFolderClear, jbBatch, jbBatchClear, jfc);
		jbModel.addActionListener(al);
		jbModelClear.addActionListener(al);
		jbScript.addActionListener(al);
		jbScriptClear.addActionListener(al);
		jbDataTrack.addActionListener(al);
		jbDataTrackClear.addActionListener(al);
		jbFolder.addActionListener(al);
		jbFolderClear.addActionListener(al);
		jbBatch.addActionListener(al);
		jbBatchClear.addActionListener(al);
		jcbBatch.addActionListener(al);
		/* Set options. */
		String[] options = {"Launch LAIS!", "About", "Quit"};
		/* Display launcher dialog. */
		URL icon = this.getClass().getResource("/logo_inst.png");
		if (icon == null) {
			printMessage("Image not found, LAIS aborting...", LAIS.ERROR_MESSAGE);
			System.exit(-1);
		}
		while (true) {
			int option = 0;
			option = JOptionPane.showOptionDialog(
						null, 
						message, 
						"LAIS Launcher", 
						0, 
						JOptionPane.PLAIN_MESSAGE, 
						new ImageIcon(icon),
						options, 
						options[0]);
			/* Verify option. */
			if (option == 0) {
				/* If launch LAIS was selected... launch LAIS! */
				/* First try to load selected XML. */
				boolean checkXML = true;
				checkXML = checkXML & loadModel(jtfModel.getText(), true);
				checkXML = checkXML & loadScript(jtfScript.getText(), true);
				checkXML = checkXML & loadDataTrack(jtfDataTrack.getText(), true);
				/* If XML is invalid, go back to launch menu. */
				if (!checkXML) continue;
				/* Set data output folder. */
				model.setOutputDir(jtfFolder.getText());
				/* Otherwise, if XML is valid, launch LAIS. */
				if (jcbBatch.isSelected()) {
					model.setTicks(((Double) jsBatchMaxTick.getValue()).longValue());
					model.setTickIntervalPrint(((Double) jsBatchIntervalTickPrint.getValue()).longValue());
					launch(true, jtfBatch.getText());
				} else {
					launch(false, null);
				}
				/* Get out of while. */
				break;
			} else if (option == 1) {
				JOptionPane.showMessageDialog(
						null, 
						"<html><strong>LAIS</strong>, a 2D agent-based simulation framework.<br><br>========== LASEEB 2008 ==========<br><br><em>Licence soon!</em></html>", 
						"About LAIS!", 
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				/* Get out of while. */
				break;
			}
		}
		/* Save properties. */
		laisProperties.setProperty("lastModelFile", jtfModel.getText());
		laisProperties.setProperty("lastScriptFile", jtfScript.getText());
		laisProperties.setProperty("lastDataTrackFile", jtfDataTrack.getText());
		laisProperties.setProperty("lastSaveFolder", jtfFolder.getText());
		laisProperties.setProperty("lastBatchParameterFile", jtfBatch.getText());
		if (jfc.getSelectedFile() != null)
			laisProperties.setProperty("lastFolder", jfc.getSelectedFile().getParent());
		laisProperties.setProperty("lastBatchTicks", "" + Math.round(Double.parseDouble(jsBatchMaxTick.getValue().toString())));
		laisProperties.setProperty("lastRunIsBatch", new Boolean(jcbBatch.isSelected()).toString());
		laisProperties.setProperty("lastBatchIntervalTickPrint", "" + Math.round(Double.parseDouble(jsBatchIntervalTickPrint.getValue().toString())));
	}
	
	/**
	 * Main method for running LAIS.
	 * Instantiates a LAIS object, which handles the argument parsing, application setup
	 * and application launching.
	 */
	public static void main(String[] args) {
		/* Check if GUI capabilities are available. */
		if (GraphicsEnvironment.isHeadless()) {
			isGui = false;
		} else {
			isGui = true;
			/* Set system look and feel. */
		    try {
		        UIManager.setLookAndFeel(
		            UIManager.getSystemLookAndFeelClassName());
		    } 
		    catch (Exception e) { /* No problem... */ }		
		}
		/* Instantiate class. */
		new LAIS(args);
	}
		
		
}
