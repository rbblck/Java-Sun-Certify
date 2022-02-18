package suncertify.business;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import suncertify.db.Contractor;
import suncertify.db.DBAccess;
import suncertify.db.RecordNotFoundException;
import suncertify.db.SecurityException;

/**
 * This is a business logic class that implements the BookingBusinessAdapter 
 * interface that contains the various transaction scripts determined by the 
 * identified use cases e.g. book contractor.
 * 
 * @author Robert Black
 * @version 1.0
 */
public class BookingBusinessAdapterImp implements BookingBusinessAdapter {
    
    /**
     * Holds an instance of <code>DBAccess</code> class.
     */
    private DBAccess dataAccess;

    /**
     * The default constructor that take a <code>DBAccess</code> object.
     * 
     * @param dataAccess the <code>DBAccess</code> instance.
     * @throws FileNotFoundException if the database file cannot be found.
     * @throws IOException if the database file cannot be read or written to.
     */
    public BookingBusinessAdapterImp(DBAccess dataAccess) 
            throws FileNotFoundException, IOException {
        this.dataAccess = dataAccess;
    }

    /**
     * Populates an HashMap of <code>Long</code> record numbers (file positions) 
     * and <code>Contractors</code> records when search criteria is entered.
     * 
     * @param name the name search criteria.
     * @param location the location search criteria.
     * @return a <code>HashMap<Contractors, Long></code> of records numbers and 
     * <code>Contractor</code> objects.
     * @throws RecordNotFoundException if the record is deleted or doesn't exist.
     * @throws SecurityException if the record is locked with a cookie other 
     * than lockCookie.
     */
    @Override
    public Map<Long, Contractor> searchContractors(String name, String location) 
            throws RecordNotFoundException {
        
        //Ctreats and array to submit to the dataAccess findByCriteria(criteria)
        //method.
        String[] criteria = new String[2];
        if (name.equals("")) {
            criteria[0] = null;
        } else {
            criteria[0] = name;
        }
        if (location.equals("")) {
            criteria[1] = null;
        } else {
            criteria[1] = location;
        }
        
        //Create a HashMap the collect the searched contractor records.
        Map<Long, Contractor> contractors = new HashMap<Long, Contractor>();
        
        //Retrieve the contractor numbers that match the search criteria.
        long[] recNos = dataAccess.findByCriteria(criteria);
        
        //if (recNos)
        //Populates the hashMap with the record numbers and Contractor objets.
        for (long recNo : recNos) {
            Contractor contractor = null;
            contractor = new Contractor(dataAccess.readRecord(recNo));
            contractors.put(recNo, contractor);
        }
        
        //returns the HashMap.
        return contractors;
    }

    /**
     * Books a particular Contractor using the combined name and location field 
     * as the primary key to locate the record, and an eight digit customer 
     * number to update the owner field.
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
            throws SecurityException, RecordAlreadyBookedException, 
            RecordNotFoundException {
        long cookie = 0l;
        try {
            //Lock the record and store the record lock cookie.
            cookie = dataAccess.lockRecord(recNo);
            
            //Checks to see if the record is already booked, if it is a 
            //RecordAlreadyBookedException is thrown.
            Contractor contractor = new Contractor(dataAccess.readRecord(recNo));
            if (!(contractor.getOwner().equals(""))) {
                throw new RecordAlreadyBookedException("The contractor is "
                        + "already booked");
            } else {
                //Create a data String array and adds the customer number to the 
                //relevant slot in the array used to update a records owner field.
                String[] data = new String[6];
                data[5] = custNo;

                //Updates the record.
                dataAccess.updateRecord(recNo, data, cookie);
            }
        } finally {
            //This will make sure that the record is unlocked preventing 
            //deadlocks incase of any problems such as e.g. IO failure, etc.
            dataAccess.unlock(recNo, cookie);
        }
    }

    /**
     * Checks to see if a particular contractor record has already been booked.
     * i.e. has a record has an eight digit customer number in the owner field.
     * 
     * @param recNo the record number (file position) to be checked.
     * @throws RecordNotFoundException if the record is deleted or doesn't 
     * exist.
     */
    @Override
    public boolean isContractorBooked(long recNo) throws RecordNotFoundException {
        String[] record = this.dataAccess.readRecord(recNo);
        if (record[5].trim().equals("")) {
            return false;
        } else {
            return true;
        }
    }
    
}
