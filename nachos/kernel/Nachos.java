// Nachos.java
//	Bootstrap code to initialize the operating system kernel.
//
// Usage: 
// run <options> ("run" is a simple script; edit it for your platform), or:
// on Windows: java -classpath 'machine.jar;.' nachos.kernel.Nachos <options>
// on Unix:    java -classpath 'machine.jar:.' nachos.kernel.Nachos <options>
// where <options> are:
//
//  GENERAL
//    -d <flags> causes debugging messages to be printed (see Debug.java)
//    -rs <seed> causes yield to occur at pseudo-random points during
//         execution.  <seed> is the seed to a pseudo-random number generator.
//         Re-execution with the same seed should produce the same results.
//    -tl <time limit> halt the machine if totalTicks exceeds <time limit>
//    -z prints the copyright message
//
//  USER_PROGRAM (set USER_PROGRAM=true below before using these options!)
//    -s causes user programs to be executed in single-step mode
//    -x <nachos file> runs a user program
//    -c <consoleIn> <consoleOut> tests the console
//         if omitted, consoleIn and consoleOut default to stdin and stdout
//
//  FILESYS (set FILESYS=true below before using these options!)
//    -f causes the physical disk to be formatted
//    -cp <unix file> <nachos file> copies a file from UNIX to Nachos
//    -p <nachos file> prints a Nachos file to stdout
//    -r <nachos file> removes a Nachos file from the file system
//    -l lists the contents of the Nachos directory
//    -D prints the contents of the entire file system 
//    -t tests the performance of the Nachos file system
//
//  NETWORK (set NETWORK=true below before using these options!)
//    -n <reliability>  sets the network reliability -- currently unsupported
//    -m <machine id> sets this machine's host id; id's start at 0.
//    -nt <numMach> tests the network, assuming <numMach> machines
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
import nachos.kernel.devices.ConsoleDriver;
import nachos.kernel.devices.DiskDriver;
import nachos.kernel.devices.NetworkDriver;
import nachos.kernel.devices.SerialDriver;
import nachos.kernel.devices.test.ConsoleTest;
import nachos.kernel.devices.test.NetworkTest;
import nachos.kernel.devices.test.SerialTest;
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

  /** Should we use the GUI console, or the stream-based version? */
  private static final boolean GUI_CONSOLE = false;

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

  /** Are we going to be using the network? */
  private static final boolean NETWORK = false;

  /** Are we going to be using the threads system? */
  private static final boolean THREADS = true;

  /** Are we going to be using serial ports? */
  private static final boolean SERIAL = false;

  /** Array containing the command-line arguments passed to main(). */
  private static String args[];

  /** Access to the Nachos file system. */
  public static FileSystem fileSystem;

  /** Access to the Nachos console. */
  public static ConsoleDriver consoleDriver;

  /** Access to the Nachos disk driver. */
  public static DiskDriver diskDriver;

  /** Access to the Nachos network. */
  public static NetworkDriver networkDriver;

  /** Access to serial ports. */
  public static SerialDriver serialDriver;

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
     * an exception handler.
     */
      if(USER_PROGRAM)
	Machine.setHandler(new ExceptionHandler());

    /*
     * Initialize the console driver.
     */
    consoleDriver = new ConsoleDriver(GUI_CONSOLE);

    /*
     * If we are going to be using the disk, then start the disk driver.
     */
    if(DISK)
	diskDriver = new DiskDriver("DISK");

    /*
     * If we are going to be using the network, then start the network driver.
     */
    if(NETWORK) {
      networkDriver = new NetworkDriver(args);
      NetworkTest.start(args);
    }

    /**
     * If we are going to be using serial ports, then start the driver.
     */
    if(SERIAL) {
	serialDriver = new SerialDriver(args);
	SerialTest.start(args);
    }

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
    args = clArgs;
    Debug.init(args);
    Debug.println('t', "Entering main");

    if (java.util.Arrays.asList(args).contains("-z"))
	System.out.println(copyright);

    /*
     * Here we do just that initialization that has to be done in order
     * to start the thread scheduler.  The rest gets done once the first
     * Nachos thread is started.
     */
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

