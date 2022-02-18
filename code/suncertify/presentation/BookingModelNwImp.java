package suncertify.presentation;

import suncertify.business.network.commands.BookContractorCommand;
import suncertify.business.network.commands.CheckContractorBookedCommand;
import suncertify.business.network.commands.Command;
import suncertify.business.network.commands.GetSearchResultsCommand;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import suncertify.business.RecordAlreadyBookedException;
import suncertify.business.network.client.BookingNwClient;
import suncertify.db.Contractor;
import suncertify.db.RecordNotFoundException;

/**
 * This class implements the <code>BookingModel</code> interface and is used 
 * to represent the <code>BookingModelImp</code> class on the server side.
 * 
 * @author Robert Black
 * @version 1.0
 */
public class BookingModelNwImp implements BookingModel {
    
    /**
     * Holds an ArrayList that collects listeners (Observers) of this class the 
     * subject.
     */
    private List<BookingView> changeListeners = new ArrayList<BookingView>();
    
    /**
     * Stores the last name search criteria entered, therefore, remembering the 
     * criteria when update event has occurred.
     */
    private String name;
    
    /**
     * Stores the last location search criteria entered, therefore, remembering 
     * the criteria when update event has occurred.
     */
    private String location;
    
    /**
     * Holds an instance of a <code>BookingNwClient</code> object.  The network 
     * client.
     */
    private final BookingNwClient nwClient;
    
    /**
     * The default constructor that take a <code>DBAccess</code> object.
     * 
     * @param nwClient the network client.
     */
    public BookingModelNwImp(BookingNwClient nwClient) {
        this.nwClient = nwClient;
    }

    /**
     * This method adds a <code>BookingView</code> to the list of listeners 
     * (Observers).
     * 
     * @param view a listener (<code>BookingView</code>).
     */
    @Override
    public void addChangeListener(BookingView view) {
        this.changeListeners.add(view);
    }

    /**
     * This method notifies all listeners (observers) when an event has happened 
     * in this class (subject).
     * 
     * @param obj the object that this event has passed.
     */
    @Override
    public void fireChangeEvent(Object obj) {
        for (BookingView view : this.changeListeners) {
            view.showDisplay(obj);
        }
    }

    /**
     * This method creates a HashMap of record numbers and contractor records, 
     * then notifies all listeners as well as returning the HashMap. Due to 
     * 
     * @param name the name search criteria.
     * @param location the location search criteria.
     * @return the HashMap with the search results.
     * @throws RecordNotFoundException if the record is deleted or doesn't exist.
     * @throws SecurityException if the record is locked with a cookie other 
     * than lockCookie.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<Long, Contractor> searchContractors(String name, String location) 
            throws RecordNotFoundException, SecurityException {
        //Create a HashMap to hold searches contractor records.
        Map<Long, Contractor> contractors = new HashMap<Long, Contractor>();
        
        //Updates the cached name and location search criteria.
        this.name = name;
        this.location = location;
        
        //Creates a filter so the contractors become matched exatly to the 
        //search criteria.
        SearchRecordsExactFilter filterContractors 
                = new SearchRecordsExactFilter(this.name, this.location);
        
        //Creates a Command  and Object variables.
        Command cmd;
        Object result;
        try {
            //Creates a GetSearchResultsCommand and sends it to the network 
            //client.
            cmd = new GetSearchResultsCommand(name, location);
            this.nwClient.send(cmd);
            
            //Recieve the executed GetSearchResultsCommand from the network 
            //client.
            Object obj = BookingModelNwImp.this.nwClient.receive();
            cmd = (Command) obj;
            
            //Extract the result object from the executed GetSearchResultsCommand 
            //object.  This throws and exception if the Command object holds an 
            //exception object.
            result = cmd.result();
            
            //Check the result object is not null and is a Map.
            if (result != null && result instanceof Map) {
                contractors = (Map<Long, Contractor>) result;
                
                //Filters the records.
                contractors = filterContractors.filterResults(contractors);
                
                //Notify all interested listeners passing the filtered HashMap.
                BookingModelNwImp.this.fireChangeEvent(contractors);
            }
        } catch (Exception ex) {
            if (ex instanceof RecordNotFoundException) {
                throw (RecordNotFoundException) ex;
            } else if (ex instanceof SecurityException) {
                throw (SecurityException) ex;
            }
        }
        
        //Returns the filtered HashMap containing the searched contractors.
        return contractors;
    }

    /**
     * This method books a contractor and creates a HashMap of record numbers 
     * and contractor records.  Then notifies all listeners passing the updated 
     * HashMap of contractors.
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
    public void bookContractor(long recNo, String custNo) 
            throws RecordNotFoundException, SecurityException, 
            RecordAlreadyBookedException {
        
        //Creates a Command  and Object variables.
        Command cmd;
        Object result;
        try {
            //Creates a BookContractorCommand and sends it to the network client.
            cmd = new BookContractorCommand(recNo, custNo);
            nwClient.send(cmd);
            
            //Recieve the executed BookContractorCommand from the network client.
            cmd = (Command) nwClient.receive();
            
            //No expectation to have an object returned from the 
            //BookContractorCommand object but it throws and exception if the 
            //Command object holds an exception object.
            result = cmd.result();
        } catch (Exception ex) {
            if (ex instanceof RecordNotFoundException) {
                throw (RecordNotFoundException) ex;
            }
            if (ex instanceof SecurityException) {
                throw (SecurityException) ex;
            }
            if (ex instanceof RecordAlreadyBookedException) {
                throw (RecordAlreadyBookedException) ex;
            }
        }
        
        //Refreshes the display in the display table.
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
        //The boolean to be returned.
        Boolean booked = null;
        
        //Creates a Command  and Object variables.
        Command cmd;
        Object result;
        try {
            //Creates a CheckContractorBookedCommand and sends it to the network 
            //client.
            cmd = new CheckContractorBookedCommand(recNo);
            this.nwClient.send(cmd);
            
            //Recieve the executed CheckContractorBookedCommand from the network 
            //client.
            Object obj = BookingModelNwImp.this.nwClient.receive();
            cmd = (Command) obj;
            
            //Extract the result object from the executed 
            //CheckContractorBookedCommand object.  This throws and exception 
            //if the Command object holds an exception object.
            result = cmd.result();
            
            //Check the result object is not null and is a Map.
            if (result instanceof Boolean) {
                booked = (Boolean) result;
                
                //Notify all interested listeners passing the HashMap.
                BookingModelNwImp.this.fireChangeEvent(booked);
            }
        } catch (Exception ex) {
            if (ex instanceof RecordNotFoundException) {
                throw (RecordNotFoundException) ex;
            }
        }
        
        //Returns the HashMap containing the searched contractors.
        return booked;
    }

    /**
     * This method takes a <code>String[]</code> containing details of a 
     * particular contractor record and notifies all interested listeners.
     * 
     * @param custDetails a <code>String[]</code> details of the contractor 
     * record.
     * @param recNo the record number (file position) of the record.
     */
    @Override
    public void storeRecordDetailsForBooking(String[] custDetails, Long recNo) {
        //Notifies all listeners.
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

    @Override
    public void refeshDisplay() throws RecordNotFoundException, 
            SecurityException {
        //Create a HashMap to hold searches contractor records.
        Map<Long, Contractor> contractors = new HashMap<Long, Contractor>();
        contractors = this.searchContractors(name, location);
        
        //Notifies listeners with an updated HashMap.
        this.fireChangeEvent(contractors);
    }

    /**
     * This method closes the application safely.
     */
    @Override
    public void exitProgram() {
        this.nwClient.close();
        System.exit(0);
    }
    
}
