package suncertify.db;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * This is the worker class that handles <b>logically</b> reserving and 
 * releasing a contractor record. <br /><br />
 * 
 * Note: that since this should only be used by the DBAccessImp class, the class
 * has been set to have default access.
 *
 * @author Robert Black
 * @version 1.0
 */
class RecordLockingManager {
    
     /**
     * A <code>HashMap</code> that contains a <code>Long</code> key that holds 
     * a the record number and a <code>Long</code> that holds the currently 
     * reserved contractor client cookie.
     */
    private static final Map<Long, Long> lockedRecords = new HashMap<Long, Long>();
    
    /**
     * This method locks a record if it is not locked already which is indicated
     * by checking the lockedRecords <code>HashMap</code> for the presents of 
     * the record number in question.  If the record number is locked, the the 
     * current <code>Thread</code> will go into wait state giving up the CPU 
     * cycles until it is notified of the record release.
     * 
     * @param recNo the contractor record to be reserved.
     * @param fileAccess the fileAcces object used to check if the record exists.
     * @return the clients cookie to be used to unlock the record later.
     * @throws RecordNotFoundException if the record is deleted or doesn't exist.
     */
    public long lockRecord(long recNo, DatabaseFileAccess fileAccess) 
            throws RecordNotFoundException {
        //Synchonized so only one thread can access this block at one time.
        synchronized(RecordLockingManager.lockedRecords) {
            //Returns a boolean to confirm existance of the record.
            boolean fileExists = fileAccess.recordExists(recNo);
            
            //Thows a RecordNotFoundException exception if the record is marked 
            //deleted or does not exist.
            if (!fileExists) {
                throw new RecordNotFoundException(
                        "The record you are trying to lock does not exist");
            }
            
            //The while loop will check if the record is already locked and if 
            //it is locked indicated by its presents in the HashMap, it will 
            //cause the current thread to go into waiting state untill notified.
            while (RecordLockingManager.lockedRecords.containsKey(recNo)) {
                try {
                    RecordLockingManager.lockedRecords.wait();
                } catch (InterruptedException ex) {
                    //if the thread is interrupted it returns a long of -1.
                    return -1l;
                }
            }

            //Creates a random cookie
            Random cookieGenerator = new Random();
            long clientCookie = cookieGenerator.nextLong();
            
            //Locks the record by putting it into the lockedRecords HashMap.
            RecordLockingManager.lockedRecords.put(recNo, clientCookie);
            
            //Returns the cookie so it can be used to unlock the record later.
            return clientCookie;
        } 
    }
    
    /**
     * This method unlocks the record if the client is the same client that had 
     * originally locked the record with the same client cookie.
     * 
     * @param recNo the record number to be unlocked.
     * @param cookie the client cookie obtained from locking a record.
     * @throws SecurityException thrown if the client cookie is not the same as 
     * the client cookie obtained when record was locked.
     */
    public void unlock(long recNo, long cookie) throws SecurityException {
        //Synchonized so only one thread can access this block at one time.
        synchronized(RecordLockingManager.lockedRecords) {
            //Retrieves the records cookie and checks that it is the same a 
            //the cookie used to unlock the record.
            Long clientCookie = RecordLockingManager.lockedRecords.get(recNo);
            if (clientCookie == null || clientCookie != cookie) {
                throw new SecurityException(
                        "The record you are trying to unlock has been locked "
                        + "by another client.");
            } else {
                //Removes the HashMap element to indicate the lock has been 
                //released and notifies all other threads to retry for the 
                //desired resources.
                RecordLockingManager.lockedRecords.remove(recNo);
                RecordLockingManager.lockedRecords.notifyAll();
            }
        }
    }
    
    /**
     * This method checks that the client had locked a record by checking its
     * locking cookie with the one stored in this HashMap related to the record 
     * number.
     * 
     * @param recNo the record number in question.
     * @param clientCookie the cookie the client has submitted for check.
     * @return true if the cookies are the same and false if not.
     */
    public boolean isCorrectClient(long recNo, long clientCookie) {
        //Retrieves the cookie from the HashMap and checks if the submitted one
        //matches the stores one.
        long cookie = RecordLockingManager.lockedRecords.get(recNo);
        if (cookie == clientCookie) {
            return true;
        } else {
            return false;
        }
    }
    
}
