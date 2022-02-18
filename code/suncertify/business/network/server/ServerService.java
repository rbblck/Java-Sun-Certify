package suncertify.business.network.server;

import java.net.Socket;

/**
 * This interface is to be implemented to server services so that the strategy 
 * design pattern can be applied to the server enabling it to supply any server 
 * service that implements this interface.
 * 
 * @author Robert Black
 * @version 1.0
 */
public interface ServerService {
    
    /**
     * This method will start the server service to what ever socket is passed 
     * to it.
     * 
     * @param socket the socket to be used.
     */
    void startService(Socket socket);
    
}
