package com.github.zathrus_writer.commandsex.helpers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.api.afk.Afk;

public class AFK implements Listener {
	
	public AFK(){
		Bukkit.getPluginManager().registerEvents(this, CommandsEX.plugin);
		
		int scheduleTime = (Afk.getIdleTimeToAfk() <= Afk.getTimeToKick() ? Afk.getIdleTimeToAfk() : Afk.getTimeToKick());
		if (CommandsEX.getConf().getBoolean("afk.autoAfk")){
			Bukkit.getScheduler().scheduleSyncRepeatingTask(CommandsEX.plugin, new Runnable(){
				@Override
				public void run() {
					Afk.checkPlayerIdleTimes();
				}
				
			}, scheduleTime * 20L, scheduleTime * 20L);
		}
	}

	public static void init(CommandsEX plugin){
		new AFK();
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent e){
		String pName = e.getPlayer().getName();
		Afk.removeAfkRecords(pName);
	}
	
	/**
	 * Events that will reset a players idle time
	 */
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent e){
		Afk.resetPlayerIdleTime(e.getPlayer().getName());
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerMove(PlayerMoveEvent e){
		Location from = e.getFrom();
		Location to = e.getTo();
		
		// don't reset their idle time for little movements
		if (from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ()){
			Afk.resetPlayerIdleTime(e.getPlayer().getName());
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerInteract(PlayerInteractEvent e){
		Afk.resetPlayerIdleTime(e.getPlayer().getName());
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerInteractEntity(PlayerInteractEntityEvent e){
		Afk.resetPlayerIdleTime(e.getPlayer().getName());
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerBedEnter(PlayerBedEnterEvent e){
		Afk.resetPlayerIdleTime(e.getPlayer().getName());
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerBedLeave(PlayerBedLeaveEvent e){
		Afk.resetPlayerIdleTime(e.getPlayer().getName());
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerDropItem(PlayerDropItemEvent e){
		Afk.resetPlayerIdleTime(e.getPlayer().getName());
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerRespawn(PlayerRespawnEvent e){
		Afk.resetPlayerIdleTime(e.getPlayer().getName());
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerChat(AsyncPlayerChatEvent e){
		Afk.resetPlayerIdleTime(e.getPlayer().getName());
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerInventoryOpen(InventoryOpenEvent e){
		HumanEntity he = e.getPlayer();
		if (he instanceof Player){
			Afk.resetPlayerIdleTime(((Player) he).getName());
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerInventoryClose(InventoryCloseEvent e){
		HumanEntity he = e.getPlayer();
		if (he instanceof Player){
			Afk.resetPlayerIdleTime(((Player) he).getName());
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerBlockPlace(BlockPlaceEvent e){
		Afk.resetPlayerIdleTime(e.getPlayer().getName());
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerBlockBreak(BlockBreakEvent e){
		Afk.resetPlayerIdleTime(e.getPlayer().getName());
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerDamage(EntityDamageByEntityEvent e){
		Entity en = e.getDamager();
		if (en instanceof Player){
			Afk.resetPlayerIdleTime(((Player) en).getName());
		}
	}
	
}
