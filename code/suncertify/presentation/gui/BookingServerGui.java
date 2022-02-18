package suncertify.presentation.gui;

import suncertify.presentation.ApplicationRunner;
import suncertify.presentation.gui.actions.CloseProgramAction;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import suncertify.presentation.ApplicationMode;

/**
 * This class extends <code>JFrame</code> and is used to frame the 
 * <code>BookingConfigurationPanel</code> on the server mode creating the GUI to 
 * control the server.  It is not part of the model viewer controller 
 * architecture.
 * 
 * @author Robert Black
 * @version 1.0
 */
public class BookingServerGui extends JFrame {
    
    /**
     * A version number for this class so that serialization can occur without 
     * worrying about the underlying class changing between serialization and 
     * de-serialization.
     */
    private static final long serialVersionUID = 954608933128109988L;
    
    /**
     * Holds the <code>ApplicationMode Enum</code>.
     */
    private ApplicationMode mode;
    
    /**
     * Holds a reference to the shared <code>ConfigurationPaneland</code>.
     */
    private JPanel configPanel;
    
    //The Swing components start.
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenu helpMenu;
    private JMenuItem closeMenuItem;
    private JMenuItem helpContentsItem;
    //Swing components end.
    
    /**
     * The constructor takes an <code>ApplicationMode Enum</code> object, 
     * updates the <code>mode</code> field and then calls the 
     * <code>init()</code> method to build and initialize the frame.
     * 
     * @param mode the <code>ApplicationMode Enum</code> object.
     */
    public BookingServerGui(ApplicationMode mode) {
        this.mode = mode;
        this.init();
    }
    
    /**
     * The initialization and build method used in the constructor. 
     */
    private void init() {
        //Actions
        Action exit = new CloseProgramAction();
        
        //Sets the title and the Default Close Operation.
        this.setTitle(
                "Bodgitt and Scarper Contractor Booking System Server Configuation");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //Creates a menubar.
        this.menuBar = new JMenuBar();
        
        //Creates a "File" menu
        this.fileMenu = new JMenu("File");
        
        //Creates an "Exit" menu item and adds an action listener to it then 
        //adds the "Exit" menu item to the "File" menu.
        this.closeMenuItem = new JMenuItem(exit);
        this.fileMenu.add(this.closeMenuItem);
        
        //Creates a "Help" menu
        this.helpMenu = new JMenu("Help");
        
        //Creates an "Help Contents" menu item and adds an action listener to it 
        //then adds the "Exit" menu item to the "Help" menu.
        this.helpContentsItem = new JMenuItem("Help Contents");
        this.helpContentsItem.setToolTipText("Click to open the help page.");
        this.helpContentsItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    //Create a JEditorPane and set editable false.
                    JEditorPane helpContentsPane = new JEditorPane();
                    helpContentsPane.setEditable(false);
                    
                    //Create a URL with a path to the root directory.
                    String userDir = System.getProperty("user.dir");
                    String fileSep = File.separator;
                    String url = "file:///" + userDir + fileSep + "docs" 
                            + fileSep + "userguide.html";
                    URL helpHtml = new URL(url);
                    helpContentsPane.setPage(helpHtml);
                    
                    //Create a JScrollPane and insert the helpContentsPane.
                    JScrollPane scrollPane = new JScrollPane(helpContentsPane);
                    
                    //Create a modeless JDialog and set its properties.
                    JDialog helpContentsDialog = new JDialog();
                    helpContentsDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                    helpContentsDialog.add(scrollPane, BorderLayout.CENTER);
                    helpContentsDialog.setSize(500, 400);
                    helpContentsDialog.setModalityType(ModalityType.MODELESS);
                    
                    //Locates the dialod into the centre of the screen.
                    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
                    int x = (int) ((d.getWidth() - helpContentsDialog.getWidth()) / 2);
                    int y = (int) ((d.getHeight() - helpContentsDialog.getHeight()) / 2);
                    helpContentsDialog.setLocation(x, y);
                    
                    //Make it visable.
                    helpContentsDialog.setVisible(true);
                } catch (MalformedURLException ex) {
                    ApplicationRunner.handleException(ex.toString());
                } catch (IOException ex) {
                    ApplicationRunner.handleException(ex.toString());
                }
            }
            
        });
        this.helpMenu.add(this.helpContentsItem);
        
        //Adds the "File" and the "Help" menu to the menu bar.
        this.menuBar.add(fileMenu);
        this.menuBar.add(this.helpMenu);
        
        //Adds the menue bar to the frame.
        this.setJMenuBar(this.menuBar);
        
        //Creates a BookingConfigurationPanel using the mode field and adds it 
        //to the frame using the default BorderLayout.
        this.configPanel = new BookingConfigurationPanel(this.mode);
        this.add(this.configPanel, BorderLayout.CENTER);
        
        //Sizes the frame to fit its components
        this.pack();
        
        //Locates the frame into the centre of the screen.
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((d.getWidth() - this.getWidth()) / 2);
        int y = (int) ((d.getHeight() - this.getHeight()) / 2);
        this.setLocation(x, y);
    }
    
}
