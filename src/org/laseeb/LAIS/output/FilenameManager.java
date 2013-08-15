package org.laseeb.LAIS.output;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Manages the naming of simulation output files.
 * To be used by implementations of the <code>Output</code> interface who save simulation data
 * to CSV files, therefore maintaining a coherent naming system for simulation output files.
 * 
 * @author Nuno Fachada
 */
public class FilenameManager {

	/* The directory where the files will be saved.*/
	private String directory;
	/* The file extension (all files will be CSV files). */
	private final String filenameSufix = ".csv";
	
	/**
	 * Creates a filename manager, determining the name of the directory where to save the
	 * output files.
	 */
	public FilenameManager(String basedir) {
		/* Create the filename prefix. */
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		this.directory = basedir + File.separator + sdf.format(new Date()) + File.separator;
		
	}
	
	/**
	 * Creates the directory where the files will be saved.
	 * @return True if directory was successfully created, false otherwise.
	 */
	public boolean createDir() {
		File dir = new File(directory);
		if (!dir.isDirectory())
			return dir.mkdir();
		return true;
	}
	
	/**
	 * Returns the name of the directory where the files will be saved.
	 * @return The name of the directory where the files will be saved.
	 */
	public String getDir() {
		return directory;
	}

	/**
	 * Creates a complete path and filename string.
	 * @param title The title of the data set.
	 * @return A cool and unique path + filename.
	 */
	public String buildFilename(String title) {
		return this.directory
			+ title
			+ this.filenameSufix;
	}

}
