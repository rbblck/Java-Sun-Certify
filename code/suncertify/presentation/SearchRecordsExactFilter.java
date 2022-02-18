package suncertify.presentation;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import suncertify.db.Contractor;

/**
 * This class is used to filter records for the GUI as it requires exact matches 
 * to the search criteria.  It takes a <code>HashMap</code> which was returned 
 * from <code>BookingBusinessAdapterImp</code> class which in turn was returned 
 * from the <code>Data</code>the class and filers the <code>HashMap</code> and 
 * populates a new <code>HashMap</code> with record numbers and contractors 
 * objects using same search criteria but only populates the new 
 * <code>HashMap</code> with exact matches rather than matching the beginning 
 * part of the field being matched.
 * 
 * @author Robert Black
 * @version 1.0
 */
public class SearchRecordsExactFilter {
    
    /**
     * The name search criteria.
     */
    private String name;
    
    /**
     * The location search criteria.
     */
    private String location;

    /**
     * The constructor takes the search criteria for the GUI. A 
     * <code>String</code> name and <code>String</code> location.
     * 
     * @param name the name criteria.
     * @param location the location criteria.
     */
    public SearchRecordsExactFilter(String name, String location) {
        this.name = name;
        this.location = location;
    }
    
    /**
     * This method filters a <code>Map<Long, Contractor></code> to return a 
     * <code>Map<Long, Contractor></code> filtered to contain exact matches 
     * to the GUI search criteria.
     * 
     * @param contractors takes the un-filtered <code>Map<Long, Contractor></code>
     * @return the the filtered <code>Map<Long, Contractor></code>
     */
    public Map<Long, Contractor> filterResults(Map<Long, Contractor> contractors) {
        //This is populated with the filtered searched contractors to contain 
        //only the header for the table and the searched contractors using the 
        //exact search criteria.
        Map<Long, Contractor> contractorsExactMatch 
                = new HashMap<Long, Contractor>();
        
        //This populates the contractorsExactMatch with the display table header
        //information used to render the table headers.
        Long headersEntry = 0l;
        Contractor headerInfo = contractors.get(headersEntry);
        contractorsExactMatch.put(headersEntry, headerInfo);
        
        //This filters searched records from name and / or location starts with 
        //to exact matches and populates the contractorsExactMatch HashMap.
        for (Map.Entry<Long, Contractor> entry : contractors.entrySet()) {
            String searchedName = entry.getValue().getName().trim();
            String searchedLocation = entry.getValue().getLocation().trim();

            //This if statement checks to see if the user did not submit any
            //name or location search criteria.
            if (this.name.equals("") && this.location.equals("")) {
                //This if statement checks if each contractor is a valid one.
                contractorsExactMatch.put(entry.getKey(), entry.getValue());
            } else {
                //If criteria is submitted by the user, this if statement 
                //checks that only the name criteria has beem submitted.
                if (location.equals("")) {
                        Pattern namePattern 
                                = Pattern.compile(name, Pattern.CASE_INSENSITIVE);
                    Matcher nameMatcher = namePattern.matcher(searchedName);
                    //This if statement checks to see if the name matches exactly.
                    if (nameMatcher.matches()) {
                        //If the contractor matches, this entry is added to the 
                        //contractorsExactMatch HashMap.
                        contractorsExactMatch.put(
                                entry.getKey(), entry.getValue());
                    }
                //If criteria is submitted by the user, this if statement 
                //checks that only the location criteria has beem submitted.
                } else if (name.equals("")) {
                    Pattern locationPattern = Pattern.compile(
                        location, Pattern.CASE_INSENSITIVE);
                    Matcher locationMatcher 
                            = locationPattern.matcher(searchedLocation);
                    //This if statement checks to see if the location matches 
                    //exactly.
                    if (locationMatcher.matches()) {
                        //If the contractor matches, this entry is added to the 
                        //contractorsExactMatch HashMap.
                        contractorsExactMatch.put(
                                entry.getKey(), entry.getValue());
                    }
                //Else the user must have submitted name and location.
                } else {
                    Pattern namePattern 
                            = Pattern.compile(name, 
                            Pattern.CASE_INSENSITIVE);
                    Pattern locationPattern 
                            = Pattern.compile(location, 
                            Pattern.CASE_INSENSITIVE);
                    Matcher nameMatcher = namePattern.matcher(searchedName);
                    Matcher locationMatcher 
                            = locationPattern.matcher(searchedLocation);
                    //This if statement checks to see if the name and location 
                    //matches exactly.
                    if (nameMatcher.matches() && locationMatcher.matches()) {
                        //If the contractor matches, this entry is added to the 
                        //contractorsExactMatch HashMap.
                        contractorsExactMatch.put(
                                entry.getKey(), entry.getValue());
                    }
                }
            }
        }
        
        //The filtered Map<Long, Contractor>.
        return contractorsExactMatch;
    };
    
}
