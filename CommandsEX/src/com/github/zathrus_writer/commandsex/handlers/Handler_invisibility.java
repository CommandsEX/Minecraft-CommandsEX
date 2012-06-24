package com.github.zathrus_writer.commandsex.handlers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.Common;

public class Handler_invisibility implements Listener {

	public Handler_invisibility() {
		CommandsEX.plugin.getServer().getPluginManager().registerEvents(this, CommandsEX.plugin);
	}
	
	@EventHandler (priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerDeath(PlayerDeathEvent e){
		Player player = (Player) e.getEntity();
		// If the player is invisible, don't show their death message
		if (Common.invisiblePlayers.contains(player.getName())){
			e.setDeathMessage("");
		}
	}
	
}
