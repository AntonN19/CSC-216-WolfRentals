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
 * Testing the HotelSuite class
 * 
 * @author Anton
 *
 */
public class HotelSuiteTest {

	/** Client to add to Conference room */
	private final Client client = new Client("Anton", "AN@123");
	/** Start date of Rental */
	private final LocalDate startDate = LocalDate.of(2020, Month.APRIL, 19);
	/** End date of Rental */
	private final LocalDate endDate = LocalDate.of(2020, Month.APRIL, 26);
	/** Conference rooms */
	private final RentalUnit suiteOne = new HotelSuite("12-15", 2);
	/** Test lease */
	private final Lease lease = new Lease(40, client, suiteOne, LocalDate.of(2021, Month.MARCH, 7),
			LocalDate.of(2021, Month.MARCH, 14), 2);

	/**
	 * Tests the HotelSuite constructor functionality
	 */
	@Test
	public void testConstructor() {
		RentalUnit hotelSuite = new HotelSuite("12-11");
		assertEquals(12, hotelSuite.getFloor());
		assertEquals(11, hotelSuite.getRoom());
		assertEquals(1, hotelSuite.getCapacity());
		hotelSuite = new HotelSuite("11-60", 1);
		assertEquals("Hotel Suite:      11-60 |   1", hotelSuite.getDescription());
		assertEquals(11, hotelSuite.getFloor());
		assertEquals(60, hotelSuite.getRoom());
		assertEquals(1, hotelSuite.getCapacity());

		try {
			hotelSuite = new HotelSuite("14-20", 3);
			fail();
		} catch (IllegalArgumentException e) {
			assertEquals("capacity over max", e.getMessage());
		}
	}

	/**
	 * Test adding and removing leases
	 */
	@Test
	public void testLeases() {
		Lease.resetConfirmationNumbering(0);

		try {
			suiteOne.reserve(client, startDate, 1, 1);
			assertEquals("000000 | 2020-04-19 to 2020-04-26 |   1 | Anton (AN@123)", suiteOne.listLeases()[0]);
		} catch (RentalOutOfServiceException e) {
			fail();
		} catch (RentalDateException e) {
			fail();
		} catch (RentalCapacityException e) {
			fail();
		} catch (IllegalArgumentException e) {
			fail();
		}

		try {
			suiteOne.reserve(client, startDate, 3, 2);
			fail();
		} catch (RentalOutOfServiceException e) {
			fail();
		} catch (RentalDateException e) {
			assertEquals(1, suiteOne.listLeases().length);
		} catch (RentalCapacityException e) {
			fail();
		}

		try {
			suiteOne.reserve(client, endDate, 3, 2);
			assertEquals("000001 | 2020-04-26 to 2020-05-17 |   2 | Anton (AN@123)", suiteOne.listLeases()[1]);
		} catch (RentalOutOfServiceException e) {
			fail();
		} catch (RentalDateException e) {
			fail();
		} catch (RentalCapacityException e) {
			fail();
		}

		try {
			Lease l = suiteOne.recordExistingLease(40, client, LocalDate.of(2021, Month.MARCH, 7),
					LocalDate.of(2021, Month.MARCH, 14), 2);
			assertEquals(lease.getClient(), l.getClient());
			assertEquals(lease.getConfirmationNumber(), l.getConfirmationNumber());
			assertEquals(lease.getStart(), l.getStart());
			assertEquals(lease.getEnd(), l.getEnd());
			assertEquals(lease.getProperty(), l.getProperty());
			assertEquals(3, suiteOne.listLeases().length);
		} catch (RentalDateException e) {
			fail();
		} catch (RentalCapacityException e) {
			fail();
		}

		try {
			suiteOne.reserve(client, LocalDate.of(2020, Month.MAY, 3), 3, 1);
			fail();
		} catch (RentalOutOfServiceException e) {
			fail();
		} catch (RentalDateException e) {
			assertEquals(3, suiteOne.listLeases().length);
		} catch (RentalCapacityException e) {
			fail();
		}

		try {
			suiteOne.reserve(client, LocalDate.of(2020, Month.MARCH, 29), 6, 1);
			fail();
		} catch (RentalOutOfServiceException e) {
			fail();
		} catch (RentalDateException e) {
			assertEquals(3, suiteOne.listLeases().length);
		} catch (RentalCapacityException e) {
			fail();
		}

		try {
			suiteOne.reserve(client, LocalDate.of(2028, Month.MAY, 7), 3, 3);
			fail();
		} catch (RentalOutOfServiceException e) {
			fail();
		} catch (RentalDateException e) {
			fail();
		} catch (RentalCapacityException e) {
			assertEquals(3, suiteOne.listLeases().length);
		}

		suiteOne.removeFromServiceStarting(LocalDate.of(2020, Month.MAY, 6));
		assertEquals("000001 | 2020-04-26 to 2020-05-03 |   2 | Anton (AN@123)", suiteOne.listLeases()[1]);
		assertEquals(2, suiteOne.listLeases().length);

		suiteOne.removeFromServiceStarting(startDate);
		assertEquals(0, suiteOne.listLeases().length);
		suiteOne.returnToService();

		try {
			suiteOne.reserve(client, LocalDate.of(2026, Month.MARCH, 29), 6, 1);
		} catch (RentalOutOfServiceException e) {
			fail();
		} catch (RentalDateException e) {
			fail();
		} catch (RentalCapacityException e) {
			fail();
		}
		suiteOne.removeFromServiceStarting(LocalDate.of(2025, Month.FEBRUARY, 20));
	}
}
