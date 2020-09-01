/**
 * 
 */
package edu.ncsu.csc216.business.list_utils;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Testing the SortedLinkedListWithIterator class
 * 
 * @author Anton
 */
public class SortedLinkedListWithIteratorTest {

	/** String 1 */
	private final String stringOne = "String 1";
	
	/** String 2 */
	private final String stringTwo = "String 2";
	
	/** String 3 */
	private final String stringThree = "String 3";
	
	/** String 4 */
	private final String stringFour = "String 4";
	
	/**
	 * Tests adding elements to SortedLinkedListWithIterator
	 */
	@Test
	public void testAdd() {
		SortedLinkedListWithIterator<String> list = new SortedLinkedListWithIterator<String>();
		assertEquals(0, list.size());
		
		list.add(stringOne);
		assertTrue(list.contains(stringOne));
		list.add(stringTwo);
		assertTrue(list.contains(stringTwo));
		list.add(stringThree);
		assertTrue(list.contains(stringThree));
		assertEquals(3, list.size());
		
		
		assertEquals(stringOne, list.get(0));
		assertEquals(stringTwo, list.get(1));
		assertEquals(stringThree, list.get(2));
		
		//Testing Illegal adds
		try {
			list.add(stringOne);
			fail();
		} catch(IllegalArgumentException e) {
			assertEquals("Element already contained in the list", e.getMessage());
		}
		try {
			list.add(null);
			fail();
		} catch(NullPointerException e) {
			assertEquals("Null element can not be added", e.getMessage());
		}
		
		list.clear();
		assertTrue(list.isEmpty());
		
	}
	
	/**
	 * Testing the remove and truncate functionalities
	 */
	@Test
	public void testRemoveAndTruncate() {
		SortedLinkedListWithIterator<String> list = new SortedLinkedListWithIterator<String>();
		list.add(stringOne);
		list.add(stringTwo);
		list.add(stringThree);
		list.add(stringFour);
		
		SortedLinkedListWithIterator<String> listCheck = new SortedLinkedListWithIterator<String>();
		
		assertEquals(stringOne, list.remove(list.indexOf(stringOne)));
		assertEquals(3, list.size());
		assertEquals(stringTwo, list.get(0));
		assertEquals(stringThree, list.get(1));
		assertEquals(stringFour, list.get(2));
		listCheck = (SortedLinkedListWithIterator<String>) list.truncate(1);
		assertEquals(stringThree, listCheck.get(0));
		assertEquals(stringFour, listCheck.get(1));
		assertEquals(1, list.size());
		
		list.add(stringThree);
		list.add(stringFour); //2 3 4
		assertEquals(3, list.size());
		assertEquals(stringThree, list.remove(1));
		
		assertEquals(2, list.size());
		assertEquals(stringTwo, list.get(0));
		assertEquals(stringFour, list.get(1));
		
		listCheck.clear();
		listCheck = (SortedLinkedListWithIterator<String>) list.truncate(2);
		assertEquals(0, listCheck.size());
		assertEquals(2, list.size());
		assertEquals(stringTwo, list.get(0));
		assertEquals(stringFour, list.get(1));
		
		try {
			list.truncate(3);
			fail();
		} catch (IllegalArgumentException e) {
			assertEquals(2, list.size());
		}
	}
	
	/**
	 * replicating failing ts tests
	 */
	@Test
	public void testReplica() {
		SortedLinkedListWithIterator<String> list = new SortedLinkedListWithIterator<String>();
		assertEquals(0, list.size());
		
		list.add("Bar");
		list.add("Car");
		list.add("Fox"); 
		list.add("Box");
		list.add("Apple"); //Apple Bar Box Car Cat Far Fox
		list.add("Far");
		list.add("Cat");
		
		assertEquals("Apple", list.get(0));
		assertEquals("Bar", list.get(1));
		assertEquals("Box", list.get(2));
		assertEquals("Car", list.get(3));
		assertEquals("Cat", list.get(4));
		assertEquals("Far", list.get(5));
		assertEquals("Fox", list.get(6));
		
		list.clear();
		list.add("Box");
		list.add("Apple");
		list.add("Bat");
		assertEquals("Apple", list.get(0));
		assertEquals("Bat", list.get(1));
		assertEquals("Box", list.get(2));
		
		list.clear();
		assertEquals(list.iterator().hasNext(), false);
	}
	
	/**
	 * Another test
	 */
	@Test
	public void test(){
		SortedLinkedListWithIterator<String> list = new SortedLinkedListWithIterator<String>();
		assertEquals(0, list.size());
		
		list.add(stringOne);
		list.add(stringTwo);
		list.add(stringThree);
		list.add(stringFour);
		list.add("Cat");
		list.add("Hat");
		list.add("Bat");
		list.add("Mat");
		
		assertEquals("Bat", list.get(0));
		assertEquals("Cat", list.get(1));
		assertEquals("Hat", list.get(2));
		assertEquals("Mat", list.get(3));
		assertEquals(stringOne, list.get(4));
		assertEquals(stringTwo, list.get(5));
		assertEquals(stringThree, list.get(6));
		assertEquals(stringFour, list.get(7));
		
		assertEquals(stringFour, list.remove(7));
		assertEquals("Bat", list.remove(0));
		assertEquals(6, list.size());
		
		SortedLinkedListWithIterator<String> listCheck = new SortedLinkedListWithIterator<String>();
		listCheck = (SortedLinkedListWithIterator<String>) list.truncate(0);
		assertEquals("Cat", listCheck.get(0));
		assertEquals("Hat", listCheck.get(1));
		assertEquals("Mat", listCheck.get(2));
		assertEquals(stringOne, listCheck.get(3));
		assertEquals(stringTwo, listCheck.get(4));
		assertEquals(stringThree, listCheck.get(5));
		
		assertTrue(list.isEmpty());
		list = (SortedLinkedListWithIterator<String>) listCheck.truncate(5);
		assertEquals(stringThree, list.get(0));
	}

}
