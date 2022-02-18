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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import suncertify.presentation.BookingController;

/**
 * This class extends <code>JFrame</code> and implements <code>BookingGui</code> 
 * so the GUI class can be used in the view of the model viewer controller 
 * design pattern for the presentation tier of the Booking application.  The 
 * implementation of the <code>BookingGui</code> interface enables this GUI to 
 * be added to the view using the strategy design pattern and the methods within 
 * to interact with the model viewer controller architecture.
 * 
 * @author Robert Black
 * @version 1.0
 */
public class BookingGuiImp extends JFrame implements BookingGui {
    
    /**
     * A version number for this class so that serialization can occur without 
     * worrying about the underlying class changing between serialization and 
     * de-serialization.
     */
    private static final long serialVersionUID = 2352985974597131654L;
    
    /**
     * Holds a list of all the panels in this GUI.
     */
    private List<BookingPanel> panels = new ArrayList<BookingPanel>();
    
    /**
     * Holds a reference to the <code>BookingController</code>.
     */
    private BookingController controller;
    
    //The Swing components start.
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenu helpMenu;
    private JMenuItem closeMenuItem;
    private JMenuItem helpContentsItem;
    //Swing components end.
    
    /**
     * The default constructor.
     */
    public BookingGuiImp() {
        this.init();
    }
    
    /**
     * The initialization and build method used in the constructor. 
     */
    private void init() {
        //Actions
        Action exit = new CloseProgramAction();
        
        //Sets the frame title
        this.setTitle("Bodgitt and Scarper Contractor Booking System");
        
        //Sets the default close operation.
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
                    String url 
                            = "file:///" + userDir + fileSep + "docs" 
                            + fileSep + "userguide.html";
                    URL helpHtml = new URL(url);
                    helpContentsPane.setPage(helpHtml);
                    
                    //Create a JScrollPane and insert the helpContentsPane.
                    JScrollPane scrollPane = new JScrollPane(helpContentsPane);
                    
                    //Create a modeless JDialog and set its properties.
                    JDialog helpContentsDialog = new JDialog();
                    helpContentsDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                    helpContentsDialog.setTitle(
                            "Bodgitt and Scarper Contractor Booking System Help");
                    helpContentsDialog.add(scrollPane, BorderLayout.CENTER);
                    helpContentsDialog.setSize(600, 500);
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
        
        //Creates the searchPanel and adds it to frame using the default 
        //BorderLayoutthe layout manager and then adds it to the panel list.
        BookingSearchPanel searchPanel = new BookingSearchPanel();
        this.add(searchPanel, BorderLayout.NORTH);
        this.panels.add(searchPanel);
        
        //Creates the displayPanel and adds it to frame using the default 
        //BorderLayoutthe layout manager and then adds it to the panel list.
        BookingDisplayPanel displayPanel = new BookingDisplayPanel();
        this.add(displayPanel, BorderLayout.CENTER);
        this.panels.add(displayPanel);
        
        //Creates the bookingControlPanel and adds it to frame using the default 
        //BorderLayoutthe layout manager and then adds it to the panel list.
        BookingControlPanel bookingControlPanel = new BookingControlPanel();
        this.add(bookingControlPanel, BorderLayout.SOUTH);
        this.panels.add(bookingControlPanel);
        
        //Sizes the frame to fit all components.
        this.pack();
        
        //Locates the frame into the centre of the screen.
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((screen.getWidth() - this.getWidth()) / 2);
        int y = (int) ((screen.getHeight() - this.getHeight()) / 2);
        this.setLocation(x, y);
    }
    
    /**
     * This method registers the <code>BookingController</code> so the 
     * <code>BookingGui</code> has a reference to the 
     * <code>BookingController</code>.
     * 
     * @param controller the <code>BookingController</code>.
     */
    @Override
    public void registerBookingController(BookingController controller) {
        //Register the controller in all panels.
        this.controller = controller;
        for (BookingPanel panel : this.panels) {
            panel.registerBookingController(this.controller);
        }
    }
    
    /**
     * This method to handles state change notifications from to the 
     * <code>BookingModel</code>.  The display will change depending on which 
     * object is passes to it.
     * 
     * @param obj the <code>Object</code>.
     */
    @Override
    public void display(Object obj) {
        //Check to see if the Object is nul and if so, return with on action.
        if (obj == null) {
            return;
        }
        
        //If the object is a Map, it will update the displayPanel panel.
        if (obj instanceof Map) {
            panels.get(1).display(obj);
        }
        
        //If the object is a Map, it will update the displayPanel panel.
        if (obj instanceof String) {
            panels.get(1).display(obj);
        }
        
        //If the object is a String[], it will update the bookingControlPanel 
        //panel.
        if (obj instanceof String[]) {
            panels.get(2).display(obj);
        }
        
        //If the object is a Long, it will update the bookingControlPanel panel.
        if (obj instanceof Long) {
            panels.get(2).display(obj);
        }
        
        if (obj instanceof Boolean) {
            panels.get(2).display(obj);
        }
        
    }
    
}
