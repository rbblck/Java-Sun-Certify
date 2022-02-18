package suncertify.presentation.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import suncertify.db.RecordNotFoundException;
import suncertify.db.SecurityException;
import suncertify.presentation.BookingController;

/**
 * This class is used to preform search operations on the database. It has two 
 * fields for entering search criteria, one for the name and one for location, 
 * both. An empty field in either matches any field value. A non-empty field 
 * matches any record name or location value that begins with the field entry. 
 * (For example, "Fred" matches "Fred" or "Freddy").  The search button will 
 * start and return all matching records to the display table.<br/><br/>
 * 
 * It implements the <code>BookingPanel</code> interface so it can be added to 
 * the a <code>BookingGui</code>.
 * 
 * @author Robert Black
 * @version 1.0
 */
public class BookingSearchPanel extends JPanel implements BookingPanel {
    
    /**
     * A version number for this class so that serialization can occur without 
     * worrying about the underlying class changing between serialization and 
     * de-serialization.
     */
    private static final long serialVersionUID = 2156204647432124537L;
    
    /**
     * Holds a reference to the <code>BookingController</code>.
     */
    private BookingController controller;
    
    //The Swing components start.
    private JButton searchButton;
    private JLabel nameLabel;
    private JLabel locationLabel;
    private JTextField nameField;
    private JTextField locationField;
    //Swing components end.

    /**
     * The default constructor calls the <code>init()</code> method to build the 
     * GUI in this panel and initialize its components.
     */
    public BookingSearchPanel() {
        this.init();
    }
    
    /**
     * The <code>init()</code> method that builds the GUI in this panel and 
     * initialize its components.
     */
    private void init() {
        //Sets the LayoutManager.
        this.setLayout(new GridBagLayout());
        
        //Creates the border.
        this.setBorder(BorderFactory.createTitledBorder("Search Records"));
        
        //Initializes the bookButton field, adds an ActionListener to it and
        //add a tool tip to it.
        this.searchButton = new JButton("Search Contractors");
        this.searchButton.setToolTipText(
                "Click here to search for contractor records.");
        this.searchButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //Extract the search criteria.
                String name = nameField.getText().trim();
                String location = locationField.getText().trim();
                
                //Create a regEx pattern to stop users useing wild card 
                //characters.
                Pattern namePattern = Pattern.compile("\\*");
                Matcher nameMatcher = namePattern.matcher(name);
                Pattern locationPattern = Pattern.compile("\\*");
                Matcher locationMatcher = locationPattern.matcher(location);
                
                //Check that wild card characters have not been used.
                if (nameMatcher.find() || locationMatcher.find()) {
                    JOptionPane.showMessageDialog(
                            null, 
                            "Do not use wild card characters (\"*\") in the "
                            + "search fields.");
                    return;
                }
                
                //Sends the gesture to the controller
                try {
                    controller.handleSearchContractorsGesture(name, location);
                } catch (RecordNotFoundException ex) {
                    JOptionPane.showMessageDialog(
                            BookingSearchPanel.this, ex.getMessage());
                } catch (SecurityException ex) {
                    JOptionPane.showMessageDialog(
                            BookingSearchPanel.this, ex.getMessage());
                }
            }
            
        });
        
        //Create JLabels tool tips.
        this.nameLabel = new JLabel("Name:");
        this.locationLabel = new JLabel("Location:");
        
        //Create JTextFields and add 
        this.nameField = new JTextField(20);
        this.nameField.setToolTipText("Enter the name of the contractor here");
        this.locationField = new JTextField(20);
        this.locationField.setToolTipText(
                "Enter the location of the contractor here");
        
        //Creates a GridBagConstraints reference.
        GridBagConstraints gridBagConstraints;
        
        //Sets the button panel grid bag constraints and adds the nameLabel to 
        //the panel.
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(10, 5, 10, 5);
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        this.add(this.nameLabel, gridBagConstraints);
        
        //Sets the button panel grid bag constraints and adds the locationLabel 
        //to the panel.
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(5, 10, 10, 5);
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        this.add(locationLabel, gridBagConstraints);
        
        //Sets the button panel grid bag constraints and adds the nameField to 
        //the panel.
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(10, 5, 5, 200);
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        this.add(this.nameField, gridBagConstraints);
        
        //Sets the button panel grid bag constraints and adds the locationField 
        //to the panel.
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(5, 5, 10, 200);
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        this.add(this.locationField, gridBagConstraints);
        
        //Sets the button panel grid bag constraints and adds the searchButton 
        //to the panel.
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(5, 15, 10, 10);
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        this.add(this.searchButton, gridBagConstraints);
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
        //Ckecks to see if the object is null and if so returns only.
        if (obj == null) {
            return;
        }
    }
    
}
