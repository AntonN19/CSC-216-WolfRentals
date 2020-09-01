/**
 * 
 */
package edu.ncsu.csc216.business.model.properties;

import java.time.LocalDate;
import java.time.Period;

import edu.ncsu.csc216.business.list_utils.SortedList;
import edu.ncsu.csc216.business.model.contracts.Lease;
import edu.ncsu.csc216.business.model.stakeholders.Client;

/**
 * Stores data and methods for a Conference room. A child class of RentalUnit.
 * 
 * @author Anton
 *
 */
public class ConferenceRoom extends RentalUnit {

	/** Maximum allowed capacity for the Conference Room */
	public static final int MAX_CAPACITY = 25;

	/** Maximum allowed duration for conference room reservation */
	public static final int MAX_DURATION = 6;

	/**
	 * Constructor for Conference Room
	 * 
	 * @param location location of Conference room in the format floor-room
	 * @param capacity the conference room capacity
	 * @throws IllegalArgumentException if the provided capacity is larger than
	 *                                  MAX_CAPACITY
	 */
	public ConferenceRoom(String location, int capacity) {
		super(location, capacity);
		if (capacity > MAX_CAPACITY) {
			throw new IllegalArgumentException("Capacity is greater than max allowed");
		}

	}

	/**
	 * Reserves a lease for this Conference room. And returns the lease that was
	 * just added
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
	 *                                     conflict with another lease, or dates out
	 *                                     of range.
	 * @throws RentalCapacityException     if the rental unit cannot hold the number
	 *                                     of occupants over the dates of the
	 *                                     proposed lease.
	 */
	@Override
	public Lease reserve(Client c, LocalDate d, int leaseDuration, int occupants)
			throws RentalOutOfServiceException, RentalDateException, RentalCapacityException {
		// Checking for exceptions
		super.checkLeaseConditions(c, d, leaseDuration, occupants); // Throws IllegalArgument and RentalOutOfService
		LocalDate endD = d.plusDays(leaseDuration - 1);
		this.checkDates(d, endD); // throw RentalDateException
		// creates new lease
		Lease newLease = new Lease(c, this, d, endD, occupants);
		try {
			checkLeaseConflict(newLease); // check for conflict with other leases in list throws RentalDateException
		} catch (RentalDateException e) {
			Lease.resetConfirmationNumbering(newLease.getConfirmationNumber());
			throw new RentalDateException();
		}
		if (occupants > this.getCapacity()) {
			throw new RentalCapacityException();
		}
		this.addLease(newLease);
		return newLease;
	}

	/**
	 * Finds an existing lease for this conference room and returns its value.
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
	@Override
	public Lease recordExistingLease(int confNumber, Client c, LocalDate startD, LocalDate endD, int occupants)
			throws RentalDateException, RentalCapacityException {
		checkDates(startD, endD); // throw RentalDateException
		Lease newLease = new Lease(confNumber, c, this, startD, endD, occupants);
		try {
			checkLeaseConflict(newLease); // check for conflict with other leases in list throws RentalDateException
		} catch (RentalDateException e) {
			Lease.resetConfirmationNumbering(newLease.getConfirmationNumber());
			throw new RentalDateException();
		}
		if (occupants > this.getCapacity()) {
			throw new RentalCapacityException();
		}
		this.addLease(newLease);
		return newLease;
	}

	/**
	 * Removes the conference room from service starting on the given date and
	 * removes all leases with their start dates on or after the cutoff date
	 * 
	 * @param date starting date of rental unit closure
	 * @return the list of leases that were removed due to rental unit closure
	 */
	public SortedList<Lease> removeFromServiceStarting(LocalDate date) {
		SortedList<Lease> returnList = super.removeFromServiceStarting(date);
		for (int i = 0; i < myLeases.size(); i++) {
			// checks for leases with end dates on or after the cutoff date
			if (myLeases.get(i).getEnd().isAfter(date) || myLeases.get(i).getEnd().isEqual(date)) {
				myLeases.get(i).setEndDateEarlier(date.minusDays(1)); // sets end date to day before cutoff date
			}
		}
		return returnList;
	}

	/**
	 * Returns the description of this conference room as a String
	 * 
	 * @return the Conference room description
	 */
	public String getDescription() {
		String retStr = this.getFloor() + "-" + this.getRoom() + " | ";
		retStr = String.format("Conference Room: %9s", retStr);
		retStr = String.format(retStr + "%3s", this.getCapacity());
		if (!this.isInService()) {
			retStr += "  Unavailable";
		}
		return retStr;
	}

	/**
	 * Checks if the dates fall between Jan 1, 2020 - Dec, 31 2029 and checks if the
	 * start date is before the end date. Also checks that start date and end date
	 * are not more than 7 days apart. Throws exception if the above conditions are
	 * not met
	 * 
	 * @param startD Start date
	 * @param endD   End date
	 * @throws RentalDateException if the above conditions are not met
	 */
	@Override
	public void checkDates(LocalDate startD, LocalDate endD) throws RentalDateException {
		super.checkDates(startD, endD);
		Period interval = Period.between(startD, endD);
		if (interval.getDays() > MAX_DURATION) {
			throw new RentalDateException("duration exceeds limit");
		}
	}

	/**
	 * Private helper method to check for conflict of a new lease with leases
	 * already made.
	 * 
	 * @param newLease new Lease being checked for conflict
	 * @throws RentalDateException if a conflict exists
	 */
	private void checkLeaseConflict(Lease newLease) throws RentalDateException {
		for (int i = 0; i < myLeases.size(); i++) {
			Lease l = myLeases.get(i);
			// if starts or end dates are equal or of start date is equal to end date and
			// vice versa
			if (l.getStart().isEqual(newLease.getStart()) || l.getEnd().isEqual(newLease.getEnd())
					|| l.getEnd().isEqual(newLease.getStart()) || l.getStart().isEqual(newLease.getEnd())) {
				throw new RentalDateException();
			}
			// checks if new lease start date is between the old lease start and end dates
			if (l.getStart().isBefore(newLease.getStart()) && l.getEnd().isAfter(newLease.getStart())) {
				throw new RentalDateException();
			}
			// checks if new lease end date is between the old lease start and end dates
			if (l.getStart().isBefore(newLease.getEnd()) && l.getEnd().isAfter(newLease.getEnd())) {
				throw new RentalDateException();
			}
		}
	}
}
