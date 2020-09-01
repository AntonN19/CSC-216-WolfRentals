/**
 * 
 */
package edu.ncsu.csc216.business.model.io;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.Scanner;

import org.junit.Test;

import edu.ncsu.csc216.business.model.stakeholders.DuplicateClientException;
import edu.ncsu.csc216.business.model.stakeholders.DuplicateRoomException;
import edu.ncsu.csc216.business.model.stakeholders.PropertyManager;

/**
 * Tests for the RentalWriter class
 * 
 * @author Anton
 *
 */
public class RentalWriterTest {

	/**
	 * Test 1
	 */
	@Test
	public void test() {
		PropertyManager manager = PropertyManager.getInstance();
		manager.flushAllData();
		//add rentals
		try {
			manager.addNewUnit("Office", "1-28", 100);
			manager.addNewUnit("Hotel Suite", "2-14", 2);
			manager.addNewUnit("Conf Room", "2-30", 15);
			manager.addNewUnit("Conf Room", "5-60", 20);
			manager.addNewUnit("Office", "8-44", 123);
			manager.addNewUnit("Hotel Suite", "12-38", 1);
		} catch(DuplicateRoomException e) {
			fail();
		}
		
		//add clients
		try {
			manager.addNewClient("Anton Nikulsin", "AN@123");
			manager.addNewClient("Billy Grub", "GruB#3");
			manager.addNewClient("Terry Jenkins", "TJBil9@#$");
			manager.addNewClient("Tony Montana", "Tigre@#$");
		} catch(DuplicateClientException e) {
			fail();
		}	
		
		//add leases
		manager.createLease(0, 2, LocalDate.of(2020, Month.MARCH, 20), 6, 13);
		manager.createLease(3, 5, LocalDate.of(2020, Month.APRIL, 5), 3, 1);
		manager.createLease(3, 0, LocalDate.of(2022, Month.APRIL, 1), 3, 25);
		manager.createLease(2, 0, LocalDate.of(2022, Month.JANUARY, 1), 5, 70);
		
		//leases contained
		//Anton Nikulsin
		//000000 | 2020-03-20 to 2020-03-26 |  13 | Conference Room:   2-30
		//Billy Grub
		//Terry Jenkins
		//000003 | 2022-01-01 to 2020-05-31 |  70 | Office:            1-28
		//Tony Montana
		//000001 | 2020-04-05 to 2020-04-26 |   1 | Hotel Suite:      12-38
		//000002 | 2022-04-01 to 2020-06-30 |  25 | Office:            1-28
		try {
			RentalWriter.writeRentalFile("");
			fail();
		} catch(IllegalArgumentException e) {
			assertEquals("invalid name", e.getMessage());
		}
		
		try {
			RentalWriter.writeRentalFile("test-files/act_manager.txt");
			fail();
		} catch(IllegalArgumentException e) {
			assertEquals("invalid name", e.getMessage());
		}
		
		RentalWriter.writeRentalFile("test-files/act_manager.md");
		checkFiles("test-files/exp_manager.md", "test-files/act_manager.md");
		
		manager.flushAllData();
	}
	
	/**
	 * Helper method to compare two files for the same contents
	 * 
	 * @param expF expected output
	 * @param actF actual output
	 */
	private void checkFiles(String expF, String actF) {
		try {
			Scanner expScanner = new Scanner(new File(expF));
			Scanner actScanner = new Scanner(new File(actF));
			while (expScanner.hasNextLine()) {
				assertEquals(expScanner.nextLine(), actScanner.nextLine());
			}
			expScanner.close();
			actScanner.close();
		} catch (IOException e) {
			fail("Error reading files.");
		}
	}

}
