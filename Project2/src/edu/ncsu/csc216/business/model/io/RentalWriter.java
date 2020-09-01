/**
 * 
 */
package edu.ncsu.csc216.business.model.io;

import java.io.PrintStream;

import edu.ncsu.csc216.business.model.stakeholders.PropertyManager;

/**
 * Class made to write data to a file
 * 
 * @author Anton
 */
public class RentalWriter {

	/**
	 * Static method made to write data from the PropertyManager class to a file.
	 * 
	 * @param filename The name of the file to write to
	 * @throws IllegalArgumentException if the file cannot be written to or if the
	 *                                  filename provided is null or does not have a
	 *                                  .md extension.
	 */
	public static void writeRentalFile(String filename) {
		if (filename == null || filename.length() == 0) {
			throw new IllegalArgumentException("invalid name");
		}
		if (!filename.endsWith(".md")) {
			throw new IllegalArgumentException("invalid name");
		}
		try {
			PrintStream fileWriter = new PrintStream(filename);
			PropertyManager manager = PropertyManager.getInstance();
			manager.filterRentalUnits("Any", false);
			String[] units = manager.listRentalUnits();
			String[] clients = manager.listClients();
			for (int i = 0; i < units.length; i++) {// print rental units
				fileWriter.println(units[i]);
			}
			fileWriter.print("\n");
			for (int i = 0; i < clients.length; i++) {
				fileWriter.println("#" + clients[i]);
				String[] leases = manager.listClientLeases(i);
				for (int j = 0; j < leases.length; j++) {
					fileWriter.println("   " + leases[j]);
				}
			}
			fileWriter.close();
		} catch (Exception e) {
			throw new IllegalArgumentException("Unable to save file");
		}

	}
}
