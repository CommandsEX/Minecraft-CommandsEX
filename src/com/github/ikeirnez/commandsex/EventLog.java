package com.github.ikeirnez.commandsex;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventLog {

    private static Path path = Paths.get(CommandsEX.plugin.getDataFolder().getAbsolutePath() + "/log.txt");
    
    /**
     * Adds a message to the event log
     * @param line The message to add, timestamp will automagically be added
     */
    public static void addToLog(String line){
        try {
            List<String> list = new ArrayList<String>();
            list.add(new Timestamp(new Date().getTime()) + " " + line);
            Files.write(path, list, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * TODO actually write this, the current return is a placeholder
     * Gets all messages in the event log
     * @return The message
     */
    public static String[] getMessages(){
        return new String[] {};
    }
    
}
