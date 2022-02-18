package suncertify.db;

/**
 * This Exception thrown when a created record matches the primary key 
 * (name and location fields) of an existing record exits in the database.
 * 
 * @author Robert Black
 * @version 1.0
 */
public class DuplicateKeyException extends Exception {
    
    /**
     * A version number for this class so that serialization can occur without 
     * worrying about the underlying class changing between serialization and 
     * de-serialization.
     */
    private static final long serialVersionUID = 3890659903537669597L;

    /**
     * Default constructor.
     */
    public DuplicateKeyException() {
    }
    
    /**
     * This constructor takes the exception message.
     * 
     * @param message the message.
     */
    public DuplicateKeyException(String message) {
        super(message);
    }
    
}
