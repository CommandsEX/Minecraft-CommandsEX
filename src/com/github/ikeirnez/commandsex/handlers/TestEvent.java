package com.github.ikeirnez.commandsex.handlers;


import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.github.ikeirnez.commandsex.api.interfaces.IEvent;

public class TestEvent implements IEvent {

    @EventHandler(ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent e){
        e.setMessage(ChatColor.GOLD + e.getMessage());
    }
    
}
