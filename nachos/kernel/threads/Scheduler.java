// Scheduler.java
//
//	fork -- create a thread to run a procedure concurrently
//		with the caller (this is done in two steps -- first
//		allocate the Thread object, then call Fork on it)
//	finish -- called when the forked procedure finishes, to clean up
//	yield -- relinquish control over the CPU to another ready thread
//	sleep -- relinquish control over the CPU, but thread is now blocked.
//		In other words, it will not run again, until explicitly 
//		put back on the ready queue.
// Copyright (c) 1992-1993 The Regents of the University of California.
// Copyright (c) 1998 Rice University.
// Copyright (c) 2003 State University of New York at Stony Brook.
// All rights reserved.  See the COPYRIGHT file for copyright notice and
// limitation of liability and disclaimer of warranty provisions.

package nachos.kernel.threads;

import nachos.Debug;
import nachos.machine.NachosThread;
import nachos.machine.Interrupt;
import nachos.machine.Timer;
import nachos.kernel.devices.InterruptHandler;

/**
 * The scheduler is responsible for maintaining a list of threads that
 * are ready to run and for choosing the next thread to run.
 *
 * These routines assume that interrupts are already disabled.
 * If interrupts are disabled, we can assume mutual exclusion
 * (since we are on a uniprocessor).
 *
 * NOTE: We can't use Locks to provide mutual exclusion here, since
 * if we needed to wait for a lock, and the lock was busy, we would 
 * end up calling findNextToRun(), and that would put us in an 
 * infinite loop.
 *
 * Very simple implementation -- no priorities, straight FIFO.
 * Might need to be improved in later assignments.
 */
public class Scheduler {

  /** Queue of threads that are ready to run, but not running. */
  private static List readyList;

  /** The currently running thread, or null, if none. */
  private static NachosThread currentThread;

  /** Terminated thread awaiting reclamation of its stack. */
  private static NachosThread threadToBeDestroyed;

  /** The timer used to implement time slicing. */
  private static Timer timer = null;

  /** Accessor method for accessing the current thread. */
  public static NachosThread currentThread() { return(currentThread); }

  /**
   * Initialize the scheduler.
   * Set the list of ready but not running threads to empty.
   * Process command-line arguments.
   */
  public static void init(String[] args) {
    readyList = new List(); 
    for (int i=0; i<args.length; i++) {
      if (args[i].equals("-rs"))
	setRandomYield(true);
    }
  }

  /**
   * Called by a Java thread (usually the initial thread that calls 
   * Nachos.main) to start the first Nachos thread.
   */
  public static void start() {
    NachosThread nextThread;

    Debug.println('t', "Scheduling first Nachos thread");

    nextThread = findNextToRun();
    if (nextThread == null) {
      Debug.print('+', "Scheduler.start(): no NachosThread ready!");
      return;
    }

    Debug.println('t', "Switching to thread: " + nextThread.getName());

    nextThread.setStatus(NachosThread.RUNNING);
    currentThread = nextThread;

    // nextThread is now running
  }

  /**
   * Mark a thread as ready, but not running.
   * Put it on the ready list, for later scheduling onto the CPU.
   *
   * @param thread The thread to be put on the ready list.
   */
  public static void readyToRun(NachosThread thread) {
    Debug.print('t', "Putting thread on ready list: " + thread.getName() + 
		"\n");

    thread.setStatus(NachosThread.READY);
    readyList.append(thread);
  }
  
  /**
   * Return the next thread to be scheduled onto the CPU.
   * If there are no ready threads, return null.
   * Side effect:
   *	Thread is removed from the ready list.
   *
   * @return the thread to be scheduled onto the CPU.
   */
  private static NachosThread findNextToRun() {
    return (NachosThread)readyList.remove();
  }

  /**
   * Dispatch the CPU to nextThread.  Save the state of the old thread,
   * and load the state of the new thread, by calling the machine
   * dependent context switch routine, NachosThread.switchTo().
   *
   * Note: we assume the state of the previously running thread has
   * already been changed from running to blocked or ready (depending).
   * Side effect:
   *    The global variable currentThread becomes nextThread.
   *
   * @param nextThread The thread to be given the CPU.
   */
  private static void run(NachosThread nextThread) {
    Debug.ASSERT(currentThread != null
		 && currentThread.getStatus() != NachosThread.RUNNING);

    currentThread.saveState();  // save the user's CPU registers and
    				// address space, if any.
    
    Debug.println('t', "Switching from thread: " + currentThread.getName() +
		  " to thread: " + nextThread.getName());

    NachosThread oldThread = currentThread;
    currentThread = nextThread;
    oldThread.switchTo(nextThread);

    Debug.println('t', "Now in thread: " + currentThread.getName());

    currentThread.restoreState();   // restore user's CPU registers and
    				    // address space, if any.

    // If the old thread gave up the processor because it was finishing,
    // we need to delete its carcass. 
    if (threadToBeDestroyed != null) {
	threadToBeDestroyed.setStatus(NachosThread.DESTROYED);
	threadToBeDestroyed = null;
    }
  }

  /**
   *
   * Start a thread executing runObj.run().
   *
   * @param t The thread to start.
   * @param runObj The object whose run() method the thread is to run.
   */
  public static void fork(NachosThread t, Runnable runObj) {
    Debug.print('t', "Forking thread: " + t.getName() + "\n");
    Debug.ASSERT((t.getStatus() == NachosThread.JUST_CREATED), 
		 "Attempt to fork a thread that's already been forked");
    t.start(runObj);

    int oldLevel = Interrupt.setLevel(Interrupt.IntOff);
    readyToRun(t);		// ReadyToRun assumes that interrupts 
				// are disabled!
    Interrupt.setLevel(oldLevel);
  }    

  /**
   * Relinquish the CPU if any other thread is ready to run.
   * If so, put the thread on the end of the ready list, so that
   * it will eventually be re-scheduled.
   *
   * NOTE: returns immediately if no other thread on the ready queue.
   * Otherwise returns when the thread eventually works its way
   * to the front of the ready list and gets re-scheduled.
   *
   * NOTE: we disable interrupts, so that looking at the thread
   * on the front of the ready list, and switching to it, can be done
   * atomically.  On return, we re-set the interrupt level to its
   * original state, in case we are called with interrupts disabled. 
   *
   * Similar to sleep(), but a little different.
   */
  public static void yield () {
    NachosThread nextThread;
    int oldLevel = Interrupt.setLevel(Interrupt.IntOff);
    Debug.ASSERT(currentThread != null);
    
    Debug.println('t', "Yielding thread: " + currentThread.getName());
    
    nextThread = Scheduler.findNextToRun();
    if (nextThread != null) {
	readyToRun(currentThread);
	run(nextThread);
    }
    Interrupt.setLevel(oldLevel);
  }

  /**
   * Relinquish the CPU, because the current thread is blocked
   * waiting on a synchronization variable (Semaphore, Lock, or Condition).
   * Eventually, some thread will wake this thread up, and put it
   * back on the ready queue, so that it can be re-scheduled.
   *
   * NOTE: if there are no threads on the ready queue, that means
   * we have no thread to run.  "Interrupt.idle" is called
   * to signify that we should idle the CPU until the next I/O interrupt
   * occurs (the only thing that could cause a thread to become
   * ready to run).
   *
   * NOTE: we assume interrupts are already disabled, because it
   * is called from the synchronization routines which must
   * disable interrupts for atomicity.   We need interrupts off 
   * so that there can't be a time slice between pulling the first thread
   * off the ready list, and switching to it.
   */
  public static void sleep () {
    NachosThread thisThread;
    NachosThread nextThread;
    
    Debug.ASSERT(Interrupt.getLevel() == Interrupt.IntOff);
    
    Debug.println('t', "Sleeping thread: " + currentThread.getName());

    currentThread.setStatus(NachosThread.BLOCKED);
    thisThread = currentThread;
    currentThread = null;
    while ((nextThread = findNextToRun()) == null)
      Interrupt.idle();	// no one to run, wait for an interrupt

    currentThread = thisThread;
    run(nextThread); // returns when we've been signalled
  }

  /**
   * Called by a thread to terminate itself.
   * A thread can't completely destroy itself, because it needs some
   * resources (e.g. a stack) as long as it is running.  So it is the
   * responsibility of the next thread to run to finish the job.
   */
  public static void finish() {
    Interrupt.setLevel(Interrupt.IntOff);		
    Debug.ASSERT(currentThread != null);

    Debug.print('t', "Finishing thread: " + currentThread.getName() +"\n");

    threadToBeDestroyed = currentThread;
    
    sleep();				
    // not reached
  }

  /**
   * Called to turn on or off a timer that forces a context switch
   * at random intervals.
   *
   * @param on True if the timer is to be turned on.
   */
  public static void setRandomYield(boolean on) {
      if(timer != null) {
	  timer.cancel();
	  timer = null;
      }
      if(on)
	timer = new Timer(new TimerInterruptHandler(), true);
  }

  /**
   * 	Interrupt handler for the timer device.  The timer device is
   *	set up to interrupt the CPU periodically (once every TimerTicks).
   *	The serviceDevice() method is called with interrupts disabled each
   *	time there is a timer interrupt.
   */
  public static class TimerInterruptHandler extends InterruptHandler {
      public void serviceDevice() {
	  // Note that instead of calling yield() directly (which would
	  // suspend the interrupt handler, not the interrupted thread
	  // which is what we wanted to context switch), we set a flag
	  // so that once the interrupt handler is done, it will appear as 
	  // if the interrupted thread called yield at the point it is 
	  // was interrupted.
	  super.yieldOnReturn();
      }
  }
}
