package suncertify.presentation;

import java.util.Map;
import suncertify.business.RecordAlreadyBookedException;
import suncertify.db.Contractor;
import suncertify.db.RecordNotFoundException;
import suncertify.db.SecurityException;

/**
 * This interface is to be implemented by the model class used in the model 
 * viewer controller design pattern for the presentation tier of the Booking 
 * application.  It contains several methods that must be implemented so that 
 * it can represent and manipulate the data contained in the database by 
 * interacting with the business tier of the application.
 * 
 * @author Robert Black
 * @version 1.0
 */
public interface BookingModel {
    
    /**
     * This allows the view part of the presentation tier to register itself 
     * as a listener so it can react to change events in this object.
     * 
     * @param view is the object requesting to be notified.
     */
    void addChangeListener(BookingView view);
    
    /**
     * Fires a change event to all registered listeners.
     */
    void fireChangeEvent(Object obj);
    
    /**
     * A method that handles a call from the <code>BookingController</code> that 
     * will call the <code>BookingBusinessAdapter</code> to search for records 
     * according to entered criteria ("name" and  / or "location".  A no entry 
     * value matches any field value. An entry will match any field value that 
     * begins with criteria entered e.g. "Fred" matches "Fred" or "Freddy"  
     * Records are returned as a <code>HashMap</code> containing a 
     * <code>Long</code> as the key which represents the record number 
     * (file position) and a <code>Contractor</code> object as the value which 
     * represents the record its self.
     * 
     * @param name the name search criteria.
     * @param location the location search criteria.
     * @return a <code>HashMap<Contractors, Long></code> of records numbers and 
     * <code>Contractor</code> objects.
     * @throws RecordNotFoundException  if the record is deleted or doesn't 
     * exist.
     * @throws SecurityException if the record is locked with a cookie other 
     * than lockCookie.
     */
    Map<Long, Contractor> searchContractors(String name, String location) 
            throws RecordNotFoundException;
    
    /**
     * A method that handles a call from the <code>BookingController</code> that 
     * will call the <code>BookingBusinessAdapter</code> to book the contractor 
     * record in question.
     * 
     * @param recNo the record number (file position).
     * @param custNo the eight digit customer id.
     * @throws RecordNotFoundException if the record is deleted or doesn't 
     * exist.
     * @throws SecurityException if the record is locked with a cookie other 
     * than lockCookie.
     * @throws RecordAlreadyBookedException if the record already has a customer 
     * number
     */
    void bookContractor(long recNo, String custNo) 
            throws RecordNotFoundException, SecurityException, 
            RecordAlreadyBookedException;
    
    /**
     * This method checks to see if a contractor has already been booked and 
     * sends a signal to the view so it can decided on the appropriate action.
     * 
     * @param recNo the record number (file position).
     * @return the Boolean object, false if not booked, true if booked.
     * @throws RecordNotFoundException if the record is deleted or doesn't 
     * exist.
     */
    Boolean isContractorBooked(long recNo) throws RecordNotFoundException;
    
    /**
     * This method will fire a change event and send the details of a selected 
     * record in the display table to the view for displaying in the 
     * <code>BookContractorDialog</code> when booking a contractor
     * 
     * @param custDetails a <coded>String[]</code> containing the record 
     * details.
     */
    void storeRecordDetailsForBooking(String[] custDetails, Long recNo);
    
    /**
     * This method deselects the table display of contractor records.
     * 
     * @param cancel a string that equals "cancel".
     */
    void bookingCanceled(String cancel);
    
    /**
     * This method refreshes the displayed records.
     * 
     * @throws RecordNotFoundException  if the record is deleted or doesn't 
     * exist.
     * @throws SecurityException if the record is locked with a cookie other 
     * than lockCookie.
     */
    void refeshDisplay() throws RecordNotFoundException;
    
    /**
     * This method safely shuts down the program.
     */
    void exitProgram();
    
}
