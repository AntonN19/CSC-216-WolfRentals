/**
 * 
 */
package edu.ncsu.csc216.business.model.properties;

/**
 * New Exception made to be thrown when a rental unit is out of service
 * 
 * @author Anton
 *
 */
public class RentalOutOfServiceException extends Exception {

	/** Id used for object serialization */
	private static final long serialVersionUID = 1L;

	/** Default message for exception */
	private static final String DEFAULT_MESSAGE = "Rental Unit out of Service";

	/**
	 * Constructor used to assign a new message to exception
	 * 
	 * @param message message to be attached
	 */
	public RentalOutOfServiceException(String message) {
		super(message);
	}

	/**
	 * Parameterless constructor which assigns default message to exception
	 */
	public RentalOutOfServiceException() {
		this(DEFAULT_MESSAGE);
	}
}
