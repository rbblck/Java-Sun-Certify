package suncertify.presentation.gui;

import suncertify.presentation.BookingController;

/**
 * This interface is to be implemented by the GUI class used in the view of the  
 * model viewer controller design pattern for the presentation tier of the 
 * Booking application.  The implementation of this interface enables any GUI to 
 * be added to the view using the strategy design pattern and the methods within 
 * to interact with the model viewer controller architecture.
 * 
 * @author Robert Black
 * @version 1.0
 */
public interface BookingGui {
    
    /**
     * This method registers the <code>BookingController</code> so the 
     * <code>BookingGui</code> has a reference to the 
     * <code>BookingController</code>.
     * 
     * @param controller the <code>BookingController</code>.
     */
    void registerBookingController(BookingController controller);
    
    /**
     * This method to handles state change notifications from to the 
     * <code>BookingModel</code>.  The display will change depending on which 
     * object is passes to it.
     * 
     * @param obj the <code>Object</code>.
     */
    void display(Object obj);
    
}
