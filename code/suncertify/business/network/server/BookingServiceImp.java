package suncertify.business.network.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import suncertify.business.network.commands.Command;
import suncertify.presentation.ApplicationRunner;
import suncertify.presentation.BookingModel;

/**
 * This class implements the <code>ServerServce</code> interface so it can be 
 * passed to the <code>BookingNwServer</code> using the strategy design pattern.  
 * This is a booking startService that the client can use to manage bookings.
 * 
 * @author Robert Black
 */
public class BookingServiceImp implements ServerService {
    
    /**
     * Holds the presentation model.
     */
    BookingModel model;

    /**
     * The constructor for the booking startService.  It takes a 
     * <code>BookingModel</code> and initializes the mode variable.
     * 
     * @param model the model to be used.
     */
    public BookingServiceImp(BookingModel model) {
        this.model = model;
    }

    /**
     * This method will start the startService that this class offers to what ever 
     * socket is passed to it.
     * 
     * @param socket the socket to be used.
     */
    @Override
    public void startService(Socket socket) {
        try {
            //Checks that the socket is not null.
            if (socket == null) {
                return;
            }
            
            //Create input streams.
            InputStream inputStream = socket.getInputStream();
            ObjectInputStream objectInputStream 
                    = new ObjectInputStream(inputStream);
            
            //Creat output streams
            OutputStream outStream = socket.getOutputStream();
            ObjectOutputStream objectOutStream 
                    = new ObjectOutputStream(outStream);
            objectOutStream.flush();
            
            //The while loop will wait to read an incoming command object 
            //execute it and send it back to the client.  If the client 
            //disconnects intentionally or unintentionally a SocketException 
            //will be thrown and the condition in the while loop will become 
            //false ending the while loop.
            boolean clientConnected = true;
            try {
                while (clientConnected) {
                    //Receive the command object.
                    Command command = (Command) objectInputStream.readObject();
                    
                    //Execute the command object.
                    command.execute(this.model);
                    
                    //Send the command object back.
                    objectOutStream.writeObject(command);
                    objectOutStream.flush();
                } 
            } catch (SocketException ex) {
                clientConnected = false;
            }
        } catch (ClassNotFoundException ex) {
            ApplicationRunner.handleException(ex.getMessage());
        } catch (IOException ex) {
            ApplicationRunner.handleException(ex.getMessage());
        }
    }
    
}
