package suncertify.db;

/**
 * This Exception thrown when a request for a record that is deleted or does 
 * not exist in the database.
 * 
 * @author Robert Black
 * @version 1.0
 */
public class RecordNotFoundException extends Exception {
    
    /**
     * A version number for this class so that serialization can occur without 
     * worrying about the underlying class changing between serialization and 
     * de-serialization.
     */
    private static final long serialVersionUID = 1400561905736538468L;
    
    /**
     * Default constructor.
     */
    public RecordNotFoundException() {
    }
    
    /**
     * This constructor takes the exception message.
     * 
     * @param message the message.
     */
    public RecordNotFoundException(String message) {
        super(message);
    }
    
}