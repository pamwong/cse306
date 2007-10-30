// ConsoleDriver.java
//
// Copyright (c) 2003 State University of New York at Stony Brook.
// All rights reserved.  See the COPYRIGHT file for copyright notice and 
// limitation of liability and disclaimer of warranty provisions.

package nachos.kernel.devices;

import nachos.machine.Console;
import nachos.kernel.threads.Lock;
import nachos.kernel.threads.Scheduler;

/**
 * This class provides for the initialization of the NACHOS console,
 * and gives NACHOS user programs a capability of outputting to the console.
 * Programmed I/O (PIO) mode is used to access the console device.
 * Students will rewrite this into a full-fledged interrupt-driven driver
 * that provides both input and output capabilities.
 */
public class ConsoleDriver {
    /** Raw console device. */
    private Console console;

    /** Lock used to ensure at most one thread trying to access at a time. */
    private Lock lock;

    /**
     * Initialize the driver and the underlying physical device.
     *
     * @param useGUI If true, use the GUI console, otherwise use the stream
     * console.
     */
    public ConsoleDriver(boolean useGUI) {
	lock = new Lock("console driver lock");
	if(useGUI) {
	    console = Console.guiConsole();
	} else {
	    console = Console.streamConsole(null, null);
	}
    }

    /**
     * Wait for a character to be available from the console and then
     * return the character.
     */
    public char getChar() {
	lock.acquire();
	while(!console.isInputAvail())
	    Scheduler.yield();
	char ch = console.getChar();
	lock.release();
	return ch;
    }

    /**
     * Print a single character on the console.  If the console is already
     * busy outputting a character, then wait for it to finish before
     * attempting to output the new character.  A lock is employed to ensure
     * that at most one thread at a time will attempt to print.
     *
     * @param ch The character to be printed.
     */
    public void putChar(char ch) {
	lock.acquire();
	while(console.isOutputBusy())
	    Scheduler.yield();
	console.putChar(ch);
	lock.release();
    }

    /**
     * Stop the console device.
     */
    public void stop() {
	lock.acquire();
	console.stop();
	lock.release();
    }
}
