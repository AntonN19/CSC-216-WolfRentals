/**
 * 
 */
package edu.ncsu.csc216.business.model.properties;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

import edu.ncsu.csc216.business.list_utils.SortedLinkedListWithIterator;
import edu.ncsu.csc216.business.list_utils.SortedList;
import edu.ncsu.csc216.business.model.contracts.Lease;
import edu.ncsu.csc216.business.model.stakeholders.Client;

/**
 * Stores data and methods for a Hotel Suite.
 * 
 * @author Anton
 *
 */
public class HotelSuite extends RentalUnit {

	/** The maximum capacity allowed for the Hotel Room */
	public static final int MAX_CAPACITY = 2;

	/**
	 * Constructor for hotel suite without a provided capacity
	 * 
	 * @param location hotel suite location
	 */
	public HotelSuite(String location) {
		super(location, 1);
	}

	/**
	 * Constructor for hotel suite with a provided capacity
	 * 
	 * @param location hotel suite location
	 * @param capacity requested capacity
	 */
	public HotelSuite(String location, int capacity) {
		super(location, capacity);

		if (capacity > MAX_CAPACITY) {
			throw new IllegalArgumentException("capacity over max");
		}
	}

	/**
	 * Reserves a lease for this hotel suite
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
	 *                                     conflict with another lease, dates the
	 *                                     start and end dates are not Sundays or if
	 *                                     the non-Sunday dates in the lease overlap
	 *                                     with another lease, or dates out of
	 *                                     range.
	 * @throws RentalCapacityException     if the rental unit cannot hold the number
	 *                                     of occupants over the dates of the
	 *                                     proposed lease.
	 */
	@Override
	public Lease reserve(Client c, LocalDate d, int leaseDuration, int occupants)
			throws RentalOutOfServiceException, RentalDateException, RentalCapacityException {
		// Checking for exceptions
		super.checkLeaseConditions(c, d, leaseDuration, occupants); // Throws IllegalArgument and RentalOutOfService
		LocalDate endD = d.plusWeeks(leaseDuration);
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
	 * Finds an existing lease for this hotel suite and returns its value.
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
			if (l.getStart().isEqual(newLease.getStart()) || l.getEnd().isEqual(newLease.getEnd())) {
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

	/**
	 * Removes the hotel suite from service starting on the given date and removes
	 * all leases with their start dates on or after the cutoff date
	 * 
	 * @param date starting date of rental unit closure
	 * @return the list of leases that were removed due to rental unit closure
	 */
	public SortedList<Lease> removeFromServiceStarting(LocalDate date) {
		SortedList<Lease> returnList = new SortedLinkedListWithIterator<Lease>();
		if (date.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
			returnList = super.removeFromServiceStarting(date);
		} else {
			returnList = super.removeFromServiceStarting(date.with(TemporalAdjusters.next(DayOfWeek.SUNDAY)));
			for (int i = 0; i < myLeases.size(); i++) {
				// checks for leases with end dates on or after the cutoff date
				if (myLeases.get(i).getEnd().isAfter(date)) {
					// sets end date to Sunday before cutoff date
					myLeases.get(i).setEndDateEarlier(date.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY)));
					// checks dates and remove lease if it falls on same day
					try {
						checkDates(myLeases.get(i).getStart(), myLeases.get(i).getEnd());
					} catch (RentalDateException e) {
						myLeases.remove(i);
					}
				}
			}
		}
		this.takeOutOfService();
		return returnList;
	}

	/**
	 * Returns the description of this hotel suite as a String
	 * 
	 * @return the hotel suite description
	 */
	public String getDescription() {
		String retStr = this.getFloor() + "-" + this.getRoom() + " | ";
		retStr = String.format("Hotel Suite: %13s", retStr);
		retStr = String.format(retStr + "%3s", this.getCapacity());
		if (!this.isInService()) {
			retStr += "  Unavailable";
		}
		return retStr;
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
	@Override
	public void checkDates(LocalDate startD, LocalDate endD) throws RentalDateException {
		super.checkDates(startD, endD);
		if (startD.getDayOfWeek() != DayOfWeek.SUNDAY || endD.getDayOfWeek() != DayOfWeek.SUNDAY) {
			throw new RentalDateException("invalid day of week");
		}
		if (startD.isEqual(endD)) {
			throw new RentalDateException("lease cannot start and end on same day");
		}
	}
}
