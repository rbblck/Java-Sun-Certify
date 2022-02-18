package suncertify.business;

import java.util.Map;
import suncertify.db.Contractor;
import suncertify.db.RecordNotFoundException;
import suncertify.db.SecurityException;

/**
 * This is a business logic interface that contains the various transaction 
 * scripts determined by the identified use cases e.g. book contractor.
 * 
 * @author Robert Black
 * @version 1.0
 */
public interface BookingBusinessAdapter {
    
    /**
     * Populates an HashMap of <code>Long</code> record numbers (file positions) 
     * and <code>Contractors</code> records when search criteria is entered and 
     * returns it.
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
     * Books a particular Contractor using the combined name and location field 
     * as the primary key to locate the record, and an eight digit customer 
     * number to update the owner field.
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
     * Checks to see if a particular contractor record has already been booked.
     * i.e. has a record has an eight digit customer number in the owner field.
     * 
     * @param recNo the record number (file position) to be checked.
     * @return true is it is booked or false if not.
     * @throws RecordNotFoundException if the record is deleted or doesn't 
     * exist.
     */
    boolean isContractorBooked(long recNo) throws RecordNotFoundException;
    
}
