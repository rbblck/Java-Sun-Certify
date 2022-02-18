package suncertify.business.network.commands;

import suncertify.presentation.BookingModel;

/**
 * This class extends the abstract command class and implements the 
 * <code>execute()</code> method to either update the <code>result</code> 
 * variable or store an exception in the <code>exception</code> variable.
 * 
 * @author Robert Black
 * @version 1.0
 */
public class CheckContractorBookedCommand extends Command {
    
    /**
     * A version number for this class so that serialization can occur without 
     * worrying about the underlying class changing between serialization and 
     * de-serialization.
     */
    private static final long serialVersionUID = -6463427460436762026L;
    
    /**
     * Holds the record number (file position) to be used.
     */
    private final long recNo;

    /**
     * The constructor.  It takes the the record number (file position)
     * and stores it in the respective variable to be used by the 
     * <code>execute()</code> method on the server side.
     * 
     * @param recNo the record number (file position).
     */
    public CheckContractorBookedCommand(long recNo) {
        this.recNo = recNo;
    }

    /**
     * This execute method updates the <code>result</code> or stores an 
     * exception in the <code>exception</code> variable.  It also takes a 
     * <code>BookingModel</code> to work with.  
     * 
     * @param model the model used to carry out the desired operation.
     */
    @Override
    public void execute(BookingModel model) {
        try {
            //Stores the object rturned from this operation.
            this.result = model.isContractorBooked(recNo);
        } catch (Exception ex) {
            //If an exception is throw from the above operation, it is stored 
            //in the exception variable.
            this.exception = ex;
        }
    }
    
}
