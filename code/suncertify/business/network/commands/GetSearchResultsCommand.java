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
public class GetSearchResultsCommand extends Command {
    
    /**
     * A version number for this class so that serialization can occur without 
     * worrying about the underlying class changing between serialization and 
     * de-serialization.
     */
    private static final long serialVersionUID = 5379375589201620477L;
    
    /**
     * Holds the name criteria to be used.
     */
    private final String name;
    
    /**
     * Holds the location criteria to be used.
     */
    private final String location;

    /**
     * The constructor.  It takes the name criteria and the location criteria 
     * and stores them in the respective variables to be used by the 
     * <code>execute()</code> method on the server side.
     * 
     * @param name the name search criteria.
     * @param location location the location search criteria.
     */
    public GetSearchResultsCommand(String name, String location) {
        this.name = name;
        this.location = location;
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
            this.result = model.searchContractors(name, location);
        } catch (Exception ex) {
            //If an exception is throw from the above operation, it is stored 
            //in the exception variable.
            this.exception = ex;
        }
    }
    
}
