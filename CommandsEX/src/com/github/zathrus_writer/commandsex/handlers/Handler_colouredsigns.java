package com.github.zathrus_writer.commandsex.handlers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Handler_colouredsigns implements Listener {

	public Handler_colouredsigns(){
		CommandsEX.plugin.getServer().getPluginManager().registerEvents(this, CommandsEX.plugin);
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onSignChange(SignChangeEvent e){
		Player player = e.getPlayer();
		if (player.hasPermission("cex.signs.format")){
			for (int i = 0; i <= 3; i++){
				e.setLine(i, Utils.replaceChatColors(e.getLine(i)));
			}
		}
	}
	
}
