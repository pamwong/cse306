// List.java
//
//     	Class of singly-linked lists of objects.
//
// Copyright (c) 1992-1993 The Regents of the University of California.
// Copyright (c) 1998 Rice University.
// Copyright (c) 2003 State University of New York at Stony Brook.
// All rights reserved.  See the COPYRIGHT file for copyright notice and
// limitation of liability and disclaimer of warranty provisions.

package nachos.kernel.threads;

/**
 * The following class defines a "list" -- a singly linked list of
 * list elements, each of which points to a single item on the list.
 * Although the Java API provides classes with functionality that subsume
 * this one, linked lists are such an important data structure in operating
 * systems that it seemed like a good idea to include here an implementation
 * "from scratch".
 *
 * By using the sortedInsert() method, the list can be kept in sorted
 * in increasing order with respect to a "sortKey".
 *
 * NOTE: Mutual exclusion must be provided by the caller.
 * If you want a synchronized list, you must use the routines 
 * in synchlist.cc.
 */
public class List {

  /** Head of the list, null if list is empty */
  private ListElement first;

  /** Last element of the list. */
  private ListElement last;

  /**
   * Initialize a list, empty to start with.
   * Elements can then be added to the list.
   */
  public void List() { 
    first = last = null; 
  }

  /**
   * Append an "item" to the end of the list.
   *
   * Allocate a ListElement to keep track of the item.
   *    If the list is empty, then this will be the only element.
   *	Otherwise, put it at the end.
   *
   * @param item The thing to put on the list, it can be any object.
   */
  public void append(Object item) {
    ListElement element = new ListElement(item, 0);

    if (isEmpty()) {		// list is empty
      first = element;
      last = element;
    } else {			// else put it after last
      last.next = element;
      last = element;
    }
  }

  /**
   *    Put an "item" on the front of the list.
   *
   * Allocate a ListElement to keep track of the item.
   *    If the list is empty, then this will be the only element.
   *	Otherwise, put it at the end.
   *
   * @param item The thing to put on the list, it can be any object.
   */
  public void prepend(Object item) {
    ListElement element = new ListElement(item, 0);

    if (isEmpty()) {		// list is empty
      first = element;
      last = element;
    } else {			// else put it before first
      element.next = first;
      first = element;
    }
  }

  /**
   * Remove the first "item" from the front of the list.
   *
   * @return the removed item, or null if nothing on the list.
   */
  public Object remove() {
    if (isEmpty()) 
      return null;
    else {
      ListElement e = first;
      if (first == last) {	// list had one item, now has none 
	  first = null;
	  last = null;
      } else {
	  first = e.next;
      }
      return(e.item);
    }
  }

  /**
   * Determine if the list is empty (has no items).
   *
   * @return true if the list is empty, otherwise false.
   */
  public boolean isEmpty() { 
    if (first == null)
      return true;
    else
      return false; 
  }

  /**
   * Insert an "item" into a list, so that the list elements are
   * sorted in increasing order by "sortKey".
   *
   * Allocate a ListElement to keep track of the item.
   * If the list is empty, then this will be the only element.
   * Otherwise, walk through the list, one element at a time,
   * to find where the new item should be placed.
   *
   * @param item The thing to put on the list, it can be any object.
   * @param sortKey The priority of the item.
   */
  public void sortedInsert(Object item, long sortKey) {
    ListElement element = new ListElement(item, sortKey);
    ListElement tmp;		// keep track

    if (isEmpty()) {	// if list is empty, put
      first = element;
      last = element;
    } else if (sortKey < first.key) {	
      // item goes on front of list
      element.next = first;
      first = element;
    } else {		// look for first elt in list bigger than item
      for (tmp = first; tmp.next != null; tmp = tmp.next) {
	if (sortKey < tmp.next.key) {
	  element.next = tmp.next;
	  tmp.next = element;
	  return;
	}
      }
      last.next = element;		// item goes at end of list
      last = element;
    }
  }

  /**
   * The following class defines a "list element" -- which is
   * used to keep track of one item on a list.  It is equivalent to a
   * LISP cell, with a "car" ("item") pointing to the item element on the list,
   * and a "cdr" ("next") pointing to the next item on the list.
   * A "list element" is allocated for each item to be put on the
   * list; it is de-allocated when the item is removed. This means
   * we don't need to keep a "next" pointer in every object we
   * want to put on a list.
   */
  protected static class ListElement {
      /** The next element on list, null if this is the last. */
      ListElement next;

      /** Priority, for a sorted list. */
      long key;

      /** Reference to item on the list. */
      Object item;

      /**
       * Initialize a list element, so it can be added somewhere on a list.
       *
       * @param item  The item to be put on the list, it can be any object.
       * @param sortKey  The priority of the item, if any.
       */
      public ListElement(Object item, long sortKey) { 
	  this.item = item;
	  key = sortKey;
	  next = null;	// assume we'll put it at the end of the list 
      }
  }
}

