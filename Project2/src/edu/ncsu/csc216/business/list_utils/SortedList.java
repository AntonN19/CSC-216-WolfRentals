package edu.ncsu.csc216.business.list_utils;

/**
 * An ordered collection (also known as a sequence). The user of this
 * interface has precise control over where in the list each element is
 * inserted. The user can access elements by their integer index
 * (position in the list), and search for elements in the list.
 * This interface is a subset of the java.util.List interface and requires that
 * elements be stored in sorted order based on Comparable. No duplicate items.
 * 
 * This interface is adapted from java.util.List.
 * 
 * @author Jessica Young Schmidt
 * @author Jo Perry
 * @author David R. Wright
 * @author Josh Bloch
 * @author Neal Gafter
 *
 * @param <E> collection element type
 * @since 1.2
 */

public interface SortedList<E extends Comparable<E>> {


    /**
     * Returns the number of elements in this list. If this list contains more
     * than Integer.MAX_VALUE elements, returns Integer.MAX_VALUE.
     * 
     * @return the number of elements in this list
     */
    int size();

    /**
     * Is this list empty?
     * 
     * @return true if this list contains no elements
     */
    boolean isEmpty();

    /**
     * Returns true if this list contains the specified element. More formally,
     * returns true if and only if this list contains at least one element a
     * such that (o==null ? a==null : o.equals(a)).
     *
     * @param e element whose presence in this list is to be tested
     * @return true if this list contains the specified element
     */
    boolean contains(E e);

    // Modification Operations

    /**
     * Adds the specified element to list in sorted order
     *
     * @param e element to be appended to this list
     * @return true (if the operation is a success) 
     * @throws NullPointerException if e is null
     * @throws IllegalArgumentException if list already contains e
     */
    boolean add(E e);    
    
    /**
     * Removes all elements from the list, making it empty.
     */
    void clear();

    // Positional Access Operations

    /**
     * Returns the element at the specified position in this list.
     *
     * @param index index of the element to return
     * @return the element at the specified position in this list
     * @throws IndexOutOfBoundsException if the index is out of range (index is negative
     *             or index is at least as large as size)
     */
    E get(int index);

    /**
     * Removes the element at the specified position in this list. Shifts any
     * subsequent elements to the left (subtracts one from their indices).
     * Returns the element that was removed from the list.
     *
     * @param index the index of the element to be removed
     * @return the element previously at the specified position
     * @throws IndexOutOfBoundsException if the index is out of range (index is negative
     *             or index is at least as large as size)
     */
    E remove(int index);
    
    /**
     * Truncates this list starting at the given index and returns the truncated tail. 
     * (Example, if the list contains 6 elements, then list.truncate(2) would contain the 
     * first two elements of the list. The truncated tail would contain the remaining 4 
     * elements.) The ordering of elements in the original list does not change nor does 
     * the ordering of elements in the truncated tail.
     *  
     * @param start index truncate the list starting at this index
     * @return the tail of this list that was truncated. 
     * @throws IndexOutOfBoundsException if the index is out of range (start is negative
     *             or start is at least as large as size)
     */
    SortedList<E> truncate(int start);

    // Search Operations

    /**
     * Returns the index of the first occurrence of the specified element in
     * this list, or -1 if this list does not contain the element. More
     * formally, returns the lowest index i such that (o==null ? get(i)==null :
     * o.equals(get(i))), or -1 if there is no such index.
     *
     * @param e element to search for
     * @return the index of the first occurrence of the specified element in
     *         this list, or -1 if this list does not contain the element
     */
    int indexOf(E e);

}