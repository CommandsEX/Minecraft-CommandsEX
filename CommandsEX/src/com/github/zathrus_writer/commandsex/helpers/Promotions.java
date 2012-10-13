package com.github.zathrus_writer.commandsex.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.Vault;

/***
 * Contains set of functions to be used for player promotion functions.
 * @author zathrus-writer
 *
 */
public class Promotions {
	
	/***
	 * The actual delayed task to check for timed promotions and promote as needed.
	 * Used in 2 places - delayed task and Quit event.
	 */
	public static void checkTimedPromotions(Player...players) {
		// load up settings from config file
		FileConfiguration f = CommandsEX.getConf();
		ConfigurationSection configGroups = f.getConfigurationSection("timedPromote");
		
		// no groups defined
		if (configGroups == null) {
			return;
		}
		
		Map<String, Integer> settings = new HashMap<String, Integer>();
		List<?> exclusions = CommandsEX.getConf().getList("timedPromoteExclude", new ArrayList<String>());
		Set<String> keys = configGroups.getKeys(true);
		
		if (keys.size() > 0) {
			for (String s : keys) {
				// ignore default group with time 0, since that one is an example record
				if (s.equals("default") && (f.getInt("timedPromote." + s) == 0)) continue;
				settings.put(s, f.getInt("timedPromote." + s));
			}
		}
		
		// run through all online players and check their playtime
		Iterator<Entry<String, Integer>> it = CommandsEX.playTimes.entrySet().iterator();
		while (it.hasNext()) {
			Player p;
			Integer playerValue;
			if (players.length > 0) {
				p = players[0];
				playerValue = CommandsEX.playTimes.get(p.getName());
			} else {
				Map.Entry<String, Integer> playTimePairs = (Map.Entry<String, Integer>)it.next();
				p = Bukkit.getServer().getPlayer(playTimePairs.getKey());
				playerValue = playTimePairs.getValue();
			}
			String promotedTo = "";
			
			String[] tPlayerGroups = Vault.perms.getPlayerGroups(p);
			List<String> playerGroups = new ArrayList<String>();

			// if the player belongs to at least one excluded group, stop here
			for (String s : tPlayerGroups) {
				if (exclusions.contains(s)) {
					return;
				} else {
					playerGroups.add(s);
				}
			}
			// check player's playtime against config settings
			Iterator<Entry<String, Integer>> it2 = settings.entrySet().iterator();
			while (it2.hasNext()) {
				Map.Entry<String, Integer> configPairs = (Map.Entry<String, Integer>)it2.next();
				if ((playerValue >= configPairs.getValue()) && !playerGroups.contains(configPairs.getKey())) {
					promotedTo = configPairs.getKey();
				}
			}
			
			// if we have a promotion to deliver, do it here
			if (!promotedTo.equals("")) {
				if (CommandsEX.getConf().getBoolean("promoteSet")){
					// if promoteSet is true, REMOVE all groups from the player
					// and then add the group the player is being promoted to
					for (String group : Vault.perms.getPlayerGroups(p)){
						Vault.perms.playerRemoveGroup(p, group);
					}
					
					Vault.perms.playerAddGroup(p, promotedTo);
				} else {
					// if promoteSet is false, simply add the group to the player
					Vault.perms.playerAddGroup(p, promotedTo);
				}
				LogHelper.showInfo("timedPromoteMessage1", p, ChatColor.GREEN);
				LogHelper.showInfo("timedPromoteMessage2#####[" + ChatColor.AQUA + promotedTo, p, ChatColor.GREEN);
			}
			
			// exit here if we requested a single player
			if (players.length > 0) {
				return;
			}
		}
	}
	
	/***
	 * The actual delayed task to check for economy-based promotions and promote as needed.
	 * Used in 2 places - delayed task and Quit event.
	 */
	public static void checkEcoPromotions(Player...players) {
		// load up settings from config file
		FileConfiguration f = CommandsEX.getConf();
		ConfigurationSection configGroups = f.getConfigurationSection("ecoPromote");
		
		// no groups defined
		if (configGroups == null) {
			return;
		}
		
		Map<String, Double> settings = new HashMap<String, Double>();
		List<?> exclusions = CommandsEX.getConf().getList("ecoPromoteExclude", new ArrayList<String>());
		Boolean checkDemotions = f.getBoolean("ecoPromoteAutoDemote");
		Set<String> keys = configGroups.getKeys(true);
		
		if (keys.size() > 0) {
			for (String s : configGroups.getKeys(true)) {
				// ignore default group with wealth of 0, since that one is an example record
				if (s.equals("default") && (f.getInt("ecoPromote." + s) == 0)) continue;
				settings.put(s, f.getDouble("ecoPromote." + s));
			}
		}
		
		// run through all online players and check their wealth
		for(Player p : Bukkit.getServer().getOnlinePlayers()) {
			String pName = p.getName();
			Double balance = Vault.econ.getBalance(pName);
			String promotedTo = "";
			String[] tPlayerGroups = Vault.perms.getPlayerGroups(p);
			List<String> playerGroups = new ArrayList<String>();
			List<String> demotions = new ArrayList<String>();

			// if the player belongs to at least one excluded group, stop here
			for (String s : tPlayerGroups) {
				if (exclusions.contains(s)) {
					return;
				} else {
					playerGroups.add(s);
				}
			}
			// check player's playtime against config settings
			Iterator<Entry<String, Double>> it2 = settings.entrySet().iterator();
			while (it2.hasNext()) {
				Map.Entry<String, Double> configPairs = (Map.Entry<String, Double>)it2.next();
				
				// check for promotions
				if ((balance >= configPairs.getValue()) && !playerGroups.contains(configPairs.getKey())) {
					promotedTo = configPairs.getKey();
				}
				
				//check for demotions
				if (checkDemotions && playerGroups.contains(configPairs.getKey()) && (balance < configPairs.getValue())) {
					demotions.add(configPairs.getKey());
					Vault.perms.playerRemoveGroup(p, configPairs.getKey());
				}
			}

			// if we have a promotion to deliver, do it here
			if (!promotedTo.equals("")) {
				if (CommandsEX.getConf().getBoolean("promoteSet")){
					// if promoteSet is true, remove all groups from a player
					for (String group : Vault.perms.getPlayerGroups(p)){
						Vault.perms.playerRemoveGroup(p, group);
					}
				}
				
				Vault.perms.playerAddGroup(p, promotedTo);
				
				LogHelper.showInfo("ecoPromoteMessage1", p, ChatColor.GREEN);
				LogHelper.showInfo("ecoPromoteMessage2#####[" + ChatColor.AQUA + promotedTo, p, ChatColor.GREEN);

				// for demotions, do the same
				if (demotions.size() > 0) {
					LogHelper.showInfo("ecoDemoteMessage1", p);
					LogHelper.showInfo("ecoDemoteMessage2#####[" + ChatColor.AQUA + Utils.implode(demotions, ", "), p);
				}
			}
			
			// exit here if we requested a single player
			if (players.length > 0) {
				return;
			}
		}
	}
	
	/***
	 * TIME2RANK - shows how much time has a player left until he'll be auto-promoted to a higher rank
	 * @param sender
	 * @param args
	 * @param command
	 * @param alias
	 * @return
	 */
	public static Boolean time2rank(CommandSender sender, String[] args, String command, String alias) {
		if (!CommandsEX.sqlEnabled) {
			LogHelper.showInfo("playTimeNoSQL", sender, ChatColor.YELLOW);
			return true;
		}
		
		if (!CommandsEX.vaultPresent || !Vault.permsEnabled()) {
			// don't show anything when Vault is not present, except for a debug message in console
			LogHelper.logDebug("[CommandsEX] time2rank could not be invoked because Vault or at least 1 permission plugin is not present");
			return true;
		}
		
		// load up settings from config file
		FileConfiguration f = CommandsEX.getConf();
		ConfigurationSection configGroups = f.getConfigurationSection("timedPromote");
		Player p = (Player)sender;
		Long currentGroupTime = f.getLong("timedPromote." + Vault.perms.getPrimaryGroup(p));
		Long nextGroupTime = 9223372036854775807L; // maximum value of LONG in Java
		String nextRankName = "?";
		for (String s : configGroups.getKeys(true)) {
			// ignore default group with time 0, since that one is an example record
			Long t = f.getLong("timedPromote." + s);
			if (s.equals("default") && (t == 0)) continue;
			
			// check if this group has more time set than current one and set it as the next group's time
			if (t > currentGroupTime) {
				// if our current time for next group is higher than the one we found now, use the one we found,
				// otherwise leave the previous one, since it's closer to our current rank
				if (nextGroupTime > t) {
					nextGroupTime = t;
					nextRankName = s;
				}
			}
		}
		
		if (nextGroupTime == 9223372036854775807L) {
			// there are no higher ranks
			LogHelper.showInfo("timedPromoteHighestRank", sender, ChatColor.GREEN);
		} else {
			// calculate how much time we have left until the next rank
			Long remain = nextGroupTime - CommandsEX.playTimes.get(p.getName());
			Map<String, Integer> m = Utils.parseTimeStamp(remain);
			LogHelper.showInfo("timedPromoteTime2RankLeft#####[" + (m.get("days") + " #####days#####[, ") + (m.get("hours") + " #####hours#####[, ") + (m.get("minutes") + " #####minutes#####[, ") + (m.get("seconds") + " #####seconds"), sender);
			LogHelper.showInfo("timedPromoteTime2RankNextRank#####[" + nextRankName, sender);
		}

		return true;
	}
}