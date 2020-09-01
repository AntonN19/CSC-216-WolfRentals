/**
 * 
 */
package edu.ncsu.csc216.business.model.contracts;

import java.time.LocalDate;

import edu.ncsu.csc216.business.model.properties.*;
import edu.ncsu.csc216.business.model.stakeholders.Client;

/**
 * Class that contains information and behavior that can be applied to a lease.
 * 
 * @author Anton
 */
public class Lease implements Comparable<Lease> {

	/** Field used to keep track of confirmation number growth */
	private static int confirmationCounter = 0;

	/** Leases confirmation Number */
	private int confirmationNumber;

	/** The maximum allowed confirmation number */
	private static final int MAX_CONF_NUM = 999999;

	/** Starting date of the lease */
	private LocalDate startDate;

	/** Date that the lease ends */
	private LocalDate endDate;

	/** Amount of occupants the lease is intended for */
	private int numOccupants;

	/** Client that is attached to the lease */
	private Client owner;

	/** Rental Unit that is being leased out */
	private RentalUnit property;

	/**
	 * Lease constructor without confirmation number.
	 * 
	 * @param client    client making the lease
	 * @param rental    rental unit that is being leased out
	 * @param startDate lease start date
	 * @param endDate   lease end date
	 * @param occupants number of occupants intended for the lease
	 */
	public Lease(Client client, RentalUnit rental, LocalDate startDate, LocalDate endDate, int occupants) {
		this(confirmationCounter, client, rental, startDate, endDate, occupants);
	}

	/**
	 * Lease constructor with confirmation number.
	 * 
	 * @param confNumber the lease confirmation number
	 * @param client     client making the lease
	 * @param rental     rental unit that is being leased out
	 * @param startDate  lease start date
	 * @param endDate    lease end date
	 * @param occupants  number of occupants intended for the lease
	 */
	public Lease(int confNumber, Client client, RentalUnit rental, LocalDate startDate, LocalDate endDate,
			int occupants) {
		if (confNumber >= confirmationCounter) {
			if (confNumber == MAX_CONF_NUM) {
				resetConfirmationNumbering(0);
			} else {
				resetConfirmationNumbering(1 + confNumber);
			}
		}
		this.confirmationNumber = confNumber;
		this.owner = client;
		this.property = rental;
		this.startDate = startDate;
		this.endDate = endDate;
		this.numOccupants = occupants;
	}

	/**
	 * Getter method for the lease confirmation number.
	 * 
	 * @return confirmation number
	 */
	public int getConfirmationNumber() {
		return this.confirmationNumber;
	}

	/**
	 * Getter method for the lease owner
	 * 
	 * @return the owner of this lease
	 */
	public Client getClient() {
		return this.owner;
	}

	/**
	 * Getter method for the property being leased out.
	 * 
	 * @return the rental unit that the lease is made for.
	 */
	public RentalUnit getProperty() {
		return this.property;
	}

	/**
	 * Getter method for the lease start date.
	 * 
	 * @return lease start date
	 */
	public LocalDate getStart() {
		return this.startDate;
	}

	/**
	 * Getter method for the lease end date.
	 * 
	 * @return lease end date
	 */
	public LocalDate getEnd() {
		return this.endDate;
	}

	/**
	 * Getter method for the amount of occupants on this lease
	 * 
	 * @return amount of occupants the lease was made for
	 */
	public int getNumOccupants() {
		return this.numOccupants;
	}

	/**
	 * Method used to change the lease end date to an earlier end date.
	 * 
	 * @param newEndDate the new end date
	 * @throws IllegalArgumentException if the new end date ends up being before the
	 *                                  start date
	 */
	public void setEndDateEarlier(LocalDate newEndDate) {
		if (newEndDate.isBefore(this.startDate)) {
			throw new IllegalArgumentException();
		}
		if (newEndDate.isAfter(this.endDate)) {
			throw new IllegalArgumentException();
		}
		this.endDate = newEndDate;
	}

	/**
	 * Method used to reset the confirmationCounter to a new number
	 * 
	 * @param newNumber new number to be assigned to the confirmationCounter
	 * @throws IllegalArgumentException if the new number is not between 0-999999
	 */
	public static void resetConfirmationNumbering(int newNumber) {
		if (newNumber < 0 || newNumber > MAX_CONF_NUM) {
			throw new IllegalArgumentException();
		}
		confirmationCounter = newNumber;
	}

	/**
	 * Compares this lease to another lease for order. Leases are ordered according
	 * to start dates, then end dates(if the start dates are the same).
	 * 
	 * @param l Lease being compared to
	 * @return A negative integer if the lease is less than l, 0 if they're of equal
	 *         order, and a positive integer if its greater than l.
	 */
	public int compareTo(Lease l) {
		int ret = startDate.compareTo(l.getStart());
		if (ret == 0) {
			ret = confirmationNumber - l.getConfirmationNumber();
		}
		return ret;
	}

	/**
	 * Returns lease data as a array of Strings
	 * 
	 * @return lease data array
	 */
	public String[] leaseData() {
		String[] retArr = new String[6];
		String confStr = Integer.toString(this.getConfirmationNumber());
		int max = 6 - confStr.length();
		for (int j = 0; j < max; j++) {// adds necessary amount of zeroes to start of confirmation
										// number
			confStr = 0 + confStr;
		}
		retArr[0] = confStr;
		retArr[1] = startDate.toString() + " to " + endDate.toString();
		retArr[2] = Integer.toString(this.getNumOccupants());
		if (this.property instanceof Office) {
			retArr[3] = String.format("Office:         %7s", this.property.getFloor() + "-" + this.property.getRoom());
		} else if (this.property instanceof HotelSuite) {
			retArr[3] = String.format("Hotel Suite:    %7s", this.property.getFloor() + "-" + this.property.getRoom());
		} else if (this.property instanceof ConferenceRoom) {
			retArr[3] = String.format("Conference Room:%7s", this.property.getFloor() + "-" + this.property.getRoom());
		}
		retArr[4] = this.getClient().getName();
		retArr[5] = this.getClient().getId();
		return retArr;
	}
}
