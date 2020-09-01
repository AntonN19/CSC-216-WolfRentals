/**
 * 
 */
package edu.ncsu.csc216.business.model.stakeholders;

/**
 * Custom exception to be thrown when there is a duplicate client in the list.
 * 
 * @author Anton
 *
 */
public class DuplicateClientException extends Exception {

	/** Id used for object serialization */
	private static final long serialVersionUID = 1L;

	/** Default message for exception */
	private static final String DEFAULT_MESSAGE = "Duplicate Client";

	/**
	 * Constructor used to assign a new message to exception
	 * 
	 * @param message message to be attached
	 */
	public DuplicateClientException(String message) {
		super(message);
	}

	/**
	 * Parameterless constructor which assigns default message to exception
	 */
	public DuplicateClientException() {
		this(DEFAULT_MESSAGE);
	}
}
