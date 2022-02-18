package suncertify.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * This class implements the required DBAccess interface and is used by the 
 * BookingBusinessLodgicImp class and possibly other applications to preform 
 * their database operations.<br/>
 * 
 * It uses two worker classes <code>DatabaseFileAccess</code> and the 
 * <code>RecordLockingManager</code> to preform its functions through their 
 * methods.
 * 
 * @author Robert Black
 * @version 1.0
 */
public class Data implements DBAccess {
    
    /**
     * Holds the <code>DatabaseFileAccess</code> worker class.
     */
    private static DatabaseFileAccess fileAccess;;
    
    /**
     * Holds the <code>RecordLockingManager</code> worker class.
     */
    private static RecordLockingManager recordLockingManager;
    
    /**
     * Holds the path to the database file.
     */
    private String filePath;

    /**
     * The default constructor that creates a class which implements the DBAcess 
     * interface, used to access the database.<br/><br/>
     * All instances of this class share the same static 
     * <code>DatabaseFileAccess</code> and <code>RecordLockingManager</code>.
     * 
     * @throws FileNotFoundException if the database file cannot be found.
     * @throws IOException if the database file cannot be read or written to.
     */
    public Data() throws FileNotFoundException, IOException {
        //Ckecks to see if an intance of fileAccess exists.
        if (Data.fileAccess == null) {
            String userDir = System.getProperty("user.dir");
            String fileSep = File.separator;
            Properties applicationProperties = new Properties();
            File propertiesFile = new File(userDir + fileSep 
                    + "suncertify.properties");

            FileInputStream propertiesIn = null;
            propertiesIn = new FileInputStream(propertiesFile);
            applicationProperties.load(propertiesIn);
            propertiesIn.close();

            this.filePath = applicationProperties.getProperty("dataFile.file");

            Data.fileAccess = new DatabaseFileAccess(filePath);
        }
        
        //Ckecks to see if an intance of fileAccess exists.
        if (Data.recordLockingManager == null) {
            Data.recordLockingManager = new RecordLockingManager();
        }
    }

    /**
     * Reads a record from the file. Returns an array where each element is a 
     * record value.
     * 
     * @param recNo the record number (file position).
     * @return a <code>String[]</code> array containing the records field data.
     * @throws <code>RecordNotFoundException</code> if the record is deleted or 
     * doesn't exist.
     */
    @Override
    public String[] readRecord(long recNo) throws RecordNotFoundException {
        return Data.fileAccess.readRecord(recNo);
    }
    
    /**
     *  Modifies the fields of a record. The new value for field n appears in 
     *  data[n].
     * 
     * @param recNo the record number (file position).
     * @param data the array containing the updated data.
     * @param lockCookie the cookie obtained from lockRecord(long recNo) from 
     * RecordLockingManager.
     * @throws RecordNotFoundException if the record is deleted or doesn't exist.
     * @throws SecurityException if the record is locked with a cookie other 
     * than lockCookie.
     */
    @Override
    public void updateRecord(long recNo, String[] data, long lockCookie) 
            throws RecordNotFoundException, SecurityException {
        if (Data.recordLockingManager.isCorrectClient(recNo, lockCookie)) {
            Data.fileAccess.updateRecord(recNo, data);
        } else {
            throw new SecurityException("The record you are tying to update is "
                    + "locked by another client.");
        }
    }
    
    /**
     * Deletes a record, making the record number and associated disk storage 
     * available for reuse. 
     * 
     * @param recNo the record number (file position).
     * @param lockCookie the cookie obtained from lockRecord(long recNo) from 
     * RecordLockingManager.
     * @throws RecordNotFoundException if the record is deleted or doesn't exist.
     * @throws SecurityException if the record is locked with a cookie other 
     * than lockCookie.
     */
    @Override
    public void deleteRecord(long recNo, long lockCookie) 
            throws RecordNotFoundException, SecurityException {
        if (Data.recordLockingManager.isCorrectClient(recNo, lockCookie)) {
            Data.fileAccess.deleteRecord(recNo);
        } else {
            throw new SecurityException("The record you are tying to delete is "
                    + "locked by another client.");
        }
    }

    /**
     * Returns an array of record numbers that match the specified criteria. 
     * Field n in the database file is described by criteria[n]. A null value 
     * in criteria[n] matches any field value. A non-null  value in criteria[n] 
     * matches any field value that begins with criteria[n]. (For example, 
     * "Fred" matches "Fred" or "Freddy".
     * 
     * @param criteria an <code>String[]</code> containing the data to match.
     * @return an <code>long[]</code> with matching record numbers 
     * (file positions).
     */
    @Override
    public long[] findByCriteria(String[] criteria) {
        return Data.fileAccess.findByCriteria(criteria);
    }

    /**
     * Creates a new record in the database (reusing a deleted entry). 
     * Inserts the given data, and returns the record number of the new record.
     * 
     * @param data an <code>String[]</code> containing the data for a new record.
     * @return the new record number (file position).
     * @throws DuplicateKeyException if a record that matches the primary key 
     * (name and location fields) of an existing record.
     */
    @Override
    public long createRecord(String[] data) throws DuplicateKeyException {
        return Data.fileAccess.createRecord(data);
    }

    /**
     * Locks a record so that it can only be updated or deleted by this client. 
     * If the specified record is already locked by a different client, the 
     * current thread gives up the CPU and consumes no CPU cycles until the 
     * record is unlocked.
     * 
     * @param recNo the record number (file position).
     * @return is a cookie that must be used when the record is unlocked, 
     * updated, or deleted.
     * @throws RecordNotFoundException if the record is deleted or doesn't exist.
     */
    @Override
    public long lockRecord(long recNo) throws RecordNotFoundException {
        return Data.recordLockingManager.lockRecord(recNo, Data.fileAccess);
    }

    /**
     * Releases the lock on a record. Cookie must be the cookie returned when 
     * the record was locked.
     * 
     * @param recNo the record number (file position).
     * @param cookie the cookie obtained from lockRecord(long recNo) from 
     * RecordLockingManager.
     * @throws SecurityException if the record is locked with a cookie other 
     * than lockCookie.
     */
    @Override
    public void unlock(long recNo, long cookie) throws SecurityException {
        Data.recordLockingManager.unlock(recNo, cookie);
    }
    
}
