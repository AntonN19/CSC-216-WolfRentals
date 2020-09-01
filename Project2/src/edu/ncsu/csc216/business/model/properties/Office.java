/**
 * 
 */
package edu.ncsu.csc216.business.model.properties;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

import edu.ncsu.csc216.business.list_utils.SortedList;
import edu.ncsu.csc216.business.model.contracts.Lease;
import edu.ncsu.csc216.business.model.stakeholders.Client;

/**
 * Stores data and methods for an office rental. Child class of RentalUnit.
 * 
 * @author Anton
 *
 */
public class Office extends RentalUnit {

	/** Maximum allowed capacity for offices */
	private static final int MAX_CAPACITY = 150;

	/** Calendar rows */
	private static final int CAL_ROWS = 10;

	/** Calendar columns */
	private static final int CAL_COLS = 12;

	/** The office calendar which contains the amount of occupants for each month */
	private int[][] calendar = new int[CAL_ROWS][CAL_COLS];

	/**
	 * Constructor for the Office class
	 * 
	 * @param location office location in the form floor-room
	 * @param capacity office capacity
	 * @throws IllegalArgumentException if capacity is greater than MAX_CAPACITY
	 */
	public Office(String location, int capacity) {
		super(location, capacity);
		if (capacity > MAX_CAPACITY) {
			throw new IllegalArgumentException("capacity over max");
		}
	}

	/**
	 * Reserves a lease for this office
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
	 *                                     conflict with another lease or the start
	 *                                     date is not the first day of the month
	 *                                     and the end date is not the last day of a
	 *                                     month, or dates out of range.
	 * @throws RentalCapacityException     if the rental unit cannot hold the number
	 *                                     of occupants over the dates of the
	 *                                     proposed lease.
	 */
	@Override
	public Lease reserve(Client c, LocalDate d, int leaseDuration, int occupants)
			throws RentalOutOfServiceException, RentalDateException, RentalCapacityException {
		// Checking for exceptions
		super.checkLeaseConditions(c, d, leaseDuration, occupants); // Throws IllegalArgument and RentalOutOfService
		LocalDate endD = d.plusMonths(leaseDuration);
		endD = endD.minusDays(1);
		this.checkDates(d, endD); // throw RentalDateException
		// creates new lease
		Lease newLease = new Lease(c, this, d, endD, occupants);
		// add occupancy to calendar and check for capacity breach
		int duration = leaseDuration;
		LocalDate date = d;
		for (int i = 0; i < duration; i++) {
			if (occupants > remainingCapacityFor(date)) {
				throw new RentalCapacityException();
			}
			int year = date.getYear() - 2020;
			int month = date.getMonthValue() - 1;
			calendar[year][month] += occupants;
			date = date.plusMonths(1);
		}
		this.addLease(newLease);
		return newLease;
	}

	/**
	 * Finds an existing lease for this office and returns its value.
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
		// add occupancy to calendar and check for capacity breach
		int duration = getMonthsDuration(startD, endD);
		LocalDate date = startD;
		for (int i = 0; i < duration; i++) {
			if (occupants > remainingCapacityFor(date)) {
				throw new RentalCapacityException();
			}
			int year = date.getYear() - 2020;
			int month = date.getMonthValue() - 1;
			calendar[year][month] += occupants;
			date = date.plusMonths(1);
		}
		this.addLease(newLease);
		return newLease;
	}

	/**
	 * Returns the amount of capacity that is left for this office for the provided
	 * date.
	 * 
	 * @param date the date provided
	 * @return amount of capacity left
	 * @throws IllegalArgumentException if the date is not between 1/1/2020 and
	 *                                  12/31/2029
	 */
	protected int remainingCapacityFor(LocalDate date) {
		LocalDate startValid = LocalDate.of(2020, Month.JANUARY, 1);
		LocalDate endValid = LocalDate.of(2029, Month.DECEMBER, 31);
		if (date.isBefore(startValid) || date.isAfter(endValid)) {
			throw new IllegalArgumentException("invalid date");
		}
		int year = date.getYear() - 2020;
		int month = date.getMonthValue() - 1;
		int remainingCap = this.getCapacity() - calendar[year][month];
		return remainingCap;
	}

	/**
	 * Removes the office from service starting on the given date and removes all
	 * leases with the start dates on or after the cutoff date
	 * 
	 * @param date starting date of office closure
	 * @return the list of leases that were removed due to office closure
	 */
	public SortedList<Lease> removeFromServiceStarting(LocalDate date) {
		SortedList<Lease> returnList;
		if (date.getDayOfMonth() == 1) {
			returnList = super.removeFromServiceStarting(date);
		} else {
			returnList = super.removeFromServiceStarting(date.with(TemporalAdjusters.firstDayOfMonth()));
		}
		int size = myLeases.size();
		for (int i = 0; i < size; i++) {
			// checks for leases with end dates on or after the cutoff date
			if (myLeases.get(i).getEnd().isAfter(date)) {
				// sets end date to last day of month before cutoff
				myLeases.get(i)
						.setEndDateEarlier(date.minus(1, ChronoUnit.MONTHS).with(TemporalAdjusters.lastDayOfMonth()));

				// checks dates and remove lease if it falls on same day
				try {
					checkDates(myLeases.get(i).getStart(), myLeases.get(i).getEnd());
				} catch (RentalDateException e) {
					myLeases.remove(i);
				}
			}
		}
		this.takeOutOfService();
		return returnList;
	}

	/**
	 * Returns the number of months between the provided dates.
	 * 
	 * @param startD the starting date
	 * @param endD   the ending date
	 * @return the number of months between the two dates
	 */
	protected static int getMonthsDuration(LocalDate startD, LocalDate endD) {
		int ret = 1;
		int yearDiff = endD.getYear() - startD.getYear();
		int monthDiff = endD.getMonthValue() - startD.getMonthValue();
		ret += CAL_COLS * yearDiff + monthDiff;
		return ret;
	}

	/**
	 * Returns the description of this office as a String
	 * 
	 * @return the hotel suite description
	 */
	public String getDescription() {
		String retStr = this.getFloor() + "-" + this.getRoom() + " | ";
		retStr = String.format("Office: %18s", retStr);
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
	public void checkDates(LocalDate startD, LocalDate endD) throws RentalDateException {
		super.checkDates(startD, endD);
		// check if dates are first of month
		if (startD.getDayOfMonth() != 1 || endD.getDayOfMonth() != endD.lengthOfMonth()) {
			throw new RentalDateException();
		}
	}

}
