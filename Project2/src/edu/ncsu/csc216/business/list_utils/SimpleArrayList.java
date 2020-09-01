package edu.ncsu.csc216.business.list_utils;

/**
 * A simple array list which is an implementation of the SimpleList.
 * Specifically tailored to better store a list of clients.
 * 
 * @param <E> Type of list element
 * 
 * @author Anton
 */
public class SimpleArrayList<E> implements SimpleList<E> {

	/** Doubles the size if the array reaches capacity */
	private static final int RESIZE = 12;

	/** Objects actually contained in the ArrayList */
	private Object[] list;

	/** Number of elements in this list */
	private int size;

	/**
	 * Constructor for the SimpleArrayList
	 */
	public SimpleArrayList() {
		this.size = 0;
		list = new Object[10];
	}

	/**
	 * Constructor for the SimpleArrayList with a set size
	 * 
	 * @param size The size of the list
	 * @throws IllegalArgumentException if the size is less than or equal to 0
	 */
	public SimpleArrayList(int size) {
		if (size <= 0) {
			throw new IllegalArgumentException();
		}
		this.size = 0;
		list = new Object[size];
	}

	/**
	 * Method used to return the array size
	 * 
	 * @return the size of the list
	 */
	@Override
	public int size() {
		return this.size;
	}

	/**
	 * Method used to determine if the array list is empty
	 * 
	 * @return true if list is empty false otherwise
	 */
	@Override
	public boolean isEmpty() {
		int counter = 0;
		for (int i = 0; i < list.length; i++) {
			if (list[i] != null) {
				counter++;
			}
		}
		if (counter != 0) {
			return false;
		}
		return true;
	}

	/**
	 * Method used to determine if the array list contains the given element
	 * 
	 * @param e The element being searched for
	 * @return true if the element is in the list
	 */
	@Override
	public boolean contains(E e) {
		if (this.isEmpty()) {
			return false;
		}
		for (int i = 0; i < size; i++) {
			if (list[i].equals(e)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Adds the given element to the end of the list
	 * 
	 * @param e the element being added
	 * @throws IllegalArgumentException if the list already contains e
	 * @return true if
	 */
	@Override
	public boolean add(E e) {
		if (this.contains(e)) {
			throw new IllegalArgumentException("List contains element");
		}
		if (e == null) {
			throw new NullPointerException("Null element");
		}
		try {
			if (this.size() == list.length) {
				// resize array
				Object[] newList = new Object[list.length + RESIZE];
				for (int i = 0; i < list.length; i++) {
					newList[i] = list[i];
				}
				list = newList;
			}
			list[this.size()] = e;
			size++;
			return true;
		} catch (Exception exp) {
			return false;
		}
	}

	/**
	 * Adds the given element to a certain position in the list
	 * 
	 * @param pos the position at which to insert the new element
	 * @param e   the element being inserted
	 * @throws NullPointerException      if the element being added is null
	 * @throws IllegalArgumentException  if some property of the specified element
	 *                                   prevents it from being added to this list
	 * @throws IndexOutOfBoundsException if the index is outside the list
	 */
	@Override
	public void add(int pos, E e) {
		if (this.contains(e)) {
			throw new IllegalArgumentException("List contains element");
		}
		if (e == null) {
			throw new NullPointerException("Null element");
		}
		if (pos < 0 || pos > this.size()) {
			throw new IndexOutOfBoundsException("pos is out of bounds");
		}
		try {
			if (pos == this.size()) {
				this.add(e);
			} else {
				// check if adding element will overflow array
				if (this.size() + 1 > list.length) {
					// resize array
					Object[] newList = new Object[list.length * RESIZE];
					for (int i = 0; i < list.length; i++) {
						newList[i] = list[i];
					}
					list = newList;
				}
				// shift elements to the right starting at pos
				for (int i = this.size; i > pos; i--) {
					@SuppressWarnings("unchecked")
					E temp = (E) list[i - 1];
					list[i] = temp;
				}
				list[pos] = e;
				size++;
			}
		} catch (Exception exp) {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Removes the element at the given index from the list
	 * 
	 * @param index the index of the element being removed
	 * @return the element that was removed
	 * @throws IndexOutOfBoundsException if the index is outside the list
	 */
	@SuppressWarnings("unchecked")
	@Override
	public E remove(int index) {
		if (index < 0 || index > this.size()) {
			throw new IndexOutOfBoundsException("Removal index out of bounds");
		}
		E removedElement = (E) this.get(index);
		E temp;
		for (int i = index + 1; i < this.size(); i++) {
			temp = (E) list[i];
			list[i - 1] = temp;
		}
		size--;
		return removedElement;
	}

	/**
	 * Returns the element from the list at the given index
	 * 
	 * @param pos the index of the element to be returned
	 * @return the element at the given position
	 * @throws IndexOutOfBoundsException if the index is outside the list
	 */
	@SuppressWarnings("unchecked")
	@Override
	public E get(int pos) {
		if (pos < 0 || pos >= this.size()) {
			throw new IndexOutOfBoundsException();
		}
		return (E) list[pos];
	}

	/**
	 * Returns the index in the list of the given element
	 * 
	 * @param e the element being searched for
	 * @return the index of the element or -1 if the element does not exist in the
	 *         list
	 */
	@Override
	public int indexOf(E e) {
		int ret = -1;
		for (int i = 0; i < size; i++) {
			if (list[i].equals(e)) {
				ret = i;
			}
		}
		return ret;
	}

}