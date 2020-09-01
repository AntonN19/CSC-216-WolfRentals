package edu.ncsu.csc216.business.model.stakeholders;


import java.time.LocalDate;

import edu.ncsu.csc216.business.model.contracts.Lease;
import edu.ncsu.csc216.business.model.properties.RentalUnit;

/**
 * Landlord describes behaviors that the back end (model) must support for a front end 
 * (user interface) of the Wolf High-Rise Rental Application.
 * 
 * @author Jo Perry
 *
 */
public interface Landlord {
	
	/**
	 * Adds a new client with the given name and id to the client database. 
	 *  
	 * @param name Name of the new client
	 * @param id Unique id of the new client 
	 * @return The new Client who was created and registered
	 * @throws DuplicateClientException if id of the new client matches one for an existing client.
	 * @throws IllegalArgumentException if the name is null, empty (when trimmed), or contains
	 *         any characters that are not blanks or not alphanumeric. Also throws
	 *         IllegalArgumentException if the id is null, empty (when trimmed), or 
	 *         contains any characters that are non-alphanumeric or that don't belong to the 
	 *         set ['@', '#', '$'].
	 */
	Client addNewClient(String name, String id) throws DuplicateClientException;
	
	/**
	 * Adds a new RentalUnit with the given parameters to the system.
	 * 
	 * @param kind Type of RentalUnit (starts with 'O' for office, 'C' for conference room,
	 *        'H' for hotel suite)
	 * @param location String of the form FF-RR, where FF is the floor, and RR is the room.
	 * @param capacity Number of people the unit can accommodate on any single day
	 * @return The new RentalUnit that was created
	 * @throws IllegalArgumentException if the parameters do not describe a valid location  
	 *         and type
	 * @throws DuplicateRoomException if the floor and room match another rental unit already
	 *         in the Landlord's property database
	 */
	RentalUnit addNewUnit(String kind, String location, int capacity) throws DuplicateRoomException;
	
	/**
	 * Sets filters for rental units so that only those that match the filters are considered.
	 * 
	 * @param filter1 String type filter that rental units under consideration must meet
	 * @param filter2 boolean type filter that rental units under consideration must meet
	 */
	void filterRentalUnits(String filter1, boolean filter2);

	/**
	 * Creates a new lease with information based on the given parameters.
	 * 
	 * @param clientIndex Index of the client in the Landlord's customer base
	 * @param propertyIndex Index of the rental unit in the Landlord's filtered list of rental units
	 * @param start Start date for the lease
	 * @param duration Duration of the lease (units depending on rental unit type)
	 * @param people Number of occupants the lease is for
	 * @return the created lease
	 * @throws IllegalArgumentException if the parameters do not constitute valid lease data
	 */
	Lease createLease(int clientIndex, int propertyIndex, LocalDate start, int duration, int people);

	/**
	 * Cancels the lease in the given position on the client's list of leases.
	 * 
	 * @param clientIndex  Index of the client whose lease is to be cancelled
	 * @param leaseIndex  Position of the lease in the client's list
	 * @throws IllegalArgumentException if clientIndex or leaseIndex are not valid
	 */
	void cancelClientsLease(int clientIndex, int leaseIndex);
	
	/**
	 * Cancels all leases for a rental unit on or after a particular date. The remaining 
	 * leases should still be valid.
	 * 
	 * @param propertyIndex  Index for the rental unit (subject to filtering)
	 * @param start Date for starting cancellations
	 * @return the RentalUnit that was removed
	 * @throws IllegalArgumentException if propertyIndex is not a valid index for the 
	 *         rental units currently under consideration
	 */
	RentalUnit removeFromService(int propertyIndex, LocalDate start);
	
	/** 
	 * Removes the rental unit at the given index from the Landlord's database and cancels
	 * all leases for that rental unit.
	 * 
	 * @param propertyIndex  Index for the rental unit to be closed (subject to filtering)
	 */
	void closeRentalUnit(int propertyIndex);
	
	/**
	 * Returns the rental unit at the given position to service. Does nothing if the rental 
	 * unit is already in service or if the position does not correspond to any rental unit 
	 * (subject to filtering).
	 * 
	 * @param propertyIndex Position/index of the rental unit (subject to filtering)
	 * @throws IllegalArgumentException if propertyIndex is not a valid index for the 
	 *         rental units currently under consideration
	 */
	void returnToService(int propertyIndex);
	
	/**
	 * Who are the clients for this Landlord's properties?
	 * 
	 * @return an array of strings, where each string describes a client
	 */
	String[] listClients();
	
	/**
	 * What are the leases for a particular client?
	 * 
	 * @param clientIndex Index of the targeted client in the landlord's list of clients
	 * @return an array of strings in which each string describes a lease for the
	 *         targeted client
	 * @throws IllegalArgumentException if the clientIndex does not correspond to any
	 *         client.
	 */
	String[] listClientLeases(int clientIndex);
	
	/**
	 * What are the rental units for this landlord? (Consider only the units that meet
	 * filters currently in place.)
	 * 
	 * @return an array of strings in which each string describes a rental unit that
	 *         meets all filters in place. There are exactly as many strings in the
	 *         array as there are such rental units.
	 */
	String[] listRentalUnits();
	
	/**
	 * What are the leases for the rental unit at this particular index in the filtered
	 * list of rental units?
	 * 
	 * @param propertyIndex Index of the targeted rental unit (subject to filtering)
	 * @return an array of strings in which each string describes a lease for the
	 *         targeted rental unit.
	 * @throws IllegalArgumentException if propertyIndex is not a valid index for the 
	 *         rental units currently under consideration
	 */
	String[] listLeasesForRentalUnit(int propertyIndex);
	
	/**
	 * Removes all Landlord data and resets the reservation confirmation numbering to 0.
	 */
	void flushAllData();
}