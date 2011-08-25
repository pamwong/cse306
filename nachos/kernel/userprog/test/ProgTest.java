// ProgTest.java
//	Test class for demonstrating that Nachos can load
//	a user program and execute it.  
//
// Copyright (c) 1992-1993 The Regents of the University of California.
// Copyright (c) 1998 Rice University.
// Copyright (c) 2003 State University of New York at Stony Brook.
// All rights reserved.  See the COPYRIGHT file for copyright notice and
// limitation of liability and disclaimer of warranty provisions.

package nachos.kernel.userprog.test;

import nachos.Debug;
import nachos.machine.Machine;
import nachos.kernel.Nachos;
import nachos.kernel.threads.Scheduler;
import nachos.kernel.userprog.AddrSpace;
import nachos.kernel.userprog.UserThread;
import nachos.kernel.filesys.OpenFile;

/**
 * This is a test class for demonstrating that Nachos can load a user
 * program and execute it.
 */
public class ProgTest implements Runnable {

  /** The name of the program to execute. */
  private String execName;

  /**
   * Start the test by creating a new address space and user thread,
   * then arranging for the new thread to begin executing the run() method
   * of this class.
   *
   * @param filename The name of the program to execute.
   */
  public ProgTest(String filename) {
    Debug.println('a', "starting ProgTest");

    execName = filename;
    AddrSpace space = new AddrSpace();
    UserThread t = new UserThread("ProgTest thread", this, space);
    Scheduler.readyToRun(t);
  }

  /**
   * Entry point for the thread created to run the user program.
   * The specified executable file is used to initialize the address
   * space for the current thread.  Once this has been done,
   * Machine.run() is called to transfer control to user mode.
   */
  public void run() {
    OpenFile executable;
    
    if((executable = Nachos.fileSystem.open(execName)) == null) {
	Debug.println('+', "Unable to open executable file: " + execName);
	Scheduler.finish();
	return;
    }

    AddrSpace space = ((UserThread)Scheduler.currentThread()).space;
    if(space.exec(executable) == -1) {
	Debug.println('+', "Unable to read executable file: " + execName);
	Scheduler.finish();
	return;
    }

    space.initRegisters();		// set the initial register values
    space.restoreState();		// load page table register

    Machine.run();			// jump to the user progam
    Debug.ASSERT(false);		// machine->Run never returns;
					// the address space exits
					// by doing the syscall "exit"
  }

  /**
   * Entry point for the test.  Command line arguments are checked for
   * the name of the program to execute, then the test is started by
   * creating a new ProgTest object.
   *
   * @param args Command line arguments that name the program to be
   * executed.
   */
  public static void start(String[] args) {
    for (int i=0; i<args.length; i++) {
	if (args[i].equals("-x")) {           // run a user program
	  Debug.ASSERT((i<args.length-1),
		       "usage: -x <filename>");
	  ProgTest testObj = new ProgTest(args[++i]);
	}
    }
  }
}
