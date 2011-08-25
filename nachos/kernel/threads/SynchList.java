// SynchList.cc
//	Synchronized List class.
//
//	Implemented by surrounding the List abstraction
//	with synchronization routines.
//
// 	Implemented in "monitor"-style -- surround each procedure with a
// 	lock acquire and release pair, using condition signal and wait for
// 	synchronization.
//
// Copyright (c) 1992-1993 The Regents of the University of California.
// Copyright (c) 1998 Rice University.
// Copyright (c) 2003 State University of New York at Stony Brook.
// All rights reserved.  See the COPYRIGHT file for copyright notice and
// limitation of liability and disclaimer of warranty provisions.

package nachos.kernel.threads;

/**
 *
 * This class defines a "synchronized list" -- a list for which
 * these constraints hold:
 *	1. Threads trying to remove an item from a list will
 *	wait until the list has an element on it.
 *      2. One thread at a time can access list data structures
 */
public class SynchList extends List {

  /** Lock to enforce mutual exclusive access to the list. */
  private Lock lock;

  /** Used in remove to wait if the list is empty. */
  private Condition listEmpty;

  /**
   *	Allocate and initialize the data structures needed for a 
   *	synchronized list, empty to start with.
   *	Elements can then be added to the list.
   */
  public SynchList() {
    super();
    lock = new Lock("SynchList lock");
    listEmpty = new Condition("SynchList condition", lock);
  }

  /**
   * Append an "item" to the end of the list.  Wake up anyone
   * waiting for an element to be appended.
   *
   * @param item The thing to put on the list, it can be any object.
   */
  public void append(Object item) {
    lock.acquire();
    super.append(item);
    listEmpty.signal();    	// wake up a waiter, if any
    lock.release();
  }

  /** Put an "item" on the front of the list. Wake up anyone
   *  waiting for an element to be appended.  
   *
   * @param item The thing to put on the list, it can be any object.
   */
  public void prepend(Object item) {
    lock.acquire();    
    super.prepend(item);
    listEmpty.signal();    	// wake up a waiter, if any
    lock.release();
  }

  /**
   * Remove an "item" from the beginning of the list.  Wait if
   *	the list is empty.
   *
   * @return the removed item.
   */
  public Object remove() {
    lock.acquire();    
    while (isEmpty()) {
      listEmpty.await();      // wait until list isn't empty
    }
    Object o = super.remove();
    lock.release();
    return o;
  }

  /**
   * Insert an "item" into a list, so that the list elements are
   * sorted in increasing order by "sortKey".
   * Wakeup anyone waiting for an item to be inserted
   *
   * @param item  The thing to put on the list, it can be any object.
   * @param sortKey  The priority of the item.
   */
  public void sortedInsert(Object item, long sortKey) {
    lock.acquire();    
    super.sortedInsert(item, sortKey);
    listEmpty.signal();    	// wake up a waiter, if any
    lock.release();
  }
}
