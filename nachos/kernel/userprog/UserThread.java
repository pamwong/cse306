// UserThread.java
//	A UserThread is a NachosThread extended with the capability of
//	executing user code.
//
// Copyright (c) 1992-1993 The Regents of the University of California.
// Copyright (c) 1998 Rice University.
// Copyright (c) 2003 State University of New York at Stony Brook.
// All rights reserved.  See the COPYRIGHT file for copyright notice and
// limitation of liability and disclaimer of warranty provisions.

package nachos.kernel.userprog;

import nachos.machine.NachosThread;
import nachos.machine.Machine;

/**
 * A UserThread is a NachosThread extended with the capability of
 * executing user code.  It is kept separate from AddrSpace to provide
 * for the possibility of having multiple UserThreads running in a
 * single AddrSpace.
 */
public class UserThread extends NachosThread {

  /** The context in which this thread will execute. */
  private AddrSpace space = null;

  // A thread running a user program actually has *two* sets of 
  // CPU registers -- one for its state while executing user code,
  // and one for its state while executing kernel code.
  // The kernel registers are managed by the super class.
  // The user registers are managed here.

  /** User-level CPU register state. */
  private int userRegisters[] = new int[Machine.NumTotalRegs];

  /**
   * Initialize a new user thread.
   *
   * @param name  An arbitrary name, useful for debugging.
   * @param runObj Execution of the thread will begin with the run()
   * method of this object.
   * @param addrSpace  The context to be installed when this thread
   * is executing in user mode.
   */
  public UserThread(String name, Runnable runObj, AddrSpace addrSpace) {
      super(name, runObj);
      space = addrSpace;
  }

  /**
   * Get the address space associated with this thread.
   *
   * @return the address space associated with this thread.
   */
  public AddrSpace getSpace() {
      return(space);
  }

  /**
   * Save the CPU state of a user program on a context switch.
   */
  public void saveState() {
    // Save state associated with the address space.
    space.saveState();  

    // Save user-level CPU registers.
    for (int i = 0; i < Machine.NumTotalRegs; i++)
      userRegisters[i] = Machine.readRegister(i);

    // Save kernel-level CPU state.
    super.saveState();
  }

  /**
   * Restore the CPU state of a user program on a context switch.
   */
  public void restoreState() {
    // Restore the kernel-level CPU state.
    super.restoreState();

    // Restore the user-level CPU registers.
    for (int i = 0; i < Machine.NumTotalRegs; i++)
      Machine.writeRegister(i, userRegisters[i]);

    // Restore state associated with the address space.
    space.restoreState();
  }
}
