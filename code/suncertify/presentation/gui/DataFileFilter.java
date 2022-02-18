package suncertify.presentation.gui;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * This class extends the <code>javax.swing.filechooser.FileFilter</code> and is 
 * used by the <code>ConfigurationPanel</code> file chooser.
 * 
 * @author Robert Black
 * @version 1.0
 */
public class DataFileFilter extends FileFilter {

    /**
     * The default constructor that call the constructor of its super class.
     */
    public DataFileFilter() {
        super();
    }

    /**
     * Returns true for files ending with ".db" and false for any others.
     * 
     * @param file the <code>File<\code>.
     * @return is accepted boolean.
     */
    @Override
    public boolean accept(File file) {
        //Checks the file extention and shows only ".db" files and directories.
        if (file.getName().endsWith(".db") || file.isDirectory()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Sets and returns the description of the database file to be chosen to be 
     * displayed in the "File of type" combo box in the file chooser.
     * 
     * @return the file description.
     */
    @Override
    public String getDescription() {
        return "Database File (.db)";
    }
    
}
