// Condition.java
//
// Copyright (c) 1992-1993 The Regents of the University of California.
// Copyright (c) 1998 Rice University.
// Copyright (c) 2003 State University of New York at Stony Brook.
// All rights reserved.  See the COPYRIGHT file for copyright notice and
// limitation of liability and disclaimer of warranty provisions.

package nachos.kernel.threads;

import nachos.Debug;
import nachos.machine.NachosThread;
import nachos.machine.Interrupt;

/**
 * This class defines a "condition variable".  A condition
 * variable does not have a value, but threads may be queued, waiting
 * on the variable.  The following are the only operations on a condition
 * variable: 
 *
 *	await() -- release the lock, relinquish the CPU until signaled, 
 *		then re-acquire the lock
 *
 *	signal() -- wake up a thread, if there are any waiting on 
 *		the condition
 *
 *	broadcast() -- wake up all threads waiting on the condition
 *
 * All operations on a condition variable must be made while
 * the current thread has acquired a lock.  Indeed, all accesses
 * to a given condition variable must be protected by the same lock.
 * In other words, mutual exclusion must be enforced among threads calling
 * the condition variable operations.
 *
 * In Nachos, condition variables are assumed to obey *Mesa*-style
 * semantics.  When a Signal or Broadcast wakes up another thread,
 * it simply puts the thread on the ready list, and it is the responsibility
 * of the woken thread to re-acquire the lock (this re-acquire is
 * taken care of within await()).  By contrast, some define condition
 * variables according to *Hoare*-style semantics -- where the signalling
 * thread gives up control over the lock and the CPU to the woken thread,
 * which runs immediately and gives back control over the lock to the 
 * signaller when the woken thread leaves the critical section.
 *
 * The consequence of using Mesa-style semantics is that some other thread
 * can acquire the lock, and change data structures, before the woken
 * thread gets a chance to run.
 */
public class Condition {

  /** Printable name useful for debugging. */
  public final String name;

  /** The lock associated with this condition. */
  private Lock conditionLock;

  /** Who's waiting on this condition? */
  private List waitingThreads;

  /**
   * Initialize a new condition variable.
   *
   * @param debugName An arbitrary name, useful for debugging.
   * @param lock A lock to be associated with this condition.
   */
  public Condition(String debugName, Lock lock) {
    name = debugName;
    conditionLock = lock;;
    waitingThreads = new List();
  }

  /**
   * Wait on a condition until signalled.  The caller must hold the
   * lock associated with the condition.  The lock is released, and
   * the caller relinquishes the CPU until it is signalled by another
   * thread that calls signal or broadcast on the same condition.
   */
  public void await() {
    Debug.ASSERT(conditionLock.isHeldByCurrentThread(),
		 "Non-owner tried to manipulate condition variable.");
    Debug.printf('s', "Thread %s waiting on condition variable %s\n",
		 Scheduler.currentThread().name, name);

    int oldLevel = Interrupt.setLevel(Interrupt.IntOff);
    conditionLock.release();
    waitingThreads.append(Scheduler.currentThread());
    Scheduler.sleep();
    Interrupt.setLevel(oldLevel);

    Debug.printf('s', "Trying to reacquire condition %s's lock (%s) for " +
		 "thread %s\n", name, conditionLock.name,
		 Scheduler.currentThread().name);

    conditionLock.acquire();

    Debug.printf('s', "Reacquired condition %s's lock (%s) for " +
		 "thread %s\n", name, conditionLock.name,
		 Scheduler.currentThread().name);
  }

  /**
   * Wake up a thread, if any, that is waiting on the condition.
   */
  public void signal() {
    Debug.ASSERT(conditionLock.isHeldByCurrentThread(),
		  "Can't signal unless we own the lock!");
    Debug.printf('s', "Signalling condition %s\n", name);

    int oldLevel = Interrupt.setLevel(Interrupt.IntOff);
    NachosThread newThread = (NachosThread)(waitingThreads.remove());
    if (newThread != null) {
      Debug.printf('s', "Waking up thread %s\n", newThread.name);
      Scheduler.readyToRun(newThread);
    }
    Interrupt.setLevel(oldLevel);
  }    

  /**
   * Wake up all threads waiting on the condition.
   */
  public void broadcast() {
    Debug.ASSERT(conditionLock.isHeldByCurrentThread(),
		 "Can't signal unless we own the lock!");
    Debug.printf('s', "Broadcasting condition %s\n", name);

    int oldLevel = Interrupt.setLevel(Interrupt.IntOff);
    NachosThread newThread = (NachosThread)(waitingThreads.remove());
    while (newThread != null) {
      Debug.printf('s', "Waking thread %s\n", newThread.name);
      Scheduler.readyToRun(newThread);
      newThread = (NachosThread)(waitingThreads.remove());
    }
    Interrupt.setLevel(oldLevel);
  }
}
