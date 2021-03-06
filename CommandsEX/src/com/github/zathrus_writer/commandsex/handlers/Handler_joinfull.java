package com.github.zathrus_writer.commandsex.handlers;

import com.avaje.ebeaninternal.util.ValueUtil;
import com.github.zathrus_writer.commandsex.Vault;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import com.github.zathrus_writer.commandsex.CommandsEX;

public class Handler_joinfull implements Listener {

	public Handler_joinfull(){
		CommandsEX.plugin.getServer().getPluginManager().registerEvents(this, CommandsEX.plugin);
	}
	
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent e){
		if (e.getResult() == Result.KICK_FULL){
			Player player = e.getPlayer();
			if (player.hasPermission("cex.joinfull") || (Vault.permsEnabled() && Vault.perms.has("", player.getName(), "cex.joinfull"))){
				e.allow();
			}
		}
	}
}
