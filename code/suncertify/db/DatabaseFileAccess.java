package suncertify.db;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import suncertify.presentation.ApplicationRunner;

/**
 * This is the worker class that does all the access and manipulation of the
 * physical file which is the actual database.
 * <br/><br/>
 * Note: that since this should only be used by the DBAccessImp class, the class
 * has been set to have default access.
 *
 * @author Robert Black
 * @version 1.0
 */
class DatabaseFileAccess {
    
    /**
     * The Character Set used to read and store data to file.
     */
    private static final Charset CHAR_SET = Charset.forName("US-ASCII");
    
    
    /**
     * The physical file on disk containing the Contractor records
     */
    private static RandomAccessFile dataFile = null;
    
    /**
     * The file position in bytes of the magic cookie.
     */
    private static final long START_OF_MAGIC_COOKIE_FILE_POS = 0l;
    
    /**
     * The cookie that the file being used should have.
     */
    private static final int MAGIC_COOKIE = 514;
    
    /**
     * The file position in bytes of the start position in bytes of record zero 
     * in the file.
     */
    private static final long START_RECORD_ZERO_FILE_POS = 4l;
    
    /**
     * The file position in bytes of the number of fields in a record.
     */
    private static final long START_NUMBER_OF_FIELDS_FILE_POS = 8l;
    
    /**
     * The file position in bytes of the schema description.
     */
    private static final long START_SCHEMA_FILE_POS = 10l;
    
    /**
     * The length in bytes of the deleted / valid flag at the beginning of each
     * record.
     */
    private static final long LENGTH_OF_FLAG_BYTES = 2;
    
    /**
     * The flag that implies deleted.
     */
    private static final int DELETED_FLAG = 0x8000;
    
    /**
     * The flag that implies valid.
     */
    private static final int VALID_FLAG = 00;
    
    /**
     * The file position in bytes of the start of record zero.
     */
    private static long startOfRecordZero;
    
    /**
     * The number of fields in a record.
     */
    private static int numberOfFields;
    
    /**
     * An array of respective field lengths in each record.
     */
    private static int[] fieldNameLengths;
    
    /**
     * An array of respective field names for a record;
     */
    private static String[] fieldNames;
    
    /**
     * An array of respective field lengths
     */
    private static int[] fieldLengths;
    
    /**
     * A Map that contains cached records consisting of a <code>Long</code> 
     * (record number representing the position of the record on disk) as the 
     * key and a <code>Contractor</code> object as the value representing the 
     * record on disk.
     */
    private static final Map<Long, Contractor> recordCache 
            = new HashMap<Long, Contractor>();
    
    /**
     * A <code>ReentrantReadWrite</code> Ensures that many users can read the 
     * cached <code>Hash Map</code> collection as long as nobody is updating it 
     * or writing to disk.
     */
    private static ReadWriteLock databaseLock = new ReentrantReadWriteLock();
    
    /**
     * A <code>String</code> which will be the same size as a Contractor record
     * and filled with nulls. Having this pre-built will save time and improve 
     * efficiency when writing to disk.
     */
    private static String emptyContractorRecord;;
    
    /**
     * The location where the database file is stored.
     */
    private static String databasePath;
    
    /**
     * Initializes the <code>emptyContractorRecord String</code>.
     */
    static {
        DatabaseFileAccess.emptyContractorRecord 
                = new String(new byte[Contractor.RECORD_LENGTH]);
    }

    /**
     * Default constructor that accepts the database path as a parameter.<br/>
     * All instances of this class share the same data file.
     * 
     * @param dbFilePath the path to the database file directory
     * @throws FileNotFoundException if the database file cannot be found.
     * @throws IOException if the database file cannot be read or written to.
     */
    public DatabaseFileAccess(String dbFilePath) 
            throws FileNotFoundException, IOException  {
        
        //As the dataFile and cache are static and all instances share the 
        //dataFile this if statment checks for an existing 
        //dataFile to prevent unnecessary disk usage.
        if (DatabaseFileAccess.dataFile == null) {
            DatabaseFileAccess.dataFile = new RandomAccessFile(dbFilePath, "rw");
            DatabaseFileAccess.databasePath = dbFilePath;
        }
    }
    
    /**
     * This is a private method used to update the record cache that in turn 
     * will be used and manipulated before updating the file on disk .
     * 
     * @throws IOException throws an exception if any problems occur with disk 
     * access.
     */
    private void updateCache() throws IOException {
        try {
            //Read only lock prevents any writing to the cache or file while the 
            //file is being read, but does allow concurrent reading.
            DatabaseFileAccess.databaseLock.readLock().lock();
            
            //initailises the record file position to be included in the cache 
            //recordCache.
            long recordNumber = 0l;

            //This if statement checks that the data file is the correct file to 
            //be used.
            if (this.magicCookieCheck()) {
                
                //This if statement checks to see if the database schema needs
                //to be populated useing three arrays.
                if (DatabaseFileAccess.fieldNameLengths == null 
                        || DatabaseFileAccess.fieldNames == null 
                        || DatabaseFileAccess.fieldLengths == null) {
                    this.setSchema();
                }
        
                //This contractor object represents the field titles and is 
                //placed in the cache as record 0.
                Contractor fieldTitles = new Contractor(fieldNames);
                DatabaseFileAccess.recordCache.put(recordNumber, fieldTitles);
                recordNumber++;

                //This for loop reads all the records in the database file and 
                //populates the recordCache including records marked deleted
                //which will be used later so they can be replaced with newly
                //created records saving disk space.
                DatabaseFileAccess.dataFile.seek(startOfRecordZero);
                for (long i = DatabaseFileAccess.startOfRecordZero; 
                        i < DatabaseFileAccess.dataFile.length(); 
                        i += (DatabaseFileAccess.LENGTH_OF_FLAG_BYTES 
                                + Contractor.RECORD_LENGTH)) {

                    //Reads the record deleted / vailid flag.
                    int flag = DatabaseFileAccess.dataFile.readUnsignedShort();
                    
                    //This for loop reads each record field and stores it into a
                    //String array to be used to create a Contractor object.
                    String[] fieldStrigs = 
                            new String[DatabaseFileAccess.numberOfFields];
                    for (int j = 0; j < DatabaseFileAccess.numberOfFields; j++) {
                        byte[] field = new byte[DatabaseFileAccess.fieldLengths[j]];
                        DatabaseFileAccess.dataFile.read(field);
                        fieldStrigs[j] = new String(field, CHAR_SET).trim();
                    }

                    //The Contractor object is created and put into the record 
                    //cache.
                    Contractor contractor = new Contractor(flag, fieldStrigs);
                    DatabaseFileAccess.recordCache.put(recordNumber, contractor);
                    recordNumber++;
                }
            }
        } finally {
            //Releases the lock as the operation has finished.
            DatabaseFileAccess.databaseLock.readLock().unlock();
        }
    }
    
    /**
     * This private method is used to compare the magic cookie to be used with 
     * the magic cookie stored on the data file, therefore ensuring the correct 
     * file and shema is being used.
     * 
     * @return a boolean to indicate the correct magic cookie.
     * @throws IOException if there is a problem reading the file.
     */
    private boolean magicCookieCheck() throws IOException {
        DatabaseFileAccess.dataFile.
                seek(DatabaseFileAccess.START_OF_MAGIC_COOKIE_FILE_POS);
        int fileMagicCookie = DatabaseFileAccess.dataFile.readInt();
        if (fileMagicCookie == DatabaseFileAccess.MAGIC_COOKIE) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     *This private method populates the schema arrays used to update the 
     * recordCache.
     * 
     * @throws IOException if there is a problem reading the file.
     */
    private void setSchema() throws IOException {
        //Stores the start position of the first record on the data file.
        DatabaseFileAccess.dataFile.
                seek(DatabaseFileAccess.START_RECORD_ZERO_FILE_POS);
        DatabaseFileAccess.startOfRecordZero 
                = (long) DatabaseFileAccess.dataFile.readInt();

        //Stores the nuber of fields in each record.
        DatabaseFileAccess.dataFile.
                seek(DatabaseFileAccess.START_NUMBER_OF_FIELDS_FILE_POS);
        DatabaseFileAccess.numberOfFields = dataFile.readShort();

        //Creates the arrays to hold the schema information.
        DatabaseFileAccess.fieldNameLengths 
                = new int[DatabaseFileAccess.numberOfFields];
        DatabaseFileAccess.fieldNames 
                = new String[DatabaseFileAccess.numberOfFields];
        DatabaseFileAccess.fieldLengths 
                = new int[DatabaseFileAccess.numberOfFields];

        //Populates the schema arrays.
        DatabaseFileAccess.dataFile.seek(DatabaseFileAccess.START_SCHEMA_FILE_POS);
        for (int i = 0; i < DatabaseFileAccess.numberOfFields; i++) {
            //Populates the fieldNameLengths array with field title lenghts in 
            //bytes as integers.
            int fieldNameLength = dataFile.readShort();
            DatabaseFileAccess.fieldNameLengths[i] = fieldNameLength;
            
            //Populates the fieldNames with the field titles as strings using 
            //the fieldNameLengths array information.
            byte[] fieldNameBytes = new byte[fieldNameLength];
            for (int j = 0; j < fieldNameBytes.length; j++) {
                fieldNameBytes[j] = DatabaseFileAccess.dataFile.readByte();
            }
            DatabaseFileAccess.fieldNames[i] 
                    = new String(fieldNameBytes, DatabaseFileAccess.CHAR_SET);
            
            //Populates the fieldLengths array with the field length in bytes
            //as integers.
            DatabaseFileAccess.fieldLengths[i] 
                    = DatabaseFileAccess.dataFile.readShort();
        }
    }
    
    /**
     * This public method is used to return a records information as a String
     * array using the file position record number.
     * 
     * @param recNo a long used to locate the record.
     * @return a String array with the record information.
     * @throws RecordNotFoundException if the record does not exist or deleted.
     */
    public String[] readRecord(long recNo) throws RecordNotFoundException {
        //A String array that will be used to return the record information as 
        //an array.
        String[] contractorData = null;
        try {
            //Read only lock prevents any writing to the cache or file while a 
            //record is being read, but does allow concurrent reading.
            DatabaseFileAccess.databaseLock.readLock().lock();
            
            //This if statment checks to see if the record is still valid or 
            //deleted or exists at all and updates the cache. If not it throws a 
            //RecordNotFoundException.
            if (!(this.recordExists(recNo))) {
                throw new RecordNotFoundException(
                        "The record does not exist or is deleted.");
            } else {
                //Extracts the contractor object from the cache and extracts the 
                //string array with the record data.
                Contractor contractor = DatabaseFileAccess.recordCache.get(recNo);
                contractorData = contractor.getStringArrayData();
                return contractorData;
            }
        } finally {
            //Releases the lock as the operation has finished.
            DatabaseFileAccess.databaseLock.readLock().unlock();
        }
    }
    
    /**
     * This public method is used to update a records information using a String
     * array and the file position record number.
     * 
     * @param recNo a long used to locate the record.
     * @param data the String array used to update the record information.
     * @throws RecordNotFoundException if the record does not exist or deleted.
     */
    public void updateRecord(long recNo, String[] data) 
            throws RecordNotFoundException {
        String[] contractorData = null;
        try {
            //Write lock prevents any writing or reading to the cache or 
            //file while a record is being updated.
            DatabaseFileAccess.databaseLock.writeLock().lock();
            
            //This if statment checks to see if the record is still valid or 
            //deleted or exists at all and updates the cache. If not it throws a 
            //RecordNotFoundException.
            if ((!(this.recordExists(recNo))) || (recNo < 1)) {
                throw new RecordNotFoundException(
                        "The record you are trying to update does not exist or "
                        + "is deleted.");
            } else {
                //Extracts the contractor object from the cache and extracts the 
                //string array with the record data.
                Contractor oldContractor 
                        = DatabaseFileAccess.recordCache.get(recNo);
                contractorData = oldContractor.getStringArrayData();
                
                //This for loop updates the relevent elements of the extracted
                //String array.
                for (int i = 0; i < data.length; i++) {
                    if (!(data[i] == null)) {
                        contractorData[i] = data[i];
                    }
                }
                
                //A new Contractor object is created and saved to the file using
                //the private saveRecord() method.
                Contractor newContractor = new Contractor(contractorData);
                this.saveRecord(recNo, newContractor);
            }
        } finally {
            //Releases the lock as the operation has finished.
            DatabaseFileAccess.databaseLock.writeLock().unlock();
        }
    }
    
    /**
     * This public method is used to delete a record using a long to locate the
     * file position.
     * 
     * @param recNo a long used to locate the record.
     * @throws RecordNotFoundException if the record does not exist or deleted.
     */
    public void deleteRecord(long recNo) throws RecordNotFoundException {
        try{
            //Write lock prevents any writing or reading to the cache or 
            //file while a record is being updated.
            DatabaseFileAccess.databaseLock.writeLock().lock();
            
            //This if statment checks to see if the record is still valid or 
            //deleted or even exists at all and updates the cache. If not it
            //throws a RecordNotFoundException.
            try {
                if ((!(this.recordExists(recNo))) || (recNo < 1)) {
                    throw new RecordNotFoundException(
                            "Record you are trying to delete does not exist or "
                            + "is deleted.");
                } else {
                    //This overwirtes the deleted / valid flag as deleted leaving 
                    //this space available to be replaced with a new record.
                    DatabaseFileAccess.dataFile.seek(DatabaseFileAccess.startOfRecordZero 
                            + ((DatabaseFileAccess.LENGTH_OF_FLAG_BYTES 
                            + Contractor.RECORD_LENGTH) * (recNo - 1)));
                    DatabaseFileAccess.dataFile.writeShort(
                            DatabaseFileAccess.DELETED_FLAG);
                }
            } catch (IOException ex) {
                ApplicationRunner.handleException("File access unsuccessful.");
            }
        } finally {
            //Releases the lock as the operation has finished.
            DatabaseFileAccess.databaseLock.writeLock().unlock();
        }
    }
    
    /**
     * This private method checks to see if the record in question is deleted or
     * exists at all. 
     * 
     * @param recNo the file position of the record in question.
     * @return true is the record is valid or false if not valid or doesn't 
     * exist.
     */
    public boolean recordExists(long recNo) {
        Contractor contractor = null;
        try {
            //Read only lock prevents any writing to the cache or file while a 
            //record is being read, but does allow concurrent reading.
            DatabaseFileAccess.databaseLock.readLock().lock();
            
            //This statement updates the cache so that the information is 
            //current at this particular time.
            try {
                this.updateCache();
            } catch (IOException ex) {
                ApplicationRunner.handleException("File access unsuccessful.");
            }
            contractor = DatabaseFileAccess.recordCache.get(recNo);
            if (contractor == null 
                    || contractor.getFlag() == DatabaseFileAccess.DELETED_FLAG) {
                return false;
            } else {
                return true;
            }
        } finally {
            //Read only lock prevents any writing to the cache or file while a 
            //record is being read, but does allow concurrent reading.
            DatabaseFileAccess.databaseLock.readLock().unlock();
        }
    }
    
    /**
     * Returns an array of record numbers that match the specified criteria. 
     * Field n in the database file is described by criteria[n]. A null value 
     * in criteria[n] matches any field value. A non-null value in criteria[n] 
     * matches any field value that begins with criteria[n]. (For example, 
     * "Fred" matches "Fred" or "Freddy").
     * 
     * @param criteria The string array containing the name or location or both.
     * @return the record numbers (file positions) as a long array.
     */
    public long[] findByCriteria(String[] criteria) {
        long[] recNums = null;
        try {
            //Read only lock prevents any writing to the cache or file while a 
            //record is being read, but does allow concurrent reading.
            DatabaseFileAccess.databaseLock.readLock().lock();
            
            //Makes sure the argument array is not larger than 6 elements.
            if (criteria.length > 6) {
                return null;
            }
            
            //This statement updates the cache so that the information is 
            //current at this particular time.
            try {
                this.updateCache();
            } catch (IOException ex) {
                ApplicationRunner.handleException("File access unsuccessful.");
            }
            
            //Creates an array with 6 element slots used to compare the record 
            //fields for matches.
            String[] criteriaCompareArray = new String[6];
            
            //Creates an ArrayList used to check only the fields that are not 
            //null by recording the field positions in the record that need to 
            //matched.
            List<Integer> fieldPositions = new ArrayList<Integer>();
            
            //Populates the criteriaCompareArray and add the field position 
            //number to the fieldPositions List.
            for (int i = 0; i < criteria.length; i++) {
                if (criteria[i] != null) {
                    fieldPositions.add(i);
                    criteriaCompareArray[i] = criteria[i];
                }
            }
            
            //A Set is created to collect the record file position numbers that
            //match the criteria argument and the field titles record number is
            //added to the Set every time to be used later.
            //A Set was used so the record numbers are ordered.
            Set<Long>  recordNumbers = new TreeSet<Long>();
            recordNumbers.add(0l);
            
            //Creates an entrySet to traverse the recordChache extracting the
            //record numbers (keys) and Contractor objects (values) as required.
            for (Map.Entry<Long, Contractor> entry : DatabaseFileAccess.recordCache.entrySet()) {
                Contractor contractor = entry.getValue();
                String[] contractorData = contractor.getStringArrayData();
                
                //All records start as a match until it is found that they don't
                //natch.
                boolean matches = true;
                
                //Tries to match the fields being searched for.
                for (int fieldPos : fieldPositions) {
                    //Creates a pattern for each field to compare the criteria 
                    //with.
                    Pattern fieldPattern 
                            = Pattern.compile(criteriaCompareArray[fieldPos], 
                            Pattern.CASE_INSENSITIVE);
                    Matcher fieldMatcher 
                            = fieldPattern.matcher(contractorData[fieldPos].trim());
                    
                    //If any of the compared fields do not match the begining of 
                    //the field the boolean matches becomes false.
                    if (!(fieldMatcher.lookingAt()) 
                            && contractor.getFlag() 
                            == DatabaseFileAccess.VALID_FLAG) {
                        matches = false;
                    }
                }
                
                //If the record does match then it is added to the recordNumbers
                //Set.
                if (matches) {
                    recordNumbers.add(entry.getKey());
                }
                
                //Convert the recordNumbers Set to a long[].
                recNums = new long[recordNumbers.size()];
                Iterator recNoIterator = recordNumbers.iterator();
                int recNumsIndex = 0;
                while (recNoIterator.hasNext()) {
                    recNums[recNumsIndex] = (Long) recNoIterator.next();
                    recNumsIndex++;
                }
            }
        } finally {
            //Releases the lock as the operation has finished.
            DatabaseFileAccess.databaseLock.readLock().unlock();
        }
        return recNums;
    }
    
    /**
     * Creates a new record in the database reusing a deleted entry file 
     * position to save disk space and inserts the given data.
     * 
     * @param data
     * @return a long, the record number of the new record.
     * @throws DuplicateKeyException if a record with the same name and location
     * exists.
     */
    public long createRecord(String[] data) throws DuplicateKeyException {
        //Initialises the record number (file position variable) for entering
        //the new record.
        long recordNumber = -1l;
        try {
            //Write lock prevents any writing or reading to the cache or 
            //file while a record is being created.
            DatabaseFileAccess.databaseLock.writeLock().lock();
            
            //This statement updates the cache so that the information is 
            //current at this particular time.
            try {
                this.updateCache();
            } catch (IOException ex) {
                ApplicationRunner.handleException("File access unsuccessful.");
            }
            
            //Creates a new Contractor object to hopfully enter into the 
            //database.
            Contractor createdContractor = new Contractor(data);
            
            //This List is created to store any record numbers (file positions)
            //of any records that are marked deleted, if any exits.
            List<Long> deletedRecords = new ArrayList<Long>();
            
            //Creates an entrySet to traverse the recordChache extracting the
            //record numbers (keys) and Contractor objects (values) as required.
            for (Map.Entry<Long, Contractor> entry : DatabaseFileAccess.recordCache.entrySet()) {
                //This if statement checks if any records already exist using
                //the primary key of name and location combination and valid 
                //flag.
                if (createdContractor.equals(entry.getValue()) 
                        && (entry.getValue().getFlag() == DatabaseFileAccess.VALID_FLAG)) {                
                    throw new DuplicateKeyException("The record already exits.");                
                }
                //This if statement add any record numbers (file positions) 
                //marked deleted if any exist.
                if (entry.getValue().getFlag() == DatabaseFileAccess.DELETED_FLAG) {
                    deletedRecords.add(entry.getKey());
                }
            }

            //This if statement checks to see if there are any deleted slots 
            //stored in the deletedRecords ArrayList.
            if (deletedRecords.isEmpty()) {
                //If no slots the record number is the last number plus 1.
                recordNumber = DatabaseFileAccess.recordCache.size();
                
                //Saves the record to file and returns the record number.
                this.saveRecord(recordNumber, createdContractor);
                return recordNumber;
                
            //Else if there are available slots.
            } else {
                //The record number is the first available slot.
                recordNumber = deletedRecords.get(0);
                
                //Saves the record to file and returns the record number.
                this.saveRecord(recordNumber, createdContractor);
                return recordNumber;
            }
        } finally {
            //Releases the lock as the operation has finished.
            DatabaseFileAccess.databaseLock.writeLock().unlock();
        }
    }
    
    /**
     * This method saves a particular record to the database file using the 
     * record number and a Contractor object.
     * 
     * @param recNo the record number (file position).
     * @param contractor a Contractor object to be saved.
     */
    private void saveRecord(long recNo, Contractor contractor) {
        //A StringBuilder is created to reduce disk writing operations.
        final StringBuilder out 
                = new StringBuilder(DatabaseFileAccess.emptyContractorRecord);
        
        //Assists in converting Strings to a byte[].
        class RecordFieldWriter {
            
            //Current position in byte[].
            private int currentPosition = 0;
            
            /**
            * Converts a String of specified length to byte[]
            *
            * @param data the String to be converted into part of the byte[].
            * @param length the maximum size of the String
            */
            void write(String data, int length) {
                if (!(data == null)) {
                    out.replace(this.currentPosition, 
                            this.currentPosition + data.length(),
                            data);
                }
                this.currentPosition += length;
            }
            
        }
        
        //A new RecordFieldWriter to populate the StringBuilder out.
        RecordFieldWriter writeRecord = new RecordFieldWriter();
        
        //Populates the StringBuilder out with the relevent fields.
        writeRecord.write(contractor.getName(), Contractor.NAME_FIELD_LENGTH);
        writeRecord.write(contractor.getLocation(), Contractor.LOCATION_FIELD_SIZE);
        writeRecord.write(contractor.getSpecialties(), Contractor.SPECIALITIES_FIELD_SIZE);
        writeRecord.write(contractor.getSize(), Contractor.SIZE_FIELD_SIZE);
        writeRecord.write(contractor.getRate(), Contractor.RATE_FIELD_SIZE);
        writeRecord.write(contractor.getOwner(), Contractor.OWNER_FIELD_SIZE);
        
        //Writes the deleted / valid flag and the contractor record to disk.
        try {
            DatabaseFileAccess.dataFile.seek(startOfRecordZero 
                    + ((LENGTH_OF_FLAG_BYTES 
                    + Contractor.RECORD_LENGTH) * (recNo - 1)));
            DatabaseFileAccess.dataFile.writeShort(contractor.getFlag());
            DatabaseFileAccess.dataFile.write(out.toString().getBytes(CHAR_SET));
        } catch (IOException ex) {
            ApplicationRunner.handleException("File access unsuccessful.");
        }
    }
    
}
