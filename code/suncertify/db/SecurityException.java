package suncertify.db;

/**
 * This Exception thrown when a client other than the client tries to unlock a 
 * locked record.
 * 
 * @author Robert Black
 * @version 1.0
 */
public class SecurityException extends Exception {
    
    /**
     * A version number for this class so that serialization can occur without 
     * worrying about the underlying class changing between serialization and 
     * de-serialization.
     */
    private static final long serialVersionUID = -5459751543868711021L;

    /**
     * Default constructor.
     */
    public SecurityException() {
    }

    /**
     * This constructor takes the exception message.
     * 
     * @param message the message.
     */
    public SecurityException(String message) {
        super(message);
    }
    
}
