package com.github.zathrus_writer.commandsex.helpers;

import static com.github.zathrus_writer.commandsex.Language._;

import java.io.File;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.SQLManager;

/***
 * Contains set of commands and event listeners to acommodate timed banning.
 * @author zathrus-writer
 *
 */
public class Bans {
	
	// regular expression that matches IPv4 and both compressed and uncompressed IPv6 addresses
	public static String ipV4Regex = "\\b(?:[0-9]{1,3}\\.){3}[0-9]{1,3}\\b";
	public static String ipV6Regex = "\\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)){3}))|:)))(%.+)?\\s*";
	
	/***
	 * Single-purpose class that handles pardoning of banned Players after their punishement time has passed
	 */
	public static class DelayedPardon implements Runnable {
    	private String pName;
    	
    	public DelayedPardon(String pName) {
    		this.pName = pName;
    	}
    	
    	public void run() {
    		// update database info if we can
    		if (CommandsEX.sqlEnabled) {
    			SQLManager.query("UPDATE " + SQLManager.prefix + "bans SET active = 0 WHERE player_name = ?", this.pName);
    		}
    		
    		// check if we should be pardoning player or an IP address
    		Boolean ipMatched = (this.pName.matches(Bans.ipV4Regex) || this.pName.matches(Bans.ipV6Regex));
    		if (ipMatched) {
    			Bukkit.unbanIP(this.pName);
    		} else {
	    		// unban using built-in banning system
	    		OfflinePlayer p = Bukkit.getOfflinePlayer(this.pName);
	    		p.setBanned(false);
    		}
    		
    		// tell everyone if not disallowed in config or not an IP address (so we don't foolishly reveal IP addresses that were banned :))
			if (!CommandsEX.getConf().getBoolean("silentBans") && !ipMatched) {
				CommandsEX.plugin.getServer().broadcastMessage(ChatColor.GREEN + this.pName + " " + _("bansPlayerPardoned", ""));
			}
    	}
    }
	
	/***
	 * INIT - initialization function, called when plugin is enabled to check BanHammer database conversion needs and plan auto-pardons
	 * @return
	 */
	public static void init(CommandsEX plugin) {
		// first of all, check if we can use any DB
		if (!CommandsEX.sqlEnabled) return;
		
		// next create bans table if it's not present yet
		SQLManager.query((SQLManager.sqlType.equals("mysql") ? "" : "BEGIN; ") + "CREATE TABLE IF NOT EXISTS "+ SQLManager.prefix +"bans (id_ban integer " + (SQLManager.sqlType.equals("mysql") ? "unsigned " : "") +"NOT NULL" + (SQLManager.sqlType.equals("mysql") ? " AUTO_INCREMENT" : "") +", player_name varchar(32) NOT NULL" + (SQLManager.sqlType.equals("mysql") ? "" : " COLLATE 'NOCASE'") + ", creation_date " + (SQLManager.sqlType.equals("mysql") ? "TIMESTAMP" : "DATETIME") + " NOT NULL DEFAULT CURRENT_TIMESTAMP, expiration_date " + (SQLManager.sqlType.equals("mysql") ? "TIMESTAMP" : "DATETIME") + " NOT NULL DEFAULT '0000-00-00 00:00:00', creator VARCHAR(32) NOT NULL, reason VARCHAR(120) DEFAULT NULL, active BOOLEAN NOT NULL DEFAULT '1', PRIMARY KEY (id_ban)" + (SQLManager.sqlType.equals("mysql") ? ", KEY player_name (player_name), KEY expiration_date (expiration_date), KEY active (active)" : "" ) + ")" + (SQLManager.sqlType.equals("mysql") ? " ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='holds ban records for players along with reasons and ban expiration times' AUTO_INCREMENT=1" : "") + (SQLManager.sqlType.equals("mysql") ? "" : "; CREATE INDEX IF NOT EXISTS player_name ON "+ SQLManager.prefix +"bans (player_name); CREATE INDEX IF NOT EXISTS expiration_date ON "+ SQLManager.prefix +"bans (expiration_date); CREATE INDEX IF NOT EXISTS active ON "+ SQLManager.prefix +"bans (active); COMMIT;"));
		
		// check for BanHammer.db file, which is a BanHammer sqlite file and if found, start conversion
		File banHammer = new File(plugin.getDataFolder(), "BanHammer.db");
		if (banHammer.exists()) {
			// determine if the table is a valid BanHammer table and perform conversion
			if (SQLManager.init_alt("sqlite", "BanHammer.db")) {
				try {
					LogHelper.logInfo("[CommandsEX] " + _("bansConversionStarted", ""));
					ResultSet res = SQLManager.query_res_alt("SELECT * FROM bh_bans");
					List<Object> insertParts = new ArrayList<Object>();
					List<Object> insertValues = new ArrayList<Object>();
					// how many records we want to import at once (to prevent SQL overloading)
					Integer flushAfter = 100;
					while (res.next()) {
						insertParts.add("SELECT ? AS 'player_name', ? AS 'creation_date', ? AS 'expiration_date', ? AS 'creator', ? AS 'reason'");
						insertValues.add(res.getString("player"));
						insertValues.add(res.getTimestamp("created_at"));
						insertValues.add(res.getTimestamp("expires_at"));
						insertValues.add(res.getString("created_by"));
						insertValues.add(res.getString("reason"));
						
						// flush SQL if we reached number of records
						if (insertParts.size() == flushAfter) {
							SQLManager.query("INSERT INTO " + SQLManager.prefix + "bans (player_name, creation_date, expiration_date, creator, reason) "+ Utils.implode(insertParts, " UNION "), insertValues);
							insertParts.clear();
							insertValues.clear();
						}
					}
					res.close();
					
					// now do a last insert, if needed :)
					if (insertParts.size() > 0) {
						SQLManager.query("INSERT INTO " + SQLManager.prefix + "bans (player_name, creation_date, expiration_date, creator, reason) "+ Utils.implode(insertParts, " UNION "), insertValues);
					}
					SQLManager.close_alt();
				} catch (Throwable e) {
					// unable to convert, tell user
					LogHelper.logSevere("[CommandsEX] " + _("bansConversionFailed", ""));
					LogHelper.logDebug("Message: " + e.getMessage() + ", cause: " + e.getCause());
					return;
				}
			}
			// rename BanHammer.db to BanHammer.db.done, so we don't attempt to convert again
			banHammer.renameTo(new File(plugin.getDataFolder(), "BanHammer.db.done"));
			
			// all done, inform about successful conversion in the console
			LogHelper.logInfo("[CommandsEX] " + _("bansConversionDone", ""));
		}
		
		// fetch all active bans and set up automatic pardon processed for them
		ResultSet res = SQLManager.query_res("SELECT player_name, expiration_date FROM " + SQLManager.prefix + "bans WHERE active = 1 AND expiration_date != '0000-00-00 00:00:00'");
		try {
			while (res.next()) {
				String pName = res.getString("player_name");
				Date d = new Date(res.getTimestamp("expiration_date").getTime());
				Date c = new Date();
				Integer timeRemain = (int) ((d.getTime() - c.getTime()) / 1000);
				
				// if we already should have pardoned this player, do it now
				if (timeRemain <= 0) {
					SQLManager.query("UPDATE " + SQLManager.prefix + "bans SET active = 0 WHERE player_name = ?", pName);
					OfflinePlayer p = Bukkit.getOfflinePlayer(pName);
		    		p.setBanned(false);
				} else {
					// set up scheduled task to be fired in number of seconds that remain from current time
					CommandsEX.plugin.getServer().getScheduler().scheduleSyncDelayedTask(CommandsEX.plugin, new DelayedPardon(pName), (20 * timeRemain));
				}
			}
			res.close();
		} catch (Throwable e) {
			// unable to load bans
			LogHelper.logSevere("[CommandsEX] " + _("bansUnableToLoad", ""));
			LogHelper.logDebug("Message: " + e.getMessage() + ", cause: " + e.getCause());
		}
	}
	
	/***
	 * BAN - bans a player for specific time period, optionally storing a reason
	 * @param sender
	 * @param args
	 * @param command
	 * @param alias
	 * @return
	 */
	public static Boolean ban(CommandSender sender, String[] args, String command, String alias) {
		// check if player is online
		Player p = Bukkit.getServer().getPlayer(args[0]);
		OfflinePlayer pl = null;
		String pName;
		Boolean isOffline = false;
		if (p == null) {
			// player is offline, get his saved record
			isOffline = true;
			pl = Bukkit.getOfflinePlayer(args[0]);
			if (pl == null) {
				LogHelper.showWarning("invalidPlayer", sender);
				return true;
			} else {
				pName = pl.getName();
			}
		} else {
			pName = p.getName();
		}
		
		// check if we have expiration date present
		Map<String, Integer> t;
		try {
			t = Utils.parseTime(args);
		} catch (Throwable e) {
			t = new HashMap<String, Integer>();
			t.put("not_found", 1);
		}

		// make timestamp from parsed time, so it can be used in database operations
		Timestamp stamp = new Timestamp(new java.util.Date().getTime());
		if (!t.containsKey("not_found")) {
			// add up required number of milliseconds to current date
			stamp = new Timestamp((new java.util.Date().getTime()) + (t.get("seconds") * 1000) + (t.get("minutes") * 60 * 1000) + (t.get("hours") * 3600 * 1000) + (t.get("days") * 86400 * 1000));
		}

		// assemble reason, if one was provided
		String reason = "";
		if (args.length > 1) {
			for (Integer i = (args[1].startsWith("t:") ? 2 : 1); i < args.length; i++) {
				reason = reason + " " + args[i];
			}
		}

		if (CommandsEX.sqlEnabled) {
			// if we don't have time value and the player has a certain number of previous bans, inform admin
			ResultSet res = SQLManager.query_res("SELECT Count(*) as Total FROM " + SQLManager.prefix + "bans WHERE player_name = ?", pName);
			try {
				while (res.next()) {
					Integer total = res.getInt("Total");
					// warn admin if temporary bans treshold was reached
					if (total > CommandsEX.getConf().getInt("minTempBansWarn")) {
						LogHelper.showInfo("bansTempBansTresholdReached1#####[" + total + " #####bansTempBansTresholdReached2", sender);
					}
					// store ban record in database
					if (t.containsKey("not_found")) {
						// no expiration - permaban
						SQLManager.query("INSERT INTO " + SQLManager.prefix + "bans (player_name, creation_date, creator, reason) VALUES (?, ?, ?, ?)", pName, new Timestamp(new java.util.Date().getTime()), sender.getName() , reason);
					} else {
						// temporary ban
						SQLManager.query("INSERT INTO " + SQLManager.prefix + "bans (player_name, creation_date, expiration_date, creator, reason) VALUES (?, ?, ?, ?, ?)", pName, new Timestamp(new java.util.Date().getTime()), stamp, sender.getName(), reason);
					}
				}
				res.close();
			} catch (Throwable e) {
				// unable to ban player
				LogHelper.showWarning("internalError", sender);
				LogHelper.logSevere("[CommandsEX] " + _("dbWriteError", ""));
				LogHelper.logDebug("Message: " + e.getMessage() + ", cause: " + e.getCause());
				return true;
			}
		}
		
		// ban player using server's banning system
		if (isOffline) {
			pl.setBanned(true);
		} else {
			p.setBanned(true);
		}
		
		// if we've banned the player temporarily, start timer
		if (!t.containsKey("not_found")) {
			Integer banTime = (t.get("seconds") + (t.get("minutes") * 60) + (t.get("hours") * 3600)+ (t.get("days") * 86400));
			CommandsEX.plugin.getServer().getScheduler().scheduleSyncDelayedTask(CommandsEX.plugin, new DelayedPardon(pName), (20 * banTime));
		}

		// tell everyone if not disallowed in config
		if (!CommandsEX.getConf().getBoolean("silentBans")) {
			CommandsEX.plugin.getServer().broadcastMessage(ChatColor.LIGHT_PURPLE + (!reason.equals("") ? (pName + " " + _("bansBeingBannedForMessage", "") + reason) : pName + " " + _("bansBeingBannedMessage", "")) + (!t.containsKey("not_found") ? " " + _("for", "") + " " + args[1].replace("t:", "") : ""));
		}

		// at last, kick the player if still online
		if (!isOffline) {
			p.kickPlayer(ChatColor.RED + (!reason.equals("") ? (_("bansYouAreBannedForMessage", "") + reason) :  _("bansGenericReason", "")));
		}
		
        return true;
	}
	
	
	/***
	 * BANIP - bans an IP for specific time period, optionally storing a reason
	 * @param sender
	 * @param args
	 * @param command
	 * @param alias
	 * @return
	 */
	public static Boolean banip(CommandSender sender, String[] args, String command, String alias) {
		// let's see if we're trying to ban a player or an IP directly
		String ip = "";
		if (!args[0].matches(ipV4Regex) && !args[0].matches(ipV6Regex)) {
			// try to get IP address of the requested player
			final String realName = args[0].toLowerCase();
			if (CommandsEX.playerIPs.containsKey(realName)) {
				ip = CommandsEX.playerIPs.get(realName);
			} else {
				// IP address not found
				LogHelper.showWarning("bansIPNotFound", sender);
				return true;
			}
		} else {
			ip = args[0];
		}

		// check if we have expiration date present
		Map<String, Integer> t;
		try {
			t = Utils.parseTime(args);
		} catch (Throwable e) {
			t = new HashMap<String, Integer>();
			t.put("not_found", 1);
		}

		// make timestamp from parsed time, so it can be used in database operations
		Timestamp stamp = new Timestamp(new java.util.Date().getTime());
		if (!t.containsKey("not_found")) {
			// add up required number of milliseconds to current date
			stamp = new Timestamp((new java.util.Date().getTime()) + (t.get("seconds") * 1000) + (t.get("minutes") * 60 * 1000) + (t.get("hours") * 3600 * 1000) + (t.get("days") * 86400 * 1000));
		}

		// assemble reason, if one was provided
		String reason = "";
		if (args.length > 1) {
			for (Integer i = (args[1].startsWith("t:") ? 2 : 1); i < args.length; i++) {
				reason = reason + " " + args[i];
			}
		}

		if (CommandsEX.sqlEnabled) {
			try {
				// store ban record in database
				if (t.containsKey("not_found")) {
					// no expiration - permaban
					SQLManager.query("INSERT INTO " + SQLManager.prefix + "bans (player_name, creator, reason) VALUES (?, ?, ?)", ip, sender.getName() , reason);
				} else {
					// temporary ban
					SQLManager.query("INSERT INTO " + SQLManager.prefix + "bans (player_name, expiration_date, creator, reason) VALUES (?, ?, ?, ?)", ip, stamp, sender.getName(), reason);
				}
			} catch (Throwable e) {
				// unable to ban the IP
				LogHelper.showWarning("internalError", sender);
				LogHelper.logSevere("[CommandsEX] " + _("dbWriteError", ""));
				LogHelper.logDebug("Message: " + e.getMessage() + ", cause: " + e.getCause());
				return true;
			}
		}
		
		// ban IP using server's banning system
		Bukkit.banIP(ip);
		
		// if we've banned the player temporarily, start timer
		if (!t.containsKey("not_found")) {
			Integer banTime = (t.get("seconds") + (t.get("minutes") * 60) + (t.get("hours") * 3600)+ (t.get("days") * 86400));
			CommandsEX.plugin.getServer().getScheduler().scheduleSyncDelayedTask(CommandsEX.plugin, new DelayedPardon(ip), (20 * banTime));
		}
		
		// at last, kick each and every player with this IP that remains online
		Iterator<Entry<String, String>> it = CommandsEX.playerIPs.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> pairs = (Map.Entry<String, String>)it.next();
			if (pairs.getValue().equals(ip)) {
				Player p = Bukkit.getServer().getPlayer(pairs.getKey());
				if (p != null) {
					p.kickPlayer(ChatColor.RED + (!reason.equals("") ? (_("bansYouAreBannedForMessage", "") + reason) :  _("bansGenericReason", "")));
				}
			}
		}
		
		// and inform the person who was banning, of course
		LogHelper.showInfo("[" + ip + " #####bansIPBanned", sender);

        return true;
	}
	
	
	/***
	 * BCHECK - checks if the given player is currently banned, for what time and why
	 * @param sender
	 * @param args
	 * @param command
	 * @param alias
	 * @return
	 */
	public static Boolean checkban(CommandSender sender, String[] args, String command, String alias) {
		if (CommandsEX.sqlEnabled) {
			try {
				// see if we can find an active record of the given player in database
				Player p = Bukkit.getServer().getPlayer(args[0]);
				String pName = "";
				if (p == null) {
					OfflinePlayer pl = Bukkit.getOfflinePlayer(args[0]);
					if (pl == null) {
						// player not found
						LogHelper.showWarning("invalidPlayer", sender);
						return true;
					}
					pName = pl.getName();
				} else {
					pName = p.getName();
				}
				
				ResultSet res = SQLManager.query_res("SELECT player_name, creation_date, expiration_date, creator, reason FROM " + SQLManager.prefix + "bans WHERE player_name = ? AND active = 1", pName);
				while (res.next()) {
					// assemble readable dates
					final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd H:m:s");
					final String creation_date = dateFormat.format(res.getTimestamp("creation_date").getTime());
					final String expiration_date = dateFormat.format(res.getTimestamp("expiration_date").getTime());
					
					// return info about the player and his current ban
					LogHelper.showInfo("[" + res.getString("player_name") + " #####bansPlayerIsBanned#####[" + creation_date + " #####byPlayer#####[ " + res.getString("creator") + "#####[.", sender);
					LogHelper.showInfo("bansReason#####[" + res.getString("reason"), sender, ChatColor.YELLOW);
					
					// return data with regards to permanent / temporary ban duration
					if (res.getString("expiration_date").equals("0000-00-00 00:00:00")) {
						// permanent ban
						LogHelper.showInfo("bansLength#####bansLengthPermanent", sender, ChatColor.YELLOW);
					} else {
						// temporary ban
						Date c;
						Date d;
						
						try {
							d = new Date(dateFormat.parse(res.getString("expiration_date")).getTime());
						} catch (Throwable e) {
							d = new Date(res.getTimestamp("expiration_date").getTime());
						}
						
						try {
							c = new Date(dateFormat.parse(res.getString("creation_date")).getTime());
						} catch (Throwable e) {
							c = new Date(res.getTimestamp("creation_date").getTime());
						}
						Integer timeAll = (int) ((d.getTime() - c.getTime()) / 1000); // total ban time
						Map<String, Integer> m = Utils.parseTimeStamp(timeAll);
						LogHelper.showInfo("bansLength#####[" + (m.get("days") + " #####days#####[, ") + (m.get("hours") + " #####hours#####[, ") + (m.get("minutes") + " #####minutes#####[, ") + (m.get("seconds") + " #####seconds"), sender, ChatColor.YELLOW);
						LogHelper.showInfo("bansExpires#####[" + expiration_date, sender, ChatColor.YELLOW);
					}
					
					res.close();
					return true;
				}
				res.close();
				
				// if we're here, it means the player has no ban history whatsoever
				LogHelper.showInfo("[" + pName + " #####bansPlayerNotBanned", sender);
			} catch (Throwable e) {
				// unable to ban the IP
				LogHelper.showWarning("internalError", sender);
				LogHelper.logSevere("[CommandsEX] " + _("dbWriteError", ""));
				LogHelper.logDebug("Message: " + e.getMessage() + ", cause: " + e.getCause());
				return true;
			}
		} else {
			// no database, no history
			LogHelper.showWarning("bansNoDB", sender);
		}

		return true;
	}
	
	
	/***
	 * BHISTORY - displays player's ban history
	 * @param sender
	 * @param args
	 * @param command
	 * @param alias
	 * @return
	 */
	public static Boolean showhistory(CommandSender sender, String[] args, String command, String alias) {
		if (CommandsEX.sqlEnabled) {
			try {
				// see if we can find an active record of the given player in database
				Player p = Bukkit.getServer().getPlayer(args[0]);
				String pName = "";
				if (p == null) {
					OfflinePlayer pl = Bukkit.getOfflinePlayer(args[0]);
					if (pl == null) {
						// player not found
						LogHelper.showWarning("invalidPlayer", sender);
						return true;
					}
					pName = pl.getName();
				} else {
					pName = p.getName();
				}
				
				ResultSet res = SQLManager.query_res("SELECT player_name, creation_date, expiration_date, creator, reason FROM " + SQLManager.prefix + "bans WHERE player_name = ?", pName);
				List<String> finalResult = new ArrayList<String>();
				finalResult.add(ChatColor.AQUA + _("bansHistory1", ""));
				finalResult.add(ChatColor.AQUA + _("bansHistory2", ""));

				while (res.next()) {
					// assemble readable dates
					final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd H:m:s");
					final String creation_date = dateFormat.format(res.getTimestamp("creation_date").getTime());
					final String expiration_date = dateFormat.format(res.getTimestamp("expiration_date").getTime());
					
					// return info about the player and his current ban
					finalResult.add(ChatColor.YELLOW + res.getString("player_name") + " " + _("bansPlayerIsBanned", "") + creation_date + " " + _("byPlayer", "") + " " + res.getString("creator") + ".");
					finalResult.add(ChatColor.YELLOW + _("bansReason", "") + res.getString("reason"));
					
					// return data with regards to permanent / temporary ban duration
					if (res.getString("expiration_date").equals("0000-00-00 00:00:00")) {
						// permanent ban
						finalResult.add(_("bansLength", "") + _("bansLengthPermanent", ""));
					} else {
						// temporary ban
						Date c;
						Date d;
						
						try {
							d = new Date(dateFormat.parse(res.getString("expiration_date")).getTime());
						} catch (Throwable e) {
							d = new Date(res.getTimestamp("expiration_date").getTime());
						}
						
						try {
							c = new Date(dateFormat.parse(res.getString("creation_date")).getTime());
						} catch (Throwable e) {
							c = new Date(res.getTimestamp("creation_date").getTime());
						}
						Integer timeAll = (int) ((d.getTime() - c.getTime()) / 1000); // total ban time
					
						Integer days = (int) Math.floor(timeAll / 86400);
						Integer hours = (int) Math.floor((timeAll - (days * 86400)) / 3600);
						Integer minutes = (int) Math.floor((timeAll - (days * 86400) - (hours * 3600)) / 60);
						Integer seconds = (timeAll - (days * 86400) - (hours * 3600) - (minutes * 60));
						
						finalResult.add(ChatColor.YELLOW + _("bansLength", "") + days + _("days", "") + ", " + hours + _("hours", "") + ", " + minutes + _("minutes", "") + ", " + seconds + _("seconds", "") + ", ");
						finalResult.add(ChatColor.YELLOW + _("bansExpires", "") + expiration_date);
						finalResult.add("");
					}
				}
				res.close();
				
				if (finalResult.size() == 2) {
					// no ban history for the player
					LogHelper.showInfo("[" + pName + " #####bansNoBanHistory", sender);
				} else {
					// show what we've got :)
					for (String s : finalResult) {
						sender.sendMessage(s);
					}
				}
			} catch (Throwable e) {
				// unable to ban the IP
				LogHelper.showWarning("internalError", sender);
				LogHelper.logSevere("[CommandsEX] " + _("dbWriteError", ""));
				LogHelper.logDebug("Message: " + e.getMessage() + ", cause: " + e.getCause());
				return true;
			}
		} else {
			// no database, no history
			LogHelper.showWarning("bansNoDB", sender);
		}

		return true;
	}
	
	
	/***
	 * PARDON - removes ban for the given player or IP address
	 * @param sender
	 * @param args
	 * @param command
	 * @param alias
	 * @return
	 */
	public static Boolean pardon(CommandSender sender, String[] args, String command, String alias) {
		if (args[0].matches(Bans.ipV4Regex) || args[0].matches(Bans.ipV6Regex)) {
			// check if we don't have an OfflinePlayer with this name banned (which can happen by mistake)
			// and unban in such case
			OfflinePlayer p = Bukkit.getOfflinePlayer(args[0]);
			if ((p != null) && p.isBanned()) {
				p.setBanned(false);
				LogHelper.showInfo("bansPlayerPardoned", sender);
			} else {
				// send message to the sender if we banned an IP, since no message is broadcasted then
				LogHelper.showInfo("bansIpPardoned", sender);
			}
		}
		
		// use the already-existing pardonning class to unban
		CommandsEX.plugin.getServer().getScheduler().scheduleSyncDelayedTask(CommandsEX.plugin, new DelayedPardon(args[0]), 0L);
		
        return true;
	}
}