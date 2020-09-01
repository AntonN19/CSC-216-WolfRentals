/**
 * 
 */
package edu.ncsu.csc216.business.model.stakeholders;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.Month;

import org.junit.Test;

import edu.ncsu.csc216.business.model.contracts.Lease;
import edu.ncsu.csc216.business.model.properties.ConferenceRoom;
import edu.ncsu.csc216.business.model.properties.RentalUnit;

/**
 * Test for the Client class
 * 
 * @author Anton
 */
public class ClientTest {

	/** Test rental */
	private final RentalUnit rental = new ConferenceRoom("20-15", 12);
	/** Test start date */
	private final LocalDate start = LocalDate.of(2020, Month.MARCH, 5);
	/** Test end date */
	private final LocalDate end = LocalDate.of(2020, Month.MARCH, 10);

	/**
	 * Tests the client constructor functionality
	 */
	@Test
	public void testClientConstructor() {
		Client c = new Client("Anton Nikulsin", "An@#Ni$23");
		assertEquals("Anton Nikulsin", c.getName());
		assertEquals("An@#Ni$23", c.getId());

		try {
			c = new Client("Anton :)", "Ant78");
			fail();
		} catch (IllegalArgumentException e) {
			assertEquals("invalid name/id", e.getMessage());
		}

		try {
			c = new Client("Anton N", "A2");
			fail();
		} catch (IllegalArgumentException e) {
			assertEquals("invalid name/id", e.getMessage());
		}

		try {
			c = new Client("Anton N", "A2&34");
			fail();
		} catch (IllegalArgumentException e) {
			assertEquals("invalid name/id", e.getMessage());
		}
	}

	/**
	 * Tests methods associated with clients myLeases field
	 */
	@Test
	public void testClientLeases() {
		Client c = new Client("Anton Nikulsin", "An@#Ni$23");
		Lease l = new Lease(2002, c, rental, start, end, 10);
		Lease l3 = new Lease(109, c, rental, LocalDate.of(2020, Month.MAY, 5), LocalDate.of(2020, Month.MAY, 11), 10);
		Lease l2 = new Lease(c, rental, LocalDate.of(2020, Month.MAY, 5), LocalDate.of(2020, Month.MAY, 11), 10);
		c.addNewLease(l);
		assertEquals("002002 | 2020-03-05 to 2020-03-10 |  10 | Conference Room:  20-15", c.listLeases()[0]);
		try {
			c.cancelLeaseAt(1);
			fail();
		} catch (IllegalArgumentException e) {
			assertEquals(1, c.listLeases().length);
		}

		c.addNewLease(l2);
		assertEquals(2, c.listLeases().length);
		c.cancelLeaseWithNumber(2003);
		assertEquals(1, c.listLeases().length);
		assertEquals("002002 | 2020-03-05 to 2020-03-10 |  10 | Conference Room:  20-15", c.listLeases()[0]);
		c.addNewLease(l3);
		assertEquals("000109 | 2020-05-05 to 2020-05-11 |  10 | Conference Room:  20-15", c.listLeases()[1]);
		c.cancelLeaseAt(0);
		c.cancelLeaseAt(0);
		assertEquals(0, c.listLeases().length);

		Client c2 = new Client("Anton", "An@#Ni$23");
		assertTrue(c.equals(c2));
	}
}
