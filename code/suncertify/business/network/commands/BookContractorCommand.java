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
public class BookContractorCommand extends Command {
    
    /**
     * A version number for this class so that serialization can occur without 
     * worrying about the underlying class changing between serialization and 
     * de-serialization.
     */
    private static final long serialVersionUID = -6248986781980881086L;
    
    /**
     * Holds the record position number.
     */
    private long recNo;
    
    /**
     * Holds the custNo the eight digit customer id.
     */
    private String custNo;

    /**
     * The constructor takes a record number and customer number and stores them 
     * in the respective variables to be used by the <code>execute()</code> 
     * method on the server side.
     * 
     * @param recNo the record position number.
     * @param custNo the eight digit customer id.
     */
    public BookContractorCommand(long recNo, String custNo) {
        this.custNo = custNo;
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
            //There is no object return required for this operation.
            model.bookContractor(recNo, custNo);
        } catch (Exception ex) {
            //If an exception is throw from the above operation, it is stored 
            //in the exception variable.
            this.exception = ex;
        }
    }
    
}
