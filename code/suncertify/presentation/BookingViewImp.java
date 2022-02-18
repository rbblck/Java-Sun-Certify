package suncertify.presentation;

import suncertify.presentation.gui.BookingGui;

/**
 * This class implements the <code>BookingView</code> and therefore must 
 * implement the methods in this interface. It is used in the model viewer 
 * controller design pattern for the presentation tier of the Booking 
 * application.  It contains several methods that renders the data contained or 
 * represented by the model using any choice of GUI that implements 
 * <code>BookingGui</code>.
 * 
 * @author Robert Black
 * @version 1.0
 */
public class BookingViewImp implements BookingView {
    
    /**
     * Holds a reference to the <code>BookingGui</code>.
     */
    private BookingGui gui;

    /**
     * The constructor takes a <code>BookingModel</code> to add this instance of 
     * <code>BookingView</code> and a <code>BookingGui</code> to initialize the 
     * <code>gui</code> variable giving reference to the <code>BookingGui</code> 
     * used.
     *
     * @param model the <code>BookingModel</code>
     * @param gui the <code>BookingGui</code>
     */
    public BookingViewImp(BookingModel model, BookingGui gui) {
        model.addChangeListener(BookingViewImp.this);
        this.gui = gui;
    }

    /**
     * Adds a requester to a list of objects to be notified of user gestures 
     * entered through a user interface i.e. <code>BookingGui</code>.
     * 
     * @param controller 
     */
    @Override
    public void addUserGestureListener(BookingController controller) {
        this.gui.registerBookingController(controller);
    }
    
    /**
     * This method to handles state change notifications from to the 
     * <code>BookingModel</code>.
     * 
     * @param obj and object that is passed to the GUI to change the 
     * display.
     */
    @Override
    public void showDisplay(Object obj) {
        this.gui.display(obj);
    }
    
}
