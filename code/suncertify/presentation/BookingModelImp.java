package suncertify.presentation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import suncertify.business.BookingBusinessAdapter;
import suncertify.business.RecordAlreadyBookedException;
import suncertify.db.Contractor;
import suncertify.db.RecordNotFoundException;
import suncertify.db.SecurityException;

/**
 * This class implements the <code>BookingModel</code> and therefore must 
 * implement the methods in this interface. It is used in the model viewer 
 * controller design pattern for the presentation tier of the Booking 
 * application.  It contains several methods that must be implemented so that it 
 * can represent and manipulate the data contained in the database by 
 * interacting with the business tier of the application.
 * 
 * @author Robert Black
 * @version 1.0
 */
public class BookingModelImp implements BookingModel {
    
    /**
     * Holds a list of registered listeners.
     */
    private List<BookingView> changeListeners = new ArrayList<BookingView>();
    
    /**
     * Holds the <code>BookingBusinessAdapter</code> reference.
     */
    private BookingBusinessAdapter bookingAdapter;
    
    /**
     * Holds the cached name search criteria.
     */
    private String name;
    
    /**
     * Holds the cached location search criteria.
     */
    private String location;

    /**
     * the constructor takes a <code>BookingBusinessAdapter</code> and 
     * initializes the <code>bookingAdapter</code> variable.
     * 
     * @param bookingAdapter the <code>BookingBusinessAdapter</code>.
     */
    public BookingModelImp(BookingBusinessAdapter bookingAdapter) {
        this.bookingAdapter = bookingAdapter;
    }

    /**
     * This allows the view part of the presentation tier to register itself 
     * as a listener so it can react to change events in this object.
     * 
     * @param view is the object requesting to be notified.
     */
    @Override
    public void addChangeListener(BookingView view) {
        this.changeListeners.add(view);
    }
    
    /**
     * Fires a change event to all registered listeners.
     */
    @Override
    public void fireChangeEvent(Object obj) {
        for (BookingView view : this.changeListeners) {
            view.showDisplay(obj);
        }
    }

    /**
     * A method that handles a call from the <code>BookingController</code> that 
     * will call the <code>BookingBusinessAdapter</code> to search for records 
     * according to entered criteria ("name" and  / or "location").  A no entry 
     * value matches any field value. An entry will match any field value that 
     * exactly matches the "name" and  / or "location" fields. Records are 
     * returned as a <code>HashMap</code> containing a <code>Long</code> as the 
     * key which represents the record number (file position) and a 
     * <code>Contractor</code> object as the value which represents the record 
     * its self.
     * 
     * @param name the name search criteria.
     * @param location the location search criteria.
     * @return a <code>HashMap<Contractors, Long></code> of records numbers and 
     * <code>Contractor</code> objects.
     * @throws RecordNotFoundException if the record is deleted or doesn't 
     * exist.
     * @throws SecurityException if the record is locked with a cookie other 
     * than lockCookie.
     */
    @Override
    public Map<Long, Contractor> searchContractors(String name, String location) 
            throws RecordNotFoundException {
        //This is populated with the searched contractors useing the Data file.
        Map<Long, Contractor> contractors = new HashMap<Long, Contractor>();
        
        //updates the name and location cache.
        this.name = name;
        this.location = location;
        
        //Creates a filter so the contractors become matched exatly to the 
        //search criteria.
        SearchRecordsExactFilter filterContractors 
                = new SearchRecordsExactFilter(this.name, this.location);
        
        //Returns the unfiltered search with records that begin with the search 
        //criteria.
        contractors 
                = this.bookingAdapter.searchContractors(this.name, this.location);
        
        //Filters the records.
        contractors = filterContractors.filterResults(contractors);
        
        //Notifies all listeners
        this.fireChangeEvent(contractors);
        
        //Returns the filtered Map<Long, Contractor>
        return contractors;
    }

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
    @Override
    public void bookContractor(long recNo, String custNo) 
            throws RecordNotFoundException, SecurityException, 
            RecordAlreadyBookedException {
        this.bookingAdapter.bookContractor(recNo, custNo);
        this.refeshDisplay();
    }

    /**
     * This method checks to see if a contractor has already been booked and 
     * sends a signal to the view so it can decided on the appropriate action.
     * 
     * @param recNo the record number (file position).
     * @return the Boolean object, false if not booked, true if booked.
     * @throws RecordNotFoundException if the record is deleted or doesn't 
     * exist.
     */
    @Override
    public Boolean isContractorBooked(long recNo) throws RecordNotFoundException {
        Boolean booked = this.bookingAdapter.isContractorBooked(recNo);
        this.fireChangeEvent(booked);
        return booked;
    }
    
    /**
     * This method will fire a change event and send the details of a selected 
     * record in the display table to the view for displaying in the 
     * <code>BookContractorDialog</code> when booking a contractor
     * 
     * @param custDetails a <coded>String[]</code> containing the record 
     * details.
     */
    @Override
    public void storeRecordDetailsForBooking(String[] custDetails, Long recNo) {
        this.fireChangeEvent(custDetails);
        this.fireChangeEvent(recNo);
    }

    /**
     * This method deselects the table display of contractor records.
     * 
     * @param cancel 
     */
    @Override
    public void bookingCanceled(String cancel) {
        this.fireChangeEvent(cancel);
    }

    /**
     * This method refreshes the displayed records.
     * 
     * @throws RecordNotFoundException  if the record is deleted or doesn't 
     * exist.
     * @throws SecurityException if the record is locked with a cookie other 
     * than lockCookie.
     */
    @Override
    public void refeshDisplay() 
            throws RecordNotFoundException {
        //Create a HashMap to hold searches contractor records.
        Map<Long, Contractor> contractors = new HashMap<Long, Contractor>();
        contractors = this.searchContractors(this.name, this.location);
        
        //Notifies listeners with an updated HashMap.
        this.fireChangeEvent(contractors);
    }

    /**
     * This method safely shuts down the program.
     */
    @Override
    public void exitProgram() {
        System.exit(0);
    }
    
}
