package suncertify.presentation;

/**
 * This interface is to be implemented by the view class used in the model 
 * viewer controller design pattern for the presentation tier of the Booking 
 * application.  It contains several methods that must be implemented so that 
 * it can render the data contained or represented by the model using any choice 
 * of GUI that implements <code>BookingGui</code>.
 * 
 * @author Robert Black
 * @version 1.0
 */
public interface BookingView {
    
    /**
     * Adds a requester to a list of objects to be notified of user gestures 
     * entered through a user interface i.e. <code>BookingGui</code>.
     * 
     * @param controller 
     */
    void addUserGestureListener(BookingController controller);
    
    /**
     * This method to handles state change notifications from to the 
     * <code>BookingModel</code>.
     * 
     * @param display and object that is passed to the GUI to change the 
     * display.
     */
    void showDisplay(Object display);
    
}
