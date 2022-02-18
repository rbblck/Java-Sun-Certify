package suncertify.presentation;

import suncertify.business.RecordAlreadyBookedException;
import suncertify.db.RecordNotFoundException;
import suncertify.db.SecurityException;

/**
 * This class implements the BookingController interface and therefore must 
 * implement the methods in this interface.  The class is responsible for 
 * capturing the users gestures in the view part of the model-viewer-controller 
 * design pattern.  This class encapsulates the presentation behavior and 
 * methods in the model will always be invoked from this class.<br/><br/>
 * 
 * The controller registers itself as a listener in the view.
 * 
 * @author Robert Black
 * @version 1.0
 */
public class BookingControllerImp implements BookingController {
    
    /**
     * Holds a reference to the model.
     */
    private BookingModel model;
    
    /**
     * Holds a reference to the view.
     */
    private BookingView view;

    /**
     * The constructor takes a <code>BookingModel</code> and a 
     * <code>BookingModel</code>, then initializes the model and view variables 
     * and registers itself with the view as a listener.
     * 
     * @param model
     * @param view 
     */
    public BookingControllerImp(BookingModel model, BookingView view) {
        //Initializes the variables.
        this.model = model;
        this.view = view;
        
        //Registering itself with the view.
        this.view.addUserGestureListener(BookingControllerImp.this);
    }

    /*
     * A method that handles a user gesture called by the BookingView in 
     * responce to the GUI or some other user interface by calling the 
     * searchContractors method in the BookingModel.
     * 
     * @param name the name search criteria.
     * @param location the location search criteria.
     * @throws RecordNotFoundException if the record is deleted or doesn't exist.
     * @throws SecurityException if the record is locked with a cookie other 
     * than lockCookie.
     */
    @Override
    public void handleSearchContractorsGesture(String name, String location) 
            throws RecordNotFoundException, SecurityException {
        model.searchContractors(name, location);
    }

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
    @Override
    public void handleBookContractorGesture(long recNo, String custNo) 
            throws RecordNotFoundException, SecurityException, 
            RecordAlreadyBookedException {
        model.bookContractor(recNo, custNo);
    }

    /**
     * A method that handles a user gesture called by the BookingView in 
     * response to the GUI or some other user interface by clicking the book
     * button which update the display action when booking a contractor.
     * 
     * @param recNo the record position number.
     * @throws RecordNotFoundException if the record is deleted or doesn't 
     * exist.
     */
    @Override
    public void handleCheckContractorBookedGesture(long recNo) 
            throws RecordNotFoundException{
        this.model.isContractorBooked(recNo);
    }
    
    /**
     * This method handles the user gesture of selecting a record in the display 
     * table and calls the <code>storeRecordDetailsForBooking()</code> method 
     * for storing the record details for display in the 
     * <code>BookContractorDialog</code>.
     * 
     * @param custDetails a <coded>String[]</code> containing the record 
     * details.
     */
    @Override
    public void handlesTableSelectionGesture(String[] custDetails, Long recNo) 
            throws RecordNotFoundException {
        model.storeRecordDetailsForBooking(custDetails, recNo);
    }

    /**
     * A method that handles a user gesture called by the BookingView in 
     * response to the GUI or some other user interface by calling booking 
     * canceled gesture
     * 
     * @param cancel the cancel string object.
     */
    @Override
    public void bookingCanceled(String cancel) {
        this.model.bookingCanceled(cancel);
    }
   
    /**
     * This method refreshes the displayed records by calling the 
     * <code>refeshDisplay()</code> in the model.
     */
    @Override
    public void refeshDisplayGesture() throws RecordNotFoundException {
        model.refeshDisplay();
    }
    
}
