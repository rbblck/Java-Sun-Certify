package suncertify.presentation.gui;

import suncertify.presentation.ApplicationRunner;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import suncertify.business.RecordAlreadyBookedException;
import suncertify.db.RecordNotFoundException;
import suncertify.db.SecurityException;
import suncertify.presentation.BookingController;

/**
 * This dialog is used for performing the operation of booking a contractor 
 * using a customer number to mark the record as booked.
 * 
 * @author Robert Black
 * @version 1.0
 */
public class BookContractorDialog extends JDialog {
    
    /**
     * A version number for this class so that serialization can occur without 
     * worrying about the underlying class changing between serialization and 
     * de-serialization.
     */
    private static final long serialVersionUID = -8440509626925934636L;
    
    /**
     * Holds a reference to the <code>BookingController</code>.
     */
    private BookingController controller;
    
    /**
     * Holds the record number (file position) of the contractor.
     */
    private long recNo;
    
    //The Swing components start.
    private JLabel nameLabel;
    private JLabel actualNameLabel;
    private JLabel locationLabel;
    private JLabel actualLocationLabel;
    private JLabel specialitiesLabel;
    private JLabel actualSpecialitiesLabel;
    private JLabel sizeLabel;
    private JLabel actualSizeLabel;
    private JLabel rateLabel;
    private JLabel actualRateLabel;
    private JLabel custNumLabel;
    private JTextField custNoField;
    private JButton bookButton;
    private JButton cancelButton;
    //Swing components end.

    /**
     * The constructor takes the owning <code>Window</code> and the 
     * <code>BookingController</code>.  It calls the super constructor to 
     * initialize the <code>JDialog</code>, initializes the 
     * <code>controller</code> field and calls the <code>init()</code> method 
     * to build and initialize the dialog components.
     * 
     * @param owner the owning <code>Window</code>.
     * @param controller the <code>BookingController</code>.
     */
    public BookContractorDialog(Window owner, BookingController controller) {
        super(owner, "Book the Selected Contractor", 
                Dialog.ModalityType.APPLICATION_MODAL);
        this.controller = controller;
        this.init();
    }
    
    /**
     * The initialization and build method used in the constructor. 
     */
    private void init() {
        //Set default close operation.
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        //Sets the LayoutManager to GridBag layout for this dialog.
        this.setLayout(new GridBagLayout());
        
        //Initializes the and creates the form components.
        this.nameLabel = new JLabel("Name:");
        this.actualNameLabel = new JLabel();
        this.locationLabel = new JLabel("Location:");
        this.actualLocationLabel = new JLabel();
        this.specialitiesLabel = new JLabel("Specialities:");
        this.actualSpecialitiesLabel = new JLabel();
        this.sizeLabel = new JLabel("Size:");
        this.actualSizeLabel = new JLabel();
        this.rateLabel = new JLabel("Rate:");
        this.actualRateLabel = new JLabel();
        this.custNumLabel = new JLabel("Customer Number:");
        this.custNoField = new JTextField(10);
        
        //Creates the booking button, add the action to it and a tool tip.
        this.bookButton = new JButton("Book");
        this.bookButton.setToolTipText("Click here to book the contractor.");
        this.bookButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //Extract the Syring (customer number) from the custNoField 
                //JTextFiled.
                String userInputCustNo 
                        = BookContractorDialog.this.custNoField.getText().trim();
                
                //Create a regEx pattern and matcher.
                Pattern custNoPattern = Pattern.compile("\\d{8}");
                Matcher custNoMatcher = custNoPattern.matcher(userInputCustNo);
                
                //If the customer number matches then go ahead with the booking
                //but if the contractor is already booked a 
                //RecordAlreadyBookedException is caught and a message dialog is 
                //displayed along with the action being cancelled.
                if (custNoMatcher.matches()) {
                    try {
                        BookContractorDialog.this.controller.handleBookContractorGesture(
                                BookContractorDialog.this.recNo, userInputCustNo);
                        JOptionPane.showMessageDialog(
                                        BookContractorDialog.this, 
                                        "Contractor Booked.");
                        BookContractorDialog.this.custNoField.setText("");
                        BookContractorDialog.this.setVisible(false);
                    } catch (RecordAlreadyBookedException ex) {
                        try {
                            ApplicationRunner.handleException(ex.getMessage());
                            BookContractorDialog.this.controller.refeshDisplayGesture();
                            BookContractorDialog.this.custNoField.setText("");
                            BookContractorDialog.this.setVisible(false);
                        } catch (RecordNotFoundException ex1) {
                            ApplicationRunner.handleException(ex.getMessage());
                        }
                    } catch (RecordNotFoundException ex) {
                        ApplicationRunner.handleException(ex.getMessage());
                    } catch (SecurityException ex) {
                        ApplicationRunner.handleException(ex.getMessage());
                    }
                //Else if the regEx does not match then the action is never 
                //actioned and along with a message dialog stating that the 
                //correct number should be entered.
                } else {
                    JOptionPane.showMessageDialog(
                            BookContractorDialog.this, 
                            "You must enter an eight digit Customer Number");
                }
            }
            
        });
        
        //Creates the cancelButton button, add the action to it and a tool tip.
        this.cancelButton = new JButton("Cancel");
        this.cancelButton.setToolTipText(
                "Click here to cancel the booking action.");
        this.cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //Closes the dialog cancelling the booking action.
                BookContractorDialog.this.custNoField.setText("");
                BookContractorDialog.this.setVisible(false);
            }
            
        });
        
        //Creates a GridBagConstraints reference.
        GridBagConstraints gridBagConstraints;
        
        //Sets the nameLabel grid bag constraints and adds it to the dialog.
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        this.add(this.nameLabel, gridBagConstraints);
        
        //Sets the actualNameLabel grid bag constraints and adds it to the dialog.
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        this.add(this.actualNameLabel, gridBagConstraints);
        
        //Sets the locationLabel grid bag constraints and adds it to the dialog.
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        this.add(this.locationLabel, gridBagConstraints);
        
        //Sets the actualLocationLabel grid bag constraints and adds it to the 
        //dialog.
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        this.add(this.actualLocationLabel, gridBagConstraints);
        
        //Sets the specialitiesLabel grid bag constraints and adds it to the 
        //dialog.
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        this.add(this.specialitiesLabel, gridBagConstraints);
        
        //Sets the actualSpecialitiesLabel grid bag constraints and adds it to 
        //the dialog.
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        this.add(this.actualSpecialitiesLabel, gridBagConstraints);
        
        //Sets the sizeLabel grid bag constraints and adds it to the dialog.
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        this.add(this.sizeLabel, gridBagConstraints);
        
        //Sets the actualSizeLabel grid bag constraints and adds it to the 
        //dialog.
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        this.add(this.actualSizeLabel, gridBagConstraints);
        
        //Sets the rateLabel grid bag constraints and adds it to the dialog.
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        this.add(this.rateLabel, gridBagConstraints);
        
        //Sets the actualRateLabel grid bag constraints and adds it to the 
        //dialog.
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        this.add(this.actualRateLabel, gridBagConstraints);
        
        //Sets the custNumLabel grid bag constraints and adds it to the dialog.
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        this.add(this.custNumLabel, gridBagConstraints);
        
        //Sets the custNoField grid bag constraints and adds it to the dialog.
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        this.add(this.custNoField, gridBagConstraints);
        
        //Sets the bookButton grid bag constraints and adds it to the dialog.
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new Insets(10, 150, 10, 100);
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        this.add(this.bookButton, gridBagConstraints);
        
        //Sets the cancelButton grid bag constraints and adds it to the dialog.
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new Insets(10, 100, 10, 150);
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        this.add(this.cancelButton, gridBagConstraints);
        
        //Sizes the dialog to fit all the components.
        this.pack();
        
        //Locates the dialog into the centre of the screen.
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((d.getWidth() - this.getWidth()) / 2);
        int y = (int) ((d.getHeight() - this.getHeight()) / 2);
        this.setLocation(x, y);
    }

    /**
     * This method takes a <code>String[]</code> with the customers details and 
     * a <code>long</code> with the record number (file position) to be used 
     * later with updating the database file.
     * 
     * @param contDetails the <code>String[]</code> containing the customer 
     * details to be displayed.
     * @param recNo the record number (file position).
     */
    public void setContDetails(String[] contDetails, long recNo) {
        //Sets the text in the actual customer labels.
        this.actualNameLabel.setText(contDetails[0]);
        this.actualLocationLabel.setText(contDetails[1]);
        this.actualSpecialitiesLabel.setText(contDetails[2]);
        this.actualSizeLabel.setText(contDetails[3]);
        this.actualRateLabel.setText(contDetails[4]);
        
        //Stores the the record number (file position) for later use.
        this.recNo = recNo;
    }
    
}
