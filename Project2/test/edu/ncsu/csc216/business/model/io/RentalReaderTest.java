/**
 * 
 */
package edu.ncsu.csc216.business.model.io;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.ncsu.csc216.business.model.stakeholders.PropertyManager;

/**
 * Testing the RentalReader Class
 * 
 * @author Anton
 *
 */
public class RentalReaderTest {

	/** Expected rental, client and lease values for manager reading from file */
	String[] expectedRentals = new String[] { "Hotel Suite:       1-99 |   1", "Conference Room:   2-34 |  25",
			"Office:            2-56 |  27", "Office:            5-10 |  75", "Office:            6-24 |  65",
			"Conference Room:   8-17 |  13", "Office:           12-23 | 100", "Office:           14-10 |  99",
			"Office:           14-24 |  50", "Conference Room:  25-44 |  12", "Office:           26-10 |  80",
			"Hotel Suite:      35-18 |   2", "Hotel Suite:      36-10 |   1", "Hotel Suite:      36-12 |   2",
			"Hotel Suite:      37-10 |   2  Unavailable", "Conference Room:  37-14 |  22",
			"Hotel Suite:      38-14 |   1", "Conference Room:  38-67 |  20", "Conference Room:  40-18 |  20" };
	String[] expectedClients = new String[] { "Amanda Smith (a12#smL)", "Sandy Shelton Biggs (hb2$32)",
			"James Tetterton (jc1012)", "Melody Griesen (gri$$0#)", "Ammar Abbas (1213aaaabb)",
			"Jason Hurst (soni@son)", "Matthew Zahn (amat456)", "Shreeya S Dhakal (ssd#SSssd)",
			"Zach Groseclose (azazaz)", "Jo Perry (DEout#)", "Sarah Heckman ($456$ssh)", "Maddy Mae (cats1)",
			"Marge Simpson (comik##)", "Walter White (reallyBad)", "Olivia Colman (123crown)", "Joe Pesci (iRish)",
			"Adam Driver (g1rlz)", "Suranne Jones (###$$)" };
	String[] amandaLeasesExp = new String[] { "000004 | 2020-05-30 to 2020-06-03 |   4 | Conference Room:  38-67" };
	String[] jamesLeasesExp = new String[] {  "000009 | 2020-10-01 to 2020-12-31 |  10 | Office:            2-56" };
	String[] zachLeasesExp = new String[] {   "000011 | 2020-10-01 to 2020-10-31 |   2 | Office:            2-56",
			"000012 | 2020-09-17 to 2020-09-23 |  15 | Conference Room:  37-14" };
	String[] maddyLeasesExp = new String[] { "000000 | 2020-03-15 to 2020-04-19 |   1 | Hotel Suite:      36-10",
			"000001 | 2020-04-01 to 2020-05-31 |  37 | Office:            6-24",
			"000008 | 2020-10-01 to 2020-11-30 |   5 | Office:            2-56" };
	String[] walterLeasesExp = new String[] { "000002 | 2020-03-01 to 2020-10-31 |  23 | Office:            6-24",
			"000003 | 2020-03-25 to 2020-03-30 |  10 | Conference Room:  38-67",
			"000005 | 2020-04-19 to 2020-05-10 |   2 | Hotel Suite:      35-18",
			"000006 | 2020-06-28 to 2020-07-05 |   2 | Hotel Suite:      35-18",
			"000007 | 2020-11-01 to 2021-06-30 |  10 | Office:            2-56" };

	/**
	 * Test reading from file
	 */
	@Test
	public void testReader() {
		PropertyManager manager = PropertyManager.getInstance();
		RentalReader.readRentalData("test-files/example.md");
		manager.filterRentalUnits("Any", false);
		String[] rentals = manager.listRentalUnits();
		assertEquals(expectedRentals.length, rentals.length);
		for (int i = 0; i < rentals.length; i++) {
			assertEquals(expectedRentals[i], rentals[i]);
		}

		String[] clients = manager.listClients();
		assertEquals(expectedClients.length, clients.length);
		for (int i = 0; i < clients.length; i++) {
			assertEquals(expectedClients[i], clients[i]);
		}

		String[] amandaLeases = manager.listClientLeases(0);
		assertEquals(amandaLeasesExp.length, amandaLeases.length);
		for(int i = 0; i < amandaLeases.length; i++) {
			assertEquals(amandaLeasesExp[i], amandaLeases[i]);
		}
		
		String[] jamesLeases = manager.listClientLeases(2);
		assertEquals(jamesLeasesExp.length, jamesLeases.length);
		for(int i = 0; i < jamesLeases.length; i++) {
			assertEquals(jamesLeasesExp[i], jamesLeases[i]);
		}
		
		String[] zachLeases = manager.listClientLeases(8);
		assertEquals(zachLeasesExp.length, zachLeases.length);
		for(int i = 0; i < zachLeases.length; i++) {
			assertEquals(zachLeasesExp[i], zachLeases[i]);
		}
		
		String[] maddyLeases = manager.listClientLeases(11);
		assertEquals(maddyLeasesExp.length, maddyLeases.length);
		for(int i = 0; i < maddyLeases.length; i++) {
			assertEquals(maddyLeasesExp[i], maddyLeases[i]);
		}
		
		String[] walterLeases = manager.listClientLeases(13);
		assertEquals(walterLeasesExp.length, walterLeases.length);
		for(int i = 0; i < walterLeases.length; i++) {
			assertEquals(walterLeasesExp[i], walterLeases[i]);
		}
		
		manager.flushAllData();
	}
	
	/**
	 * Recreating failing TS tests
	 */
	@Test
	public void tsTests(){
		PropertyManager manager = PropertyManager.getInstance();
		RentalReader.readRentalData("test-files/test_file.md");
		manager.filterRentalUnits("Any", false);
		assertEquals(3, manager.listLeasesForRentalUnit(3).length);
		assertEquals(4, manager.listClientLeases(2).length);
		
/*  	Hotel Suite:       1-99 |   1
		Conference Room:   2-34 |  25
		Office:            5-10 |  75
		Office:           10-10 |  27  Unavailable

		#Pablo Costa (pbC#$23)
		   000004 | 2020-05-30 to 2020-06-03 |   4 | Conference Room:   2-34
		#Quentin Tarantino (Taran#)
		#Britney Palmer (britPalm)
		   000009 | 2020-10-01 to 2020-12-31 |  10 | Office:           10-10
		   000010 | 2020-10-01 to 2020-12-31 |  16 | Office:           10-10
		   000011 | 2020-10-01 to 2020-12-31 |  10 | Office:           10-10
		   000104 | 2022-05-30 to 2022-06-03 |   4 | Conference Room:   2-34 */
		
	}

}
