package suncertify.business;

/**
 * This Exception thrown when an attempt is made to book a contractor that is 
 * already booked
 * 
 * @author Robert Black
 * @version 1.0
 */
public class RecordAlreadyBookedException extends Exception {
    
    /**
     * A version number for this class so that serialization can occur without 
     * worrying about the underlying class changing between serialization and 
     * de-serialization.
     */
    private static final long serialVersionUID = 2800868917625884646L;

    /**
     * Default constructor.
     */
    public RecordAlreadyBookedException() {
    }

    /**
     * This constructor takes the exception message.
     * 
     * @param message the message.
     */
    public RecordAlreadyBookedException(String message) {
        super(message);
    }
    
}
