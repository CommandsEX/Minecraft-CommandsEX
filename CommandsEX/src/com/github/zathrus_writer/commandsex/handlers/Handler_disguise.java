package com.github.zathrus_writer.commandsex.handlers;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.Disguise;

public class Handler_disguise implements Listener {

	public Handler_disguise(){
		CommandsEX.plugin.getServer().getPluginManager().registerEvents(this, CommandsEX.plugin);
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent e){
		Player player = e.getPlayer();
		if (Disguise.ids.containsKey(player.getName())){
			for (Player p : Bukkit.getOnlinePlayers()){
				((CraftPlayer) p).getHandle().netServerHandler.sendPacket(Disguise.movePacket(e.getTo(), Disguise.ids.get(player.getName())));
			}
		}
	}
	
}
