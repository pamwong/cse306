// AddrSpace.java
//	Class to manage address spaces (executing user programs).
//
//	In order to run a user program, you must:
//
//	1. link with the -N -T 0 option 
//	2. run coff2noff to convert the object file to Nachos format
//		(Nachos object code format is essentially just a simpler
//		version of the UNIX executable object code format)
//	3. load the NOFF file into the Nachos file system
//		(if you haven't implemented the file system yet, you
//		don't need to do this last step)
//
// Copyright (c) 1992-1993 The Regents of the University of California.
// Copyright (c) 1998 Rice University.
// Copyright (c) 2003 State University of New York at Stony Brook.
// All rights reserved.  See the COPYRIGHT file for copyright notice and
// limitation of liability and disclaimer of warranty provisions.

package nachos.kernel.userprog;

import nachos.Debug;
import nachos.machine.SegmentDescriptor;
import nachos.machine.Machine;
import nachos.kernel.filesys.OpenFile;

/**
 * This class manages "address spaces", which are the contexts in which
 * user programs execute.  For now, an address space contains a
 * "segment descriptor", which describes the the virtual-to-physical
 * address mapping that is to be used when the user program is executing.
 * As you implement more of Nachos, it will probably be necessary to add
 * other fields to this class to keep track of things like open files,
 * network connections, etc., in use by a user program.
 *
 * NOTE: Most of what is in currently this class assumes that just one user
 * program at a time will be executing.  You will have to rewrite this
 * code so that it is suitable for multiprogramming.
 */
public class AddrSpace {

  /**
   * Segment descriptor mapping the virtual address space.
   */
  private SegmentDescriptor descriptor = new SegmentDescriptor();


  /** Default size of the user stack area -- increase this as necessary! */
  private static final int UserStackSize = 1024;

  /**
   * Create a new address space.
   */
  public AddrSpace() { }

  /**
   * Load the program from a file "executable", and set everything
   * up so that we can start executing user instructions.
   *
   * Assumes that the object code file is in NOFF format.
   *
   * First, set up the translation from program memory to physical 
   * memory.  For now, this is really simple (1:1), since we are
   * only uniprogramming.
   *
   * @param executable The file containing the object code to 
   * 	load into memory
   * @return -1 if an error occurs while reading the object file,
   *    otherwise 0.
   */
  public int exec(OpenFile executable) {
    NoffHeader noffH;
    long size;
    
    if((noffH = NoffHeader.readHeader(executable)) == null)
	return(-1);

    // First, how big is the address space?
    size = roundToPage(noffH.code.size)
	   + roundToPage(noffH.initData.size + noffH.uninitData.size)
	   + UserStackSize;   // we need to increase the size
     			      // to leave room for the stack

    Debug.ASSERT(size <= Machine.MemorySize,
		 "AddrSpace constructor: Not enough memory!");
    	                                        // check we're not trying
                                                // to run anything too big --
						// at least until we have
						// virtual memory


    Debug.println('a', "Initializing address space, size=" + size);

    // Next, set up the segment descriptor for the memory-management hardware.
    // We use segment 0 for code, data, and stack -- the entire address space.
    descriptor.base = 0;
    descriptor.size = size;
    descriptor.valid = true;

    // Zero out the entire address space, to zero the unitialized data 
    // segment and the stack segment.
    for(int i = 0; i < size; i++)
	Machine.mainMemory[i] = 0;

    // then, copy in the code and data segments into memory
    if (noffH.code.size > 0) {
      Debug.printf('a', "Initializing code segment, at 0x%x, size "
		   + noffH.code.size + "\n",
		   new Long(noffH.code.virtualAddr));

      executable.seek(noffH.code.inFileAddr);
      executable.read(Machine.mainMemory, (int)noffH.code.virtualAddr, 
		      (int)noffH.code.size);
    }

    if (noffH.initData.size > 0) {
      Debug.printf('a', "Initializing data segment, at 0x%x, size "
		   + noffH.initData.size + "\n",
		   new Long(noffH.initData.virtualAddr));

      executable.seek(noffH.initData.inFileAddr);
      executable.read(Machine.mainMemory, (int)noffH.initData.virtualAddr, 
		      (int)noffH.initData.size);
    }

    return(0);
  }

  /**
   * Initialize the user-level register set to values appropriate for
   * starting execution of a user program loaded in this address space.
   *
   * We write these directly into the "machine" registers, so
   * that we can immediately jump to user code.
   */
  public void initRegisters() {
    int i;
    
    for (i = 0; i < Machine.NumTotalRegs; i++)
      Machine.writeRegister(i, 0);

    // Initial program counter -- must be location of "Start"
    Machine.writeRegister(Machine.PCReg, 0);	

    // Need to also tell MIPS where next instruction is, because
    // of branch delay possibility
    Machine.writeRegister(Machine.NextPCReg, 4);

    // Set the stack register to the end of the segment,
    // but subtract off a bit, to accomodate compiler convention that
    // assumes space in the current frame to save four argument registers.
    int sp = (int)(descriptor.size - 16);
    Machine.writeRegister(Machine.StackReg, sp);
    Debug.printf('a', "Initializing stack register to 0x%x\n",
		 new Integer(sp));
  }

  /**
   * On a context switch, save any machine state, specific
   * to this address space, that needs saving.
   *
   * For now, nothing!
   */
  public void saveState() {}

  /**
   * On a context switch, restore any machine state specific
   * to this address space.
   *
   * For now, just set the hardware segment descriptors appropriately
   * for this address space.
   */
  public void restoreState() {
    SegmentDescriptor[] segmentTable = Machine.getSegmentTable();
    segmentTable[0] = descriptor;
    for(int i = 1; i < segmentTable.length; i++)
	segmentTable[i] = null;
  }

  /**
   * Utility method for rounding up to a multiple of Machine.PageSize;
   */
  private long roundToPage(long size) {
    return(Machine.PageSize * ((size+(Machine.PageSize-1))/Machine.PageSize));
  }
}
