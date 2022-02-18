package suncertify.presentation;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Depending on the command line argument used, it will launch the 
 * appropriate application mode.
 * 
 * @author Robert Black
 * @version 1.0
 */
public class ApplicationRunner {
    
    /**
     * Holds the <code>ApplicationMode Enum</code> object.
     */
    private ApplicationMode mode;

    /**
     * The constructor takes the command line flag and uses it to set the 
     * application mode which is used to determine the type of application 
     * to be started.
     * 
     * @param args the command line argument.
     */
    public ApplicationRunner(String[] args) {
        //Sets the look and feel of the gui.
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            ApplicationRunner.handleException(ex.getMessage());
        } catch (InstantiationException ex) {
            ApplicationRunner.handleException(ex.getMessage());
        } catch (IllegalAccessException ex) {
            ApplicationRunner.handleException(ex.getMessage());
        } catch (UnsupportedLookAndFeelException ex) {
            ApplicationRunner.handleException(ex.getMessage());
        }
        
        //Sets the application mode ENUM.
        if (args.length == 0) {
            this.mode = ApplicationMode.NETWORK_CLIENT;
        } else if (args[0].equalsIgnoreCase("alone")) {
            this.mode = ApplicationMode.STANDALONE_CLIENT;
        } else if (args[0].equalsIgnoreCase("server")) {
            this.mode = ApplicationMode.SERVER;
        } else {
            //Prints an error to the console if the wrong command line argument 
            //was used.
            System.err.println("Command line options may be one of:");
            System.err.println("\"server\" - starts server application");
            System.err.println("\"alone\" - starts non-networked client");
            System.err.println("\"\" - (no command line option): networked "
                    + "client will start");
        }
        //Calls the application initailization method.
        this.init();
    }
    
    /**
     * This is the <code>main()</code> method that launches the Bodgitt and 
     * Scarper contractor booking application.
     * 
     * @param args the command line arguments "server" or "alone" or null.<br/>
     * "server" launches the server.<br/>
     * "alone" launches the stand alone version.<br/>
     * null launches the network client
     */
    public static void main(String[] args) {
        ApplicationRunner application = new ApplicationRunner(args);
    }
    
    /**
     * This initializes the application creating a <code>BookingMainFrame</code> and 
     * passing it the application mode.
     */
    private void init() {
        BookingMainFrame frame = new BookingMainFrame(this.mode);
    }
    
    /**
     * This static method is used to notify the user of any exceptions thrown 
     * that are not used for any other purpose than to warn the user of an 
     * unexpected condition.
     * 
     * @param message the message to be displayed.
     */
    public static void handleException(String message) {
        JOptionPane alert = new JOptionPane(message, JOptionPane.ERROR_MESSAGE, 
                JOptionPane.DEFAULT_OPTION);
        JDialog dialog = alert.createDialog(null, "Alert");
        
        // Center on screen
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((d.getWidth() - dialog.getWidth()) / 2);
        int y = (int) ((d.getHeight() - dialog.getHeight()) / 2);
        dialog.setLocation(x, y);
        dialog.setVisible(true);
    }
}
