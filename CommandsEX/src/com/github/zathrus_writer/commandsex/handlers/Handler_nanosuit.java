package com.github.zathrus_writer.commandsex.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.server.Packet201PlayerInfo;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.Common;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Permissions;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Handler_nanosuit implements Listener {
	
	public static Map<String, Map<String, Object>> suitedPlayers = new HashMap<String, Map<String, Object>>();
	public static Map<String, Integer> lastUse = new HashMap<String, Integer>();
	public static List<String> powered = new ArrayList<String>();
	public static List<String> sped = new ArrayList<String>();
	public static List<String> jumps = new ArrayList<String>();
	
	/***
	 * Activate event listener.
	 */
	public Handler_nanosuit() {
		CommandsEX.plugin.getServer().getPluginManager().registerEvents(this, CommandsEX.plugin);
	}
	
	/***
	 * Cancels all NanoSuit powers for the given player.
	 * @param p
	 */
	public static void CleanupNanoPowers(Player p) {
		String pName = p.getName();
		if (!suitedPlayers.containsKey(pName)) return;
		Map<String, Object> m = suitedPlayers.get(pName);
		suitedPlayers.remove(pName);

		// deactivate periodic task showing NanoSuit power remaining time
		Bukkit.getScheduler().cancelTask((Integer) m.get("taskID"));
		
		// disable God mode
		if ((m.get("godMode") != null) && ((Integer) m.get("godMode") == 1) && Common.godPlayers.contains(pName)) {
			Common.god(p, new String[] {}, "god", "god", true);
		}
		
		// disable invisibility
		if (Common.invisiblePlayers.contains(pName)) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.showPlayer(p);
				((CraftServer) player.getServer()).getHandle().sendAll(
						new Packet201PlayerInfo(((CraftPlayer) player).getHandle().listName, true, 1000));
			}
		}
		
		// disable strength multiplier
		if (powered.contains(pName)) {
			powered.remove(pName);
		}
		
		// disable speed multiplier
		if (sped.contains(pName)) {
			p.removePotionEffect(PotionEffectType.SPEED);
			sped.remove(pName);
		}
		
		// disable jump multiplier
		if (jumps.contains(pName)) {
			p.removePotionEffect(PotionEffectType.JUMP);
			jumps.remove(pName);
		}
		
		// remove NanoSuit armor
		p.getInventory().setArmorContents(null);
		
		// tell the user
		LogHelper.showInfo("nanoSuitPowersDeactivated", p);
	}
	
	// informs player about NanoSuit's power remaining time
	public static class TimeRemaining implements Runnable {
    	private String pName;
    	
    	public TimeRemaining(String pName) {
    		this.pName = pName;
    	}
    	
    	public void run() {
    		// check if the player is still online and his NanoSuit is active
    		if (!Handler_nanosuit.suitedPlayers.containsKey(pName)) return;
    		
    		Player p = Bukkit.getPlayer(pName);
    		if (p == null) return;
    		
    		// if player has less than 10 seconds remaining, we show this each second, otherwise we show it every 10 seconds
    		Integer stamp = Utils.getUnixTimestamp(0L);
    		Integer timeRemaining = (CommandsEX.getConf().getInt("nanoSuitTime") - (stamp - Integer.parseInt("" + Handler_nanosuit.suitedPlayers.get(pName).get("timeBegin"))));
    		if (((timeRemaining < 10) && (timeRemaining > 0)) || ((timeRemaining % 10) == 0)) {
    			LogHelper.showInfo("nanoSuitTimeRemaining#####[" + timeRemaining + " #####seconds", p);
    		} else if (timeRemaining <= 0) {
    			Handler_nanosuit.CleanupNanoPowers(p);
    		}
    	}
    }
	
	/***
	 * Checks whether player has equipped full chain armor.
	 * @param e
	 * @return
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	public void checkArmor(InventoryCloseEvent e) {
		Player p = (Player) e.getPlayer();
		// can we access NanoSuit powers?
		if (!Permissions.checkPermEx(p, "cex.nanosuit")) return;
		
		String pName = p.getName();
		ItemStack[] istack = p.getInventory().getArmorContents();
		Boolean nanoSuit = true;
		
		for (ItemStack i : istack) {
			if ((i != null) && !i.getType().equals(Material.CHAINMAIL_BOOTS) && ((!CommandsEX.getConf().getBoolean("nanoSuitPumpkin") && !i.getType().equals(Material.CHAINMAIL_HELMET)) || (CommandsEX.getConf().getBoolean("nanoSuitPumpkin") && !i.getType().equals(Material.PUMPKIN))) && !i.getType().equals(Material.CHAINMAIL_LEGGINGS) && !i.getType().equals(Material.CHAINMAIL_CHESTPLATE)) {
				nanoSuit = false;
				break;
			}
		}
		
		// NanoSuit down, cancel powers immediately and update cooldown
		if (!nanoSuit && suitedPlayers.containsKey(pName)) {
			lastUse.put(pName, Utils.getUnixTimestamp(0L));
			CleanupNanoPowers(p);
			return;
		}
		
		// activate nanosuit if not in cooldown
		Integer stamp = Utils.getUnixTimestamp(0L);
		Integer time = CommandsEX.getConf().getInt("nanoSuitTime", 60);
		if (nanoSuit) {
			if ((!lastUse.containsKey(pName) || ((stamp - lastUse.get(pName)) > CommandsEX.getConf().getInt("nanoSuitRechargeTime")))) {
				
				// set up periodic time, informing the suited person about NanoSuit power time left
				Integer taskID = Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask(CommandsEX.plugin, new TimeRemaining(pName), 20, 20);
				
				Map<String, Object> m = new HashMap<String, Object>();
				m.put("timeBegin", stamp);
				m.put("taskID", taskID);
				suitedPlayers.put(pName, m);
				lastUse.put(pName, stamp + (time * 20));
				// tell the player
				LogHelper.showInfo("nanoSuitActive#####[" + time + " #####seconds", p);
				LogHelper.showInfo("nanoSuitActiveHelp", p);
			}
		}
	}
	
	/***
	 * Checks whether player doesn't have expired NanoSuit powers.
	 * @param e
	 * @return
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	public void checkExpiredSuit(PlayerJoinEvent e) {
		String pName = e.getPlayer().getName();
		if (!suitedPlayers.containsKey(pName)) return;
		Integer stamp = Utils.getUnixTimestamp(0L);
		Integer timeRemaining = (CommandsEX.getConf().getInt("nanoSuitTime") - (stamp - Integer.parseInt("" + Handler_nanosuit.suitedPlayers.get(pName).get("timeBegin"))));
		
		if (timeRemaining <= 0) {
			CleanupNanoPowers(e.getPlayer());
		}
	}
	
	/***
	 * Check if a player has NanoSuit active when they click on armor slot and disallows changes until the power has wore out.
	 * @param e
	 * @return
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	public void checkExpiredSuit(InventoryClickEvent e) {
		if (suitedPlayers.containsKey(e.getWhoClicked().getName()) && (e.getRawSlot() >= 5) && (e.getRawSlot() <= 8)) {
			// do not allow armor changes while NanoSuit is active
			e.setCancelled(true);
		}
	}
	
	/***
	 * Check if a player has NanoSuit active and multiply the damage when hitting an entity.
	 * @param e
	 * @return
	 */
	@EventHandler(priority = EventPriority.LOWEST)
	public void checkExpiredSuit(EntityDamageByEntityEvent e) {
		if (e.getDamager().getType().equals(EntityType.PLAYER) && powered.contains(((Player) e.getDamager()).getName()) && e.getEntityType().isAlive()) {
			e.setDamage((int) (e.getDamage() * CommandsEX.getConf().getDouble("nanoSuitDamage")));
		}
	}
}
