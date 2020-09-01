/**
 * 
 */
package edu.ncsu.csc216.business.model.stakeholders;

import edu.ncsu.csc216.business.list_utils.SimpleArrayList;
import edu.ncsu.csc216.business.model.contracts.Lease;

/**
 * Class that provides state and behavior for a single Wolf-Rentals client
 * 
 * @author Anton
 */
public class Client {

	/** Clients name */
	private String name;

	/** Clients id */
	private String id; 

	/** list of leases associated with the client */
	private SimpleArrayList<Lease> myLeases = new SimpleArrayList<Lease>();

	/**
	 * Constructor which uses the provided name and id to make a new Client object
	 * 
	 * @param name client name
	 * @param id   clients id
	 * @throws IllegalArgumentException if either client name or id are not valid
	 */
	public Client(String name, String id) {
		if (id == null || name == null) {
			throw new IllegalArgumentException();
		} else {
			id = id.trim();
			name = name.trim();
		}
		if (!isValidName(name) || !isValidId(id)) {// checks if valid name and id
			throw new IllegalArgumentException("invalid name/id");
		}
		this.id = id;
		this.name = name;
	}

	/**
	 * Private helper method to determine if id is valid
	 * 
	 * @param id id passed to the method
	 * @return true if id is valid
	 */
	private boolean isValidId(String id) {
		if (id == null || id.length() < 3) {
			return false;
		}
		char[] idC = id.toCharArray();
		char[] allowedChars = { '@', '#', '$' };
		for (char ch : idC) {
			if (!Character.isLetterOrDigit(ch) && allowedChars[0] != ch && allowedChars[1] != ch
					&& allowedChars[2] != ch) {// check if not alphanumeric and check if one of allowed characters
				return false;
			}
		}
		return true;
	}

	/**
	 * Private method used to determine if name is valid
	 * 
	 * @param name name passed to method
	 * @return true if name is valid
	 */
	private boolean isValidName(String name) {
		if (name == null || name.length() == 0) {
			return false;
		}
		int counter = 0;
		char[] nameC = name.toCharArray();
		for (char ch : nameC) {
			if (!Character.isLetterOrDigit(ch)) {
				if (!Character.isWhitespace(ch)) {
					return false;
				} else {
					counter++;
				}
			}
		}
		if (counter == name.length()) {
			return false;
		}
		return true;
	}

	/**
	 * Getter method for the clients name
	 * 
	 * @return the clients name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Getter method for the clients id
	 * 
	 * @return the clients id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * Generates hash code for the client class
	 * 
	 * @return hash code
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/**
	 * Determines if this object is equal to another object, Clients with identical
	 * ids are considered equal.
	 * 
	 * @return true if the two are equal
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Client))
			return false;
		Client other = (Client) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	/**
	 * Adds the provided lease to the end of this clients list of leases
	 * 
	 * @param lease the provided lease
	 * @throws IllegalArgumentException if the leases does not belong to this client
	 */
	public void addNewLease(Lease lease) {
		if(lease == null) {
			throw new IllegalArgumentException();
		}
		if (!this.equals(lease.getClient())) {// checks if lease does not belong to client
			throw new IllegalArgumentException("Lease does not belong to client");
		}
		this.myLeases.add(lease);
	}

	/**
	 * Returns an array of Strings with each String representing a lease attached to
	 * this client
	 * 
	 * @return array of Leases as a String
	 */
	public String[] listLeases() {
		String[] ret = new String[this.myLeases.size()];
		for (int i = 0; i < this.myLeases.size(); i++) {
			Lease l = myLeases.get(i);
			String[] data = l.leaseData();
			ret[i] = data[0] + " | " + data[1] + " |";
			String insert = data[2] + " | " + data[3];
			ret[i] = String.format(ret[i] + " %29s", insert);
		}
		return ret;
	}

	/**
	 * Cancels a clients lease at the provided index.
	 * 
	 * @param index the index of the lease to be cancelled
	 * @return the lease that just got cancelled
	 * @throws IllegalArgumentException if the provided position is invalid
	 */
	public Lease cancelLeaseAt(int index) {
		try {
			Lease ret = this.myLeases.remove(index);
			return ret;
		} catch (IndexOutOfBoundsException e) {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Cancels lease with the given confirmation number.
	 * 
	 * @param num provided confirmation number
	 * @return the lease that just got cancelled
	 * @throws IllegalArgumentException if the confirmation number does not
	 *                                  correspond to any of the leases for this
	 *                                  client
	 */
	public Lease cancelLeaseWithNumber(int num) {
		int index = -1;
		for (int i = 0; i < myLeases.size(); i++) {
			Lease l = myLeases.get(i);
			if (l.getConfirmationNumber() == num) {
				index = i;
			}
		}
		if (index == -1) {
			throw new IllegalArgumentException();
		}
		return cancelLeaseAt(index);
	}
}
