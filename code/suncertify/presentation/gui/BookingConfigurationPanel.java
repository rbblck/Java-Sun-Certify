package suncertify.presentation.gui;

import suncertify.business.BookingBusinessAdapterImp;
import suncertify.business.network.server.BookingNwServer;
import suncertify.db.Data;
import suncertify.presentation.ApplicationRunner;
import suncertify.presentation.gui.actions.CloseProgramAction;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import suncertify.presentation.BookingModelNwImp;
import suncertify.business.network.client.BookingNwClient;
import suncertify.db.DBAccess;
import suncertify.presentation.ApplicationMode;
import suncertify.presentation.BookingController;
import suncertify.presentation.BookingControllerImp;
import suncertify.presentation.BookingModel;
import suncertify.presentation.BookingModelImp;
import suncertify.presentation.BookingView;
import suncertify.presentation.BookingViewImp;

/**
 * This class extends <code>JPanel</code> and is a sheared panel used to 
 * configure the application depending on which mode it has been started.  It 
 * takes an <code>ApplicationMode Enum</code> and uses it to determine the 
 * layout and functionality of the various components in this panel.  Once the 
 * layout and functionality has been initialized, it is used to set the startup 
 * parameters for the application appropriate to the mode in a properties file 
 * (<code>suncertify.properties</code>) located in the root directory.  If the 
 * <code>suncertify.properties</code> does not exist, one will be created.
 * 
 * @author Robert Black
 * @version 1.0
 */
public class BookingConfigurationPanel extends JPanel {
    
    /**
     * A version number for this class so that serialization can occur without 
     * worrying about the underlying class changing between serialization and 
     * de-serialization.
     */
    private static final long serialVersionUID = 8188209074913903765L;
    
    /**
     * Holds the <code>ApplicationMode Enum</code>.
     */
    private ApplicationMode mode;
    
    /**
     * contains the <code>Properties</code> object representing a persistent set 
     * of properties.
     */
    private Properties applicationProperties;
    
    /**
     * The <code>File</code> object representing the 
     * <code>suncertify.properties</code> file.
     */
    private File propertiesFile;
    
    /**
     * Holds a reference to the <code>BookingNwServer</code>.
     */
    private BookingNwServer server;
    
    /**
     * Holds a reference to the <code>BookingNwClient</code>.
     */
    private BookingNwClient client;
    
    /**
     * Holds a reference to the <code>Thread</code> that the server runs in.
     */
    private Thread serverThread;
    
    /**
     * Holds a reference to the dialog that this panel in contained in.
     */
    private Container dialog;
    
    /**
     * A <code>String</code> containing the path to the user directory.
     */
    private String userDir;
    
    /**
     * The current systems file separator character.
     */
    private String fileSep;
    
    /**
     * Holds the close program action.
     */
    private Action closeProgramAction;
    
    //The Swing components start.
    private JLabel dbLocationLabel;
    private JLabel serverPortLabel;
    private JLabel serverIpAddLabel;
    private JTextField dbLocationField;
    private JTextField serverPortField;
    private JTextField serverIpAddField;
    private JPanel buttonPanel;
    private JButton fileLocationButton;
    private JButton confirmButton;
    private JButton startServerButton;
    private JButton stopServerButton;
    private JButton exitButton;
    private JFileChooser chooser;
    //Swing components end.

    /**
     * This constructor is used when the application is started in either stand 
     * alone or network client mode.  It takes an 
     * <code>ApplicationMode Emun</code> used to initialize, create 
     * functionality and layout appropriate to the application mode used and a 
     * <code>Container</code> to so it can be references later e.g. to close the 
     * dialog.  After initializing the <code>mode</code> and <code>dialog</code> 
     * fields, it calls the <code>init()</code> method to build and initialize 
     * the panel components.
     * 
     * @param mode the <code>ApplicationMode Emun</code>.
     * @param dialog the <code>Container</code> containing this panel.
     */
    public BookingConfigurationPanel(ApplicationMode mode, Container dialog) {
        this.mode = mode;
        this.dialog = dialog;
        this.init();
    }
    
    /**
     * This constructor is used when the application is started in server mode.  
     * It takes an <code>ApplicationMode Emun</code> used to initialize, create 
     * functionality and layout appropriate to the application mode used.  After 
     * initializing the <code>mode</code> field, it calls the <code>init()</code> 
     * method to build and initialize the panel components.
     * 
     * @param mode the <code>ApplicationMode Emun</code>.
     */
    public BookingConfigurationPanel(ApplicationMode mode) {
        this.mode = mode;
        this.init();
    }
    
    /**
     * The initialization and build method used in the constructor and builds 
     * the common components and functionality of the sheared panel.
     */
    private void init() {
        //Actions
        this.closeProgramAction = new CloseProgramAction();
        
        //Sets the LayoutManager.
        this.setLayout(new GridBagLayout());
        
        //Creates the border.
        this.setBorder(BorderFactory.createTitledBorder("Configuration Parameters"));
        
        //Initializes the fields required to initialize the Properties object and
        //creates it.  If a properties file does not exist, one will be created 
        //in the working diresctory.
        this.userDir = System.getProperty("user.dir");
        this.fileSep = File.separator;
        this.applicationProperties = new Properties();
        this.propertiesFile = new File(userDir + fileSep + "suncertify.properties");
        if (!(this.propertiesFile.exists())) {
            try {
                this.propertiesFile.createNewFile();
            } catch (IOException ex) {
                ApplicationRunner.handleException(
                        "Cannot create a properties file.\nSystem shutting down.");
            }
        }
        
        //Creates a FileInputStream to read the properties into the Properties 
        //object.
        FileInputStream propertiesIn = null;
        try {
            propertiesIn = new FileInputStream(this.propertiesFile);
        } catch (FileNotFoundException ex) {
            ApplicationRunner.handleException(ex.getMessage());
        }

        //Loads the Properties object with the key / value pairs.
        try {
            this.applicationProperties.load(propertiesIn);
            propertiesIn.close();
        } catch (IOException ex) {
            ApplicationRunner.handleException(ex.getMessage());
        }
        
        //Creates the Swing Conponets JLabels according to the 
        //ApplicationMode.
        if (this.mode == ApplicationMode.NETWORK_CLIENT) {
            this.dbLocationLabel = new JLabel("Database Location:");
            this.serverIpAddLabel = new JLabel("Server Address:");
            this.serverPortLabel = new JLabel("Server Port:");
        } else if (this.mode == ApplicationMode.SERVER) {
            this.dbLocationLabel = new JLabel("Database Location:");
            this.serverPortLabel = new JLabel("Server Port:");
        } else if (this.mode == ApplicationMode.STANDALONE_CLIENT) {
            this.dbLocationLabel = new JLabel("Database Location:");
        }
        
        //Creates the Swing Components JTextFields according to the 
        //ApplicationMode.
        
        //Network client mode.
        if (this.mode == ApplicationMode.NETWORK_CLIENT) {
            this.serverIpAddField = new JTextField(10);
            this.serverIpAddField.setToolTipText(
                    "Enter the network address of the server here.");
            this.serverIpAddField.setText(
                    applicationProperties.getProperty("dataFile.serverIp"));
            this.serverPortField = new JTextField(5);
            this.serverPortField.setToolTipText(
                    "Enter the address of the server.");
            this.serverPortField.setText(
                    applicationProperties.getProperty("dataFile.serverIp"));
            this.serverPortField.setToolTipText(
                    "Enter the port number you would like the server to listen "
                    + "on.");
            this.serverPortField.setText(
                    applicationProperties.getProperty("dataFile.serverPort"));
            
        //Server mode.
        } else if (this.mode == ApplicationMode.SERVER) {
            
            this.serverPortField = new JTextField(5);
            this.serverPortField.setText(
                    applicationProperties.getProperty("dataFile.serverIp"));
            this.serverPortField.setToolTipText(
                    "Enter the port number you would like the server to listen "
                    + "on.");
            this.serverPortField.setText(
                    applicationProperties.getProperty("dataFile.serverPort"));
        }
        
        //Creates the dbLocationField JTextField and the fileLocationButton 
        //JButton and adds an ActionListener to it.  If the mode is set to 
        //server or stand alone.
        if (this.mode == ApplicationMode.SERVER || 
                this.mode == ApplicationMode.STANDALONE_CLIENT) {
            this.dbLocationField = new JTextField(50);
            this.dbLocationField.setEditable(false);
            this.dbLocationField.setToolTipText(
                    "Select the database file location using the browse button "
                    + "to the right.");
            this.dbLocationField.setText(
                    applicationProperties.getProperty("dataFile.file"));
            this.fileLocationButton = new JButton("...");
            this.fileLocationButton.setToolTipText(
                    "Click to browse for the database file.");
            this.fileLocationButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    //Creates a FileChooser to select the database file.
                    BookingConfigurationPanel.this.chooser = new JFileChooser();
                    BookingConfigurationPanel.this.chooser.setCurrentDirectory(
                            new File(userDir));
                    BookingConfigurationPanel.this.chooser.setFileSelectionMode(
                            JFileChooser.FILES_ONLY);
                    BookingConfigurationPanel.this.chooser.setFileFilter(
                            new DataFileFilter());
                    BookingConfigurationPanel.this.chooser.
                            setAcceptAllFileFilterUsed(false);

                    //Extracts the approve / canel option selection from the file 
                    //chooser.
                    int result = chooser.showOpenDialog(
                            BookingConfigurationPanel.this);

                    //If approve selected, the absolute path and file is set as text 
                    //into the dbLocationField.
                    if (result == JFileChooser.APPROVE_OPTION) {
                        try {
                            BookingConfigurationPanel.this.dbLocationField.setText(
                                    BookingConfigurationPanel.this.chooser.
                                    getSelectedFile().
                                    getCanonicalPath());
                        } catch (IOException ex) {
                            ApplicationRunner.handleException(ex.toString());
                        }
                    }
                }

            });
        }
        
        //Creates the fileLocationButton JButton and adds an ActionListener to 
        //it according to the ApplicationMode. If the mode is set to network 
        //client or stand alone.
        if (this.mode == ApplicationMode.NETWORK_CLIENT 
                || this.mode == ApplicationMode.STANDALONE_CLIENT) {
            this.confirmButton = new JButton("Confirm");
            this.confirmButton.setToolTipText(
                    "Click \"Confirm\" to start application.");
            this.confirmButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    //If application mode is a ApplicationMode.NETWORK_CLIENT the 
                    //confirmNwClientAction() method is called to set its action 
                    //for a network client.
                    if (BookingConfigurationPanel.this.mode 
                            == ApplicationMode.NETWORK_CLIENT) {
                        BookingConfigurationPanel.this.confirmNwClientAction();
                    //Else if application mode is a ApplicationMode.STANDALONE_CLIENT 
                    //the confirmActionStandAlone() method is called to set its action 
                    //for a stand alone application.
                    } else if (
                            BookingConfigurationPanel.this.mode 
                            == ApplicationMode.STANDALONE_CLIENT) {
                        BookingConfigurationPanel.this.confirmActionStandAlone();
                    }
                }

            });
        }
        
        //Creates the exitButton JButton and adds an ActionListener to 
        //it.  The ApplicationMode is not needed here due to this being common 
        //to the all three modes and the same functionality needed.
        this.exitButton = new JButton(this.closeProgramAction);
        
        //Creates the startServerButton JButton and adds an ActionListener to 
        //it.  If the mode is set to server.
        if (this.mode == ApplicationMode.SERVER) {
            this.startServerButton = new JButton("Start Server");
            this.startServerButton.setToolTipText("Click to start the server");
            this.startServerButton.addActionListener(new ActionListener() {

                //Starts the server.
                @Override
                public void actionPerformed(ActionEvent e) {
                    //Creates a separate Thread so it does not block the EventQueue 
                    //Thread when the server starts.
                     BookingConfigurationPanel.this.serverThread 
                             = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                String msg = "You must select a valid database "
                                        + "file \nlocation with a valid file "
                                        + "extention \".db\"and\na five digit "
                                        + "between 1025 and 49152 must be"
                                        + "\nentered into the port number "
                                        + "before the\nserver can start.";
                                
                                //Checks to see if a file location and port number
                                //has been entered corectly.
                                String portEntry 
                                        = BookingConfigurationPanel.this.
                                        serverPortField.getText().trim();
                                
                                //Create a matcher for the port entry.
                                Pattern portPattern = Pattern.compile("\\d{4,5}");
                                Matcher portMatcher 
                                        = portPattern.matcher(portEntry);
                                
                                //Check that the port entry is a number between 
                                //4 and 5 digits.
                                if ((!(portMatcher.matches()))) {
                                    JOptionPane.showMessageDialog(
                                            BookingConfigurationPanel.this, 
                                            msg);
                                    return;
                                }
                                
                                //Convert the port entry to a int for futher 
                                //checking.
                                int portNumber = Integer.parseInt(portEntry);
                                
                                //Check the entries.
                                if (!(BookingConfigurationPanel.this.
                                        dbLocationField.getText().trim().endsWith(".db")) 
                                        || (portNumber < 1025 || portNumber > 49152)) {
                                    JOptionPane.showMessageDialog(
                                            BookingConfigurationPanel.this, 
                                            msg);
                                    return;
                                }
                                
                                //Reads the stores configuation from the server 
                                //configuation panel.
                                String dbPath 
                                        = BookingConfigurationPanel.this.
                                        dbLocationField.getText().trim();
                                BookingConfigurationPanel.this.
                                        applicationProperties.setProperty(
                                        "dataFile.file", dbPath);
                                String serverPort = BookingConfigurationPanel.this.
                                        serverPortField.getText().trim();
                                BookingConfigurationPanel.this.applicationProperties.
                                        setProperty("dataFile.serverPort", serverPort);

                                //Creates a FileOutputStream to store the information 
                                //above in the suncertify.properties file.
                                FileOutputStream propertiesOut = null;
                                try {
                                     propertiesOut 
                                             = new FileOutputStream(
                                                     BookingConfigurationPanel.this.
                                                     propertiesFile);
                                } catch (FileNotFoundException ex) {
                                    ApplicationRunner.handleException(ex.toString());
                                }

                                //Stores the information above in the 
                                //suncertify.properties file.
                                try {
                                    BookingConfigurationPanel.this.
                                            applicationProperties.store(
                                                    propertiesOut, null);
                                    propertiesOut.flush();
                                    propertiesOut.close();
                                } catch (IOException ex) {
                                    ApplicationRunner.handleException(ex.toString());
                                }

                                //Creates a BookingNwServer and updates the server
                                //field.
                                BookingConfigurationPanel.this.server 
                                        =  new BookingNwServer();

                                //Disables the configuation buttons and exitButton and 
                                //enables the stopServerButton.
                                BookingConfigurationPanel.this.dbLocationField.
                                        setEnabled(false);
                                BookingConfigurationPanel.this.fileLocationButton.
                                        setEnabled(false);
                                BookingConfigurationPanel.this.serverPortField.
                                        setEnabled(false);
                                BookingConfigurationPanel.this.startServerButton.
                                        setEnabled(false);
                                BookingConfigurationPanel.this.stopServerButton.
                                        setEnabled(true);
                                BookingConfigurationPanel.this.exitButton.
                                        setEnabled(false);

                                //Starts the server.
                                BookingConfigurationPanel.this.server.startServer();
                            } catch (FileNotFoundException ex) {
                                ApplicationRunner.handleException(ex.toString());
                            } catch (IOException ex) {
                                ApplicationRunner.handleException(ex.toString());
                            }
                        }

                    });

                    //Starts the serverThread.
                    BookingConfigurationPanel.this.serverThread.start();
                }

            });
        }
        
        //Creates the stopServerButton JButton and adds an ActionListener to 
        //it.  If the mode is set to server.
        if (this.mode == ApplicationMode.SERVER) {
            this.stopServerButton = new JButton("Stop Server");
            this.stopServerButton.setToolTipText("Click to stop the server.");
            this.stopServerButton.setEnabled(false);//Not needed until server started.
            this.stopServerButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    //Shuts the server down gracfully as it had handed the sockets 
                    //over to the server service, therefore, clients already connected
                    //can finish their jobs.
                    BookingConfigurationPanel.this.server.shutDownServer();

                    //Reset the server to null.
                    BookingConfigurationPanel.this.server = null;

                    //Disables the stopServerButton and enables the configuation 
                    //buttons.
                    BookingConfigurationPanel.this.stopServerButton.setEnabled(false);
                    BookingConfigurationPanel.this.dbLocationField.setEnabled(true);
                    BookingConfigurationPanel.this.fileLocationButton.setEnabled(true);
                    BookingConfigurationPanel.this.serverPortField.setEnabled(true);
                    BookingConfigurationPanel.this.startServerButton.setEnabled(true);
                    BookingConfigurationPanel.this.exitButton.setEnabled(true);
                }

            });
        }
        
        //Creates a button panel and adds the appropriate buttons according to 
        //the mode.
        this.buttonPanel = new JPanel();
        if (this.mode == ApplicationMode.NETWORK_CLIENT 
                || this.mode == ApplicationMode.STANDALONE_CLIENT) {
            this.buttonPanel.add(this.confirmButton);
            this.buttonPanel.add(this.exitButton);
        } else if (mode == ApplicationMode.SERVER) {
            this.buttonPanel.add(this.startServerButton);
            this.buttonPanel.add(this.stopServerButton);
            this.buttonPanel.add(this.exitButton);
        }
        
        //The application mode specific init() methods are then called to 
        //initialize the panel according to the mode.
        if (this.mode == ApplicationMode.NETWORK_CLIENT) {
            this.initNwClient();
        } else if (this.mode == ApplicationMode.SERVER) {
            this.initServer();
        } else if (this.mode == ApplicationMode.STANDALONE_CLIENT) {
            this.initStandAlone();
        }
        
        //Creates a GridBagConstraints reference.
        GridBagConstraints gridBagConstraints;
        
        //Sets the button panel grid bag constraints and adds it to the panel 
        //as this is a common place for all modes.
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        this.add(this.buttonPanel, gridBagConstraints);
    }
    
    /**
     * The network client specific initialization and build method used in the 
     * <code>init()</code> called in the constructor which builds the components 
     * and functionality of the network client specific panel.
     */
    private void initNwClient() {
        //Creates a GridBagConstraints reference.
        GridBagConstraints gridBagConstraints;
        
        //Sets the serverIpAddLabel grid bag constraints and adds it to the 
        //panel.
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(10, 50, 10, 10);
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        this.add(this.serverIpAddLabel, gridBagConstraints);
        
        //Sets the serverIpAddField grid bag constraints and adds it to the panel.
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 50);
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        this.add(this.serverIpAddField, gridBagConstraints);
        
        //Sets the serverPortLabel grid bag constraints and adds it to the panel.
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        this.add(this.serverPortLabel, gridBagConstraints);
        
        //Sets the serverPortField grid bag constraints and adds it to the panel.
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        this.add(this.serverPortField, gridBagConstraints);
    }
    
    /**
     * The server application specific initialization and build method used 
     * in the <code>init()</code> called in the constructor which builds the 
     * components and functionality of the server application specific panel.
     */
    private void initServer() {
        //Creates a GridBagConstraints reference.
        GridBagConstraints gridBagConstraints;
        
        //Sets the dbLocationLabel grid bag constraints and adds it to the panel.
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        this.add(this.dbLocationLabel, gridBagConstraints);
        
        //Sets the dbLocationField grid bag constraints and adds it to the panel.
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        this.add(this.dbLocationField, gridBagConstraints);
        
        //Sets the fileLocationButton grid bag constraints and adds it to the 
        //panel.
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 15);
        this.add(this.fileLocationButton, gridBagConstraints);
        
        //Sets the serverPortLabel grid bag constraints and adds it to the panel.
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        this.add(this.serverPortLabel, gridBagConstraints);
        
        //Sets the serverPortField grid bag constraints and adds it to the panel.
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        this.add(this.serverPortField, gridBagConstraints);
    }
    
    /**
     * The stand alone application specific initialization and build method used 
     * in the <code>init()</code> called in the constructor which builds the 
     * components and functionality of the stand alone application specific panel.
     */
    private void initStandAlone() {
        //Creates a GridBagConstraints reference.
        GridBagConstraints gridBagConstraints;
        
        //Sets the dbLocationLabel grid bag constraints and adds it to the panel.
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        gridBagConstraints.anchor = GridBagConstraints.EAST;
        this.add(this.dbLocationLabel, gridBagConstraints);
        
        //Sets the dbLocationField grid bag constraints and adds it to the panel.
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 10);
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        this.add(this.dbLocationField, gridBagConstraints);
        
        //Sets the fileLocationButton grid bag constraints and adds it to the panel.
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new Insets(10, 10, 10, 15);
        this.add(this.fileLocationButton, gridBagConstraints);
    }
    
    /**
     * This method is used to add the functionality to the action listener of 
     * the <code>confirmButton</code> if the mode is stand alone application.
     */
    private void confirmActionStandAlone() {
        try {
            //Get the text from the JTextFields and set the properties to 
            //persist them.
            String dbPath = this.dbLocationField.getText().trim();
            this.applicationProperties.setProperty("dataFile.file", dbPath);

            //Create FileOutputStream and write the properties to file.
            FileOutputStream propertiesOut = null;
            try {
                 propertiesOut = new FileOutputStream(this.propertiesFile);
            } catch (FileNotFoundException ex) {
                ApplicationRunner.handleException(ex.getMessage());
            }
            this.applicationProperties.store(propertiesOut, null);
            propertiesOut.flush();
            propertiesOut.close();
            
            //Close the dialog containing this panel.
            this.dialog.setVisible(false);
            
            //Start the application as a stand alone client.
            DBAccess dbAccess = new Data();
            BookingBusinessAdapterImp dataAccessAdapter 
                    = new BookingBusinessAdapterImp(dbAccess);
            BookingModel model = new BookingModelImp(dataAccessAdapter);
            final BookingGuiImp gui = new BookingGuiImp();
            BookingView view = new BookingViewImp(model, gui);
            BookingController cont = new BookingControllerImp(model, view);
            
            //Start the GUI.
            EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    gui.setVisible(true);
                }

            });   
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(
                    null, 
                    "\nFile not found, check file path\nApplication Shutting Down");
            System.exit(0);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    null, "\nFile IO error\nApplication Shutting Down");
            System.exit(0);
        }
    }
    
    /**
     * This method is used to add the functionality to the action listener of 
     * the <code>confirmButton</code> if the mode is network client.
     */
    private void confirmNwClientAction() {
        try {
            //Get the text from the JTextFields and 
            String serverAdd = this.serverIpAddField.getText().trim();
            String serverPort = this.serverPortField.getText().trim();
            
            //Set the properties to persist them.
            this.applicationProperties.setProperty(
                    "dataFile.serverIp", serverAdd);
            this.applicationProperties.setProperty(
                    "dataFile.serverPort", serverPort);

            //Create FileOutputStream and write the properties to file.
            FileOutputStream propertiesOut = null;
            try {
                propertiesOut = new FileOutputStream(this.propertiesFile);
            } catch (FileNotFoundException ex) {
                ApplicationRunner.handleException(ex.getMessage());
            }
            this.applicationProperties.store(propertiesOut, null);
            propertiesOut.flush();
            propertiesOut.close();
            
            //Close the dialog containing this panel.
            this.dialog.setVisible(false);
            
            //Start the application as a network client.
            this.client = new BookingNwClient();
            BookingModel model = new BookingModelNwImp(this.client);
            final BookingGuiImp gui = new BookingGuiImp();
            BookingView view = new BookingViewImp(model, gui);
            BookingController cont = new BookingControllerImp(model, view);
            
            //Start the GUI.
            EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    gui.setVisible(true);
                }

            });
            
            //Connect the client.
            this.client.connect();
        } catch (IOException ex) {
            ApplicationRunner.handleException(ex.getMessage());
        }
    }
    
}
