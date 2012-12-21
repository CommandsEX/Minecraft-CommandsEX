package com.github.ikeirnez.commandsex.handlers;


import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.github.ikeirnez.commandsex.CommandsEX;
import com.github.ikeirnez.commandsex.api.IEvent;
import com.github.ikeirnez.commandsex.api.IInit;

public class TestEvent implements IEvent, IInit {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent e){
        e.setMessage(ChatColor.GOLD + e.getMessage());
    }

    @Override
    public void init(CommandsEX cex, FileConfiguration config) {
        
    }
    
}
