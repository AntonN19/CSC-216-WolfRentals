
/**
 * 
 */
package edu.ncsu.csc216.business.model.stakeholders;

import java.time.LocalDate;
import java.time.Month;
import java.util.Scanner;

import edu.ncsu.csc216.business.list_utils.SimpleArrayList;
import edu.ncsu.csc216.business.list_utils.SortedLinkedListWithIterator;
import edu.ncsu.csc216.business.model.contracts.Lease;
import edu.ncsu.csc216.business.model.properties.*;

/**
 * Stores and manages all the data associated with the property such as clients,
 * leases, and rental units.
 * 
 * @author Anton
 */
public class PropertyManager implements Landlord {

	/** The earliest date for a lease allowed */
	private static final LocalDate EARLIEST_DATE = LocalDate.of(2020, Month.JANUARY, 1);

	/** The latest date for a lease allowed */
	private static final LocalDate LATEST_DATE = LocalDate.of(2029, Month.DECEMBER, 31);

	/** Filters the type of rental unit to filter Rental Units by */
	private String kindFilter = "A";

	/** Determines whether to filter out rental units that are not in service */
	private boolean inServiceFilter = false;

	/** List of all clients using Wolf Rentals Services */
	private SimpleArrayList<Client> customerBase = new SimpleArrayList<Client>();

	/** List of all rental units in Wolf Rental Services */
	private SortedLinkedListWithIterator<RentalUnit> rooms = new SortedLinkedListWithIterator<RentalUnit>();

	/** Singleton instance variable */
	private static PropertyManager instance;

	/**
	 * Adds a new client with the given name and id to the client list
	 * 
	 * @param name Name of the new client
	 * @param id   Unique id of the new client
	 * @return The new Client who was created and registered
	 * @throws DuplicateClientException if id of the new client matches one for an
	 *                                  existing client.
	 * @throws IllegalArgumentException if the name is null, empty (when trimmed),
	 *                                  or contains any characters that are not
	 *                                  blanks or not alphanumeric. Also throws
	 *                                  IllegalArgumentException if the id is null,
	 *                                  empty (when trimmed), or contains any
	 *                                  characters that are non-alphanumeric or that
	 *                                  don't belong to the set ['@', '#', '$'].
	 */
	@Override
	public Client addNewClient(String name, String id) throws DuplicateClientException {
		Client newClient = new Client(name, id); // throws illegalArgument if invalid params for client
		for (int i = 0; i < customerBase.size(); i++) {// checks for duplicate clients
			if (newClient.equals(customerBase.get(i))) {
				throw new DuplicateClientException();
			}
		}
		customerBase.add(newClient);
		return newClient;
	}

	/**
	 * Adds a new RentalUnit with the given parameters to the system.
	 * 
	 * @param kind     Type of RentalUnit (starts with 'O' for office, 'C' for
	 *                 conference room, 'H' for hotel suite)
	 * @param location String of the form FF-RR, where FF is the floor, and RR is
	 *                 the room.
	 * @param capacity Number of people the unit can accommodate on any single day
	 * @return The new RentalUnit that was created
	 * @throws IllegalArgumentException if the parameters do not describe a valid
	 *                                  location and type
	 * @throws DuplicateRoomException   if the floor and room match another rental
	 *                                  unit already in the Landlord's property
	 *                                  database
	 */
	@Override
	public RentalUnit addNewUnit(String kind, String location, int capacity) throws DuplicateRoomException {
		RentalUnit newUnit;
		kind = kind.trim();
		if (kind.startsWith("O")) {
			newUnit = new Office(location, capacity);
		} else if (kind.startsWith("C")) {
			newUnit = new ConferenceRoom(location, capacity);
		} else if (kind.startsWith("H")) {
			newUnit = new HotelSuite(location, capacity);
		} else {
			throw new IllegalArgumentException("invalid kind");
		}
		for (int i = 0; i < rooms.size(); i++) {
			if (rooms.get(i).equals(newUnit)) {
				throw new DuplicateRoomException();
			}
		}
		rooms.add(newUnit);
		return newUnit;
	}

	/**
	 * Adds lease with provided information to the system.
	 * 
	 * @param c            client
	 * @param confNumber   clients confirmation number
	 * @param r            rental unit
	 * @param startD       starting date
	 * @param endD         ending date
	 * @param numOccupants number of occupants
	 * @throws IllegalArgumentException if the attempt to add a lease from this file
	 *                                  causes inconsistencies.
	 */
	public void addLeaseFromFile(Client c, int confNumber, RentalUnit r, LocalDate startD, LocalDate endD,
			int numOccupants) {
		if (!this.customerBase.contains(c) || !this.rooms.contains(r)) {
			throw new IllegalArgumentException();
		}
		if (numOccupants <= 0) {
			throw new IllegalArgumentException();
		}
		if (startD.isBefore(EARLIEST_DATE) || startD.isAfter(LATEST_DATE) || endD.isBefore(EARLIEST_DATE)
				|| endD.isAfter(LATEST_DATE)) {
			throw new IllegalArgumentException();
		}
		try {
			Lease l = new Lease(confNumber, c, r, startD, endD, numOccupants);
			for (int i = 0; i < rooms.size(); i++) {
				if (rooms.get(i).equals(r)) {
					if (!rooms.get(i).isInService()) { // if not in service
						rooms.get(i).returnToService();
						rooms.get(i).addLease(l);
						rooms.get(i).takeOutOfService();
					} else {
						rooms.get(i).addLease(l);
					}
				}
			}
			for (int i = 0; i < customerBase.size(); i++) {
				if (customerBase.get(i).equals(c)) {
					customerBase.get(i).addNewLease(l);
				}
			}
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Sets filters for rental units so that only those that match the filters are
	 * considered.
	 * 
	 * @param kindFilter      filters by kind of rental unit
	 * @param inServiceFilter filters rental units that are not in service
	 */
	@Override
	public void filterRentalUnits(String kindFilter, boolean inServiceFilter) {
		kindFilter = kindFilter.trim();
		String filter = Character.toString(kindFilter.charAt(0)).toUpperCase();
		this.kindFilter = filter;
		this.inServiceFilter = inServiceFilter;
	}

	/**
	 * Creates a new lease with information based on the given parameters.
	 * 
	 * @param clientIndex   Index of the client in the Landlord's customer base
	 * @param propertyIndex Index of the rental unit in the Landlord's filtered list
	 *                      of rental units
	 * @param start         Start date for the lease
	 * @param duration      Duration of the lease (units depending on rental unit
	 *                      type)
	 * @param people        Number of occupants the lease is for
	 * @return the created lease
	 * @throws IllegalArgumentException if the parameters do not constitute valid
	 *                                  lease data
	 */
	@Override
	public Lease createLease(int clientIndex, int propertyIndex, LocalDate start, int duration, int people) {
		SortedLinkedListWithIterator<RentalUnit> filtered = this.getFilteredRoomList();
		try {
			Client c = customerBase.get(clientIndex);
			RentalUnit r = filtered.get(propertyIndex);
			Lease l = r.reserve(c, start, duration, people);
			customerBase.get(clientIndex).addNewLease(l);
			return l;
		} catch (Exception e) {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Cancels the lease in the given position on the client's list of leases.
	 * 
	 * @param clientIndex Index of the client whose lease is to be cancelled
	 * @param leaseIndex  Position of the lease in the client's list
	 * @throws IllegalArgumentException if clientIndex or leaseIndex are not valid
	 */
	@Override
	public void cancelClientsLease(int clientIndex, int leaseIndex) {
		if (clientIndex >= customerBase.size() || clientIndex < 0) {
			throw new IllegalArgumentException();
		}
		Client c = this.customerBase.get(clientIndex);
		if (leaseIndex >= c.listLeases().length || leaseIndex < 0) {
			throw new IllegalArgumentException();
		}
		Lease l = c.cancelLeaseAt(leaseIndex);
		RentalUnit rental = l.getProperty();
		int confNum = l.getConfirmationNumber();
		this.rooms.get(rooms.indexOf(rental)).cancelLeaseByNumber(confNum); // cancel lease at rental unit
	}

	/**
	 * Cancels all leases for a rental unit on or after a particular date. The
	 * remaining leases should still be valid.
	 * 
	 * @param propertyIndex Index for the rental unit (subject to filtering)
	 * @param start         Date for starting cancellations
	 * @return the RentalUnit that was removed
	 * @throws IllegalArgumentException if propertyIndex is not a valid index for
	 *                                  the rental units currently under
	 *                                  consideration
	 */
	@Override
	public RentalUnit removeFromService(int propertyIndex, LocalDate start) {
		SortedLinkedListWithIterator<RentalUnit> filteredList = this.getFilteredRoomList();
		if (propertyIndex >= filteredList.size() || propertyIndex < 0) {
			throw new IllegalArgumentException("index outside of list");
		}
		String location = filteredList.get(propertyIndex).getFloor() + "-" + filteredList.get(propertyIndex).getRoom();
		RentalUnit ret = this.getUnitAtLocation(location);
		SortedLinkedListWithIterator<Lease> leases = (SortedLinkedListWithIterator<Lease>) ret
				.removeFromServiceStarting(start);
		if (leases != null) {
			for (int i = 0; i < customerBase.size(); i++) {// iterates through customerBase
				String[] customerLeases = customerBase.get(i).listLeases();
				for (int j = 0; j < customerLeases.length; j++) {// iterates through each customers leases
					Scanner getConf = new Scanner(customerLeases[j]);
					int confNum = getConf.nextInt();
					for (int k = 0; k < leases.size(); k++) {
						if (leases.get(k).getConfirmationNumber() == confNum) {
							customerBase.get(i).cancelLeaseWithNumber(confNum);
						}
					}
					getConf.close();
				}
			}
		}
		return ret;
	}

	/**
	 * Removes the rental unit at the given index from the Landlord's database and
	 * cancels all leases for that rental unit.
	 * 
	 * @param propertyIndex Index for the rental unit to be closed (subject to
	 *                      filtering)
	 * @throws IllegalArgumentException if propertyIndex is not a valid index for
	 *                                  the rental units currently under
	 *                                  consideration
	 */
	@Override
	public void closeRentalUnit(int propertyIndex) {
		SortedLinkedListWithIterator<RentalUnit> filteredList = this.getFilteredRoomList();
		if (propertyIndex >= filteredList.size() || propertyIndex < 0) {
			throw new IllegalArgumentException("index outside of list");
		}
		String location = filteredList.get(propertyIndex).getFloor() + "-" + filteredList.get(propertyIndex).getRoom();
		RentalUnit ret = this.getUnitAtLocation(location);
		String[] rentalClients = ret.listLeases();
		for (int i = 0; i < rentalClients.length; i++) {
			Scanner stringScan = new Scanner(rentalClients[i]);
			int confNumRental = stringScan.nextInt();
			for (int j = 0; j < customerBase.size(); j++) {
				String[] customerLeases = customerBase.get(j).listLeases();
				for (int k = 0; k < customerLeases.length; k++) {
					Scanner scan = new Scanner(customerLeases[k]);
					int confNumCustomer = scan.nextInt();
					if (confNumCustomer == confNumRental) {
						customerBase.get(j).cancelLeaseWithNumber(confNumCustomer);
					}
					scan.close();
				}
			}
			stringScan.close();
		}
		rooms.remove(rooms.indexOf(ret));
	}

	/**
	 * Returns the rental unit at the given position to service. Does nothing if the
	 * rental unit is already in service or if the position does not correspond to
	 * any rental unit (subject to filtering).
	 * 
	 * @param propertyIndex Position/index of the rental unit (subject to filtering)
	 * @throws IllegalArgumentException if propertyIndex is not a valid index for
	 *                                  the rental units currently under
	 *                                  consideration
	 */
	@Override
	public void returnToService(int propertyIndex) {
		if (propertyIndex >= rooms.size() || propertyIndex < 0) {
			throw new IllegalArgumentException();
		}
		String location = this.getFilteredRoomList().get(propertyIndex).getFloor() + "-"
				+ this.getFilteredRoomList().get(propertyIndex).getRoom();
		this.getUnitAtLocation(location).returnToService();
	}

	/**
	 * Gets all the clients for this property as a array of strings.
	 * 
	 * @return an array of strings, where each string describes a client
	 */
	@Override
	public String[] listClients() {
		String[] retStr = new String[customerBase.size()];
		for (int i = 0; i < customerBase.size(); i++) {
			retStr[i] = customerBase.get(i).getName() + " (" + customerBase.get(i).getId() + ")";
		}
		return retStr;
	}

	/**
	 * What are the leases for a particular client?
	 * 
	 * @param clientIndex Index of the targeted client in the PropertyManagers's
	 *                    list of clients
	 * @return an array of strings in which each string describes a lease for the
	 *         targeted client
	 * @throws IllegalArgumentException if the clientIndex does not correspond to
	 *                                  any client.
	 */
	@Override
	public String[] listClientLeases(int clientIndex) {
		if (clientIndex >= customerBase.size() || clientIndex < 0) {
			throw new IllegalArgumentException();
		}
		String[] leases = customerBase.get(clientIndex).listLeases();
		return leases;
	}

	/**
	 * What are the rental units for this landlord? (Consider only the units that
	 * meet filters currently in place.)
	 * 
	 * @return an array of strings in which each string describes a rental unit that
	 *         meets all filters in place. There are exactly as many strings in the
	 *         array as there are such rental units.
	 */
	@Override
	public String[] listRentalUnits() {
		String[] retStr;
		if (!kindFilter.equals("A") || inServiceFilter) {
			SortedLinkedListWithIterator<RentalUnit> filtered = getFilteredRoomList();
			retStr = new String[filtered.size()];
			for (int i = 0; i < filtered.size(); i++) {
				retStr[i] = filtered.get(i).getDescription();
			}
		} else {
			retStr = new String[rooms.size()];
			for (int i = 0; i < rooms.size(); i++) {
				retStr[i] = rooms.get(i).getDescription();
			}
		}
		return retStr;
	}

	/**
	 * Private helper method that returns a SortedLinkedList of rental units which
	 * is filtered according to the state of inServiceFilter and kindFilter.
	 * 
	 * @return filtered list of RentalUnits
	 */
	private SortedLinkedListWithIterator<RentalUnit> getFilteredRoomList() {
		SortedLinkedListWithIterator<RentalUnit> filtered = new SortedLinkedListWithIterator<RentalUnit>();
		if (kindFilter.equals("C") || kindFilter.equals("H") || kindFilter.equals("O")) {
			for (int i = 0; i < rooms.size(); i++) {// creates new sorted list of filtered units
				RentalUnit rental = rooms.get(i);
				if (this.inServiceFilter) {
					if (rental.isInService() && kindFilter.equals("C") && rental instanceof ConferenceRoom) {
						filtered.add(rental);
					}
					if (rental.isInService() && kindFilter.equals("H") && rental instanceof HotelSuite) {
						filtered.add(rental);
					}
					if (rental.isInService() && kindFilter.equals("O") && rental instanceof Office) {
						filtered.add(rental);
					}
				} else {
					if (kindFilter.equals("C") && rental instanceof ConferenceRoom) {
						filtered.add(rental);
					}
					if (kindFilter.equals("H") && rental instanceof HotelSuite) {
						filtered.add(rental);
					}
					if (kindFilter.equals("O") && rental instanceof Office) {
						filtered.add(rental);
					}
				}
			}
		} else if (inServiceFilter) {
			for (int i = 0; i < rooms.size(); i++) {
				RentalUnit rental = rooms.get(i);
				if (rental.isInService()) {
					filtered.add(rental);
				}
			}
		} else {
			return rooms;
		}
		return filtered;
	}

	/**
	 * What are the leases for the rental unit at this particular index in the
	 * filtered list of rental units?
	 * 
	 * @param propertyIndex Index of the targeted rental unit (subject to filtering)
	 * @return an array of strings in which each string describes a lease for the
	 *         targeted rental unit.
	 * @throws IllegalArgumentException if propertyIndex is not a valid index for
	 *                                  the rental units currently under
	 *                                  consideration
	 */
	@Override
	public String[] listLeasesForRentalUnit(int propertyIndex) {
		if (propertyIndex >= rooms.size() || propertyIndex < 0) {
			throw new IllegalArgumentException();
		}
		SortedLinkedListWithIterator<RentalUnit> filtered = getFilteredRoomList();
		String[] retStr = filtered.get(propertyIndex).listLeases();
		return retStr;
	}

	/**
	 * Returns the rental unit information at provided location.
	 * 
	 * @param location location of the unit in the building in the format floor-room
	 * @return rental unit at the specified location
	 * @throws IllegalArgumentException if an invalid location string was provided
	 *                                  of the rental unit could not be found.
	 */
	public RentalUnit getUnitAtLocation(String location) {
		String[] floorRoom = location.split("-");
		if (floorRoom == null || floorRoom.length != 2) {
			throw new IllegalArgumentException("invalid location string");
		}
		try {// check floor and room for validity and construct object
			int floorR = Integer.parseInt(floorRoom[0]);
			int roomR = Integer.parseInt(floorRoom[1]);
			for (int i = 0; i < rooms.size(); i++) {
				if (floorR == rooms.get(i).getFloor() && roomR == rooms.get(i).getRoom()) {
					return rooms.get(i);
				}
			}
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("invalid location string");
		}
		throw new IllegalArgumentException("unit not found");
	}

	/**
	 * Removes all Landlord data and resets the reservation confirmation numbering
	 * to 0.
	 */
	@Override
	public void flushAllData() {
		Lease.resetConfirmationNumbering(0);
		customerBase = new SimpleArrayList<Client>();
		rooms = new SortedLinkedListWithIterator<RentalUnit>();
	}

	/**
	 * Gets the PropertyManagers instance variable.
	 * 
	 * @return Instance variable
	 */
	public static PropertyManager getInstance() {
		if (instance == null) {
			instance = new PropertyManager();
		}
		return instance;
	}

}
