/**
 * 
 */
package edu.ncsu.csc216.business.model.io;

import java.io.FileInputStream;
import java.time.LocalDate;
import java.util.Scanner;

import edu.ncsu.csc216.business.model.properties.RentalUnit;
import edu.ncsu.csc216.business.model.stakeholders.Client;
import edu.ncsu.csc216.business.model.stakeholders.PropertyManager;

/**
 * Class made to read data from a file and help load it into the program
 * 
 * @author Anton
 */
public class RentalReader {

	/**
	 * Static method made to read data from a file and load it into the
	 * PropertyManager class.
	 * 
	 * @param filename The name of the file to read
	 * @throws IllegalArgumentException If the data from the file could not be read
	 */
	public static void readRentalData(String filename) {
		try {
			Scanner fileReader = new Scanner(new FileInputStream(filename));
			String fileContents = "";
			PropertyManager manager = PropertyManager.getInstance();
			manager.flushAllData();
			boolean finishedUnits = false;
			Client currentClient = null;
			while (fileReader.hasNextLine()) {// goes through each line
				fileContents = fileReader.nextLine();
				if (fileContents.length() != 0) {// checks if line is empty
					fileContents = fileContents.trim();
					if (fileContents.charAt(0) == '#') {// customer reader
						finishedUnits = true; // confirms that rental units have already been recorded
						String[] nameId = fileContents.split("\\(");
						String name = nameId[0].trim().substring(1, nameId[0].length() - 1);
						nameId[1] = nameId[1].trim();
						if (nameId[1].indexOf(')') != nameId[1].length() - 1) {
							throw new IllegalArgumentException();
						}
						String id = nameId[1].substring(0, nameId[1].length() - 1);
						currentClient = manager.addNewClient(name, id);
					} else if (finishedUnits) { // lease reader
						if (currentClient == null) {
							throw new IllegalArgumentException();
						}
						String[] leases = fileContents.split("\\|");
						int confNum = Integer.parseInt(leases[0].trim());
						String[] startEnd = leases[1].trim().split(" "); // splits dates into start,"to", end
						LocalDate startD = LocalDate.parse(startEnd[0].trim());
						LocalDate endD = LocalDate.parse(startEnd[2].trim());
						int occupants = Integer.parseInt(leases[2].trim());
						String[] typeFloor = leases[3].split(":");
						String location = typeFloor[1].trim();
						RentalUnit rental = manager.getUnitAtLocation(location);
						manager.addLeaseFromFile(currentClient, confNum, rental, startD, endD, occupants);
					} else if (!finishedUnits) {// rental units reader
						String[] typeLocCap = fileContents.split(":|\\|");
						String type = typeLocCap[0].trim();
						String location = typeLocCap[1].trim();
						String[] capAvailability = typeLocCap[2].trim().split(" ");
						int cap = Integer.parseInt(capAvailability[0].trim());
						manager.addNewUnit(type, location, cap);
						if (capAvailability.length > 1) {
							for (int i = 1; i < capAvailability.length; i++) {
								if (capAvailability[i].trim().toLowerCase().equals("unavailable")) {
									manager.getUnitAtLocation(location).takeOutOfService();
								}
							}
						}
					} else {
						manager.flushAllData();
						throw new IllegalArgumentException("invalid order");
					}
				}
			}
			fileReader.close();
		} catch (Exception e) {
			throw new IllegalArgumentException("Unable to load file");
		}
	}

}
