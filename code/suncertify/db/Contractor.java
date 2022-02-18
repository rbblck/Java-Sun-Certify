package suncertify.db;

import java.io.Serializable;

/**
 * This class is used to represent a database contractor record.  I can hold all
 * the field data in the record and be used to extract individual field data or 
 * a <code>String[]</code> holding the database record field data.  I can also 
 * hold a record marked for deletion if needed.
 * 
 * @author Robert Black
 * @version 1.0
 */
public class Contractor implements Serializable {
    
    /**
     * A version number for this class so that serialization can occur
     * without worrying about the underlying class changing between
     * serialization and de-serialization.
     */
    private static final long serialVersionUID = -2996538168260361943L;
    
    /**
     * The size of the field for containing the flag to indicate whether the
     * record is marked deleted or valid.
     */
    public static final int FLAG_FIELD_LENGTH = 2;
    
    /**
     * The size of the Name field for the contractor.
     */
    public static final int NAME_FIELD_LENGTH = 32;
    
    /**
     * The size of the Location field for the contractor.
     */
    public static final int LOCATION_FIELD_SIZE = 64;
    
    /**
     * The size of the Specialities field for the contractor.
     */
    public static final int SPECIALITIES_FIELD_SIZE = 64;
    
    /**
     * The size of the Size field for the contractor.
     */
    public static final int SIZE_FIELD_SIZE = 6;
    
    /**
     * The size of the Rate field for the contractor.
     */
    public static final int RATE_FIELD_SIZE = 8;
    
    /**
     * The size of the Owner field for the contractor.
     */
    public static final int OWNER_FIELD_SIZE = 8;
    
    /**
     * The size of a complete record in the database. Calculated by adding all
     * the previous fields together. Knowing this makes it easy to work with
     * an entire block of data at a time (rather than reading individual
     * fields), reducing the time needed to block on database access.
     */
    public static final int RECORD_LENGTH = NAME_FIELD_LENGTH 
            + LOCATION_FIELD_SIZE 
            + SPECIALITIES_FIELD_SIZE 
            + SIZE_FIELD_SIZE 
            + RATE_FIELD_SIZE 
            + OWNER_FIELD_SIZE;
    
    /**
     * Stores the flag field of the contractor record.
     */
    private int flag = 00;
    
    /**
     * Stores the name field of the contractor record as a single
     * <code>short</code> containing first and last name.
     */
    private String name = "";
    
    /**
     * Stores the location field of the contractor record as a single
     * <code>String</code> containing the location of the contractor.
     */
    private String location = "";
    
    /**
     * Stores the specialties field of the contractor record as a single
     * <code>String</code> containing a comma separated list of speciality 
     * services that the contractor can offer.
     */
    private String specialties = "";
    
    /**
     * Stores the size field of the contractor record as a single
     * <code>String</code> containing the number of employees the contractor
     * has.
     */
    private String size = "";
    
    /**
     * Stores the rate field of the contractor record as a single
     * <code>String</code> containing the charge of the contractor 
     * including the currency symbol.
     */
    private String rate = "";
    
    /**
     * Stores the owner field of the contractor record as a single
     * <code>String</code> containing the 8 digit customer id number. The 
     * contractor is considered booked if customer id is present.
     */
    private String owner = "";

    /**
     * Creates an instance of this object with default values.
     */
    public Contractor() {
    }

    /**
     * Creates an instance of this object with a specified list of
     * initial values. Assumes that the record is not deleted.
     * 
     * @param name holds the name of the contractor.
     * @param location holds the location of the contractor.
     * @param specialties holds the specialties of the contractor.
     * @param size holds the number of employees of the contractor.
     * @param rate holds the hourly rate of the contractor.
     * @param owner holds the 8 digit customer id if booked.
     */
    public Contractor(String name, String location, String specialties, 
            String size, String rate, String owner) {
        this (00, name, location, specialties, size, rate, owner);
    }
    
    /**
     * Creates an instance of this object with an array of initial values. 
     * Assumes that the record is not deleted.
     * 
     * @param fields a <code>String[]</code> containing name, location, 
     * specialties, size, rate, owner elements Respectively.
     */
    public Contractor(String[] fields) {
        this (00, fields[0], fields[1], fields[2], fields[3], fields[4], 
                fields[5]);
    }
    
    /**
     * Creates an instance of this object with the the flag indicator 
     * (deleted - 0x8000, not deleted - 00) and an array of initial values.
     * 
     * @param flag
     * @param fields 
     */
    public Contractor(int flag, String[] fields) {
        this (flag, fields[0], fields[1], fields[2], fields[3], fields[4], 
                fields[5]);
    }
    
    /**
     * * Creates an instance of this object with a specified list of initial
     * values.
     * 
     * @param flag holds the flag indicator (deleted - 0x8000, not deleted - 00).
     * @param name holds the name of the contractor.
     * @param location holds the location of the contractor.
     * @param specialties holds the specialties of the contractor.
     * @param size holds the number of employees of the contractor.
     * @param rate holds the hourly rate of the contractor.
     * @param owner holds the 8 digit customer id if booked.
     */
    public Contractor(int flag, String name, String location, String specialties, 
            String size, String rate, String owner) {
        this.flag = flag;
        this.name = name;
        this.location = location;
        this.specialties = specialties;
        this.size = size;
        this.rate = rate;
        this.owner = owner;
    }

    /**
     * Returns the deleted / not deleted indicator flag.
     * @return the flag.
     */
    public int getFlag() {
        return flag;
    }

    /**
     * Sets the deleted / not deleted indicator flag.
     * @param flag the flag.
     */
    public void setFlag(int flag) {
        this.flag = flag;
    }

    /**
     * Returns the name of the contractor.
     * @return the contractors name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the contractors name.
     * @param name the contractors name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the location of the contractor.
     * @return the contractors location.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the contractors location.
     * @param location the contractors location.
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Returns the specialties of the contractor.
     * @return the contractors specialties.
     */
    public String getSpecialties() {
        return specialties;
    }

    /**
     * Sets the contractors specialties.
     * @param specialties the contractors specialties.
     */
    public void setSpecialties(String specialties) {
        this.specialties = specialties;
    }

    /**
     * Returns the number of employees of the contractor.
     * @return the contractors number of employees.
     */
    public String getSize() {
        return size;
    }

    /**
     * Sets the contractors number of employees.
     * @param size the contractors number of employees.
     */
    public void setSize(String size) {
        this.size = size;
    }

    /**
     * Returns the hourly rate of the contractor.
     * @return the contractors hourly rate.
     */
    public String getRate() {
        return rate;
    }

    /**
     * Sets the contractors hourly rate.
     * @param rate the contractors hourly rate.
     */
    public void setRate(String rate) {
        this.rate = rate;
    }

    /**
     * Returns the customer id used to book the contractor.
     * @return the customer id used to book the contractor.
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Sets the customer id used to book the contractor.
     * @param owner the customer id used to book the contractor.
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }
    
    /**
     * This method returns a <code>String[]</code> which contains the fields of 
     * the contractor record that this object represents.
     * 
     * @return the <code>String[]</code> which contains the fields of the 
     * contractor record that this object represents.
     */
    public String[] getStringArrayData() {
        String[] contractorFields = new String[6];
        contractorFields[0] = this.getName();
        contractorFields[1] = this.getLocation();
        contractorFields[2] = this.getSpecialties();
        contractorFields[3] = this.getSize();
        contractorFields[4] = this.getRate();
        contractorFields[5] = this.getOwner();
        return contractorFields;
    }
    
    /**
     * Checks whether two Contractor objects are the same by comparing their
     * name and location. Since combination of the name and location is 
     * considered the unique primary key for every Contractor, if the two name 
     * and location combinations are the same, the two Contractor records must 
     * be the same record.
     * 
     * @param aContractor the Contractor to compare with this Contractor
     * @return true if this Contractor and the other Contractor have the same 
     * name and location.
     */
    @Override
    public boolean equals(Object aContractor) {
        //A test to see if the objects are identical.
        if (this == aContractor) {
            return true;
        }
        
        //Must return false if the other object is null.
        if (aContractor == null) {
            return false;
        }
        
        //Must return false if the classes don't match.
        if (this.getClass() != aContractor.getClass()) {
            return false;
        }
        
        //Now we know that aContractor is a non null Contractor.
        Contractor otherContractor = (Contractor) aContractor;
        
        //Test whether the name and location fields are identical
        return this.name.equals(otherContractor.name) 
                && this.location.equals(otherContractor.location);
    }

    /**
     * Returns a <code>hashcode</code> for this Contractor object that should be 
     * reasonably unique amongst Contractor objects. As with the 
     * <code>equals</code> method, we know that the name and location combination 
     * will be unique amongst Contractor records, so all we need do is return 
     * the name and location combination <code>hashcode</code>.
     *
     * @return the <code>hashcode</code> for this instance of Contractor.
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 13 * hash + (this.location != null ? this.location.hashCode() : 0);
        return hash;
    }

    /**
     * Returns a <code>String</code> representation of the Contractor class.
     * @return contractor <code>String</code> representation of the Contractor 
     * class.
     */
    @Override
    public String toString() {
        String contractor = "[" 
                + this.getName()  + "; " 
                + this.getLocation() + "; " 
                + this.getSpecialties() + "; " 
                + this.getSize() + "; " 
                + this.getRate() + "; " 
                + this.getOwner();
        if (this.getFlag() == 0x8000) {
            contractor += "; Deleted.]";
        } else {
            contractor += "; Valid.]";
        }
        return contractor;
    }
}