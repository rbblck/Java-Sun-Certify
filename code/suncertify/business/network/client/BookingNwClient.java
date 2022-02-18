package suncertify.business.network.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;
import javax.swing.JOptionPane;
import suncertify.presentation.ApplicationRunner;

/**
 * This class is responsible for communicating with the booking server using 
 * sockets.
 * 
 * @author Robert Black
 * @version 1.0
 */
public class BookingNwClient {
    
    /**
     * This constant sets the number of send tries.
     */
    private final int MAXRETRIES = 10;
    
    /**
     * This constant sets the time to wait between in milli seconds
     */
    private final int THREADSLEEPTIME = 5000;
    
    /**
     * Holds the server address (URL).
     */
    private String host;
    
    /**
     * Holds port number that the server is listening on.
     */
    private int port;
    
    /**
     * Holds the clients socket object.
     */
    private Socket socket;
    
    /**
     * Holds the socket input stream.
     */
    private InputStream inStream;
    
    /**
     * Holds the socket object input stream.
     */
    private ObjectInputStream objectInStream;
    
    /**
     * Holds the socket output stream.
     */
    private OutputStream outStream;
    
    /**
     * Holds the socket object output stream.
     */
    private ObjectOutputStream objectOutStream;

    /**
     * The default constructor.
     * 
     * @throws FileNotFoundException if the properties file can not be located.
     * @throws IOException if the file can not be read or written to.
     */
    public BookingNwClient() throws FileNotFoundException, IOException {
        //Creates a File object to represent the properties file.
        String userDir = System.getProperty("user.dir");
        String fileSep = File.separator;
        File propertiesFile = new File(userDir + fileSep 
                + "suncertify.properties");
        
        //Creates a properties object.
        Properties applicationProperties = new Properties();
        FileInputStream propertiesIn = new FileInputStream(propertiesFile);
        propertiesIn = new FileInputStream(propertiesFile);
        applicationProperties.load(propertiesIn);
        propertiesIn.close();
        
        //Reads the server listening port and the server address (URL) from the 
        //Properties file and sets the variables.
        String propertiesPort = 
                applicationProperties.getProperty("dataFile.serverPort").trim();
        this.host = applicationProperties.getProperty("dataFile.serverIp").trim();
        this.port = Integer.valueOf(propertiesPort);
    }
    
    /**
     * This method is responsible for connecting to the server.
     */
    public void connect() {
        try {
            //Create the client socket.
            this.socket = new Socket(host, port);
            
            //Create the out stream.
            this.outStream = this.socket.getOutputStream();
            this.objectOutStream = new ObjectOutputStream(outStream);
            
            //Makes sure all information has been sent to the server to prevent
            //a deadlock with the objectInputStream blocking trying to read the 
            //servers handshake.
            this.objectOutStream.flush();
            
            //Create the input stream.
            this.inStream = this.socket.getInputStream();
            this.objectInStream = new ObjectInputStream(this.inStream);
        } catch (UnknownHostException ex) {
            JOptionPane.showMessageDialog(
                    null, ex.getMessage() + "\nis not a valid address\nNetwork "
                    + "Client Shutting Down");
            System.exit(0);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    null, ex.getMessage() + "\nNetwork Client Shuting down");
            System.exit(0);
        }
    }
    
    /**
     * This method is responsible for sending an <code>Object</code> to the 
     * server.
     * 
     * @param obj the object to be sent.
     * @throws IOException if there is a communication problem with the serve.
     */
    public void send(Object obj) throws IOException {
        //Initailise an integer used to retry ten times.
        int retry = this.MAXRETRIES;
        
        //The while loop is used for retrys.
        while (retry > 0) {
            try {
                //Writes the object to the stream
                this.objectOutStream.writeObject(obj);
                this.objectOutStream.flush();
                
                //If successful (no exceptions) then the retry variable is set to 
                //0 to leave the while loop.
                retry = 0;
            } catch (IOException ex) {
                //Counnts down the send trys.
                retry--;
                
                //If ten unsuccessful trys, then throws and exception
                if (retry == 0) {
                    throw ex;
                }
                try {
                    Thread.sleep(this.THREADSLEEPTIME);
                } catch (InterruptedException ex1) {
                    ApplicationRunner.handleException(ex1.getMessage());
                }
                this.connect();
            }
        }
    }
    
    /**
     * This method is responsible for receiving an object from the server.
     * 
     * @return the object from the server.
     * @throws ClassNotFoundException if class of a serialized object cannot be
     * found.
     * @throws IOException if there is a communication problem with the serve.
     */
    public Object receive() throws ClassNotFoundException, IOException {
        Object obj = null;
        try {
            obj = this.objectInStream.readObject();
        } catch (ClassNotFoundException ex) {
            connect();
            throw ex;
        } catch (IOException ex) {
            connect();
            throw ex;
        }
        
        return obj;
    }
    
    /**
     * Closes the client socket.
     */
    public void close() {
        try {
            //Left empty
        } finally {
            if (this.socket != null) {
                try {
                    this.socket.close();
                } catch (IOException ex) {
                    ApplicationRunner.handleException(ex.getMessage());
                }
            }
        }
    }
    
}
