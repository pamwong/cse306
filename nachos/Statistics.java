// Statistics.java
//	Class for managing statistics about Nachos performance.
//
// DO NOT CHANGE -- these stats are maintained by the machine emulation.
//
// Copyright (c) 1992-1993 The Regents of the University of California.
// Copyright (c) 1998 Rice University.
// Copyright (c) 2003 State University of New York at Stony Brook.
// All rights reserved.  See the COPYRIGHT file for copyright notice and
// limitation of liability and disclaimer of warranty provisions.


package nachos;

/**
 * This class defines the statistics that are to be kept
 * about Nachos behavior -- how much time (ticks) elapsed, how
 * many user instructions executed, etc.
 *
 * The fields in this class are public to make it easier to update.
 */
public class Statistics {

  // Constants used to reflect the relative time an operation would
  // take in a real system.  A "tick" is a just a unit of time -- if you 
  // like, a microsecond.
  //
  // Since Nachos kernel code is directly executed, and the time spent
  // in the kernel measured by the number of calls to enable interrupts,
  // these time constants are none too exact.
  
  /** Time to send or receive one packet on the network. */
  public static final int NetworkTime = 100;

  // instance variables

  /** Total time running Nachos. */
  public int totalTicks;

  /** Time spent idle (no threads to run). */
  public int idleTicks;

  /** Time spent executing system code. */
  public int systemTicks;

  /**
   * Time spent executing user code
   * (this is also equal to # of
   * user instructions executed).
   */
  public int userTicks;

  /** Number of disk read requests. */
  public int numDiskReads;

  /** Number of disk write requests. */
  public int numDiskWrites;

  /** Number of chars read from the keyboard. */
  public int numConsoleCharsRead;

  /** Number of chars written to the display. */
  public int numConsoleCharsWritten;

  /** Number of virtual memory page faults. */
  public int numPageFaults;

  /** Number of packets sent over the network. */
  public int numPacketsSent;

  /** Number of packets received over the network. */
  public int numPacketsRecvd;

  /**
   * Initialize performance metrics to zero, at system startup.
   */
  public Statistics() {
    totalTicks = idleTicks = systemTicks = userTicks = 0;
    numDiskReads = numDiskWrites = 0;
    numConsoleCharsRead = numConsoleCharsWritten = 0;
    numPageFaults = numPacketsSent = numPacketsRecvd = 0;
  }

  /**
   * 	Print performance metrics, when we've finished everything
   *	at system shutdown.
   */
  public void print() {

    Debug.printf('+', "Ticks: total %d, idle %d, system %d, user %d\n", 
		 new Integer(totalTicks), new Integer(idleTicks), 
		 new Integer(systemTicks), new Integer(userTicks));
    Debug.printf('+', "Disk I/O: reads %d, writes %d\n", 
		 new Integer(numDiskReads), new Integer(numDiskWrites));
    Debug.printf('+', "Console I/O: reads %d, writes %d\n", 
		 new Integer(numConsoleCharsRead), 
		 new Integer(numConsoleCharsWritten));
    Debug.printf('+', "Paging: faults %d\n", new Integer(numPageFaults));
    Debug.printf('+', "Network I/O: packets received %d, sent %d\n", 
		 new Integer(numPacketsRecvd), new Integer(numPacketsSent));

  }

}
