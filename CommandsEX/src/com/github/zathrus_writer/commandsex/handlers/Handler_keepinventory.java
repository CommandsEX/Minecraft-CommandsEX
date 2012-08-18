package com.github.zathrus_writer.commandsex.handlers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.Common;

public class Handler_keepinventory implements Listener {
	
	/***
	 * 
	 */
	
	public Handler_keepinventory(){
		CommandsEX.plugin.getServer().getPluginManager().registerEvents(this, CommandsEX.plugin);
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e){
		final Player player = e.getEntity();
		if (!Common.keepInventoryDisabledPlayers.contains(player.getName())){
			
			final boolean invEnabled = player.hasPermission("cex.keepinventory.inventory");
			final boolean armorEnabled = player.hasPermission("cex.keepinventory.armor");
			final boolean xpEnabled = player.hasPermission("cex.keepinventory.xp");
			
			final ItemStack[] inv = player.getInventory().getContents();
			final ItemStack[] armor = player.getInventory().getArmorContents();
			
			if (invEnabled){
				player.getInventory().clear();
			}
			
			if (armorEnabled){
				player.getInventory().getArmorContents();
			}
			
			if (xpEnabled){
				e.setKeepLevel(true);
				e.setDroppedExp(0);
			}
			
			Bukkit.getScheduler().scheduleSyncDelayedTask(CommandsEX.plugin, new Runnable() {
				@Override
				public void run() {
					if (invEnabled){
						player.getInventory().setContents(inv);
					}
					
					if (armorEnabled){
						player.getInventory().setArmorContents(armor);
					}
				}
			});
		}
	}
}
