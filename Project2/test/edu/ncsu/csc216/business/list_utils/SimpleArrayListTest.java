/**
 * 
 */
package edu.ncsu.csc216.business.list_utils;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Testing the SimpleArrayListTest Class
 * 
 * @author Anton
 *
 */
public class SimpleArrayListTest {

	/** SimpleArrayList size */
	private final int size = 20;
	
	/** String 1 */
	private final String stringOne = "String 1";
	
	/** String 2 */
	private final String stringTwo = "String 2";
	
	/** String 3 */
	private final String stringThree = "String 3";
	
	/** String 4 */
	private final String stringFour = "String 4";
	
	/**
	 * Testing constructors
	 */
	@Test
	public void testConstructor() {
		SimpleArrayList<String> simpList = new SimpleArrayList<String>();
		assertTrue(simpList.isEmpty());
		simpList = new SimpleArrayList<String>(size);
		assertEquals(simpList.size(), 0);
	}
	
	/**
	 * Test adding elements
	 */
	@Test
	public void testAdd() {
		SimpleArrayList<String> simpList = new SimpleArrayList<String>(3);
		simpList.add(stringOne); // [String 1]
		assertTrue(simpList.contains(stringOne));
		simpList.add(0, stringTwo); //[String 2, String 1]
		assertTrue(simpList.contains(stringTwo));
		simpList.add(1, stringThree); //[String 2, String 3, String 1]
		assertTrue(simpList.contains(stringThree));
		assertEquals(stringTwo, simpList.get(0));
		assertEquals(stringThree, simpList.get(1));
		assertEquals(stringOne, simpList.get(2));
		
		//Check if array resizes
		simpList.add(stringFour);
		assertEquals(3, simpList.indexOf(stringFour));
		
		//Check invalid addition
		try {
			simpList.add(3, stringTwo); 
			fail();
		} catch (IllegalArgumentException e) {
			assertEquals("List contains element", e.getMessage());
		}
		try {
			simpList.add(5, "String");
			fail();
		} catch(IndexOutOfBoundsException e) {
			assertEquals("pos is out of bounds", e.getMessage());
		}
		try {
			simpList.add(0, null);
			fail();
		} catch(NullPointerException e) {
			assertEquals("Null element", e.getMessage());
		}
		
		simpList = new SimpleArrayList<String>(5);
		simpList.add(stringOne); // [String 1]
		simpList.add(0, stringTwo); //[String 2, String 1]
		simpList.add(0, stringThree); //[String 3, String 2, String 1]
		
		assertEquals(stringThree, simpList.get(0));
		assertEquals(stringTwo, simpList.get(1));
		assertEquals(stringOne, simpList.get(2));
	}
	
	/**
	 * Test removing elements
	 */
	@Test
	public void testRemove() {
		SimpleArrayList<String> simpList = new SimpleArrayList<String>();
		simpList.add(stringOne); // [String 1]
		simpList.add(stringTwo); //[String 1, String 2]
		simpList.add(stringThree); //[String 1, String 2, String 3]
		
		assertEquals(stringOne, simpList.get(0));
		assertEquals(stringTwo, simpList.get(1));
		assertEquals(stringThree, simpList.get(2));
		
		assertEquals(stringTwo, simpList.remove(1));
		assertEquals(stringOne, simpList.get(0));
		assertEquals(stringThree, simpList.get(1));
		assertEquals(2, simpList.size());
		
		assertEquals(stringThree, simpList.remove(1));
		assertEquals(stringOne, simpList.get(0));
		assertEquals(1, simpList.size());
		
		assertEquals(stringOne, simpList.remove(0));
		assertEquals(0, simpList.size());
	}
	
	

}
