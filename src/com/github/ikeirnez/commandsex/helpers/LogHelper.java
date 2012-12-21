package com.github.ikeirnez.commandsex.helpers;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.ikeirnez.commandsex.CommandsEX;
import com.github.ikeirnez.commandsex.EventLog;

/**
 * Helper class for logging messages to the EventLog, Console and Players
 */
public class LogHelper {

    private static Logger logger = CommandsEX.plugin.getLogger();
    
    /**
     * Logs a message to the EventLog, will not be printed to console, with level INFO
     * @param message The message to record to the EventLog
     */
    public static void logMessage(String message){
        logMessage(message, false);
    }
    
    /**
     * Logs a message to the EventLog, with level INFO
     * @param message The message to record to the EventLog
     * @param print Should we print the message to the console?
     */
    public static void logMessage(String message, boolean print){
        logMessage(message, print, Level.INFO);
    }

    /**
     * Logs a message to the EventLog
     * @param message The message to record to the EventLog
     * @param print Should we print the message to the console?
     * @param level What level should we alert the user at?
     */
    public static void logMessage(String message, boolean print, Level level){
        if (print){
            logger.log(level, message);
        }

        EventLog.addToLog(level.getName() + " - " + message);
    }
    
    /**
     * Logs an exception to the EventLog, while printing it to the console
     * @param e The exception
     */
    public static void logException(Exception e){
        logException(e, true);
    }
    
    /**
     * Logs an exception to the EventLog
     * @param e The exception
     * @param print Should we print the exception to the console?
     */
    public static void logException(Exception e, boolean print){
        Throwable throwable = e.getCause();
        
        if (throwable != null){
            String message = throwable.getMessage();
            if (message != null){
                if (print){
                    logger.log(Level.SEVERE, message);
                }
                
                EventLog.addToLog(message);
            }
        }
        
        for (StackTraceElement ste : e.getStackTrace()){
            String message = ste.toString();
            
            if (print){
                logger.log(Level.SEVERE, message);
            }
            
            EventLog.addToLog(message);
        }
    }

}
