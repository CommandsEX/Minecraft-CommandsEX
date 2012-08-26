package com.github.zathrus_writer.commandsex.handlers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
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
			
			final boolean helmetEnabled = player.hasPermission("cex.death.keep.armor.helmet");
			final boolean chestplateEnabled = player.hasPermission("cex.death.keep.armor.chestplate");
			final boolean leggingsEnabled = player.hasPermission("cex.death.keep.armor.leggings");
			final boolean bootsEnabled = player.hasPermission("cex.death.keep.armor.boots");
			final boolean xpEnabled = player.hasPermission("cex.death.keep.xp");
			
			final List<ItemStack> contents = new ArrayList<ItemStack>();
			final ItemStack helmet = player.getInventory().getHelmet();
			final ItemStack chestplate = player.getInventory().getChestplate();
			final ItemStack leggings = player.getInventory().getLeggings();
			final ItemStack boots = player.getInventory().getBoots();

			for (ItemStack is : player.getInventory().getContents()){
				if (is != null){
					if (player.hasPermission("cex.death.keep.inventory." + is.getTypeId())
							&& !player.hasPermission("-cex.death.keep.inventory." + is.getTypeId())){
						player.getInventory().remove(is);
						contents.add(is);
					}
				}
			}

			if(helmetEnabled){
				player.getInventory().setHelmet(new ItemStack(Material.AIR));
			}
			
			if (chestplateEnabled){
				player.getInventory().setChestplate(new ItemStack(Material.AIR));
			}
			
			if (leggingsEnabled){
				player.getInventory().setLeggings(new ItemStack(Material.AIR));
			}
			
			if (bootsEnabled){
				player.getInventory().setBoots(new ItemStack(Material.AIR));
			}

			if (xpEnabled){
				e.setKeepLevel(true);
				e.setDroppedExp(0);
			}
			
			Bukkit.getScheduler().scheduleSyncDelayedTask(CommandsEX.plugin, new Runnable() {
				@Override
				public void run() {
					for (ItemStack is : contents){
						player.getInventory().addItem(is);
					}
					
					if (helmetEnabled){
						player.getInventory().setHelmet(helmet);
					}
					
					if (chestplateEnabled){
						player.getInventory().setChestplate(chestplate);
					}
					
					if (leggingsEnabled){
						player.getInventory().setLeggings(leggings);
					}
					
					if (bootsEnabled){
						player.getInventory().setBoots(boots);
					}
				}
			});
		}
	}
}
