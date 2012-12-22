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
    public static void addToEventLog(String message){
        addToEventLog(message, false);
    }
    
    /**
     * Logs a message to the EventLog, with level INFO
     * @param message The message to record to the EventLog
     * @param print Should we print the message to the console?
     */
    public static void addToEventLog(String message, boolean print){
        addToEventLog(message, print, Level.INFO);
    }

    /**
     * Logs a message to the EventLog
     * @param message The message to record to the EventLog
     * @param print Should we print the message to the console?
     * @param level What level should we alert the user at?
     */
    public static void addToEventLog(String message, boolean print, Level level){
        if (print){
            logger.log(level, message);
        }

        EventLog.addToLog(level.getName() + " - " + message);
    }
    
    /**
     * Logs an exception to the EventLog, while printing it to the console
     * @param e The exception
     */
    public static void addExceptionToEventLog(Exception e){
        addExceptionToEventLog(e, true);
    }
    
    /**
     * Logs an exception to the EventLog
     * @param e The exception
     * @param print Should we print the exception to the console?
     */
    public static void addExceptionToEventLog(Exception e, boolean print){
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

}
