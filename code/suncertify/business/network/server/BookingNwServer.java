package suncertify.business.network.server;

import suncertify.business.BookingBusinessAdapterImp;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import suncertify.business.BookingBusinessAdapter;
import suncertify.db.DBAccess;
import suncertify.db.Data;
import suncertify.presentation.ApplicationRunner;
import suncertify.presentation.BookingModel;
import suncertify.presentation.BookingModelImp;

/**
 * The class is the socket server responsible for listening and connecting any 
 * client that request connection, then passing the socket on to a startService 
 * using a thread pool so it can take multiple clients.
 * 
 * @author Robert Black
 * @version 1.0
 */
public class BookingNwServer {
    
    /**
     * Holds the maximum number of threads allowed in the thread pool.
     */
    private static final int NTHREADS = 100;
    
    /**
     * Holds the <code>Executor</code> used to create the thread pool.
     */
    private static Executor exec;
    
    /**
     * Holds the port number the server will listen on.
     */
    private int port;
    
    /**
     * Holds the server socket.
     */
    private ServerSocket serverSocket;
    
    /**
     * Holds the socket created by a request to be passed to a startService.
     */
    private Socket serviceSocket;
    
    /**
     * Holds the server startService that the socket is passed to in its own 
     * thread.
     */
    private ServerService serverService;

    /**
     * The default constructor that builds the instance to the 
     * <code>BookingNwServer</code>.
     *
     * @throws FileNotFoundException  if the properties file cannot be found.
     * @throws IOException  if the properties file cannot be read or written to.
     */
    public BookingNwServer() 
            throws FileNotFoundException, IOException {
            //Get the systems interpertaion of the useres directory
            String userDir = System.getProperty("user.dir");

            //Get the systems interpertaion of the file separator character.
            String fileSep = File.separator;

            //Create a properties object.
            Properties applicationProperties = new Properties();

            //Create a file object to represent the properties file and any 
            //associated streams needed if it exists.
            File propertiesFile = new File(userDir + fileSep 
                    + "suncertify.properties");
            FileInputStream propertiesIn = new FileInputStream(propertiesFile);

            //Load the properties stord on file to the properties object.
            applicationProperties.load(propertiesIn);

            //Close the FileInputStream.
            propertiesIn.close();

            //Initailizes the Executor for the thread pool.
            BookingNwServer.exec = Executors.newFixedThreadPool(NTHREADS);

            //Read the required properties and store them into their respective 
            //variable.
            String propertiesPort = 
                    applicationProperties.getProperty("dataFile.serverPort").trim();
            this.port = Integer.parseInt(propertiesPort);
    }
    
    /**
     * This method is used start the server and creates a 
     * <code>ServerSocket</code> that listens on the stored port number.  It 
     * then enters a while loop until a request comes in.  When a request comes 
     * in it passes the socket to a startService in its own thread in the thread 
     * pool created by the <code>Executor</code> allowing the server to listen 
     * for further incoming requests.
     */
    public void startServer() {
        try {
            //Creating the ServerSocket.
            this.serverSocket = new ServerSocket(port);
            
            while (true) {
                //the ServerSocket is now waiting for a request.
                this.serviceSocket = this.serverSocket.accept();
                
                //This is a Runnable that contains a task which passed on the 
                //socket to a startService and starts the startService in a new 
                //thread in the thread pool.
                Runnable serviceTask = new Runnable() {

                    @Override
                    public void run() {
                        BookingNwServer.this.startService(
                                BookingNwServer.this.serviceSocket);
                    }

                };
                BookingNwServer.exec.execute(serviceTask);
            }
        } catch (IOException ex) {
            ApplicationRunner.handleException("Server has shut down.");
        }
    }

    /**
     * This method is used start a startService with a <code>ServerService</code> 
     * startService passing the <code>Socket</code> to it.
     * 
     * @param socket 
     */
    private void startService(Socket socket) {
        try {
            DBAccess dbAccess = new Data();
            BookingBusinessAdapter businessLodgic 
                    = new BookingBusinessAdapterImp(dbAccess);
            BookingModel serverModel = new BookingModelImp(businessLodgic);
            ServerService bookingService = new BookingServiceImp(serverModel);
            bookingService.startService(socket);
        } catch (FileNotFoundException ex) {
            ApplicationRunner.handleException(ex.getMessage());
        } catch (IOException ex) {
            ApplicationRunner.handleException(ex.getMessage());
        }
    }
    
    /**
     * This method shuts down the server by closing the 
     * <code>ServerSocket</code>.
     */
    public void shutDownServer() {
        try {
            //Check to see if the ServerSocket is not null.
            if (this.serverSocket != null) {
                //Close the ServerSocket.
                this.serverSocket.close();
                
            }
        } catch (SocketException ex) {
            ApplicationRunner.handleException(ex.getMessage());
        } catch (IOException ex) {
            ApplicationRunner.handleException(ex.getMessage());
        } finally {
            //Set the serverSocket to null.
            this.serverSocket = null;
        }
    }
    
}
