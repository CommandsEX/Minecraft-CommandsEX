package com.github.zathrus_writer.commandsex.handlers;

import net.minecraft.server.EntityPlayer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
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
		
		Location from = e.getFrom();
		Location to = e.getTo();
		
		if (Disguise.ids.containsKey(player.getName())){
			
			int id = Disguise.ids.get(player.getName());
			
			for (Player p : Bukkit.getOnlinePlayers()){
				EntityPlayer ePlayer = ((CraftPlayer) p).getHandle();
				
				if ((from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ())
						&& from.getYaw() != to.getYaw()){
					ePlayer.netServerHandler.sendPacket(Disguise.relMovePacket(to, id));
				} else {
					if (from.getYaw() != to.getYaw()){
						ePlayer.netServerHandler.sendPacket(Disguise.headTurnPacket(to, id));
					}
					
					if (from.getPitch() != to.getPitch()){
						ePlayer.netServerHandler.sendPacket(Disguise.lookPacket(to, id));
					}
					
					if (from.getX() != to.getX() || from.getY() != to.getY() || from.getZ() != to.getZ()){
						ePlayer.netServerHandler.sendPacket(Disguise.movePacket(to, id));
					}
				}
			}
		}
	}
}
