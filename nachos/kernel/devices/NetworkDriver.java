// should SendHandler and ReceiveHandler call yieldOnReturn?
// NetworkDriver.java
//	Class for synchronous access of the network.  The physical network 
//	is an asynchronous device (network requests return immediately, and
//	an interrupt happens later on).  This is a layer on top of
//	the network providing a synchronous interface (requests wait until
//	the request completes).
//
//	Use a semaphore to synchronize the interrupt handlers with the
//	pending requests.  Also, sending a packet before the previous
//	send operation completes is an error, so a lock is used to
//	prevent concurrent sends.
//
// Copyright (c) 1992-1993 The Regents of the University of California.
// Copyright (c) 1998 Rice University.
// Copyright (c) 2003 State University of New York at Stony Brook.
// All rights reserved.  See the COPYRIGHT file for copyright notice and 
// limitation of liability and disclaimer of warranty provisions.

package nachos.kernel.devices;

import nachos.Debug;
import nachos.machine.Network;
import nachos.machine.Packet;
import nachos.kernel.threads.Semaphore;
import nachos.kernel.threads.Lock;
import nachos.kernel.devices.InterruptHandler;

/**
 * This class defines a "synchronous" network abstraction for sending
 * and receiving packets on the network.  As with other I/O devices,
 * the raw physical network is an asynchronous device -- requests to
 * send or receive packets return immediately, and an interrupt occurs
 * later to signal that the operation completed.
 *
 * This class provides synchronization so that its methods (send and
 * receive) return when the network operation finishes.  It also
 * provides synchronization to ensure that the next send operation
 * starts only after the previous send operation (if any) finished;
 * this is required for the network to work correctly. 
 *
 * @see	nachos.machine.Packet
 * @see nachos.machine.Network */
public class NetworkDriver {
  /** Raw network device. */
  private Network network;

  /** Semaphore used to synchronize sending threads with the interrupt
      handler. */
  private Semaphore sendSemaphore;

  /** Semaphore used to synchronize receiving threads with the
      interrupt handler. */
  private Semaphore receiveSemaphore;

  /** Lock used to ensure that the previous send request finished
      before the next send request starts. */
  private Lock sendLock;

  /** Network address of this machine. */
  private byte id;

  /**
   * Initialize the synchronous interface to the physical network, in turn
   * initializing the physical network.
   *
   * @param args      Array containing command-line arguments
   */
  public NetworkDriver(String[] args) {
    
    boolean foundId = false;

    sendSemaphore = new Semaphore("synch network send sem", 0);
    receiveSemaphore = new Semaphore("synch network receive sem", 0);
    sendLock = new Lock("synch network send lock");
    
    // set this machine's id
    id = 0;
    for (int i=0; i<args.length; i++) {
	if (args[i].equals("-m")) {
	  Debug.ASSERT((i<args.length-1), "NetworkTest.start: include a number after -m!");
	  id = (byte)Integer.parseInt(args[++i]);
	  foundId = true;
	  break;
	}
    }
    Debug.ASSERT(foundId, "NetworkDriver: please specify this machine's id!");

    network = new Network(id, new ReceiveHandler(), new SendHandler());
  }

  /**
   * Receive a network packet.  Return only after a packet has been
   * received.
   *
   */
  public Packet receive() {
    Packet p;
    receiveSemaphore.P();		// wait for interrupt
    p = network.receive();
    return p;
  }

  /**
   * Send a network packet.  Return only after the packet has been
   * sent.
   *
   * @param p The packet to send.
   */
  public void send(Packet p) {
    sendLock.acquire();                 // only one send at a time
    network.send(p);
    sendSemaphore.P();			// wait for interrupt
    sendLock.release();
  }


  /**
   * Network driver interrupt handler class.
   */
  private class ReceiveHandler extends InterruptHandler {
      /**
       * When the network interrupts, wake up the thread that issued
       * the request that just finished.
       */
      public void serviceDevice() {
	  receiveSemaphore.V();
      }
  }

  /**
   * Network driver interrupt handler class.
   */
  private class SendHandler extends InterruptHandler {
      /**
       * When the network interrupts, wake up the thread that issued
       * the request that just finished.
       */
      public void serviceDevice() {
	  sendSemaphore.V();
      }
  }

}
