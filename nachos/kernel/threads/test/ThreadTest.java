// ThreadTest.java
//	Simple test class for the threads assignment.
//
//	Create two threads, and have them context switch
//	back and forth between themselves by calling yield(), 
//	to illustrate the thread system.
//
// Copyright (c) 1992-1993 The Regents of the University of California.
// Copyright (c) 1998 Rice University.
// Copyright (c) 2003 State University of New York at Stony Brook.
// All rights reserved.  See the COPYRIGHT file for copyright notice and
// limitation of liability and disclaimer of warranty provisions.

package nachos.kernel.threads.test;

import nachos.Debug;
import nachos.machine.NachosThread;
import nachos.kernel.threads.Scheduler;

/**
 * Set up a ping-pong between two threads, by forking two threads
 * to execute SimpleRunnable objects.
 */
public class ThreadTest implements Runnable {
  /**
   * Static variables to hold the instances we create and keep them
   * from being garbage collected.
   */
  private static ThreadTest t1, t2;

  /** Integer identifier that indicates which thread we are. */
  private int which;

  /**
   * Entry point for the test.
   *
   * @param args Command-line arguments -- currently ignored.
   */
  public static void start(String[] args) {
    Debug.println('t', "Entering ThreadTest");
    t1 = new ThreadTest(1);
    t2 = new ThreadTest(2);
  }

  /**
   * Initialize an instance of ThreadTest and start a new thread running
   * on it.
   *
   * @param w  An integer identifying this instance of ThreadTest.
   */
  public ThreadTest(int w) {
    which = w;
    NachosThread t = new NachosThread("Test thread " + w, this);
    Scheduler.readyToRun(t);
  }

  /**
   * Loop 5 times, yielding the CPU to another ready thread 
   * each iteration.
   */
  public void run() {
    for (int num = 0; num < 5; num++) {
	System.out.print("*** thread " + which + " looped " + num + " times\n");
	Scheduler.yield();
    }
    Scheduler.finish();
  }
}
