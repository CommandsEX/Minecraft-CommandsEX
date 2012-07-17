package com.github.zathrus_writer.commandsex.handlers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.github.zathrus_writer.commandsex.CommandsEX;

public class Handler_devwelcome implements Listener {
	
	public Handler_devwelcome() {
		CommandsEX.plugin.getServer().getPluginManager().registerEvents(this, CommandsEX.plugin);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent e){
		if (CommandsEX.getConf().getBoolean("announceDevelopers")){
			Player player = e.getPlayer();
			for (String author : CommandsEX.authors){
				if (player.getName().equalsIgnoreCase(author)){
					Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "A CommandsEX developer has just joined the game, " + player.getName());
				}
			}
		}
	}

}
