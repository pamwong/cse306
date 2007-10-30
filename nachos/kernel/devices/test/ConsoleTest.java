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
import nachos.kernel.Nachos;
import nachos.kernel.devices.ConsoleDriver;

/**
 * Class for testing the Console hardware device.
 */
public class ConsoleTest {

  /** Reference to the console device driver. */
  private static ConsoleDriver console;

  /**
   * Test the console by echoing characters typed at the input onto
   * the output.  Stop when the user types a 'q'.
   */
  public static void run() {
    char ch;

    Debug.println('c', "ConsoleTest: starting");

    console = Nachos.consoleDriver;
    while (true) {
      ch = console.getChar();
      console.putChar(ch);	// echo it!

      if (ch == 'q') {
	  Debug.println('c', "ConsoleTest: quitting");
	  console.stop();
	  return;    // if q, quit
      }
    }
  }

  /**
   * Entry point for the Console test.  If "-c" is included in the
   * command-line arguments, then run the console test; otherwise, do
   * nothing.
   *
   * The console test reads characters from the input and echoes them
   * onto the output.  The test ends when a 'q' is read.
   *
   * @param args  Command-line arguments.
   */
  public static void start(String[] args) {
      for (int i=0; i<args.length; i++) {
	  if (args[i].equals("-c")) {          // test the console
	      ConsoleTest.run();
	      // once we start the console, then 
	      // Nachos will loop forever waiting 
	      // for console input
	  }
      }
  }
}


