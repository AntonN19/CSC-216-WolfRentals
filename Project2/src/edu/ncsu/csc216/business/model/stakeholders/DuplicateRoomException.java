/**
 * 
 */
package edu.ncsu.csc216.business.model.stakeholders;

/**
 * Custom exception to be thrown when a duplicate room is being added to a list.
 * 
 * @author Anton
 *
 */
public class DuplicateRoomException extends Exception {
	/** Id used for object serialization */
	private static final long serialVersionUID = 1L;

	/** Default message for exception */
	private static final String DEFAULT_MESSAGE = "Invalid Rental Capacity";

	/**
	 * Constructor used to assign a new message to exception
	 * 
	 * @param message message to be attached
	 */
	public DuplicateRoomException(String message) {
		super(message);
	}

	/**
	 * Parameterless constructor which assigns default message to exception
	 */
	public DuplicateRoomException() {
		this(DEFAULT_MESSAGE);
	}
}
