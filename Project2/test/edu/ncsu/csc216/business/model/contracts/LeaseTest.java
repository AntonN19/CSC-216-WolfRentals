/**
 * 
 */
package edu.ncsu.csc216.business.model.contracts;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.Month;

import org.junit.Test;

import edu.ncsu.csc216.business.model.properties.ConferenceRoom;
import edu.ncsu.csc216.business.model.properties.RentalUnit;
import edu.ncsu.csc216.business.model.stakeholders.Client;

/**
 * Testing the Lease Class
 * 
 * @author Anton
 *
 */
public class LeaseTest {

	private final int confNumber = 15;
	private final Client client = new Client("Anton", "AN@346");
	private final RentalUnit rental = new ConferenceRoom("20-15", 12);
	private final LocalDate start = LocalDate.of(2020, Month.MARCH, 5);
	private final LocalDate end = LocalDate.of(2020, Month.MARCH, 10);
	private final int occupants = 20;

	/**
	 * Test to check if lease functionality works
	 */
	@Test
	public void testLease() {
		Lease.resetConfirmationNumbering(0);
		Lease l = new Lease(client, rental, start, end, occupants);
		assertEquals(0, l.getConfirmationNumber());
		l = new Lease(confNumber, client, rental, start, end, occupants);
		assertEquals(confNumber, l.getConfirmationNumber());
		assertEquals(client, l.getClient());
		assertEquals(rental, l.getProperty());
		assertEquals(start, l.getStart());
		assertEquals(end, l.getEnd());
		assertEquals(occupants, l.getNumOccupants());
		
		String[] leaseData = l.leaseData();
		assertEquals("000015", leaseData[0]);
		assertEquals("2020-03-05 to 2020-03-10", leaseData[1]);
		assertEquals("20", leaseData[2]);
		assertEquals("Conference Room:  20-15", leaseData[3]);
		assertEquals("Anton", leaseData[4]);
		assertEquals("AN@346", leaseData[5]);
		
		l.setEndDateEarlier(LocalDate.of(2020, Month.MARCH, 7));
		assertEquals(LocalDate.of(2020, Month.MARCH, 7), l.getEnd());
		
		try {
			l.setEndDateEarlier(LocalDate.of(2020, Month.MARCH, 8));
			fail();
		} catch(IllegalArgumentException e) {
			assertEquals(LocalDate.of(2020, Month.MARCH, 7), l.getEnd());
		}
		
		try {
			l.setEndDateEarlier(LocalDate.of(2020, Month.MARCH, 8));
			fail();
		} catch(IllegalArgumentException e) {
			assertEquals(LocalDate.of(2020, Month.MARCH, 7), l.getEnd());
		}
		
		try {
			l.setEndDateEarlier(LocalDate.of(2020, Month.MARCH, 4));
			fail();
		} catch(IllegalArgumentException e) {
			assertEquals(LocalDate.of(2020, Month.MARCH, 7), l.getEnd());
		}
	}

}
