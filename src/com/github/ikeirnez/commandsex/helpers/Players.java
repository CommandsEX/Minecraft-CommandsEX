package com.github.ikeirnez.commandsex.helpers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Players {

    /**
     * Gets an online player
     * If the arg begins with x: then we will get the player by that exact name
     * @param arg The players name to get
     * @return The player
     */
    public static Player getPlayer(String arg){
        if (arg.startsWith("x:")){
            String name = arg.replaceFirst("x:", "");
            Player toReturn = Bukkit.getPlayerExact(name);
            
            if (toReturn == null){
                toReturn = Bukkit.getOfflinePlayer(name).getPlayer();
            }
            
            return toReturn;
        } else {
            return Bukkit.getPlayer(arg);
        }
    }
}
