// AlarmTest.java
//	Demonstrates the use of timer objects.
//
// Copyright (c) 1992-1993 The Regents of the University of California.
// Copyright (c) 1998 Rice University.
// Copyright (c) 2003 State University of New York at Stony Brook.
// All rights reserved.  See the COPYRIGHT file for copyright notice and
// limitation of liability and disclaimer of warranty provisions.

package nachos.kernel.threads.test;

import nachos.Debug;
import nachos.machine.Timer;
import nachos.kernel.threads.Scheduler;
import nachos.kernel.devices.InterruptHandler;

/**
 * This class demonstrates the use of timers.
 * Some timers are created, then they are cancelled.
 * If there is something else using the CPU, then the timers might
 * go off before being cancelled.
 */
public class AlarmTest extends InterruptHandler {
  /** Timer instances created for the test. */
  private static Timer t1,t2,t3;
    
  /** Integer that identifies which instance this is. */
  private int which;

  /**
   * Initialize a handler for a timer instance.
   *
   * @param w Integer that identifies the instance.
   */
  public AlarmTest(int w) {
    which = w;
  }

  /**
   * Entry point for the test.
   *
   * @param args Command-line arguments -- currently ignored.
   */
  public static void start(String[] args) {
    Debug.println('t', "Entering AlarmTest");

    t1 = new Timer("AlarmTest timer 1", new AlarmTest(1), true);
    t2 = new Timer("AlarmTest timer 2", new AlarmTest(2), true);
    t3 = new Timer("AlarmTest timer 3", new AlarmTest(3), true);

    t1.cancel();
    //t2.cancel();
    //t3.cancel();
  }

  /**       
   * Interrupt service routine called when the timer expires.
   */
  public void serviceDevice() {
    System.out.println("Alarm " + which);
  }
}
