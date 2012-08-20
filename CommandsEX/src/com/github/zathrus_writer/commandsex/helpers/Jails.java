package com.github.zathrus_writer.commandsex.helpers;

import static com.github.zathrus_writer.commandsex.Language._;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.SQLManager;
import com.github.zathrus_writer.commandsex.handlers.Handler_savebackposition;

/***
 * Contains set of functions and event listeners used to handle the jail command.
 * @author zathrus-writer
 *
 */
public class Jails {

	public static Map<String, Location> jailPlaces = new HashMap<String, Location>();
	// this map contains name of the jailed player and his last location before he was teleported to jail
	public static Map<String, Location> jailedPlayers = new HashMap<String, Location>();
	
	/***
	 * INIT - initialization function, called when plugin is enabled to create jail table if not created yet
	 * @return
	 */
	public static void init(CommandsEX plugin) {
		// first of all, check if we can use any DB
		if (!CommandsEX.sqlEnabled) {
			LogHelper.logWarning(_("jailsNoDatabaseWarning", ""));
			return;
		}
		
		// next create jails table if it's not present yet
		SQLManager.query("CREATE TABLE IF NOT EXISTS "+ SQLManager.prefix +"jails (id_jail integer " + (SQLManager.sqlType.equals("mysql") ? "unsigned NOT NULL AUTO_INCREMENT" : "NOT NULL") +", world_name varchar(32) NOT NULL, x double NOT NULL, y double NOT NULL, z double NOT NULL, PRIMARY KEY (id_jail), UNIQUE " + (SQLManager.sqlType.equals("mysql") ? "KEY world_name " : "") +"(world_name)" + ")" + (SQLManager.sqlType.equals("mysql") ? " ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='contains information on jail locations per world' AUTO_INCREMENT=1" : ""));

		// load per-world jails
		try {
			ResultSet res = SQLManager.query_res("SELECT * FROM " + SQLManager.prefix + "jails");
			while (res.next()) {
				// assemble and store location for the jail
				Location l = new Location(CommandsEX.plugin.getServer().getWorld(res.getString("world_name")), res.getDouble("x"), res.getDouble("y"), res.getDouble("z"));
				jailPlaces.put(res.getString("world_name"), l);
			}
			res.close();
		} catch (Throwable e) {
			// unable to load jail locations
			LogHelper.logSevere("[CommandsEX] " + _("dbReadError", ""));
			LogHelper.logDebug("Message: " + e.getMessage() + ", cause: " + e.getCause());
			return;
		}
	}
	
	/***
	 * JAIL - freezes a player, making him unable to perform physical actions on the server and moves him to a jail location
	 * @param sender
	 * @param args
	 * @param command
	 * @param alias
	 * @return
	 */
	public static Boolean jail(CommandSender sender, String[] args, String command, String alias, Boolean... omitMessage) {
		Boolean showMessages = (omitMessage.length == 0);
		// check if requested player is online
		Player p = Bukkit.getServer().getPlayer(args[0]);
		Boolean isOnline = false;
		String pName = "";
		if (p != null) {
			pName = p.getName();
			isOnline = true;
		} else {
			pName = args[0];
		}

		// check if requested player is jailed
		if ((jailedPlayers.size() > 0) && jailedPlayers.containsKey(pName)) {
			// unfreeze player and teleport him back to his previous location
			Location l = jailedPlayers.get(pName);
			jailedPlayers.remove(pName);
			Common.freeze(sender, args, command, alias, true);
			
			// teleport player back
			if (isOnline) {
				try {Handler_savebackposition.omittedPlayers.add(pName);} catch (Throwable e) {}
				Teleportation.delayedTeleport(p, l);
			}

			// inform the command sender and the player
			if (showMessages) {
				LogHelper.showInfo("[" + Nicknames.getNick(pName) + " #####jailsPlayerUnJailed", sender);
				if (isOnline) {
					LogHelper.showInfo("jailsEndMessage", p);
				}
			}
			
			return true;
		}

		// we are trying to jail a player
		String worldName = "";
		if (!isOnline) {
			// requested player not found
			LogHelper.showWarning("invalidPlayer", sender);
			return true;
		} else {
			worldName = p.getWorld().getName();
			// check if we have a jail location set for this world
			if (!jailPlaces.containsKey(worldName)) {
				LogHelper.showWarning("jailsNoJailInWorld", sender);
				return true;
			}
		}

		// insert player's name into jailed players' list
		jailedPlayers.put(pName, p.getLocation());

		// set a random position within the jail area
		Location jailPoint = jailPlaces.get(worldName);
		Integer jailArea = CommandsEX.getConf().getInt("jailArea");
		Integer halfJailArea = Math.round(jailArea / 2);
		Integer xMin = (int) (jailPoint.getX() - halfJailArea);
		Integer xMax = (int) (jailPoint.getX() + halfJailArea);
		Integer zMin = (int) (jailPoint.getZ() - halfJailArea);
		Integer zMax = (int) (jailPoint.getZ() + halfJailArea);
		jailPoint.setX(xMin + (int)(Math.random() * ((xMax - xMin) + 1)));
		jailPoint.setZ(zMin + (int)(Math.random() * ((zMax - zMin) + 1)));
		
		// teleport player to jail and freeze him upon teleportation
		try {Handler_savebackposition.omittedPlayers.add(pName);} catch (Throwable e) {}
		Teleportation.delayedTeleport(p, jailPoint, new DelayedFreeze(sender, args, command, alias, true));

		if (showMessages) {
			// inform both players
			LogHelper.showInfo("[" + Nicknames.getNick(pName) + " #####jailsPlayerJailed", sender);
			LogHelper.showInfo("jailsYouAreJailed1", p);
			LogHelper.showInfo("jailsYouAreJailed2", p);
		}
		
		return true;
	}
	
	/***
	 * SETJAIL - sets a jail location in the current world
	 * @param sender
	 * @param args
	 * @param command
	 * @param alias
	 * @return
	 */
	public static Boolean setjail(CommandSender sender, String[] args, String command, String alias) {
		// first of all, save the jail location locally
		Player p = (Player) sender;
		Location l = p.getLocation();
		jailPlaces.put(p.getWorld().getName(), l);
		
		// now see if we can put the location inside a database
		if (CommandsEX.sqlEnabled) {
			try {
				SQLManager.query("INSERT " + (SQLManager.sqlType.equals("mysql") ? "" : "OR REPLACE ") + "INTO " + SQLManager.prefix + "jails (world_name, x, y, z) VALUES (?, ?, ?, ?)" + (SQLManager.sqlType.equals("mysql") ? " ON DUPLICATE KEY UPDATE x = VALUES(x), y = VALUES(y), z = VALUES(z)" : ""), p.getWorld().getName(), l.getX(), l.getY(), l.getZ());
			} catch (Throwable e) {
				LogHelper.logSevere("[CommandsEX] " + _("dbWriteError", ""));
				LogHelper.logDebug("Message: " + e.getMessage() + ", cause: " + e.getCause());
			}
		}
		
		// inform player
		LogHelper.showInfo("jailsJailCreated", sender);
		return true;
	}
}