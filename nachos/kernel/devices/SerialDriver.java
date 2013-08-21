// SerialDriver.java
//
//	Test driver that operates serial ports in PIO mode.
//	Students will replace this with a bona fide interrupt-driven version.
//
// Copyright (c) 2010 State University of New York at Stony Brook.
// All rights reserved.  See the COPYRIGHT file for copyright notice and 
// limitation of liability and disclaimer of warranty provisions.

package nachos.kernel.devices;

import nachos.Debug;
import nachos.machine.Machine;
import nachos.machine.Interrupt;
import nachos.machine.SerialPort;
import nachos.kernel.threads.Semaphore;

import java.util.Queue;
import java.util.LinkedList;
import java.net.SocketAddress;

/**
 * Driver for serial port devices.
 *
 * @author Eugene W. Stark
 * @version 20100720
 */
public class SerialDriver {

    /** SerialPort device units. */
    private SerialPort[] units = new SerialPort[SerialPort.NUM_UNITS];
    
    /** SerialPort output queues. */
    @SuppressWarnings("unchecked")
    private Queue<Byte>[] outqs = new Queue[SerialPort.NUM_UNITS];
    
    /** SerialPort input queues. */
    @SuppressWarnings("unchecked")
    private Queue<Byte>[] inqs = new Queue[SerialPort.NUM_UNITS];
    
    /** Input semaphores. */
    private Semaphore[] dataAvail = new Semaphore[SerialPort.NUM_UNITS];
    
    /** Output semaphores. */
    private Semaphore[] spaceAvail = new Semaphore[SerialPort.NUM_UNITS];
    
    /** Busy status of each unit. */
    private boolean[] busy = new boolean[SerialPort.NUM_UNITS];

    /**
     * Initialize the driver.
     *
     * @param args  Command-line arguments passed to Nachos.
     */
    public SerialDriver(String[] args) {
	// Nothing for now.  The units are initialized individually
	// on demand via openPort().
    }

    /**
     * Shut down the driver and the underlying devices.
     */
    public void stop() {
	for(int i = 0; i < units.length; i++)
	    closePort(i);
    }

    /**
     * Open a specified serial port and ready it for use.
     * By default, the port is in loopback mode, in which the
     * transmit and receive sides are "wired together".
     *
     * @param i The unit number of the port to open.
     */
    public void openPort(int i) {
	SerialPort unit = SerialPort.getUnit(i);
	units[i] = unit;
	outqs[i] = new LinkedList<Byte>();
	inqs[i] = new LinkedList<Byte>();
	dataAvail[i] = new Semaphore("data available: serial unit" + i, 0);
	spaceAvail[i] = new Semaphore("space available: serial unit" + i, 1);
	unit.setHandler(new SerialIntHandler(i));
	int oldLevel = Interrupt.setLevel(Interrupt.IntOff);
	unit.writeMCR(SerialPort.MCR_DTR | SerialPort.MCR_RTS);
	Debug.print('p', "Serial port " + i + " open at address "
		    + unit.getLocalAddress() + "\n");
	Interrupt.setLevel(oldLevel);
    }

    /**
     * Close a previously opened port.
     *
     * @param i The unit number of the port to close.
     */
    public void closePort(int i) {
	int oldLevel = Interrupt.setLevel(Interrupt.IntOff);
	SerialPort unit = units[i];
	if(unit != null) {
	    unit.writeMCR(0);
	    unit.setHandler(null);
	    unit.stop();    
	    units[i] = null;
	    outqs[i] = null;
	    inqs[i] = null;
	}
	Interrupt.setLevel(oldLevel);
    }

    /**
     * Connect the wire attached to a specified unit number to a remote
     * port located at a specified address.
     * 
     * @param i The unit number of the port to be connected.
     * @param addr  The address of the remote port to connect to,
     * or null to set the specified unit into loopback mode.
     */
    public void connectPort(int i, SocketAddress addr) {
	units[i].connectTo(addr);
    }

    /**
     * Send a byte of data over a serial port.
     * The calling thread spins in a polling loop until the port is ready
     * to accept the data to be transmitted.
     *
     * @param i The unit number of the port to use.
     * @param data The data byte to be transmitted.
     */
    public void putByte(int i, byte data) {
	SerialPort unit = units[i];
	int oldLevel = Interrupt.setLevel(Interrupt.IntOff);
	spaceAvail[i].P();
	outqs[i].add(data);
	startXmit(i);
	Interrupt.setLevel(oldLevel);
    }

    /**
     * Wait for a byte of data to be received over the serial port
     * and return it.  The calling thread spins in a polling loop until
     * a byte of data is available.
     *
     * @param i The unit number of the port to use.
     * @return  The byte of data received.
     */
    public byte getByte(int i) {
	int oldLevel = Interrupt.setLevel(Interrupt.IntOff);
	dataAvail[i].P();
	byte data = (Byte)inqs[i].poll();
	Interrupt.setLevel(oldLevel);
	return data;
    }

    /**
     * Start transmission of the next outgoing byte.
     * Call with interrupts disabled.
     * 
     * @param i
     */
    private void startXmit(int i) {
	if(!busy[i]) {
	    Byte data = (Byte)outqs[i].poll();
	    if(data != null) {
		busy[i] = true;
		units[i].writeTDR(data);
		spaceAvail[i].V();
	    }
	}
    }
    
    /**
     * SerialDriver interrupt handler class (sample: not currently used).
     */
    private class SerialIntHandler extends InterruptHandler {

	private int index;

	public SerialIntHandler(int i) {
	    this.index = i;
	}

	public void serviceDevice() {
	    System.out.println("Serial interrupt: unit #" + index
			       + " at time " + Machine.stats.totalTicks);
	    SerialPort unit = units[index];
	    unit.showState();
	    if((unit.readLSR() & SerialPort.LSR_TRDY) != 0) {
		busy[index] = false;
		startXmit(index);
	    }
	    if((unit.readLSR() & SerialPort.LSR_RRDY) != 0) {
		byte data = unit.readRDR();
		inqs[index].add(data);
		dataAvail[index].V();
	    }
	}
    }
}
