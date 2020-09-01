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
 * Tests cases for the Office class
 * 
 * @author Anton
 *
 */
public class OfficeTest {

	/** Client to add to Conference room */
	private final Client client = new Client("Anton", "AN@123");
	/** Start date of Rental */
	private final LocalDate startDate = LocalDate.of(2020, Month.APRIL, 1);
	/** Conference rooms */
	private final RentalUnit officeOne = new Office("12-15", 150);
	/** Test lease */
	private final Lease lease = new Lease(40, client, officeOne, LocalDate.of(2023, Month.MARCH, 1),
			LocalDate.of(2023, Month.MARCH, 31), 30);

	/**
	 * Tests the Office constructor functionality
	 */
	@Test
	public void testConstructor() {
		RentalUnit office = new Office("11-60", 125);
		assertEquals(11, office.getFloor());
		assertEquals(60, office.getRoom());
		assertEquals(125, office.getCapacity());
		office = new Office("10-12", 10);
		assertEquals(10, office.getFloor());
		assertEquals(12, office.getRoom());
		assertEquals(10, office.getCapacity());

		try {
			office = new HotelSuite("14-20", 151);
			fail();
		} catch (IllegalArgumentException e) {
			assertEquals("capacity over max", e.getMessage());
		}
	}

	/**
	 * Test 1
	 */
	@Test
	public void test() {

		Lease.resetConfirmationNumbering(0);
		try {// add valid lease
			officeOne.reserve(client, startDate, 1, 1);
			assertEquals("000000 | 2020-04-01 to 2020-04-30 |   1 | Anton (AN@123)", officeOne.listLeases()[0]);
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
			officeOne.reserve(client, startDate, 12, 149); // april capacity at max
			assertEquals("000001 | 2020-04-01 to 2021-03-31 | 149 | Anton (AN@123)", officeOne.listLeases()[1]);
		} catch (RentalOutOfServiceException e) {
			fail();
		} catch (RentalDateException e) {
			fail();
		} catch (RentalCapacityException e) {
			fail();
		}

		try {
			officeOne.reserve(client, LocalDate.of(2020, Month.FEBRUARY, 1), 4, 12); // breaches april capacity
			fail();
		} catch (RentalOutOfServiceException e) {
			fail();
		} catch (RentalDateException e) {
			fail();
		} catch (RentalCapacityException e) {
			assertEquals(2, officeOne.listLeases().length);
		}

		try {
			Lease l = officeOne.recordExistingLease(40, client, LocalDate.of(2023, Month.MARCH, 1),
					LocalDate.of(2023, Month.MARCH, 31), 30);
			assertEquals(lease.getClient(), l.getClient());
			assertEquals(lease.getConfirmationNumber(), l.getConfirmationNumber());
			assertEquals(lease.getStart(), l.getStart());
			assertEquals(lease.getEnd(), l.getEnd());
			assertEquals(lease.getProperty(), l.getProperty());
			assertEquals(3, officeOne.listLeases().length);
		} catch (RentalDateException e) {
			fail();
		} catch (RentalCapacityException e) {
			fail();
		}

		officeOne.removeFromServiceStarting(LocalDate.of(2020, Month.MAY, 6));
		assertEquals(2, officeOne.listLeases().length);
		assertEquals("000001 | 2020-04-01 to 2020-04-30 | 149 | Anton (AN@123)", officeOne.listLeases()[1]);
	}

}
