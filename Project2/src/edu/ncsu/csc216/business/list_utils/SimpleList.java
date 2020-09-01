package edu.ncsu.csc216.business.list_utils;

/**
 * A simple generic list (ordered collection) interface adapted from
 * java.util.List. List elements have zero-based indexes. List implementations
 * can put restrictions on elements (such as no duplicate elements or null
 * elements).
 * 
 * @param <E> type of list element
 * 
 * @author Jo Perry
 */
public interface SimpleList<E> {

	/**
	 * Returns the number of elements in this list or Integer.MAX_VALUE if the list
	 * has more than Integer.MAX_VALUE elements.
	 *
	 * @return the number of elements in this list
	 */
	int size();

	/**
	 * Does this list contain elements?
	 *
	 * @return true if and only if this list contains no elements
	 */
	boolean isEmpty();

	/**
	 * Does this list contains the specified element?
	 *
	 * @param e element whose presence in this list is to be tested
	 * @return true if this list contains the specified element e
	 */
	boolean contains(E e);

	/**
	 * Adds the specified element to the end of the list.
	 *
	 * @param e element to be appended to this list
	 * @return true (as specified by {@link Collection#add})
	 * @throws IllegalArgumentException if the list already contains e
	 */
	boolean add(E e);

	/**
	 * Inserts the specified element at the specified position in this list. Shifts
	 * any element currently at that position plus all subsequent elements to the
	 * right (adds one to their indexes).
	 *
	 * @param pos index at which the specified element is to be inserted
	 * @param e   element to be inserted
	 * @throws NullPointerException      if the specified element is null and this
	 *                                   list does not permit null elements
	 * @throws IllegalArgumentException  if some property of the specified element
	 *                                   prevents it from being added to this list
	 * @throws IndexOutOfBoundsException if the index is out of range (pos < 0 ||
	 *                                   pos > size())
	 */
	void add(int pos, E e);

	/**
	 * Removes the element at the specified position in this list, shifting any
	 * subsequent elements to the left (subtracts one from their indexes). Returns
	 * the element that was removed from the list.
	 *
	 * @param index the index of the element to be removed
	 * @return the element previously at the specified position
	 * @throws IndexOutOfBoundsException if the index is out of range (index < 0 ||
	 *                                   index >= size())
	 */
	E remove(int index);

	/**
	 * Returns the element at the specified position in this list.
	 *
	 * @param pos index of the element to return
	 * @return the element at the specified position in this list
	 * @throws IndexOutOfBoundsException if the index is out of range (pos < 0 ||
	 *                                   pos >= size())
	 */
	E get(int pos);

	/**
	 * Returns the index of the first occurrence of the specified element in this
	 * list, or -1 if this list does not contain the element.
	 *
	 * @param e element to search for
	 * @return the index of the first occurrence of the specified element in this
	 *         list, or -1 if this list does not contain the element
	 */
	int indexOf(E e);

}