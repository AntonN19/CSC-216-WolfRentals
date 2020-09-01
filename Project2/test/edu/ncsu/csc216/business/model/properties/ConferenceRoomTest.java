/**
 * 
 */
package edu.ncsu.csc216.business.model.properties;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.Month;

import org.junit.Test;

import edu.ncsu.csc216.business.model.contracts.Lease;
import edu.ncsu.csc216.business.model.stakeholders.Client;

/**
 * Testing the ConferenceRoom class
 * 
 * @author Anton
 */
public class ConferenceRoomTest {

	/** Client to add to Conference room */
	private final Client client = new Client("Anton", "AN@123");
	/** Start date of Rental */
	private final LocalDate startDate = LocalDate.of(2020, Month.MARCH, 20);
	/** End date of Rental */
	private final LocalDate endDate = LocalDate.of(2020, Month.MARCH, 26);
	/** Conference rooms */
	private final RentalUnit confOne = new ConferenceRoom("12-15", 23);
	/** Test lease */
	private final Lease lease = new Lease(40, client, confOne, startDate, endDate, 20);

	/**
	 * Test the conference room constructor
	 */
	@Test
	public void testConstructor() {
		ConferenceRoom conf = new ConferenceRoom("11-60", 25);
		assertEquals(11, conf.getFloor());
		assertEquals(60, conf.getRoom());
		assertEquals(25, conf.getCapacity());

		try {
			conf = new ConferenceRoom("14-20", 26);
			fail();
		} catch (IllegalArgumentException e) {
			assertEquals("Capacity is greater than max allowed", e.getMessage());
		}
	}

	/**
	 * Test adding and removing leases
	 */
	@Test
	public void testLeases() {

		confOne.takeOutOfService();
		try {
			confOne.reserve(client, startDate, 4, 12);
			fail();
		} catch (RentalOutOfServiceException e) {
			assertEquals("not in service", e.getMessage());
		} catch (RentalDateException e) {
			fail();
		} catch (RentalCapacityException e) {
			fail();
		} catch (IllegalArgumentException e) {
			fail();
		}
		confOne.returnToService();

		try {
			confOne.reserve(client, startDate, 8, 15);
			fail();
		} catch (RentalOutOfServiceException e) {
			fail();
		} catch (RentalDateException e) {
			assertEquals("duration exceeds limit", e.getMessage());
		} catch (RentalCapacityException e) {
			fail();
		} catch (IllegalArgumentException e) {
			fail();
		}

		try {
			Lease l = confOne.reserve(client, startDate, 7, 20);
			assertEquals(lease.getClient(), l.getClient());
			assertEquals(lease.getStart(), l.getStart());
			assertEquals(lease.getEnd(), l.getEnd());
			assertEquals(lease.getProperty(), l.getProperty());
		} catch (RentalOutOfServiceException e) {
			fail();
		} catch (RentalDateException e) {
			fail();
		} catch (RentalCapacityException e) {
			fail();
		}

		try {
			confOne.reserve(client, endDate, 6, 15);
			fail();
		} catch (RentalDateException e) {
			assertEquals(1, confOne.listLeases().length);
		} catch (RentalCapacityException e) {
			fail();
		} catch (RentalOutOfServiceException e) {
			fail();
		}

		try {
			confOne.reserve(client, LocalDate.of(2020, Month.MARCH, 18), 6, 15);
			fail();
		} catch (RentalDateException e) {
			assertEquals(1, confOne.listLeases().length);
		} catch (RentalCapacityException e) {
			fail();
		} catch (RentalOutOfServiceException e) {
			fail();
		}

		try {
			confOne.reserve(client, LocalDate.of(2020, Month.MARCH, 18), 3, 15);
			fail();
		} catch (RentalDateException e) {
			assertEquals(1, confOne.listLeases().length);
		} catch (RentalCapacityException e) {
			fail();
		} catch (RentalOutOfServiceException e) {
			fail();
		}

		try {
			confOne.reserve(client, LocalDate.of(2020, Month.MARCH, 10), 5, 24);
			fail();
		} catch (RentalDateException e) {
			fail();
		} catch (RentalCapacityException e) {
			assertEquals(1, confOne.listLeases().length);
		} catch (RentalOutOfServiceException e) {
			fail();
		}

		try {
			confOne.recordExistingLease(12, client, LocalDate.of(2020, Month.MARCH, 10),
					LocalDate.of(2020, Month.MARCH, 20), 10);
			fail();
		} catch (RentalDateException e) {
			assertEquals("duration exceeds limit", e.getMessage());
		} catch (RentalCapacityException e) {
			fail();
		}

		try {
			confOne.recordExistingLease(12, client, LocalDate.of(2020, Month.MARCH, 10),
					LocalDate.of(2020, Month.MARCH, 15), 25);
			fail();
		} catch (RentalDateException e) {
			fail();
		} catch (RentalCapacityException e) {
			assertEquals(1, confOne.listLeases().length);
		}

		try {
			confOne.recordExistingLease(12, client, LocalDate.of(2020, Month.MARCH, 28),
					LocalDate.of(2020, Month.MARCH, 29), 20);
			assertEquals(2, confOne.listLeases().length);
		} catch (RentalDateException e) {
			fail();
		} catch (RentalCapacityException e) {
			fail();
		}

		try {
			confOne.reserve(client, LocalDate.of(2023, Month.MARCH, 28), 1, 14);
		} catch (RentalOutOfServiceException e) {
			fail();
		} catch (RentalDateException e) {
			System.out.print(e.getMessage());
			fail();
		} catch (RentalCapacityException e) {
			fail();
		}

		// check that default message is thrown
		try {
			throwRentalException();
		} catch (RentalOutOfServiceException e) {
			assertEquals("Rental Unit out of Service", e.getMessage());
		}

	}

	/**
	 * Class used to test RentalOutOfServiceException
	 * 
	 * @throws RentalOutOfServiceException
	 */
	private void throwRentalException() throws RentalOutOfServiceException {
		throw new RentalOutOfServiceException();
	}

}
