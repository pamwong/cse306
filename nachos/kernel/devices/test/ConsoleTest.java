// ConsoleTest.java
//
//	Class for testing the Console hardware device.
//
// Copyright (c) 1998 Rice University.
// Copyright (c) 2003 State University of New York at Stony Brook.
// All rights reserved.  See the COPYRIGHT file for copyright notice and 
// limitation of liability and disclaimer of warranty provisions.

package nachos.kernel.devices.test;

import nachos.Debug;
import nachos.machine.Console;
import nachos.kernel.threads.Semaphore;
import nachos.kernel.devices.InterruptHandler;

/**
 * Class for testing the Console hardware device.
 */
public class ConsoleTest {

  // Objects needed for the console test.  Threads making
  // I/O requests wait on a Semaphore to delay until the I/O completes.

  /** Instance of the console device being tested. */
  private static Console console;

  /** Semaphore used to wait for available input. */
  private static Semaphore readAvail;

  /** Semaphore used to wait until printing is done. */
  private static Semaphore writeDone;

  /**
   * Test the console by echoing characters typed at the input onto
   * the output.  Stop when the user types a 'q'.
   *
   * @param in  If non-null, the name of a file from which to read
   * input.  If null, then read input from the keyboard.
   * @param out If non-null, the name of a file to which to direct
   * output.  If null, then direct output to the display.
   */
  public static void run(String in, String out) {
    char ch;

    Debug.println('c', "ConsoleTest: starting");

    readAvail = new Semaphore("read avail", 0);
    writeDone = new Semaphore("write done", 0);
    console = Console.streamConsole(in, out, new ConsHandler(readAvail), 
				    new ConsHandler(writeDone));
    //console = Console.guiConsole(new ConsHandler(readAvail),
    //			 new ConsHandler(writeDone));

    while (true) {
      readAvail.P();		// wait for character to arrive
      ch = console.getChar();

      console.putChar(ch);	// echo it!
      writeDone.P();            // wait for write to finish

      if (ch == 'q') {
	  Debug.println('c', "ConsoleTest: quitting");
	  return;    // if q, quit
      }
    }
  }

  /**
   * Entry point for the Console test.
   *
   * @param args  Command-line arguments.  If "-c" is given, then
   * the next two arguments are the names of files from which to read
   * the console input and to which to direct the console output.
   * If "-c" is not given, then input is from the keyboard and output
   * is to the display.
   */
  public static void start(String[] args) {
      for (int i=0; i<args.length; i++) {
	  if (args[i].equals("-c")) {          // test the console
	      if (i < args.length-2) {
		  ConsoleTest.run(args[i+1],args[i+2]);
		  i += 2;
	      } else {
		  ConsoleTest.run(null, null);
	      }
	      // once we start the console, then 
	      // Nachos will loop forever waiting 
	      // for console input
	  }
      }
  }

  /**
   * Console interrupt handler class.
   */
  static class ConsHandler extends InterruptHandler {
      /** Semaphore used to awaken requesting thread. */
      private Semaphore semaphore;

      /**
       * Initialize a handler with a given semaphore to use to
       * wake up the requesting thread when the I/O is complete.
       *
       * @param s The semaphore to use to wake up the requesting thread.
       */
      public ConsHandler(Semaphore s) {
	  semaphore = s;
      }

      /**
       * To service a Console interrupt, just wake up the thread that
       * requested the I/O.
       */
      public void serviceDevice() {
	  //Debug.println('c', "ConsoleTest: 10 " + semaphore.name);
	  semaphore.V();
      }
  }
}


