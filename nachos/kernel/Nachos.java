// Nachos.java
//	Bootstrap code to initialize the operating system kernel.
//
// Usage: nachos -d <debugflags> -rs <random seed #>
//		-s -x <nachos file> -c <consoleIn> <consoleOut>
//		-f -cp <unix file> <nachos file>
//		-p <nachos file> -r <nachos file> -l -D -t
//              -n <network reliability> -m <machine id>
//              -o <other machine id>
//              -z
//
//    -d causes certain debugging messages to be printed (cf. utility.h)
//    -rs causes Yield to occur at random (but repeatable) spots
//    -z prints the copyright message
//
//  USER_PROGRAM
//    -s causes user programs to be executed in single-step mode
//    -x runs a user program
//    -c tests the console
//
//  FILESYS
//    -f causes the physical disk to be formatted
//    -cp copies a file from UNIX to Nachos
//    -p prints a Nachos file to stdout
//    -r removes a Nachos file from the file system
//    -l lists the contents of the Nachos directory
//    -D prints the contents of the entire file system 
//    -t tests the performance of the Nachos file system
//
//  NETWORK
//    -n sets the network reliability
//    -m sets this machine's host id (needed for the network)
//    -o runs a simple test of the Nachos network software
//
// Copyright (c) 1992-1993 The Regents of the University of California.
// Copyright (c) 1998 Rice University.
// Copyright (c) 2003 State University of New York at Stony Brook.
// All rights reserved.  See the COPYRIGHT file for copyright notice and
// limitation of liability and disclaimer of warranty provisions.

package nachos.kernel;

import nachos.Debug;
import nachos.Statistics;
import nachos.machine.Machine;
import nachos.machine.NachosThread;
import nachos.kernel.threads.Scheduler;
import nachos.kernel.threads.test.ThreadTest;
import nachos.kernel.threads.test.AlarmTest;
import nachos.kernel.filesys.FileSystem;
import nachos.kernel.filesys.test.FileSystemTest;
import nachos.kernel.devices.DiskDriver;
import nachos.kernel.devices.test.ConsoleTest;
import nachos.kernel.userprog.test.ProgTest;
import nachos.kernel.userprog.ExceptionHandler;

/**
 * The Nachos main class.  Nachos is "booted up" when a Java thread calls the
 * main() method of this class.
 */
public class Nachos implements Runnable {

private static final String copyright = "Copyright (c) 1992-1993 The Regents of the University of California.  Copyright (c) 1998-1999 Rice University. Copyright (c) 2003 State University of New York at Stony Brook.  All rights reserved.";

  /*
   * Most of the following constants that control the Nachos configuration
   * have been inherited by tradition from the original version of Nachos.
   * It is not clear that they all do something useful in the current
   * version.
   */

  /** Are we going to be running user programs? */
  private static final boolean USER_PROGRAM = true;

  /**
   * Are we going to be using the disk?
   * NOTE: We need the disk if either we are using the "real" Nachos
   * filesystem, or else we are using the disk as backing store for
   * virtual memory.
   */
  private static final boolean DISK = false;

  /** Are we going to be using the filesystem? */
  private static final boolean FILESYS = true;

  /**
   * Should we use the stub filesystem, rather than the Nachos filesystem?
   * NOTE: if FILESYS is true and this is false, then make sure to set
   * DISK to true.
   */
  private static final boolean FILESYS_STUB = true;

  /** Are we going to be using the network (NOTE: doesn't exist yet). */
  private static final boolean NETWORK = false;

  /** Are we going to be using the threads system? */
  private static final boolean THREADS = true;

  /** References to the command-line arguments passed to main(). */
  private static String args[];

  /** Access to the Nachos file system. */
  public static FileSystem fileSystem;

  /** Access to the Nachos disk driver. */
  public static DiskDriver diskDriver;

  /**
   * 	Nachos initialization -- performed by first Nachos thread.
   *	Initialize various subsystems, depending on configuration.
   *	The command line arguments are passed to each of the subsystems
   *	so they can scan them for configuration options.
   *	Start test programs, if appropriate.
   *	Once this method is finished, the first thread terminates.
   *	Any activities that are to continue must have their own threads
   *	by that point.
   */
  public void run() {
    /*
     * If we are going to be running user programs, then register
     * register an exception handler.
     */
      if(USER_PROGRAM)
	Machine.setHandler(new ExceptionHandler());

    /*
     * If we are going to be using the disk, then start the disk driver.
     */
    if(DISK)
	diskDriver = new DiskDriver("DISK");

    /*
     * If we are going to be using the filesystem, then initialize it.
     */
    if(FILESYS) {
	Debug.ASSERT(FILESYS_STUB || DISK);
	fileSystem = FileSystem.init(args, FILESYS_STUB);
    }

    /*
     * Start test programs, where appropriate.
     */
    if (THREADS) {
      ThreadTest.start(args);
      //AlarmTest.start(args);
    }

    if (USER_PROGRAM) {
	ProgTest.start(args);
	ConsoleTest.start(args);
    }

    if(FILESYS && !FILESYS_STUB)
	FileSystemTest.start(args);

    /* Terminate the first thread. */
    Scheduler.finish();
  }

  /**
   * 	Bootstrap the operating system kernel.  
   *
   *	@param clArgs is the array of command line arguments.
   *	The various arguments are described in the comments in
   *    Nachos.java.  Most of the arguments are interpreted by
   *	the various subsystems, rather than here.
   */
  public static void main(String clArgs[]) {
    Debug.println('t', "Entering main");

    args = clArgs;
    for (int i=0; i<args.length; i++) {
      if (args[i].equals("-z"))
	System.out.println(copyright);
    }
    /*
     * Here we do just that initialization that has to be done in order
     * to start the thread scheduler.  The rest gets done once the first
     * Nachos thread is started.
     */
    Debug.init(args);
    Machine.init(args);
    Scheduler.init(args);
    /*
     * We are in the context of a Java thread, not a Nachos Thread.
     * All we can do is to create the first NachosThread and take it
     * from there.
     */
    NachosThread t = new NachosThread("FirstThread", new Nachos());
    Scheduler.readyToRun(t);
    /*
     * Our final responsibility is to start the Nachos scheduler.
     * This will cause the thread we just created to begin running at
     * the run() method of this class.
     */
    Scheduler.start();
  }
}

