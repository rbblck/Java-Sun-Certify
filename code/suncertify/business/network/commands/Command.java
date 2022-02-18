package suncertify.business.network.commands;

import java.io.Serializable;
import suncertify.presentation.BookingModel;

/**
 * This is an abstract class that has two attributes and two methods.  It is 
 * used to create concrete sub classes that will be sent to the server which 
 * invokes the <code>execute()</code> method.  The <code>execute()</code> 
 * method either updates the <code>result</code> or stores an exception in the 
 * <code>exception</code> variable.  It is the subclasses responsibility to 
 * implement the <code>execute()</code> method.
 * 
 * @author Robert Black
 * @version 1.0
 */
public abstract class Command implements Serializable {
    
     /**
     * A version number for this class so that serialization can occur without 
     * worrying about the underlying class changing between serialization and 
     * de-serialization.
     */
    private static final long serialVersionUID = 2169633843326176906L;
    
    /**
     * Holds the serializable object.
     */
    protected Object result = null;
    
    /**
     * Holds the Serializable exception.
     */
    protected Exception exception = null;
    
    /**
     * The execute method that updates the <code>result</code> or stores an 
     * exception in the <code>exception</code> variable.  It also takes a 
     * <code>BookingModel</code> to work with.
     * 
     * @param model the <code>BookingModel</code>.
     */
    public abstract void execute(BookingModel model);
    
    /**
     * This method will either return the stored object or throw the stored 
     * exception if an exception is stored
     * 
     * @return the stored object.
     * @throws Exception if there is an exception stored.
     */
    public Object result() throws Exception {
        //Checks if an object is present and returns it, if it is.
        if (this.result != null) {
            return this.result;
        }
        
        //Checks to see if an exception is stores and throws it, if it is.
        if (this.exception != null) {
            throw this.exception;
        }
        
        //If no object or exception is stored, returns null.
        return null;
    }
    
}
