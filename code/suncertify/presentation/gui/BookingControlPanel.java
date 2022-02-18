package suncertify.presentation.gui;

import suncertify.presentation.ApplicationRunner;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import suncertify.db.RecordNotFoundException;
import suncertify.presentation.BookingController;

/**
 * This class is used to preform operations on a record that has already been 
 * selected in the display table using it's various buttons.<br/><br/> 
 * 
 * It implements the <code>BookingPanel</code> interface so it can be added to 
 * the a <code>BookingGui</code>.
 * 
 * @author Robert Black
 * @version 1.0
 */
public class BookingControlPanel extends JPanel implements BookingPanel {
    
    /**
     * A version number for this class so that serialization can occur without 
     * worrying about the underlying class changing between serialization and 
     * de-serialization.
     */
    private static final long serialVersionUID = 2141157767239325679L;
    
    /**
     * Holds a reference to the <code>BookingController</code>.
     */
    private BookingController controller;
    
    /**
     * Holds the record number (file position).
     */
    private long recNo;
    
    /**
     * Holds a <code>String[]</code> with the contractor details to be displayed 
     * in the <code>BookContractorDialog</code> dialog.
     */
    private String[] contDetails;
    
    /**
     * Holds a reference to this containers parent.
     */
    private JFrame parent;
    
    /**
     * Holds a reference to the "Book" button.
     */
    private JButton bookButton;
    
    /**
     * Holds a reference to the <code>BookContractorDialog</code>.
     */
    private BookContractorDialog bookingDialog;

    /**
     * The default constructor calls the <code>init()</code> method to build the 
     * GUI in this panel and initialize its components.
     */
    public BookingControlPanel() {
        this.init();
    }
    
    /**
     * The <code>init()</code> method that builds the GUI in this panel and 
     * initialize its components.
     */
    private void init() {
        //Sets the border display.
        this.setBorder(BorderFactory.createTitledBorder("Record Operations"));
        
        //Initializes the parent field.
        this.parent = (JFrame) BookingControlPanel.this.getParent();
        
        //Initializes the bookButton field, adds an ActionListener to it and
        //add a tool tip to it.
        this.bookButton = new JButton("Book");
        this.bookButton.setToolTipText(
                "Click to book a contractor after you have searched and "
                + "selected a record in the display table.");
        this.bookButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    //Checks to see if the contDetails is null, if it is then a 
                    //record has not been selected in the display table and a 
                    //message dialog will be shown to prompt the user to select a
                    //record from the display table.
                    if (BookingControlPanel.this.contDetails == null) {
                        JOptionPane.showMessageDialog(
                                parent, 
                                "You must select a Contractor from the table.");
                    } else {
                        //Checks to see if the record is already booked, 
                        //depending on wether is is or not will determine the 
                        //GUI's next diplay.
                        BookingControlPanel.this.
                                controller.handleCheckContractorBookedGesture(recNo);
                    }
                } catch (RecordNotFoundException ex) {
                    ApplicationRunner.handleException(ex.getMessage());
                }
                
            }
            
        });
        
        //Adds the book button to the panel.
        this.add(this.bookButton);
    }

    /**
     * This method registers a reference to the controller so its methods can be 
     * called.
     * 
     * @param controller the <cod>BookingController</code>.
     */
    @Override
    public void registerBookingController(BookingController controller) {
        this.controller = controller;
    }

    /**
     * This method takes an <code>Object</code> and depending on what type, it 
     * will update the display in the panel.
     * 
     * @param obj the <code>Object</code>.
     */
    @Override
    public void display(Object obj) {
        //Checks and returns if the Object is null.
        if (obj == null) {
            return;
        }
        
        if (obj instanceof String[]) {
            //If the object is a string[] it will update the contract details.
            this.contDetails = (String[]) obj;
        }
        
        if (obj instanceof Long) {
            //If the object is a Long it will update the record number 
            //(file position).
            this.recNo = (Long) obj;
        }
        
        if (obj instanceof Boolean) {
            boolean booked = (Boolean) obj;
            if (booked) {
                try {
                    //Show a message and cancel the selcetion achtion.
                    JOptionPane.showMessageDialog(
                            parent, "This Contractor is already booked.");
                    this.controller.refeshDisplayGesture();
                } catch (RecordNotFoundException ex) {
                    ApplicationRunner.handleException(ex.getMessage());
                } catch (SecurityException ex) {
                    ApplicationRunner.handleException(ex.getMessage());
                }
            } else {
                //Creates a BookContractorDialog if not created already.
                if (bookingDialog == null) {
                    BookingControlPanel.this.bookingDialog 
                            = new BookContractorDialog(parent, controller);
                }

                //Sets the details for display in the BookContractorDialog
                //and displays it.
                BookingControlPanel.this.bookingDialog.setContDetails(
                        contDetails, recNo);
                BookingControlPanel.this.bookingDialog.setVisible(true);

                //Clears the contractor details and deselects the display 
                //panel for another selection when the dialog closes.
                BookingControlPanel.this.contDetails = null;
                BookingControlPanel.this.controller.bookingCanceled("cancel");
            }
        }
    }
    
}
