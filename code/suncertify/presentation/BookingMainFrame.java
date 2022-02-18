package suncertify.presentation;

import java.awt.EventQueue;
import suncertify.presentation.gui.BookingConfigurationDialog;
import suncertify.presentation.gui.BookingServerGui;

/**
 * This class is responsible for launching the appropriate GUI depending on the 
 * application mode (<code>ApplicationMode</code> ENUM).
 * 
 * @author Robert Black
 * @version 1.0
 */
public class BookingMainFrame {
    
    /**
     * Holds the <code>ApplicationMode</code>.
     */
    private ApplicationMode mode;

    /**
     * The constructor set the mode variable and calls the initialization method 
     * <code>init()</code>.
     * 
     * @param mode the <code>ApplicationMode</code>.
     */
    public BookingMainFrame(ApplicationMode mode) {
        this.mode = mode;
        this.init();
    }
    
    /**
     * This method initializes the GUI and calls a further appropriate 
     * initialization method.
     */
    private void init() {
        if (this.mode == ApplicationMode.NETWORK_CLIENT) {
            this.networkClientInit();
        } else if (this.mode == ApplicationMode.SERVER) {
            this.serverInit();
        } else if (this.mode == ApplicationMode.STANDALONE_CLIENT) {
            this.aloneInit();
        }
    }
    
    /**
     * This method initializes the server GUI for configuring the network, 
     * starting and stopping the server.
     */
    private void networkClientInit() {
        
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                
                //Start the configuration dialog.
                BookingConfigurationDialog configuration 
                        = new BookingConfigurationDialog(
                                BookingMainFrame.this.mode);
                configuration.setVisible(true);
                
            }

        });
    }
    
    
    private void serverInit() {
        
        
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                BookingServerGui serverGui 
                        = new BookingServerGui(BookingMainFrame.this.mode);
                serverGui.setVisible(true);
            }
            
        });
    }
    
    /**
     * This method initializes the dialog window for configuring the stand alone  
     * application before the main GUI is launched.
     */
    private void aloneInit() {
        
        EventQueue.invokeLater(new Runnable() {
        
            @Override
            public void run() {
                    
                    BookingConfigurationDialog configuration 
                            = new BookingConfigurationDialog(
                                    BookingMainFrame.this.mode);
                    configuration.setVisible(true);
               
            }

        }); 
        
    }
    
}
