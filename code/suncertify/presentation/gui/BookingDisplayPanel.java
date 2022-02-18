package suncertify.presentation.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import suncertify.db.Contractor;
import suncertify.db.RecordNotFoundException;
import suncertify.presentation.ApplicationRunner;
import suncertify.presentation.BookingController;

/**
 * This class extends <code>JPanel</code> is used to display searched records in 
 * a <code>JTable</code>.<br/><br/>
 * 
 * It implements the <code>BookingPanel</code> interface so it can be added to 
 * the a <code>BookingGui</code>.
 * 
 * @author Robert Black
 * @version 1.0
 */
public class BookingDisplayPanel extends JPanel implements BookingPanel {
    
    /**
     * A version number for this class so that serialization can occur without 
     * worrying about the underlying class changing between serialization and 
     * de-serialization.
     */
    private static final long serialVersionUID = 3080958710140098435L;
    
    /**
     * A Map that contains searched records consisting of a <code>Long</code>  
     * (record number representing the position of the record on disk) as the 
     * key and a <code>Contractor</code> object as the value representing the 
     * record on disk.
     */
    private Map<Long, Contractor> contractors;
    
    /**
     * Holds a reference to the <code>BookingController</code>.
     */
    private BookingController controller;
    
    /**
     * An array of <code>String</code> objects representing the table headers.
     */
    private String[] tableHeaders;
    
    /**
     * An array of <code>String[]</code> objects representing the table records.
     */
    private String[][] contractorArray;
    
    /**
     * A <code>String[]</code> array containing a contractors details.
     */
    private String[] contractorDetails;
    
    /**
     * Holds the record number (file position).
     */
    private long recNo;
    
    /**
     * Holds the table model used to build the JTable.
     */
    private TableModel tableModel;
    
    //The Swing components start.
    private JScrollPane scrollPane;
    private JTable displayTable;
    //Swing components end.
    
    /**
     * The default constructor calls the <code>init()</code> method to build the 
     * GUI in this panel and initialize its components.
     */
    public BookingDisplayPanel() {
        this.init();
    }
    
    /**
     * The <code>init()</code> method that builds the GUI in this panel and 
     * initialize its components.
     */
    private void init() {
        //Sets the border display.
        this.setBorder(BorderFactory.createTitledBorder("Displayed Records"));
        
        //Creates a JTable, sets its properties, adds a MouseListener and a tool 
        //tip.
        this.displayTable = new JTable();
        this.displayTable.setToolTipText(
                "Click a record here to select a contractor for operations.");
        displayTable.setAutoCreateRowSorter(true);
        displayTable.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                try {
                    //initializes the contractorDetails field.
                    contractorDetails = new String[5];
                    
                    //Retrive the selected row.
                    int row = displayTable.getSelectedRow();
                    
                    //Extract the information from the selected row and populate the 
                    //contractorDetails array with the record details.
                    contractorDetails[0] = (String) tableModel.getValueAt(row, 0);
                    contractorDetails[1] = (String) tableModel.getValueAt(row, 1);
                    contractorDetails[2] = (String) tableModel.getValueAt(row, 2);
                    Integer column3 = (Integer) tableModel.getValueAt(row, 3);
                    contractorDetails[3] = String.valueOf(column3);
                    Double column4 = (Double) tableModel.getValueAt(row, 4);
                    NumberFormat formatDouble = NumberFormat.getNumberInstance();
                    formatDouble.setMaximumFractionDigits(2);
                    formatDouble.setMinimumFractionDigits(2);
                    String formatedColumn4 = formatDouble.format(column4);
                    contractorDetails[4] = "$" + formatedColumn4;
                    
                    //Extract the record number (file position) from the searched 
                    //records in the HashMap in the model useing the primary key of 
                    //name and location.  The HashMap is updated by the search gesture 
                    //in the SearchPanel.
                    for (Map.Entry<Long, Contractor> entry : contractors.entrySet()) {
                        Contractor contractor = entry.getValue();
                        if (contractor.getName().equals(contractorDetails[0]) 
                                && contractor.getLocation().equals(contractorDetails[1])) {
                            recNo = entry.getKey();
                        }
                    }
                    
                    //Calls the controller to Handle the selection gesture which will 
                    //eventually update the booking dialog display.
                    controller.handlesTableSelectionGesture(contractorDetails, recNo);
                } catch (RecordNotFoundException ex) {
                    ApplicationRunner.handleException(ex.getMessage());
                }
            }
            
        });
        
        //Create ans set the properties of a JScrollPane, then set the display 
        //table in the view port.
        this.scrollPane = new JScrollPane();
        scrollPane.setPreferredSize(new Dimension(650, 300));
        scrollPane.getViewport().add(displayTable);
        
        //Add the scrollpane to the panel using the default BorderLayout.
        this.add(this.scrollPane, BorderLayout.CENTER);
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
    @SuppressWarnings("unchecked")
    public void display(Object obj) {
        if (obj != null && obj instanceof Map) {
            this.contractors = (Map<Long, Contractor>) obj;
            this.refresh();
        }
        if (obj != null && obj instanceof String) {
            String cancel = (String) obj;
            if (cancel.equals("cancel")) {
                this.displayTable.clearSelection();
            }
        }
    }

    /**
     * This private method is used to refresh the display in the display table.
     */
    private void refresh() {
        //Create a Set so when it is populated with Long objects they are sorted
        //acending.
        Set<Long>  recordNumbers = new TreeSet<Long>();
        
        //Create an array of String[] objects that inturn will hold the details 
        //of each record.
        String newData[][] = new String[contractors.size()][];

        //Traverse the contractors HashMap, extract the record numbers (file 
        //positions) and populate the recordNumbers Set.
        for (Map.Entry<Long, Contractor> entry : contractors.entrySet()) {
            recordNumbers.add(entry.getKey());
        }
        
        //Use an Iterator<Long to extract the Contractor objects in order and 
        //extract the String[] data from each Contractor object using its 
        //getStringArrayData() and add it to the newData[][].
        Iterator<Long> iter = recordNumbers.iterator();
        int index = 0;
        while (iter.hasNext()) {
            String[] contractor = contractors.get(iter.next()).getStringArrayData();
            newData[index] = contractor;
            index++;
        }
        
        //Update the tableHeaders with newData[0] array.
        this.tableHeaders = newData[0];
        
        //Change the first character of each header to uppercase.
        for (int i = 0; i < tableHeaders.length; i++) {
            tableHeaders[i] 
                    = tableHeaders[i].substring(0, 1).toUpperCase() 
                    + tableHeaders[i].substring(1).toLowerCase();
        }
        
        //Update the contractorArray with the Sting[] arrays from newData[0] 
        //onwards to the end of newData[0]
        this.contractorArray = new String[newData.length - 1][];
        for (int i = 1; i < newData.length; i++) {
            contractorArray[i - 1] = newData[i];
        }
        
        //Create a new BookingDisplayTableModel and populate the display table 
        //it with the new BookingDisplayTableModel.
        tableModel = new BookingDisplayTableModel(tableHeaders, contractorArray);
        displayTable.setModel(tableModel);
        
        //Set the default renderer so the Double objects render as in the database.
        displayTable.setDefaultRenderer(
                Double.class, new DefaultTableCellRenderer() {
            
            /**
             * A version number for this class so that serialization can occur without 
             * worrying about the underlying class changing between serialization and 
             * de-serialization.
             */
            private static final long serialVersionUID = -347184672129699492L;

            /**
             * If column 4, then render the <code>Double</code> as a formated 
             * the character "$" Concatenated with the </code>String</code> 
             * version of the <code>Double</code>.
             */
            @Override
            public Component getTableCellRendererComponent(JTable table, 
                    Object value, boolean isSelected, boolean hasFocus, 
                    int row, int column) {
                if (column == 4) {
                    //Set the horozontal alignment to RIGHT.
                    this.setHorizontalAlignment(JLabel.RIGHT);
                    
                    //Extract the Double.
                    Double column4 = (Double) value;
                    
                    //Create a NumberFormat and set its properties.
                    NumberFormat formatDouble = NumberFormat.getNumberInstance();
                    formatDouble.setMaximumFractionDigits(2);
                    formatDouble.setMinimumFractionDigits(2);
                    
                    //Format the number
                    String formatedColumn4 = formatDouble.format(column4);
                    
                    //Create the String to be rendered.
                    String newValue = "$" + formatedColumn4;
                    
                    //return the call to the super getTableCellRendererComponent()
                    //method with the newValue String.
                    return super.getTableCellRendererComponent(table, newValue, 
                            isSelected, hasFocus, row, column);
                } else {
                    //Else return the same value with a call to the super 
                    //getTableCellRendererComponent() method.
                    return super.getTableCellRendererComponent(table, value, 
                            isSelected, hasFocus, row, column);
                }
            }
            
        });
        
        //Set the widths of the display table columns expect the last one so 
        //it will fill to the end of the JScrollPane.
        TableColumn nameColumn = displayTable.getColumnModel().getColumn(0);
        nameColumn.setPreferredWidth(180);
        TableColumn locationColumn = displayTable.getColumnModel().getColumn(1);
        locationColumn.setPreferredWidth(100);
        TableColumn specColumn = displayTable.getColumnModel().getColumn(2);
        specColumn.setPreferredWidth(210);
        TableColumn sizeColumn = displayTable.getColumnModel().getColumn(3);
        sizeColumn.setPreferredWidth(35);
        TableColumn rateColumn = displayTable.getColumnModel().getColumn(4);
        rateColumn.setPreferredWidth(55);
    }
    
}
