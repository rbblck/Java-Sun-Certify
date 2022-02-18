package suncertify.presentation.gui;

import javax.swing.table.AbstractTableModel;

/**
 * The contractor records display table model used by the
 * <code>SearchedRecordsDisplayPanel</code> instance to populate the display 
 * table.
 * 
 * @author Robert Black
 * @version 1.0
 */
public class BookingDisplayTableModel extends AbstractTableModel {
    
    /**
     * A version number for this class so that serialization can occur without 
     * worrying about the underlying class changing between serialization and 
     * de-serialization.
     */
    private static final long serialVersionUID = 7303746499308579597L;
    
    /**
     * An array of <code>String</code> objects representing the table headers.
     */
    String[] tableHeaders;
    
    /**
     * An array of <code>String[]</code> objects representing the table records.
     */
    String[][] contractorArray;

    /**
     * The constructor takes a <code>String[]</code> to store the header titles 
     * and a <code>String[][]</code> to store the records for each row.  It 
     * then initializes the <code>tableHeaders</code> and 
     * <code>contractorArray</code> fields.
     * 
     * @param tableHeaders the <code>String[]</code> of header titles.
     * @param contractorArray the <code>String[][]</code> of the records.
     */
    public BookingDisplayTableModel(
            String[] tableHeaders, String[][] contractorArray) {
        this.tableHeaders = tableHeaders;
        this.contractorArray = contractorArray;
    }

    /**
     * Returns the number of rows.
     * 
     * @return the number of rows.
     */
    @Override
    public int getRowCount() {
        return contractorArray.length;
    }

    /**
     * Returns the number of columns.
     * 
     * @return the number of columns.
     */
    @Override
    public int getColumnCount() {
        return tableHeaders.length;
    }

    /**
     * Returns the <code>Object</code> to be displayed in each cell.
     * 
     * @param rowIndex the row index.
     * @param columnIndex the column index
     * @return the <code>Object</code> to be displayed.
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        //Column 3 gets an Integer object for proper sorting.
        if (columnIndex == 3) {
            String value = contractorArray[rowIndex][columnIndex];
            if (!(value.equals(""))) {
                Integer intValue = Integer.valueOf(value);
                return intValue;
            } else {
                return null;
            }
        //Column 4 gets a Double object for proper sorting.
        } else if (columnIndex == 4) {
            String value = contractorArray[rowIndex][columnIndex];
            if (!(value.equals(""))) {
                value = value.substring(1);
                Double doubleValue = Double.valueOf(value);
                return doubleValue;
            } else {
                return null;
            }
        } else {
            return contractorArray[rowIndex][columnIndex];
        }
    }

    /**
     * Returns the column header name to be displayed.
     * 
     * @param column the column index.
     * @return the String to be displayed in the column header.
     */
    @Override
    public String getColumnName(int column) {
        return tableHeaders[column];
    }

    /**
     * Returns the class of object to be displayed in each column.
     * 
     * @param columnIndex the column index.
     * @return the class.
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        //Column 3 and 5 gets an Integer class for rendering aligned right.
        //Although column 5 is a string, it looks better aligned right and the 
        //string sorting will hold good as there are always eight digits in the 
        //customer numbers.  All the other columes get String classes.
        if (columnIndex == 3 || columnIndex == 5) {
            return Integer.class;
        } else if (columnIndex == 4) {
            return Double.class;
        } else {
            return String.class;
        }
    }
    
}
