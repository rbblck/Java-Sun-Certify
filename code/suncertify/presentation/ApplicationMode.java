package suncertify.presentation;

/**
 * Specifies the modes the application can run in.
 * 
 * @author Robert Black
 * @version 1.0
 */
public enum ApplicationMode {
    
    /** 
     * Application will be a standalone client - no network access. 
     */
    STANDALONE_CLIENT,
    
    /**
     * This is used when the user has not specified any command line parameters 
     * when starting the application, so we know that we are going to be making 
     * a network connection.
     */
    NETWORK_CLIENT,
    
    /** 
     * The server application. 
     */
    SERVER
}

