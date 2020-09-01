/**
 * 
 */
package edu.ncsu.csc216.business.model.stakeholders;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.Month;

import org.junit.Test;

import edu.ncsu.csc216.business.model.properties.ConferenceRoom;
import edu.ncsu.csc216.business.model.properties.RentalUnit;

/**
 * Testing the PropertyManager class
 * 
 * @author Anton
 *
 */
public class PropertyManagerTest {

	/** Client to test property manager with */
	private final Client client = new Client("Anton", "AN@123");
	/** Start date of Rental */
	private final LocalDate startDate = LocalDate.of(2020, Month.MARCH, 20);
	/** End date of Rental */
	private final LocalDate endDate = LocalDate.of(2020, Month.MARCH, 26);
	/** conference room rental */
	private final RentalUnit rental = new ConferenceRoom("2-30", 15);

	/**
	 * Test case for the property manager
	 */
	@Test
	public void testManager() {
		PropertyManager manager = PropertyManager.getInstance();
		manager.flushAllData();
		// add clients
		try {
			manager.addNewClient("Anton N", "AN@123");
			manager.addNewClient("Billy Grub", "GruB#3");
			manager.addNewClient("Terry Jenkins", "TJBil9@#$");
			manager.addNewClient("Tony Montana", "Tigre@#$");
		} catch (DuplicateClientException e) {
			fail();
		}
		assertEquals("Anton N (AN@123)", manager.listClients()[0]);
		assertEquals("Billy Grub (GruB#3)", manager.listClients()[1]);
		assertEquals("Terry Jenkins (TJBil9@#$)", manager.listClients()[2]);
		assertEquals("Tony Montana (Tigre@#$)", manager.listClients()[3]);
		// add rentals
		try {
			manager.addNewUnit("Office", "1-28", 100);
			manager.addNewUnit("Hotel Suite", "2-14", 2);
			manager.addNewUnit("Conf Room", "2-30", 15);
			manager.addNewUnit("Hotel Suite", "12-38", 1);
		} catch (DuplicateRoomException e) {
			fail();
		}
		assertEquals("Office:            1-28 | 100", manager.listRentalUnits()[0]);
		assertEquals("Hotel Suite:       2-14 |   2", manager.listRentalUnits()[1]);
		assertEquals("Conference Room:   2-30 |  15", manager.listRentalUnits()[2]);
		assertEquals("Hotel Suite:      12-38 |   1", manager.listRentalUnits()[3]);
		// try adding duplicates
		try {
			manager.addNewClient("Anita Beejay", "AN@123");
			fail();
		} catch (DuplicateClientException e) {
			assertEquals(4, manager.listClients().length);
		}
		try {
			manager.addNewUnit("Office", "2-30", 111);
			fail();
		} catch (DuplicateRoomException e) {
			assertEquals(4, manager.listRentalUnits().length);
		}

		// add and remove leases from file
		manager.addLeaseFromFile(client, 234, rental, startDate, endDate, 13);
		assertEquals("000234 | 2020-03-20 to 2020-03-26 |  13 | Conference Room:   2-30",
				manager.listClientLeases(0)[0]);
		manager.createLease(3, 3, LocalDate.of(2020, Month.APRIL, 5), 3, 1);
		assertEquals("000235 | 2020-04-05 to 2020-04-26 |   1 | Tony Montana (Tigre@#$)",
				manager.listLeasesForRentalUnit(3)[0]);

		manager.filterRentalUnits("Hotel Suite", true);
		assertEquals("Hotel Suite:       2-14 |   2", manager.listRentalUnits()[0]);
		assertEquals("Hotel Suite:      12-38 |   1", manager.listRentalUnits()[1]);
		assertEquals(2, manager.listRentalUnits().length);
		manager.filterRentalUnits("Office", false);
		assertEquals("Office:            1-28 | 100", manager.listRentalUnits()[0]);
		manager.filterRentalUnits("Conf Room", true);
		assertEquals("Conference Room:   2-30 |  15", manager.listRentalUnits()[0]);
		manager.filterRentalUnits("Any", false);

		manager.removeFromService(0, startDate);
		manager.removeFromService(1, startDate);
		assertEquals("Office:            1-28 | 100  Unavailable", manager.listRentalUnits()[0]);
		assertEquals("Hotel Suite:       2-14 |   2  Unavailable", manager.listRentalUnits()[1]);
		manager.returnToService(0);
		assertEquals("Office:            1-28 | 100", manager.listRentalUnits()[0]);
		manager.removeFromService(0, startDate);

		manager.removeFromService(2, startDate);
		assertEquals("Conference Room:   2-30 |  15  Unavailable", manager.listRentalUnits()[2]);
		manager.returnToService(2);

		manager.filterRentalUnits("Any", true);
		assertEquals("Conference Room:   2-30 |  15", manager.listRentalUnits()[0]);
		assertEquals("Hotel Suite:      12-38 |   1", manager.listRentalUnits()[1]);
		manager.filterRentalUnits("Any", false);

		manager.closeRentalUnit(3);
		assertEquals(3, manager.listRentalUnits().length);
		assertEquals(rental, manager.getUnitAtLocation("2-30"));
		manager.flushAllData();
	}

	/**
	 * Replicating failing ts tests
	 */
	@Test
	public void repeatTsTests() {
		PropertyManager pm = PropertyManager.getInstance();
		pm.flushAllData();
		try {
			pm.addNewUnit("C", "10-99", 25);
			pm.addNewUnit("O", "12-99", 25);
			pm.addNewUnit("H", "10-86", 2);
			pm.addNewUnit("C", "45-50", 15);
			pm.addNewUnit("O", "10-14", 37);
			pm.addNewUnit("H", "10-87", 2);
			pm.addNewUnit("C", "11-15", 20);
		} catch (DuplicateRoomException e) {
			fail();
		}
		pm.filterRentalUnits("C", false);
		assertEquals(3, pm.listRentalUnits().length);
		pm.flushAllData();
	}

}
