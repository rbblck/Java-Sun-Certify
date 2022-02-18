package suncertify.presentation.gui;

import suncertify.presentation.BookingController;

/**
 * This interface is to be implemented by any panel or dialog that is used in 
 * the GUI.  It enables the implementing class to be added to a <code>List</code>
 * of <code>BookingPanel</code>.  It ensures that the appropriate methods are 
 * implemented and are called when this collection is traversed.<br/><br/>
 * 
 * Note: it is only required for panels that interact with the model viewer 
 * controller architecture.
 * 
 * @author Robert Black
 * @version 1.0
 */
public interface BookingPanel {
    
    /**
     * This method registers a reference to the controller so its methods can be 
     * called.
     * 
     * @param controller the <cod>BookingController</code>.
     */
    void registerBookingController(BookingController controller);
    
    /**
     * This method takes an <code>Object</code> and depending on what type, it 
     * will update the display in the panel.
     * 
     * @param obj the <code>Object</code>.
     */
    void display(Object obj);
    
}
