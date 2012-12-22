package com.github.ikeirnez.commandsex.helpers;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.ikeirnez.commandsex.CommandsEX;

/**
 * Helper class for logging messages to the EventLog, Console and Players
 */
public class LogHelper {

    private static Logger logger = CommandsEX.plugin.getLogger();
    
    /**
     * Log a message with level INFO
     * @param message The message to log
     */
    public static void logInfo(String message){
        logger.log(Level.INFO, message);
    }
    
    /**
     * Log a message with level WARNING
     * @param message The message to log
     */
    public static void logWarning(String message){
        logger.log(Level.WARNING, message);
    }
    
    /**
     * Log a message with level SEVERE
     * @param message The message to log
     */
    public static void logSevere(String message){
        logger.log(Level.SEVERE, message);
    }
    
    /**
     * Log a message which will only be shown if debug mode is enabled
     * @param message The message to log if debug mode is enabled
     */
    public static void logDebug(String message){
        
    }

}
