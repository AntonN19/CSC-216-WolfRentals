/**
 * 
 */
package edu.ncsu.csc216.business.model.properties;

/**
 * New Exception made to be thrown when a lease is created that the rental unit
 * does not have the capacity to provide
 * 
 * @author Anton
 *
 */
public class RentalCapacityException extends Exception {

	/** Id used for object serialization */
	private static final long serialVersionUID = 1L;

	/** Default message for exception */
	private static final String DEFAULT_MESSAGE = "Invalid Rental Capacity";

	/**
	 * Constructor used to assign a new message to exception
	 * 
	 * @param message message to be attached
	 */
	public RentalCapacityException(String message) {
		super(message);
	}

	/**
	 * Parameterless constructor which assigns default message to exception
	 */
	public RentalCapacityException() {
		this(DEFAULT_MESSAGE);
	}
}
