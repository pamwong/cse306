// Copyright (c) 2003 State University of New York at Stony Brook.
// All rights reserved.  See the COPYRIGHT file for copyright notice and 
// limitation of liability and disclaimer of warranty provisions.

package nachos.kernel.devices;

import nachos.Debug;
import nachos.machine.Interrupt;
import nachos.kernel.threads.Scheduler;

/**
 * This class controls entry to and exit from interrupt service routines.
 * It is an abstract class which should be extended to create interrupt
 * handlers for specific devices.  Each subclass must provide an
 * implemention for the serviceDevice() method.
 * The main reason this class is here is to implement the yieldOnReturn()
 * method, which provides an interrupt service routine (such as the one
 * for the timer) the ability to request that rescheduling of the CPU
 * take place at the completion of interrupt service.
 */
public abstract class InterruptHandler
    implements nachos.machine.InterruptHandler {

  /** Are we currently running an interrupt handler? */
  private static boolean inHandler;

  /** Should the CPU be rescheduled on return from the current handler? */
  private static boolean yieldOnReturn;

  static {
      inHandler = false;
      yieldOnReturn = false;
  }

  /**
   * Handler called by the machine when any interrupt occurs.
   */
  final public void handleInterrupt() {
      inHandler = true;
      serviceDevice();
      inHandler = false;

      if (yieldOnReturn) {	// if a device handler asked 
				// for a context switch, ok to do it now
	  yieldOnReturn = false;
	  if(Scheduler.currentThread() != null)
	      Scheduler.yield();
      }
  }

  /**
   * Subclasses must implement this method to provide device-specific
   * interrupt service.
   */
  public abstract void serviceDevice();

  /**
   * Called from within an interrupt handler, to cause a context switch
   * (for example, on a time slice) in the interrupted thread,
   * when the handler returns.
   *
   * We can't do the context switch right here, because that would switch
   * out the interrupt handler, and we want to switch out the 
   * interrupted thread.  Instead, we set a flag and take care of the
   * context switch when the current handler returns.
   */
  public static void yieldOnReturn() { 
    Debug.ASSERT(inHandler == true);
    yieldOnReturn = true; 
  }
}
