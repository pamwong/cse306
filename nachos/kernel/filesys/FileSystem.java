// FileSystem.java
//	Interface to a Nachos file system.
//
// Copyright (c) 1992-1993 The Regents of the University of California.
// Copyright (c) 1998 Rice University.
// Copyright (c) 2003 State University of New York at Stony Brook.
// All rights reserved.  See the COPYRIGHT file for copyright notice and
// limitation of liability and disclaimer of warranty provisions.

package nachos.kernel.filesys;

/**
 * This abstract class defines the interface to a Nachos file system.
 *
 * A file system is a set of files stored on disk, organized
 * into directories.  Operations on the file system have to
 * do with "naming" -- creating, opening, and deleting files,
 * given a textual file name.  Operations on an individual
 * "open file" (read, write, close) are to be found in the OpenFile
 * class (Openfile.java).
 *
 * We define two separate implementations of the file system:
 * a "real" file system, built on top of a disk simulator,
 * and a "stub" file system, which just re-defines the Nachos file system 
 * operations as operations on the native file system on the machine
 * running the Nachos simulation.
 */
public abstract class FileSystem {
  /**
   * Create a new file with a specified name and size.
   *
   * @param name The name of the file.
   * @param initialSize The size of the file.
   * @return true if the operation was successful, otherwise false.
   */
  public abstract boolean create(String name, long initialSize);

  /**
   * Open the file with the specified name and return an OpenFile
   * object that provides access to the file contents.
   *
   * @param name The name of the file.
   * @return An OpenFile object that provides access to the file contents,
   * if the file was successfully opened, otherwise null.
   */
  public abstract OpenFile open(String name);

  /**
   * Remove the file with the specified name.
   *
   * @param name The name of the file.
   * @return true if the operation was successful, otherwise false.
   */
  public abstract boolean remove(String name);

  /**
   * Protected constructor to force creation of a filesystem using
   * the init() factory method.
   */
  protected FileSystem() { }

  /**
   * Factory method to create the proper type of filesystem and
   * hide the type actually being used.
   *
   * @param args Command-line arguments, used to determine whether the
   * filesystem should be "formatted" after being initialized.
   * @param stub True if we should be using the stub filesystem,
   * rather than the real filesystem.
   */
  public static FileSystem init(String[] args, boolean stub) {
      boolean format = false;
      for (int i=0; i<args.length; i++) {
	  if (args[i].equals("-f"))
	      format = true;
      }
      if(stub)
	  return((FileSystem)new FileSystemStub());
      else
	  return((FileSystem)new FileSystemReal(format));
  }

  /**
   * List contents of the filesystem directory (for debugging).
   * This is only implemented by the real filesystem.
   */
  public void list() { }

  /**
   * Print contents of the entire filesystem (for debugging).
   * This is only implemented by the real filesystem.
   */
  public void print() { }

}
