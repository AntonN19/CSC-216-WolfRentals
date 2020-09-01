package edu.ncsu.csc216.business.model.properties;

import java.time.LocalDate;
import java.time.Month;

import edu.ncsu.csc216.business.list_utils.SortedLinkedListWithIterator;
import edu.ncsu.csc216.business.list_utils.SortedList;
import edu.ncsu.csc216.business.model.contracts.Lease;
import edu.ncsu.csc216.business.model.stakeholders.Client;

/**
 * Parent abstract class for the different types of rental units. Implements
 * comparable to compare rental units position.
 * 
 * @author Anton
 */
public abstract class RentalUnit implements Comparable<RentalUnit> {

	/** Highest floor in the building */
	private static final int MAX_FLOOR = 45;

	/** Lowest floor in the building */
	private static final int MIN_FLOOR = 1;

	/** Max room number allowed in the building */
	private static final int MAX_ROOM = 99;

	/** Minimum room number allowed in the building */
	private static final int MIN_ROOM = 10;

	/** Is the rental unit in service or not */
	private boolean inService;

	/** Rental unit floor */
	private int floor;

	/** Rental unit room number */
	private int room;

	/** Rental Unit capacity */
	private int capacity;

	/**
	 * A Linked list that contains all of the leases for this rental unit sorted in
	 * order
	 */
	protected SortedLinkedListWithIterator<Lease> myLeases = new SortedLinkedListWithIterator<Lease>();

	/**
	 * Abstract method intended to reserve a lease
	 * 
	 * @param c             Client that the lease is being reserved for
	 * @param d             starting date of reservation
	 * @param leaseDuration duration of reservation in days, weeks, or months
	 * @param occupants     amount of occupants
	 * @return The newly created lease
	 * @throws RentalOutOfServiceException if the rental unit is currently out of
	 *                                     service and not available for lease.
	 * @throws RentalDateException         if the start date or computed end dates
	 *                                     are not valid, which could be from
	 *                                     conflict with another lease, dates
	 *                                     improper for the given type of rental
	 *                                     unit, or dates out of range.
	 * @throws RentalCapacityException     if the rental unit cannot hold the number
	 *                                     of occupants over the dates of the
	 *                                     proposed lease.
	 */
	public abstract Lease reserve(Client c, LocalDate d, int leaseDuration, int occupants)
			throws RentalOutOfServiceException, RentalDateException, RentalCapacityException;

	/**
	 * Abstract method used to find an existing lease and return its value.
	 * 
	 * @param confNumber Lease confirmation number
	 * @param c          Client that the lease is being reserved for
	 * @param startD     Lease starting date
	 * @param endD       Lease ending date
	 * @param occupants  amount of occupants
	 * @return the lease that was found
	 * @throws RentalDateException     if the start date or computed end dates are
	 *                                 not valid, which could be from conflict with
	 *                                 another lease, dates improper for the given
	 *                                 type of rental unit, or dates out of range.
	 * @throws RentalCapacityException if the rental unit cannot hold the number of
	 *                                 occupants over the dates of the proposed
	 *                                 lease.
	 */
	public abstract Lease recordExistingLease(int confNumber, Client c, LocalDate startD, LocalDate endD, int occupants)
			throws RentalDateException, RentalCapacityException;

	/**
	 * Constructor for the rental unit
	 * 
	 * @param location The room location in the format floor-room
	 * @param capacity the rental units capacity
	 * @throws IllegalArgumentException if either the capacity or location string
	 *                                  are invalid.
	 */
	public RentalUnit(String location, int capacity) {
		if (capacity <= 0) {
			throw new IllegalArgumentException("invalid capacity");
		}
		if (location == null || location.length() == 0) {
			throw new IllegalArgumentException("invalid location");
		}
		// split string and check that its properly formatted
		String[] floorRoom = location.split("-");
		if (floorRoom == null || floorRoom.length != 2) {
			throw new IllegalArgumentException("invalid location string");
		}

		try {// check floor and room for validity and construct object
			int floorR = Integer.parseInt(floorRoom[0]);
			int roomR = Integer.parseInt(floorRoom[1]);
			if (floorR < MIN_FLOOR || floorR > MAX_FLOOR || roomR < MIN_ROOM || roomR > MAX_ROOM) {
				throw new IllegalArgumentException("invalid floor/room");
			}
			this.floor = floorR;
			this.room = roomR;
			this.capacity = capacity;
			this.inService = true;
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("invalid location string");
		}
	}

	/**
	 * Getter method for this rental units capacity
	 * 
	 * @return the Rental units capacity
	 */
	public int getCapacity() {
		return this.capacity;
	}

	/**
	 * Getter method for rental units floor number
	 * 
	 * @return the rental units floor number
	 */
	public int getFloor() {
		return this.floor;
	}

	/**
	 * Getter method for Rental units room number
	 * 
	 * @return the room number
	 */
	public int getRoom() {
		return this.room;
	}

	/**
	 * Compares the rental unit locations
	 * 
	 * @param r rental unit being compared
	 * @return difference between positions of rental units
	 */
	public int compareTo(RentalUnit r) {
		int ret = 0;
		ret = this.floor - r.getFloor();
		if (ret == 0) {
			ret = this.room - r.getRoom();
		}
		return ret;
	}

	/**
	 * Returns a rental unit back to service
	 */
	public void returnToService() {
		this.inService = true;
	}

	/**
	 * Checks if the rental unit is in service
	 * 
	 * @return true only if the rental unit is in service
	 */
	public boolean isInService() {
		return this.inService;
	}

	/**
	 * Remove a rental unit from service
	 */
	public void takeOutOfService() {
		this.inService = false;
	}

	/**
	 * Checks if the dates fall between Jan 1, 2020 - Dec, 31 2029 and checks if the
	 * start date is before the end date. Throws exception if the above conditions
	 * are not met
	 * 
	 * @param startD Start date
	 * @param endD   End date
	 * @throws RentalDateException if the above conditions are not met
	 */
	public void checkDates(LocalDate startD, LocalDate endD) throws RentalDateException {
		LocalDate startValid = LocalDate.of(2020, Month.JANUARY, 1);
		LocalDate endValid = LocalDate.of(2029, Month.DECEMBER, 31);
		if (startD.isBefore(startValid) || endD.isBefore(startValid) || startD.isAfter(endValid)
				|| endD.isAfter(endValid)) {
			throw new RentalDateException("invalid date");
		}
		if (!(startD.isBefore(endD) || startD.isEqual(endD))) {
			throw new RentalDateException("end date before start date");
		}
	}

	/**
	 * Checks if the lease conditions make sense and meet all of the requirements
	 * throws an exception if the requirements are not met
	 * 
	 * @param c         Client requesting the lease
	 * @param d         starting date
	 * @param duration  lease duration
	 * @param occupants amount of occupants
	 * @throws IllegalArgumentException    if any parameters are null or
	 *                                     duration/occupants is less than 1
	 * @throws RentalOutOfServiceException if the requested rental unit is out of
	 *                                     service
	 */
	protected void checkLeaseConditions(Client c, LocalDate d, int duration, int occupants)
			throws RentalOutOfServiceException, IllegalArgumentException {
		if (c == null || d == null || duration < 1 || occupants < 1) {
			throw new IllegalArgumentException("invalid input");
		}
		if (!this.isInService()) {
			throw new RentalOutOfServiceException("not in service");
		}
	}

	/**
	 * Removes the rental unit from service starting on the given date and removes
	 * all leases with the start dates on or after the cutoff date
	 * 
	 * @param d starting date of rental unit closure
	 * @return the list of leases that were removed due to rental unit closure
	 */
	public SortedList<Lease> removeFromServiceStarting(LocalDate d) {
		SortedList<Lease> returnList;
		int dateIndex = this.cutoffIndex(d);
		if (dateIndex == -1) {
			returnList = new SortedLinkedListWithIterator<Lease>();
		} else {
			returnList = myLeases.truncate(dateIndex);
		}
		this.takeOutOfService();
		return returnList;
	}

	/**
	 * Finds the index of the first lease with a start date on or after the given
	 * start date
	 * 
	 * @param d the provided start date
	 * @return the lease index or -1 if no leases match the above conditions
	 */
	protected int cutoffIndex(LocalDate d) {
		int ret = -1;
		for (int i = 0; i < myLeases.size(); i++) {
			Lease l = myLeases.get(i);
			if (!l.getStart().isBefore(d)) {
				ret = i;
				break;
			}
		}
		return ret;
	}

	/**
	 * Cancels the lease with the provided confirmation number
	 * 
	 * @param number the given confirmation number
	 * @return the lease that was cancelled
	 * @throws IllegalArgumentException if there is no leases with the given
	 *                                  confirmation number
	 */
	public Lease cancelLeaseByNumber(int number) {
		for (int i = 0; i < myLeases.size(); i++) {
			if (myLeases.get(i).getConfirmationNumber() == number) {
				Lease l = myLeases.remove(i);
				return l;
			}
		}
		throw new IllegalArgumentException("No such lease");
	}

	/**
	 * Adds a lease to this units list of leases
	 * 
	 * @param l the lease that needs to be added
	 * @throws IllegalArgumentException if the lease is made for a different rental
	 *                                  unit
	 */
	public void addLease(Lease l) {
		if (!inService) {
			return;
		}
		// works only if rental unit is in service
		if (l.getProperty().getFloor() != this.floor || l.getProperty().getRoom() != this.room) {
			throw new IllegalArgumentException("Lease is not for this rental unit");
		}
		myLeases.add(l);
	}

	/**
	 * Gives a list of leases for this rental unit as and array of Strings, with
	 * each string representing a different lease ex: 000607 | 2020-02-01 to
	 * 2020-06-30 | 6 | James Tetterton (jc1012)
	 *
	 * @return the list of leases as a String array
	 */
	public String[] listLeases() {
		String[] leases = new String[myLeases.size()];
		for (int i = 0; i < myLeases.size(); i++) {
			Lease l = myLeases.get(i);
			String[] data = l.leaseData();
			leases[i] = data[0] + " | " + data[1] + " | ";
			leases[i] = String.format(leases[i] + "%3s | " + data[4] + " (" + data[5] + ")", data[2]);
		}
		return leases;
	}

	/**
	 * Gets this rental units description as a String
	 * 
	 * @return a String in the format floor, room, capacity and when needed the
	 *         Unavailable notation
	 */
	public String getDescription() {
		String retStr = this.getFloor() + "-" + this.getRoom() + " | " + this.getCapacity();
		if (!this.isInService()) {
			retStr += "  Unavailable";
		}
		return retStr;
	}

	/**
	 * Finds the hashCode for this rental unit
	 * 
	 * @return the rental units hash code
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + floor;
		result = prime * result + room;
		return result;
	}

	/**
	 * Determines if this rental unit is equal to another object
	 * 
	 * @param obj the object that the rental unit is being compared to
	 * @return true only if this rental unit is equal to the provided object
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof RentalUnit))
			return false;
		RentalUnit other = (RentalUnit) obj;
		if (floor != other.floor)
			return false;
		if (room != other.room)
			return false;
		return true;
	}
}
