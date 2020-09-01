package edu.ncsu.csc216.business.model.properties;

/**
 * Exception to be thrown for an invalid rental date.
 * 
 * @author Anton
 */
public class RentalDateException extends Exception {

	/** Id used for object serialization */
	private static final long serialVersionUID = 1L;

	/** Default message for exception */
	private static final String DEFAULT_MESSAGE = "Invalid Rental Date";

	/**
	 * Constructor used to assign a new message to exception
	 * 
	 * @param message message to be attached
	 */
	public RentalDateException(String message) {
		super(message);
	}

	/**
	 * Parameterless constructor which assigns default message to exception
	 */
	public RentalDateException() {
		this(DEFAULT_MESSAGE);
	}
}
