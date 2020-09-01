/**
 * 
 */
package edu.ncsu.csc216.business.model.properties;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.Month;

import org.junit.Test;

import edu.ncsu.csc216.business.list_utils.SortedList;
import edu.ncsu.csc216.business.model.contracts.Lease;
import edu.ncsu.csc216.business.model.stakeholders.Client;

/**
 * Testing the RentalUnit class
 * 
 * @author Anton
 *
 */
public class RentalUnitTest {

	/** Client to add to rental unit */
	private final Client client = new Client("Anton", "AN@123");
	/** Start date of Rental */
	private final LocalDate startDate = LocalDate.of(2020, Month.MARCH, 20);
	/** End date of Rental */
	private final LocalDate endDate = LocalDate.of(2020, Month.MARCH, 23);
	/** Rental Units */
	private final RentalUnit rentalOne = new ConferenceRoom("12-15", 12);
	private final RentalUnit rentalTwo = new ConferenceRoom("12-20", 15);
	/** Test lease*/
	private final Lease lease = new Lease(40, client, rentalOne, startDate, endDate, 10);
	
	/**
	 * Test for RentalUnit Constructor
	 */
	@Test
	public void testConstructor() {
		RentalUnit rentalConf = new ConferenceRoom("12-15", 12);
		assertEquals(12, rentalConf.getFloor());
		assertEquals(15, rentalConf.getRoom());
		assertEquals(12, rentalConf.getCapacity());
		assertTrue(rentalConf.isInService());
		
		rentalConf.takeOutOfService();
		assertFalse(rentalConf.isInService());
		rentalConf.returnToService();
		assertTrue(rentalConf.isInService());
		assertTrue(rentalOne.equals(rentalConf));
		rentalConf.takeOutOfService();
		assertEquals("Conference Room:  12-15 |  12  Unavailable", rentalConf.getDescription());
		
		try {
			rentalConf = new ConferenceRoom("12-15", 0);
			fail();
		} catch (IllegalArgumentException e) {
			assertEquals("invalid capacity", e.getMessage());
		}
		try {
			rentalConf = new ConferenceRoom("floor-room", 13);
			fail();
		} catch (IllegalArgumentException e) {
			assertEquals("invalid location string", e.getMessage());
		}
		try {
			rentalConf = new ConferenceRoom("12-13-14", 13);
			fail();
		} catch (IllegalArgumentException e) {
			assertEquals("invalid location string", e.getMessage());
		}
		try {
			rentalConf = new ConferenceRoom("0-12", 13);
			fail();
		} catch (IllegalArgumentException e) {
			assertEquals("invalid floor/room", e.getMessage());
		}
		try {
			rentalConf = new ConferenceRoom("46-12", 13);
			fail();
		} catch (IllegalArgumentException e) {
			assertEquals("invalid floor/room", e.getMessage());
		}
		try {
			rentalConf = new ConferenceRoom("18-9", 13);
			fail();
		} catch (IllegalArgumentException e) {
			assertEquals("invalid floor/room", e.getMessage());
		}
		try {
			rentalConf = new ConferenceRoom("12-100", 13);
			fail();
		} catch (IllegalArgumentException e) {
			assertEquals("invalid floor/room", e.getMessage());
		}
	}
	
	/**
	 * Testing the compareTo() method
	 */
	@Test
	public void testCompareTo() {
		assertEquals(-5, rentalOne.compareTo(rentalTwo));
		assertEquals(5, rentalTwo.compareTo(rentalOne));
		RentalUnit rentalConf = new ConferenceRoom("12-15", 12);
		assertEquals(0, rentalOne.compareTo(rentalConf));
		rentalConf = new ConferenceRoom("1-34", 12);
		assertEquals(11, rentalOne.compareTo(rentalConf));
		assertEquals(-11, rentalConf.compareTo(rentalOne));
	}
	
	/**
	 * Testing the checkDates method
	 */
	@Test
	public void testCheckDates() {
		LocalDate date1 = LocalDate.of(2020, Month.MARCH, 25);
		LocalDate date2 = LocalDate.of(2019, Month.FEBRUARY, 2);
		LocalDate date3 = LocalDate.of(2019, Month.FEBRUARY, 5);
		try {
			rentalOne.checkDates(startDate, date1);
		} catch (RentalDateException e) {
			fail();
		}
		try {
			rentalOne.checkDates(date2, date3);
			fail();
		} catch(RentalDateException e) {
			assertEquals("invalid date", e.getMessage());
		}
		try {
			rentalOne.checkDates(date1, startDate);
			fail();
		} catch(RentalDateException e) {
			assertEquals("end date before start date", e.getMessage());
		}
	}
	
	/**
	 * Testing methods associated with the myLeases field
	 */
	@Test
	public void testMyLeases() {
		rentalOne.takeOutOfService();
		rentalOne.addLease(lease);
		assertEquals(0, rentalOne.listLeases().length);
		rentalOne.returnToService();
		rentalOne.addLease(lease);
		try {
			rentalTwo.addLease(lease);
			fail();
		} catch(IllegalArgumentException e) {
			assertEquals("Lease is not for this rental unit", e.getMessage());
		}
		try {
			rentalOne.checkLeaseConditions(client, startDate, 0, 12);
			fail();
		} catch (IllegalArgumentException e) {
			assertEquals("invalid input", e.getMessage());
		} catch (RentalOutOfServiceException e) {
			fail();
		}
		
		SortedList<Lease> cutOff;
		cutOff = rentalOne.removeFromServiceStarting(LocalDate.of(2020, Month.MARCH, 19));
		assertEquals(lease.getClient(), cutOff.get(0).getClient());
		assertEquals(lease.getConfirmationNumber(), cutOff.get(0).getConfirmationNumber());
		assertEquals(lease.getNumOccupants(), cutOff.get(0).getNumOccupants());
		assertEquals(lease.getProperty(), cutOff.get(0).getProperty());
		assertEquals(0, rentalOne.listLeases().length);
		rentalOne.returnToService();
		
		rentalOne.addLease(lease);
		cutOff = rentalOne.removeFromServiceStarting(LocalDate.of(2020, Month.MARCH, 26));
		assertEquals(0, cutOff.size());
		assertEquals("000040 | 2020-03-20 to 2020-03-23 |  10 | Anton (AN@123)", rentalOne.listLeases()[0]);
		cutOff = rentalOne.removeFromServiceStarting(LocalDate.of(2020, Month.MARCH, 22));
		assertEquals(0, cutOff.size());
		assertEquals("000040 | 2020-03-20 to 2020-03-21 |  10 | Anton (AN@123)", rentalOne.listLeases()[0]);

		rentalOne.cancelLeaseByNumber(40);
		assertEquals(0, rentalOne.listLeases().length);
		assertFalse(rentalOne.isInService());
		rentalOne.addLease(lease);
		assertEquals(0, rentalOne.listLeases().length);
		try {
			rentalOne.cancelLeaseByNumber(41);
			fail();
		} catch (IllegalArgumentException e) {
			assertEquals("No such lease", e.getMessage());
		}
	}
}
