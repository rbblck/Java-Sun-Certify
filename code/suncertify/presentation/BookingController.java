package suncertify.presentation;

import suncertify.business.RecordAlreadyBookedException;
import suncertify.db.RecordNotFoundException;
import suncertify.db.SecurityException;

/**
 * This interface is to be implemented by the controller class used in the model 
 * viewer controller design pattern for the presentation tier of the Booking 
 * application.  It contains several methods that must be implemented for the 
 * controller to capture the user gestures used in the presentation tier.
 * 
 * @author Robert Black
 * @version 1.0
 */
public interface BookingController {
    
    /**
     * A method that handles a user gesture called by the BookingView in 
     * response to the GUI or some other user interface by calling the 
     * searchContractors method in the BookingModel.
     * 
     * @param name the name search criteria.
     * @param location the location search criteria.
     * @throws RecordNotFoundException if the record is deleted or doesn't exist.
     * @throws SecurityException if the record is locked with a cookie other 
     * than lockCookie.
     */
    void handleSearchContractorsGesture(String name, String location) 
            throws RecordNotFoundException, SecurityException;
    
    /**
     * A method that handles a user gesture called by the BookingView in 
     * response to the GUI or some other user interface by calling the 
     * bookContractor method in the BookingModel.
     * 
     * @param recNo the record position number.
     * @param custNo the eight digit customer id.
     * @throws RecordNotFoundException if the record is deleted or doesn't 
     * exist.
     * @throws SecurityException if the record is locked with a cookie other 
     * than lockCookie.
     * @throws RecordAlreadyBookedException if the record already has a customer 
     * number stored in the owner field i.e. already booked.
     */
    void handleBookContractorGesture(long recNo, String custNo) 
            throws RecordNotFoundException, SecurityException, 
            RecordAlreadyBookedException;
    
    /**
     * A method that handles a user gesture called by the BookingView in 
     * response to the GUI or some other user interface by clicking the book
     * button which update the display action when booking a contractor.
     * 
     * @param recNo the record position number.
     * @throws RecordNotFoundException if the record is deleted or doesn't 
     * exist.
     */
    void handleCheckContractorBookedGesture(long recNo) 
            throws RecordNotFoundException;
    
    /**
     * This method handles the user gesture of selecting a record in the display 
     * table and calls the <code>storeRecordDetailsForBooking()</code> method 
     * for storing the record details for display in the 
     * <code>BookContractorDialog</code>.
     * 
     * @param custDetails a <coded>String[]</code> containing the record 
     * details.
     */
    void handlesTableSelectionGesture(String[] custDetails, Long recNo) 
            throws RecordNotFoundException;
    
    /**
     * A method that handles a user gesture called by the BookingView in 
     * response to the GUI or some other user interface by calling booking 
     * <code>bookingCanceled</code> method in the model.
     * 
     * @param cancel the cancel string object that equals "cancel".
     */
    void bookingCanceled(String cancel);
    
    /**
     * This method refreshes the displayed records by calling the 
     * <code>refeshDisplay()</code> in the model.
     */
    void refeshDisplayGesture() throws RecordNotFoundException;
    
}
