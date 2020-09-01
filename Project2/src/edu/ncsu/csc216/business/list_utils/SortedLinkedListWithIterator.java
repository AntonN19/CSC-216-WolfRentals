/**
 * 
 */
package edu.ncsu.csc216.business.list_utils;

import java.util.*;

/**
 * Sorted Linked list class that includes an iterator. Specifically designed to
 * use comparable when sorting the list. Tailored for the storage and sorting of
 * leases and clients.
 * 
 * @param <E> the type of list element
 * 
 * @author Anton
 */
public class SortedLinkedListWithIterator<E extends Comparable<E>> implements SortedList<E> {

	/** The first node in the list */
	private Node<E> head;

	/**
	 * Method constructor
	 */
	public SortedLinkedListWithIterator() {
		head = new Node<E>(null, null);
	}

	/**
	 * Returns the size of linked list
	 * 
	 * @return the size of the list
	 */
	@SuppressWarnings("unchecked")
	@Override
	public int size() {
		int size = 0;
		Cursor current = new Cursor();
		if (!this.isEmpty()) {
			while (current.hasNext()) {
				size++;
				current.next();
			}
		}
		return size;
	}

	/**
	 * Determines if the list is empty
	 * 
	 * @return true only if the list is empty
	 */
	@Override
	public boolean isEmpty() {
		if (head == null || head.value == null) {
			return true;
		}
		return false;
	}

	/**
	 * Determines if the list contains a given element
	 * 
	 * @param e the element being searched for
	 * @return true if the element is a part of the list
	 */
	@Override
	public boolean contains(E e) {
		Cursor current = new Cursor();
		if (!this.isEmpty()) {
			if (head.value.equals(e)) {
				return true;
			}
			while (current.hasNextInternal()) {
				if (current.nextInternal().equals(e)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Adds element to the list in a sorted order
	 * 
	 * @param e The element to be added
	 * @return true if the element was added successfully
	 * @throws NullPointerException     if element being added is null
	 * @throws IllegalArgumentException if list already contains the element being
	 *                                  added
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean add(E e) {
		if (e == null) {
			throw new NullPointerException("Null element can not be added");
		}
		if (this.contains(e)) {
			throw new IllegalArgumentException("Element already contained in the list");
		}
		try {
			if (this.isEmpty()) { // empty list
				this.head = new Node<E>(e, null);
				return true;
			} else if (this.size() == 1) {
				if (e.compareTo(head.value) < 0) {
					head = new Node<E>(e, head);
				} else {
					head.next = new Node<E>(e, null);
				}
				return true;
			} else {
				Cursor current = new Cursor();
				Node<E> temp = null;
				Cursor prev = null;
				int counter = 0;
				while (current.hasNextInternal()) {
					temp = current.traveler;
					if (e.compareTo(temp.value) < 0) { // e comes before current element
						if (counter == 0) {
							head = new Node<E>(e, temp);
							break;
						} else {
							current.traveler = new Node<E>(e, temp);
							prev.traveler.next = current.traveler;
							break;
						}
					}
					current.nextInternal();
					if (counter == 0) {
						prev = new Cursor();
					} else {
						prev.nextInternal();
					}
					counter++;
				}
				if (counter == this.size() - 1) {// adding to the back
					temp = current.traveler;
					if (e.compareTo(current.traveler.value) < 0) {
						current.traveler = new Node<E>(e, temp);
						prev.traveler.next = current.traveler;
					} else {
						current.traveler.next = new Node<E>(e, null);
					}
				}
				return true;
			}
		} catch (Exception exp) {
			return false;
		}
	}

	/**
	 * Clears the list making it empty
	 */
	@Override
	public void clear() {
		head = null;
	}

	/**
	 * Returns the element in the list at the given position
	 * 
	 * @param index the position of the element
	 * @return the element at the given position
	 * @throws IndexOutOfBoundsException if the index is outside of the list
	 */
	@Override
	public E get(int index) {
		if (index < 0 || index >= this.size()) {
			throw new IndexOutOfBoundsException("index is outside of list");
		}
		Cursor current = new Cursor();
		E ret = current.traveler.value;
		for (int i = 1; i <= index; i++) {
			ret = current.nextInternal();
		}
		return ret;
	}

	/**
	 * Removes an element from the list at the given index
	 * 
	 * @param index the index of the element to be removed
	 * @return the removed element
	 * @throws IndexOutOfBoundsException if the index is outside of the list
	 */
	@Override
	public E remove(int index) {
		if (index < 0 || index >= this.size()) {
			throw new IndexOutOfBoundsException("index is outside of list");
		}
		Cursor current = new Cursor();
		// find value at given index
		E ret = this.get(index);

		if (index == 0) { // removal from front of list
			head = head.next;
		} else {
			// shift elements to the left after removal point
			for (int i = 1; i <= index; i++) {
				if (i == this.size() - 1) {// removal from end of list
					current.traveler.next = null;
					break;
				}
				if (i < index) { // before the removal point
					current.nextInternal();
				}
				if (i == index) {
					current.traveler.next = current.traveler.next.next;
				}
			}
		}
		return ret;
	}

	/**
	 * Converts list to a string
	 * 
	 * @return the list as a string
	 */
	@Override
	public String toString() {
		String ret = "";
		ret += "[";
		if (this.size() == 1) {
			ret += this.get(0).toString();
		} else {
			for (int i = 0; i < this.size(); i++) {
				if (i == 0) {
					ret += this.get(i).toString();
				} else {
					ret += ", " + this.get(i).toString();
				}
			}
		}
		ret += "]";
		return ret;
	}

	/**
	 * Truncates the list starting at the given index and returns the tail
	 * 
	 * @param start the index to start the truncation at
	 * @return the truncated tail as a SortedList
	 * @throws IllegalArgumentException if the index outside of the list
	 */
	@Override
	public SortedList<E> truncate(int start) {
		if (start < 0 || start > this.size()) {
			throw new IllegalArgumentException("index is outside of list");
		}
		Cursor current = new Cursor();
		SortedList<E> returnedList = new SortedLinkedListWithIterator<E>();
		if (start == this.size()) {// start point right after list end return an empty list
			return returnedList;
		}
		// fills up the returned list
		for (int i = 0; i < this.size(); i++) {
			if (i < start) {
				current.nextInternal();
			} else {
				returnedList.add(current.traveler.value);
				if (current.hasNextInternal()) {// moves pointer if there is a next value
					current.nextInternal();
				}
			}
		}

		// truncates list
		if (start == 0) { // if truncation starts at idx 0
			this.clear();
		} else {
			current = new Cursor();
			for (int i = 1; i <= start; i++) {
				if (i < start) {
					current.nextInternal();
				} else {
					current.traveler.next = null;
				}
			}
		}
		return returnedList;
	}

	/**
	 * Determines the index of a given element in the list
	 * 
	 * @param e The element being searched for
	 * @return the index of the element in the list -1 if the given element is not
	 *         in the list
	 */
	@Override
	public int indexOf(E e) {
		int ret = -1;
		Cursor current = new Cursor();
		if (this.contains(e)) {
			ret = 0;
			if (current.traveler.value.equals(e)) {
				return ret;
			}
			while (current.hasNextInternal()) {
				current.nextInternal();
				ret++;
				// increases current index counter and checks if the value at this index is
				// equal to e
				if (current.traveler.value.equals(e)) {
					return ret;
				}
			}
		}
		return ret;
	}

	/**
	 * Returns the iterator for this list
	 * 
	 * @return iterator
	 */
	public SimpleListIterator<E> iterator() {
		Cursor iterator = new Cursor();
		return iterator;
	}

	/**
	 * A class containing the element and a reference to the next element in the
	 * list
	 * 
	 * @author Anton
	 *
	 * @param <E> The type element
	 */
	private static class Node<E> {

		/** The value of the node element */
		protected E value;

		/** Reference to the next node */
		private Node<E> next;

		/**
		 * Constructor for the node class
		 * 
		 * @param value the value contained in the node
		 * @param next  reference to the next node
		 */
		public Node(E value, Node<E> next) {
			if (next != null) {
				this.next = next;
			}
			this.value = value;
		}
	}

	/**
	 * A class that provides a cursor for going through the list without changing it
	 * 
	 * @author Anton
	 *
	 */
	private class Cursor implements SimpleListIterator<E> {

		/** The node that the cursor is pointing at */
		private Node<E> traveler;

		/**
		 * Constructor for the cursor class
		 */
		public Cursor() {
			traveler = head;
		}

		/**
		 * Determine if there is a next element in the list
		 * 
		 * @return true if there is a next element
		 */
		@Override
		public boolean hasNext() {
			if (traveler == null || traveler.value == null) {
				return false;
			}
			return true;
		}

		private boolean hasNextInternal() {
			if (traveler == null) {
				return false;
			} else {
				if (traveler.next == null) {
					return false;
				}
				return true;
			}
		}

		/**
		 * Next element in the list
		 * 
		 * @return the next element in the list
		 * @throws NoSuchElementException if the list has already been traversed and
		 *                                there is no element next.
		 */
		@SuppressWarnings("unused")
		@Override
		public E next() {
			if (!this.hasNext()) {
				throw new NoSuchElementException();
			}
			E ret = traveler.value;
			traveler = traveler.next;
			return ret;
		}

		@SuppressWarnings("unchecked")
		private E nextInternal() {
			if (!this.hasNextInternal()) {
				throw new NoSuchElementException();
			}
			traveler = traveler.next;
			E ret = traveler.value;
			return ret;
		}

	}
}
