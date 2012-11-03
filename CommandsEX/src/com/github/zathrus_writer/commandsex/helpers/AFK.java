package com.github.zathrus_writer.commandsex.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

public class AFK implements Listener {
	public static HashMap<String, Long> playerIdleTime = new HashMap<String, Long>();
	public static List<String> afkPlayers = new ArrayList<String>();
	
	public static int idleTime = CommandsEX.getConf().getInt("afk.idleTime");
	public static boolean kickEnabled = CommandsEX.getConf().getBoolean("afk.kick.enabled");
	public static int kickTime = CommandsEX.getConf().getInt("afk.kick.time");
	public static String kickMessage = ChatColor.translateAlternateColorCodes("&".charAt(0), CommandsEX.getConf().getString("afk.kick.message"));
	
	public AFK(){
		Bukkit.getPluginManager().registerEvents(this, CommandsEX.plugin);
		
		int scheduleTime = (idleTime <= kickTime ? idleTime : kickTime);
		if (CommandsEX.getConf().getBoolean("afk.autoAfk")){
			Bukkit.getScheduler().scheduleSyncRepeatingTask(CommandsEX.plugin, new Runnable(){
				@Override
				public void run() {
					checkPlayerIdleTimes();
				}
				
			}, scheduleTime * 20L, scheduleTime * 20L);
		}
	}

	public static void init(CommandsEX plugin){
		new AFK();
	}
	
	public static void checkPlayerIdleTimes(){
		for (String player : playerIdleTime.keySet()){
			Player p = Bukkit.getPlayerExact(player);
			if (p == null){
				continue;
			}
			
			if (kickEnabled && p.hasPermission("cex.afk.kick") && (System.currentTimeMillis() - playerIdleTime.get(player)) / 1000 >= kickTime){
				p.kickPlayer(kickMessage);
				
				// send the kick message to each player in their own language
				for (Player pl : Bukkit.getOnlinePlayers()){
					LogHelper.showWarning("[" + ChatColor.RED + player + " #####afkKickNotify", pl);
				}
			} else if ((System.currentTimeMillis() - playerIdleTime.get(player)) / 1000 >= idleTime){
				setAfk(player);
			}
		}
	}

	public static void resetIdleTime(String player){
		Player p = Bukkit.getPlayerExact(player);
		
		if (p != null && p.hasPermission("cex.afk.auto")){
			if (playerIdleTime.containsKey(player)){
				playerIdleTime.remove(player);
			}
			
			if (afkPlayers.contains(player)){
				setNotAfk(player);
			}
			
			playerIdleTime.put(player, System.currentTimeMillis());
		}
	}
	
	public static void setAfk(String player){
		if (!afkPlayers.contains(player)){
			afkPlayers.add(player);
			for (Player p : Bukkit.getOnlinePlayers()){
				LogHelper.showInfo("[" + player + " #####afkIsNowAfk", p);
			}
		}
	}
	
	public static void setNotAfk(String player){
		if (afkPlayers.contains(player)){
			afkPlayers.remove(player);
			resetIdleTime(player);
			
			for (Player p : Bukkit.getOnlinePlayers()){
				LogHelper.showInfo("[" + player + " #####afkIsNoLongerAfk", p);
			}
		}
	}
	
	public static void toggleAfk(String player){
		if (afkPlayers.contains(player)){
			setNotAfk(player);
		} else {
			setAfk(player);
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent e){
		String pName = e.getPlayer().getName();
		playerIdleTime.remove(pName);
		afkPlayers.remove(pName);
	}
	
	/**
	 * Events that will reset a players idle time
	 */
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent e){
		resetIdleTime(e.getPlayer().getName());
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerMove(PlayerMoveEvent e){
		Location from = e.getFrom();
		Location to = e.getTo();
		
		// don't reset their idle time for little movements
		if (from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ()){
			resetIdleTime(e.getPlayer().getName());
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerInteract(PlayerInteractEvent e){
		resetIdleTime(e.getPlayer().getName());
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerInteractEntity(PlayerInteractEntityEvent e){
		resetIdleTime(e.getPlayer().getName());
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerBedEnter(PlayerBedEnterEvent e){
		resetIdleTime(e.getPlayer().getName());
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerBedLeave(PlayerBedLeaveEvent e){
		resetIdleTime(e.getPlayer().getName());
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerDropItem(PlayerDropItemEvent e){
		resetIdleTime(e.getPlayer().getName());
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerRespawn(PlayerRespawnEvent e){
		resetIdleTime(e.getPlayer().getName());
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerChat(AsyncPlayerChatEvent e){
		resetIdleTime(e.getPlayer().getName());
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerInventoryOpen(InventoryOpenEvent e){
		HumanEntity he = e.getPlayer();
		if (he instanceof Player){
			resetIdleTime(((Player) he).getName());
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerInventoryClose(InventoryCloseEvent e){
		HumanEntity he = e.getPlayer();
		if (he instanceof Player){
			resetIdleTime(((Player) he).getName());
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerBlockPlace(BlockPlaceEvent e){
		resetIdleTime(e.getPlayer().getName());
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerBlockBreak(BlockBreakEvent e){
		resetIdleTime(e.getPlayer().getName());
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerDamage(EntityDamageByEntityEvent e){
		Entity en = e.getDamager();
		if (en instanceof Player){
			resetIdleTime(((Player) en).getName());
		}
	}
	
}
