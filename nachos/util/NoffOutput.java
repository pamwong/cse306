package nachos.util;

import java.io.*;

/**
 * Class for writing Nachos Object Format (NOFF) files.
 */

class NoffOutput {
    
    /*
     * Magic number denoting Nachos object code file.
     */
    private static final int NOFFMAGIC = 0xbadfad;

    /*
     * NOFF files have three segments: code, initialized data,
     * uninitialized data.
     */
    private static class Segment {
	int virtualAddr;	/* location of segment in virt addr space */
	int inFileAddr;		/* location of segment in this file */
	int size;		/* size of segment */
	byte[] contents;	/* contents of segment */

	Segment() {
	    size = 0;
	    contents = new byte[0];
	    virtualAddr = 0;
	    inFileAddr = 0;
	}
    }

    /*
     * Contents of the NOFF header.
     */
    private Segment code;	/* executable code segment */
    private Segment initData;	/* initialized data segment */
    private Segment uninitData;	/* uninitialized data segment */

    /*
     * The underlying Java stream.
     */
    private OutputStream out;


    public NoffOutput(File outFile) throws FileNotFoundException {
	this.out = new FileOutputStream(outFile);
	code = new Segment();
	initData = new Segment();
	uninitData = new Segment();
    }

    public void setCode(int virtualAddr, byte[] contents) {
	code.virtualAddr = virtualAddr;
	code.contents = contents;
	code.size = contents.length;
    }

    public void setInitData(int virtualAddr, byte[] contents) {
	initData.virtualAddr = virtualAddr;
	initData.contents = contents;
	initData.size = contents.length;
    }

    public void setUninitData(int virtualAddr, int size) {
	uninitData.virtualAddr = virtualAddr;
	uninitData.size = size;
    }

    public void write() throws IOException {
	// First compute the offsets of the file data,
	// without actually writing anything.
	int size = 0;
	size += writeHeader(false);
	code.inFileAddr = size;
	size += writeContents(code.contents, false);
	initData.inFileAddr = size;
	size += writeContents(initData.contents, false);

	// Now actually emit the output.
	writeHeader(true);
	writeContents(code.contents, true);
	writeContents(initData.contents, true);
	out.flush();
    }

    private int writeHeader(boolean doWrite) throws IOException {
	int size = 0;
	size += writeInt(NOFFMAGIC, doWrite);
	size += writeSegment(code, doWrite);
	size += writeSegment(initData, doWrite);
	size += writeSegment(uninitData, doWrite);
	return(size);
    }

    private int writeSegment(Segment seg, boolean doWrite) throws IOException {
	int size = 0;
	size += writeInt(seg.virtualAddr, doWrite);
	size += writeInt(seg.inFileAddr, doWrite);
	size += writeInt(seg.size, doWrite);
	return(size);
    }

    private int writeContents(byte[] contents, boolean doWrite)
       throws IOException {
	if(doWrite)
	    out.write(contents);
	return(contents.length);
    }

    private int writeInt(int value, boolean doWrite)
	throws IOException {
	byte[] buf = new byte[4];
	if(doWrite) {
	    intToBytes(value, buf, 0);
	    out.write(buf);
	}
	return(buf.length);
    }

  /**
   * Utility method to serialize an integer into a sequence of bytes.
   *
   * @param val The integer value to be serialized.
   * @param buffer The buffer into which the value is to be serialized.
   * @param pos Starting offset from the beginning of the buffer at which
   * the serialized bytes representing the value are to be placed.
   */
  private static void intToBytes(int val, byte[] buffer, int pos) {
    buffer[pos+3] = (byte)(val >> 24 & 0xff);
    buffer[pos+2] = (byte)(val >> 16 & 0xff);
    buffer[pos+1] = (byte)(val >> 8 & 0xff);
    buffer[pos+0] = (byte)(val & 0xff);
  }

}

