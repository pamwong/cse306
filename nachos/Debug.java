// Debug.java
//
//	The debugging methods allow the user to turn on selected
//	debugging messages, controllable from the command line arguments
//	passed to Nachos (-d).  You are encouraged to add your own
//	debugging flags.  The pre-defined debugging flags are:
//
//	'+' -- turn on all debug messages
//   	't' -- thread system
//   	's' -- semaphores, locks, and conditions
//   	'i' -- interrupt emulation
//   	'm' -- machine emulation (USER_PROGRAM)
//   	'd' -- disk emulation (FILESYS)
//   	'c' -- console emulation 
//   	'f' -- file system (FILESYS)
//   	'a' -- address spaces (USER_PROGRAM)
//   	'n' -- network emulation (NETWORK)
//
// Copyright (c) 1992-1993 The Regents of the University of California.
// Copyright (c) 1998 Rice University.
// Copyright (c) 2003 State University of New York at Stony Brook.
// All rights reserved.  See the COPYRIGHT file for copyright notice and
// limitation of liability and disclaimer of warranty provisions.

package nachos;

/**
 * This class contains debugging methods for generating user-selectable
 * debugging printout and for placing assertions in the code that
 * terminate execution and print debugging messages when they fail.
 * The user can select which debugging printout will be generated
 * by the command line argument (-d) passed to Nachos.  See the comments
 * in this class for a list of the pre-defined debugging flags.
 * You are also encouraged to add your own debugging flags.
 */
public class Debug {

  /** List of debugging flags for which printing is enabled. */
  private static String enableFlags = "+";

  /**
   * Exception used to terminate Nachos when an assertion fails.
   */
  public static class AssertException extends RuntimeException {

      /** An AssertException with a generic message. */
      public AssertException() {
	  super("Assert failed");
      }

      /**
       * An AssertException with a user-specified message.
       *
       * @param msg The user-specified message.
       */
      public AssertException(String msg) {
	  super(msg);
      }
  }

  /**
   * Equivalent of the C++ Nachos ASSERT macro.  Comes to a screeching
   * halt when the condition is false.  (Nifty thing about the JVM is
   * that it'll print out a full stack trace, automatically.  All we
   * have to do is throw an exception.)
   *
   * @param condition If false, the assertion fails and an exception
   * is thrown.
   */
  public static void ASSERT(boolean condition) {
    if (!condition)
      throw new AssertException();
  }
    
  /**
   * An optional form of ASSERT which allows us to print our own
   * message.
   *
   * @param condition If false, the assertion fails and an exception
   * is thrown.
   * @param msg The message to be printed in case of failure.
   */
  public static void ASSERT(boolean condition, String msg) {
    if (!condition)
      throw new AssertException(msg);
  }

  /**
   * Process command-line arguments and initialize the list of
   * debugging flags for which printing is enabled.
   *
   * @param args  Command-line arguments.
   */
  public static void init(String[] args) {
    enableFlags = "";
    for (int i=0; i<args.length; i++) {
      if (args[i].equals("-d"))
	if (i < args.length-1) enableFlags = args[++i];
	else enableFlags = "+";
    }
  }

  /**
   * Query whether printing is enabled for a specified debugging flag.
   *
   * @param flag The flag to be queried.
   * @return true if the flag is enabled, otherwise false.
   */
  public static boolean isEnabled(char flag) {
    return (flag == '+' || enableFlags.indexOf(flag) >= 0 || 
	    enableFlags.indexOf('+') >= 0);
  }

  /**
   * Print a debugging message if a specified flag is enabled.
   *
   * @param flag The flag.
   * @param text The message.
   */
  public static void print(char flag, String text) {
    if (isEnabled(flag))
      System.out.print(text);
  }

  /**
   * Print a debugging message, followed by a newline,
   * if a specified flag is enabled.
   *
   * @param flag The flag.
   * @param text The message.
   */
  public static void println(char flag, String text) {
    if (isEnabled(flag))
      System.out.println(text);
  }

  /**
   * Convenience version of printf for printing one object.
   */
  public static void printf(char flag, String format, Object o1) {
    Object objs[] = new Object[1];

    objs[0] = o1;
    printf(flag, format, objs);
  }

  /**
   * Convenience version of printf for printing two objects.
   */
  public static void printf(char flag, String format, Object o1, Object o2) {
    Object objs[] = new Object[2];

    objs[0] = o1;
    objs[1] = o2;
    printf(flag, format, objs);
  }

  /**
   * Convenience version of printf for printing three objects.
   */
  public static void printf(char flag, String format, 
			    Object o1, Object o2, Object o3) {
    Object objs[] = new Object[3];

    objs[0] = o1;
    objs[1] = o2;
    objs[2] = o3;
    printf(flag, format, objs);
  }

  /**
   * Convenience version of printf for printing four objects.
   */
  public static void printf(char flag, String format, 
			    Object o1, Object o2, Object o3, Object o4) {
    Object objs[] = new Object[4];

    objs[0] = o1;
    objs[1] = o2;
    objs[2] = o3;
    objs[3] = o4;
    printf(flag, format, objs);
  }

  /**
   * A C-style printing function that uses a format string to control
   * the printing of an array of objects.
   *
   * @param flag If true, then do the printing, otherwise don't.
   * @param format C-style format string.
   * @param o Array of objects to be printed.
   */
  public static void printf(char flag, String format, Object o[]) {
    boolean pcFlag = false;
    boolean lFlag = false;
    int nextObj = 0;
    long l;
    char ch;

    if (!isEnabled(flag)) return;

    for (int i = 0; i < format.length(); i++) {
      ch = format.charAt(i);
      if (ch == '%') {
	pcFlag = true;
	continue;
      }
      if (!pcFlag) {
	System.out.print(ch);
        continue;
      }

      switch (ch) {

      case 'l':
	lFlag = true;
	continue;

      case 'c':
	byte tmp[] = new byte[1];
	tmp[0] = (byte)((Number) o[nextObj++]).intValue();
	System.out.print(new String(tmp));
	pcFlag = lFlag = false;
	continue;

      case 's':
	System.out.print((String)o[nextObj++]);
	pcFlag = lFlag = false;
	continue;

      case 'd':
	if (lFlag) l = ((Number) o[nextObj++]).longValue();
	else       l = ((Number) o[nextObj++]).longValue();
	if (l < 0) {
	  System.out.print('-');
	  l = -l;
	}
	kprintn(l, 10);
	pcFlag = lFlag = false;
	continue;

      case 'o':
	if (lFlag) l = ((Number) o[nextObj++]).longValue();
	else       l = ((Number) o[nextObj++]).longValue();
	kprintn(l, 8);
	pcFlag = lFlag = false;
	continue;

      case 'u':
	if (lFlag) l = ((Number) o[nextObj++]).longValue();
	else       l = ((Number) o[nextObj++]).longValue();
	kprintn(l, 10);
	pcFlag = lFlag = false;
	continue;

      case 'x':
	if (lFlag) l = ((Number) o[nextObj++]).longValue();
	else       l = ((Number) o[nextObj++]).longValue();
	kprintn(l, 16);
	pcFlag = lFlag = false;
	continue;

      default:
	System.out.print('%');
	if (lFlag) System.out.print('l');
	System.out.print(ch);
	pcFlag = lFlag = false;
      }
    }
  }

  /**
   * Print an integer value in a specified base.
   *
   * @param l The value to print.
   * @param base The base in which to print the value.
   */
  private static void kprintn(long l, int base) {
      if(base == 8)
	  System.out.print(Long.toOctalString(l));
      else if(base == 16)
	  System.out.print(Long.toHexString(l));
      else
	  System.out.print(Long.toString(l, base));
  }

}

