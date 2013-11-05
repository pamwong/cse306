package nachos.kernel.userprog;

import java.util.ArrayList;

import nachos.kernel.filesys.BitMap;
import nachos.machine.Machine;


/**
 * This is a physical memory manager it keeps track of what memory in the machine 
 * has been allocated by a user program. It does this using a Bitmap where one bit is related
 * to a page of data in memory. This memory manager uses a simple paging schema.
 * 
 * This class uses a the singleton design pattern
 * @author david
 *
 */
public class PhysicalMemoryManager {
 //We probably won't need this but II'll keep it here just in case
    ArrayList AddrSpaceList;
    
    //In our BitMap a bit will relate to a page in memory
    BitMap MemoryMap;
    
    // This holds the on instance of the physical memory manager
    private static PhysicalMemoryManager singleton;
    
   private PhysicalMemoryManager(){
       //We set the Bitmap to hold as many bits as there are pages are bits start off clear
       //to signify that no memory has been allocated.
	MemoryMap = new BitMap(Machine.NumPhysPages);
	
	
    }
   
   /**
    * This gets the refrecne to the physical memory manager
 * @return 
    */
   public static PhysicalMemoryManager getPhysicalMemoryManager(){
       if(singleton == null){
	 singleton = new PhysicalMemoryManager();
       }
       
       return singleton;
       
   }
   
   /**
    * This method marks bits in the BitMap to represent memory that will be allocated by a user program
    * this method should be called before the memory is allocated and it should be locked by a semaphore.
    * 
    * @param numPages - the amount of pages needed by the user program
    * @return an array of memory  locations that will be allocated
    */
   public int[] allocateMemory(int numPages){
       int [] memoryLocations = new int [numPages];
       if(numPages >  MemoryMap.numClear()){
         //we return null to signify that there is not enough space for this program
	   return null;
       }
       else{
	   //we enough find enough free locations in memory for the amount of pages that we need
	   for(int i = 0; i < numPages; i++){
	       memoryLocations[i] = MemoryMap.find();	       	       
	   }
       }
    //we return the locations of free memory
    return memoryLocations;
       
   }
   /**
    * This method clears the bits that have been freed in memory it should be called 
    * after the memory has been freed and it should be locked by a semaphore.
    * @param memoryLocations - an array of memory locations that have been freed, whose bits need to be cleared
    */
   public void freeMemory(int [] memoryLocations){
       //in this class we clear all the bits that are associated with the memory locations that are free
       for(int i = 0; i < memoryLocations.length; i++){
	   MemoryMap.clear(memoryLocations[i]);
       }
       
       
   }
    
    
}
