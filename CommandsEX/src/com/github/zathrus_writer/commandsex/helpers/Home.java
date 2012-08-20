package com.github.zathrus_writer.commandsex.helpers;

import static com.github.zathrus_writer.commandsex.Language._;

import java.io.File;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.SQLManager;

/***
 * Contains set of commands to be executed for home setting/getting purposes.
 * @author zathrus-writer
 *
 */
public class Home {	

	public static Boolean multiHomesEnabled = CommandsEX.getConf().getBoolean("allowMultiworldHomes");
	// stores IDs of homes to be removed should a player decide to confirm the /home iclear command
	public static Map<String, List<Object>> clearHomesConfirmation = new HashMap<String, List<Object>>();

	/***
	 * INIT - initialization function, called when plugin is enabled to check MyHome database conversion needs.
	 * @return
	 */
	public static void init(CommandsEX plugin) {
		// first of all, check if we can use any DB
		if (!CommandsEX.sqlEnabled) return;
		
		// next create homes table if it's not present yet
		SQLManager.query("CREATE TABLE IF NOT EXISTS "+ SQLManager.prefix +"homes (id_home integer " + (SQLManager.sqlType.equals("mysql") ? "unsigned " : "") +"NOT NULL" + (SQLManager.sqlType.equals("mysql") ? " AUTO_INCREMENT" : "") +", player_name varchar(32) NOT NULL" + (SQLManager.sqlType.equals("mysql") ? "" : " COLLATE 'NOCASE'") + ", world_name varchar(32) NOT NULL, x double NOT NULL, y double NOT NULL, z double NOT NULL, yaw double NOT NULL, pitch double NOT NULL, is_public BOOLEAN NOT NULL DEFAULT '0', allowed_players text NULL DEFAULT NULL, PRIMARY KEY (id_home), UNIQUE " + (SQLManager.sqlType.equals("mysql") ? "KEY player_name " : "") +"(player_name,world_name))" + (SQLManager.sqlType.equals("mysql") ? " ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='stores home locations and permissions for players' AUTO_INCREMENT=1" : ""));
		
		// upgrade to yaw and pitch-aware, if not upgraded yet
		try {
			Boolean upgradedToYaw = false;
			ResultSet res = SQLManager.query_res((SQLManager.sqlType.equals("mysql") ? "DESCRIBE "+ SQLManager.prefix +"homes" : "PRAGMA table_info("+ SQLManager.prefix +"homes)"));
			while (res.next()) {
				if (res.getString(SQLManager.sqlType.equals("mysql") ? "Field" : "name").equals("yaw")) {
					res.close();
					upgradedToYaw = true;
					break;
				}
			}
			res.close();
			
			if (!upgradedToYaw) {
				// not upgraded yet, upgrade here
				SQLManager.query("ALTER TABLE "+ SQLManager.prefix +"homes ADD COLUMN yaw double NOT NULL DEFAULT 0");
				SQLManager.query("ALTER TABLE "+ SQLManager.prefix +"homes ADD COLUMN pitch double NOT NULL DEFAULT 0");
			}
		} catch (Throwable e) {
			LogHelper.logSevere("[CommandsEX] " + _("dbErrorModuleUnavailable", "") + "homes");
			LogHelper.logDebug("Message: " + e.getMessage() + ", cause: " + e.getCause());
			return;
		}
		
		
		// check for homes.db file, which is a MyHome sqlite file and if found, start conversion
		File myHomes = new File(plugin.getDataFolder(), "homes.db");
		if (myHomes.exists()) {
			// determine if the table is a valid myHomes table and perform conversion
			if (SQLManager.init_alt("sqlite", "homes.db")) {
				try {
					LogHelper.logInfo("[CommandsEX] " + _("homeConversionStarted", ""));
					ResultSet res = SQLManager.query_res_alt("SELECT * FROM homeTable");
					List<Object> insertParts = new ArrayList<Object>();
					List<Object> insertValues = new ArrayList<Object>();
					// how many records we want to import at once (to prevent SQL overloading)
					Integer flushAfter = 100;
					while (res.next()) {
						insertParts.add("SELECT ? AS 'id_home', ? AS 'player_name', ? AS 'world_name', ? AS 'x', ? AS 'y', ? AS 'z', ? AS 'is_public', ? AS 'allowed_players'");
						insertValues.add(res.getInt("id"));
						insertValues.add(res.getString("name"));
						insertValues.add(res.getString("world"));
						insertValues.add(res.getDouble("x"));
						insertValues.add(res.getDouble("y"));
						insertValues.add(res.getDouble("z"));
						insertValues.add(res.getBoolean("publicAll"));
						insertValues.add(res.getString("permissions"));
						
						// flush SQL if we reached number of records
						if (insertParts.size() == flushAfter) {
							SQLManager.query("INSERT "+ (SQLManager.sqlType.equals("mysql") ? "" : "OR ") +"IGNORE INTO " + SQLManager.prefix + "homes "+ Utils.implode(insertParts, " UNION "), insertValues);
							insertParts.clear();
							insertValues.clear();
						}
					}
					res.close();
					
					// now do the actual insert :)
					if (insertParts.size() > 0) {
						SQLManager.query("INSERT "+ (SQLManager.sqlType.equals("mysql") ? "" : "OR ") +"IGNORE INTO " + SQLManager.prefix + "homes "+ Utils.implode(insertParts, " UNION "), insertValues);
					}
					SQLManager.close_alt();
				} catch (Throwable e) {
					// unable to convert, tell user
					LogHelper.logSevere("[CommandsEX] " + _("homeConversionFailed", ""));
					LogHelper.logDebug("Message: " + e.getMessage() + ", cause: " + e.getCause());
					return;
				}
			}
			// rename homes.db to homes.db.done, so we don't attempt to convert again
			myHomes.renameTo(new File(plugin.getDataFolder(), "homes.db.done"));
			
			// all done, inform about successful conversion in the console
			LogHelper.logInfo("[CommandsEX] " + _("homeConversionDone", ""));
		}
	}
	
	/***
	 * SETHOME - sets home location for a player provided he's played the required amount of time on the server.
	 * @param sender
	 * @param args
	 * @param command
	 * @param alias
	 * @return
	 */
	public static Boolean setHome(CommandSender sender, String[] args, String command, String alias) {
		if (CommandsEX.sqlEnabled) {
			Player player = (Player)sender;

			// check if player is not spamming the command too much
			if (!Utils.checkCommandSpam(player, "sethome")) {
				String pName = player.getName();
				// check if the player has been on the server long enough
				Integer hTime = CommandsEX.getConf().getInt("homeQualifyTime");
				if (Permissions.checkPermEx(player, "cex.bypasshomequalify") || (CommandsEX.playTimes.containsKey(pName) && (CommandsEX.playTimes.get(pName) >= hTime))) {
					// all ok, let's save our home
					Location l = player.getLocation();
					
					// if player cannot have multiworld homes, we need to delete their home first
					if (!CommandsEX.getConf().getBoolean("allowMultiworldHomes")) {
						SQLManager.query("DELETE FROM " + SQLManager.prefix + "homes WHERE player_name = ?", pName);
					}
					
					// all done :-)
					if (SQLManager.query("INSERT "+ (SQLManager.sqlType.equals("mysql") ? "" : "OR REPLACE ") +"INTO " + SQLManager.prefix + "homes (player_name, world_name, x, y, z, yaw, pitch) VALUES(?, ?, ?, ?, ?, ?, ?)" + (SQLManager.sqlType.equals("mysql") ? " ON DUPLICATE KEY UPDATE x = VALUES(x), y = VALUES(y), z = VALUES(z), yaw = VALUES(yaw), pitch = VALUES(pitch)" : ""), pName, player.getWorld().getName(), l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch())) {
						// home successfuly created
						LogHelper.showInfo("homeSetComplete#####[" + Nicknames.getNick(pName) + "!", sender);
					} else {
						// an error has occured...
						LogHelper.showWarning("internalError", sender);
					}
				} else {
					// player did not play long enough
					try {
						Class.forName("com.github.zathrus_writer.commandsex.handlers.Handler_setbedhome");
						LogHelper.showInfo("homeInsufficientPlayTime", sender);
					} catch (Throwable e) {
						LogHelper.showInfo("homeInsufficientPlayTimeNoBeds", sender);
					}
				}
			}
		} else {
			// no database, no homes
			LogHelper.showInfo("homeNoDatabase", sender);
		}
		
        return true;
	}
	
	/***
	 * HOME - teleports a player to their home location or a home location of another player
	 * @param sender
	 * @param args
	 * @param command
	 * @param alias
	 * @return
	 */
	public static Boolean home(CommandSender sender, String[] args, String command, String alias) {
		if (CommandsEX.sqlEnabled) {
			Player player = (Player)sender;

			// get home data from DB, if we didn't spam too much :)
			if (!Utils.checkCommandSpam(player, "home-go")) {
				String pName = player.getName();
				String homePlayerName = "";
				// determine whose home we're trying to get to
				if (args.length > 0) {
					// going to friend's home
					homePlayerName = args[0];
				} else {
					// going to our home
					homePlayerName = pName;
				}
				
				try {
					String homePlayerForSQL = homePlayerName.toLowerCase();
					String foundPlayerName = "";
					Integer numHomes = 0;
					Location l = null;
					ResultSet res;
					
					if (multiHomesEnabled) {
						// if multiple homes are enabled, we check for a home in player's current world
						res = SQLManager.query_res("SELECT * FROM " + SQLManager.prefix + "homes WHERE (player_name = ? OR player_name LIKE ?) AND world_name = ? LIMIT 2", homePlayerForSQL, homePlayerForSQL + "%", player.getWorld().getName());
					} else {
						// multiple homes disabled, we check for player's home anywhere
						res = SQLManager.query_res("SELECT * FROM " + SQLManager.prefix + "homes WHERE (player_name = ? OR player_name LIKE ?) LIMIT 2", homePlayerForSQL, homePlayerForSQL + "%");
					}

					while (res.next()) {
						numHomes++;
						foundPlayerName = res.getString("player_name");
						res.getString("allowed_players");
						Boolean noPlayers = res.wasNull();
						// check if player is allowed to go to this home
						if (!Permissions.checkPermEx(player, "cex.bypassinvite") && !res.getBoolean("is_public") && !res.getString("player_name").equals(homePlayerName) && (noPlayers || (!noPlayers && !res.getString("allowed_players").equals(pName) && !res.getString("allowed_players").contains("," + pName) && !res.getString("allowed_players").contains(pName + ",")))) {
							// player is not allowed in
							LogHelper.showWarning("homeNotAllowed" + (multiHomesEnabled ? "#####homeInCurrentWorld" : ""), sender);
							return true;
						}

						// assemble location for the home
						l = new Location(CommandsEX.plugin.getServer().getWorld(res.getString("world_name")), res.getDouble("x"), res.getDouble("y"), res.getDouble("z"), (float) res.getDouble("yaw"), (float) res.getDouble("pitch"));
						if (l.getWorld() == null){
							LogHelper.showInfo("homeWorldNotExist", sender, ChatColor.RED);
							return true;
						}
						
						// if the name matches exactly what we've been looking for, adjust numHomes and break the loop
						if (foundPlayerName.toLowerCase().equals(homePlayerForSQL)) {
							numHomes = 1;
							break;
						}
					}
					res.close();

					if (numHomes > 1) {
						// too many players match the selection
						numHomes = 0;
						foundPlayerName = homePlayerName;
					} else if (numHomes == 1) {
						// teleport player home and welcome him :-)
						Teleportation.delayedTeleport(player, l);
						LogHelper.showInfo((pName.equals(homePlayerName) ? "homeSetComplete#####[" + Nicknames.getNick(pName) + "!" : "homeWelcomeTo#####[" + Nicknames.getNick(foundPlayerName) + "#####homePlayersHome"), sender);
						return true;
					}
					
					// if player's home was not found, show message
					if (numHomes == 0) {
						if (pName.equals(homePlayerName)) {
							if (multiHomesEnabled) {
								LogHelper.showInfos(sender, new String[] {"homeNoHomeOwnerCurrentWorld1", "homeNoHomeOwnerCurrentWorld2"});
							} else {
								LogHelper.showInfos(sender, new String[] {"homeNoHomeOwner1", "homeNoHomeOwner2"});
							}
						} else {
							LogHelper.showInfo("[" + Nicknames.getNick(homePlayerName) + " #####homeNoHomeVisitor" + (multiHomesEnabled ? "#####homeInCurrentWorld" : ""), sender);
						}
					}
				} catch (Throwable e) {
					// unable to load players' home
					LogHelper.showWarning("internalError", sender);
					LogHelper.logSevere("[CommandsEX] " + _("dbReadError", ""));
					LogHelper.logDebug("Message: " + e.getMessage() + ", cause: " + e.getCause());
					return true;
				}
			}
		} else {
			// no database, no homes
			LogHelper.showInfo("homeNoDatabase", sender);
		}
		
        return true;
	}
	
	
	/***
	 * INVITE - invites a player to your home
	 * @param sender
	 * @param args
	 * @param command
	 * @param alias
	 * @return
	 */
	public static Boolean invite(CommandSender sender, String[] args, String command, String alias) {
		if (CommandsEX.sqlEnabled) {
			Player player = (Player)sender;
			
			// save invite, if we didn't spam too much :)
			if (!Utils.checkCommandSpam(player, "home-invite")) {
				String pName = player.getName();
				if (args.length == 1) {
					// no arguments, provide command help
					Commands.showCommandHelpAndUsage(sender, "cex_home", "home_invite");
					return true;
				}
				
				try {
					ResultSet res;
					if (multiHomesEnabled) {
						// if multiple homes are enabled, we check for a home in player's current world
						res = SQLManager.query_res("SELECT id_home, allowed_players FROM " + SQLManager.prefix + "homes WHERE player_name = ? AND world_name = ?", pName, player.getWorld().getName());
					} else {
						// multiple homes disabled, we check for player's home anywhere
						res = SQLManager.query_res("SELECT id_home, allowed_players FROM " + SQLManager.prefix + "homes WHERE player_name = ?", pName);
					}

					while (res.next()) {
						String allowedPlayers;
						if ((res.getString("allowed_players") != null) && !res.getString("allowed_players").equals("") && !res.wasNull()) {
							allowedPlayers = res.getString("allowed_players");
							if (!allowedPlayers.equals(args[1]) && !allowedPlayers.contains("," + args[1]) && !allowedPlayers.contains(args[1] + ",")) {
								allowedPlayers = allowedPlayers + "," + args[1];
							} else {
								// player already invited
								LogHelper.showInfos(sender, "[" + args[1] + " #####homePlayerAlreadyInvited2#####[" + pName + "#####homePlayerAlreadyInvited3");
								return true;
							}
						} else {
							allowedPlayers = args[1];
						}

						// update the DB with new allowed_players value
						SQLManager.query("UPDATE " + SQLManager.prefix + "homes SET allowed_players = ? WHERE id_home = ?", allowedPlayers, res.getInt("id_home"));
						
						// inform user that his friend was invited
						LogHelper.showInfos(sender, "[" + Nicknames.getNick(args[1]) + " #####homeInviteSuccessful");
						
						// if the actual friend is online, inform him as well
						Player p = Bukkit.getServer().getPlayer(args[1]);
						if (p != null) {
							LogHelper.showInfos(p, "homeYouHaveBeenInvited1#####[" + Nicknames.getNick(pName) + "#####homeYouHaveBeenInvited2", "homeYouHaveBeenInvited3#####[" + Nicknames.getNick(pName) + " #####homeYouHaveBeenInvited4");
						}
						
						// return here, since we can only invite to a single home
						res.close();
						return true;
					}

					// if we're here, it means no home was found for our player in current world... so we tell him
					if (multiHomesEnabled) {
						LogHelper.showInfos(sender, new String[] {"homeNoHomeOwnerCurrentWorld1", "homeNoHomeOwnerCurrentWorld2"});
					} else {
						LogHelper.showInfos(sender, new String[] {"homeNoHomeOwner1", "homeNoHomeOwner2"});
					}
				} catch (Throwable e) {
					// unable to load players' home
					LogHelper.showWarning("internalError", sender);
					LogHelper.logSevere("[CommandsEX] " + _("dbReadError", ""));
					LogHelper.logDebug("Message: " + e.getMessage() + ", cause: " + e.getCause());
					return true;
				}
			}
		} else {
			// no database, no homes
			LogHelper.showInfo("homeNoDatabase", sender);
		}
		
        return true;
	}
	
	/***
	 * UNINVITE - uninvites a player from your home
	 * @param sender
	 * @param args
	 * @param command
	 * @param alias
	 * @return
	 */
	public static Boolean uninvite(CommandSender sender, String[] args, String command, String alias) {
		if (CommandsEX.sqlEnabled) {
			Player player = (Player)sender;
			
			// remove the requested invite, if we didn't spam too much :)
			if (!Utils.checkCommandSpam(player, "home-uninvite")) {
				String pName = player.getName();
				if (args.length == 1) {
					// no arguments, provide command help
					Commands.showCommandHelpAndUsage(sender, "cex_home", "home_uninvite");
					return true;
				}
				
				try {
					ResultSet res;
					if (multiHomesEnabled) {
						// if multiple homes are enabled, we uninvite from home in player's current world
						res = SQLManager.query_res("SELECT id_home, allowed_players FROM " + SQLManager.prefix + "homes WHERE player_name = ? AND world_name = ?", pName, player.getWorld().getName());
					} else {
						// multiple homes disabled, we uninvite from player's home anywhere
						res = SQLManager.query_res("SELECT id_home, allowed_players FROM " + SQLManager.prefix + "homes WHERE player_name = ?", pName);
					}

					while (res.next()) {
						List<Object> newAllowedPlayers = new ArrayList<Object>();
						Boolean playerFound = false;
						if ((res.getString("allowed_players") != null) && !res.getString("allowed_players").equals("") && !res.wasNull()) {
							String[] allowedPlayers = res.getString("allowed_players").split(",");
							for (String s : allowedPlayers) {
								if (!s.equals(args[1])) {
									newAllowedPlayers.add(s);
								} else {
									playerFound = true;
								}
							}
							
							// the requested player is not invited to this home
							if (!playerFound) {
								LogHelper.showInfo("[" + Nicknames.getNick(args[1]) + " #####homePlayerUninviteNotFound", sender);
								return true;
							}
						} else {
							// no invited players
							LogHelper.showInfo("homeNoPlayersInvited", sender);
							return true;
						}

						// update the DB with new newAllowedPlayers value
						SQLManager.query("UPDATE " + SQLManager.prefix + "homes SET allowed_players = ? WHERE id_home = ?", Utils.implode(newAllowedPlayers, ","), res.getInt("id_home"));
						
						// inform user that the player was uninvited
						LogHelper.showInfos(sender, "[" + Nicknames.getNick(args[1]) + " #####homePlayerUninvited");
						
						// if the actual player is online, inform him as well
						Player p = Bukkit.getServer().getPlayer(args[1]);
						if (p != null) {
							LogHelper.showInfos(p, "homePlayerUninviteNotify#####[" + Nicknames.getNick(pName) + "#####homeYouHaveBeenInvited2");
						}
						
						// return here, since we can only uninvite a single player
						res.close();
						return true;
					}

					// if we're here, it means no home was found for our player in current world... so we tell him
					if (multiHomesEnabled) {
						LogHelper.showInfos(sender, new String[] {"homeNoHomeOwnerCurrentWorld1", "homeNoHomeOwnerCurrentWorld2"});
					} else {
						LogHelper.showInfos(sender, new String[] {"homeNoHomeOwner1", "homeNoHomeOwner2"});
					}
				} catch (Throwable e) {
					// unable to load players' home
					LogHelper.showWarning("internalError", sender);
					LogHelper.logSevere("[CommandsEX] " + _("dbReadError", ""));
					LogHelper.logDebug("Message: " + e.getMessage() + ", cause: " + e.getCause());
					return true;
				}
			}
		} else {
			// no database, no homes
			LogHelper.showInfo("homeNoDatabase", sender);
		}
		
        return true;
	}
	
	/***
	 * single-purpose class used to run /home list command, since we don't have index on allowed_players
	 * field and it could take a while to determine to which homes a player is invited
	 */
	static class ListInvitedHomes implements Runnable {
		
		private CommandSender sender;
		
		public ListInvitedHomes(CommandSender s) {
			this.sender = s;
		}
		
		@Override
		public void run() {
			Player player = (Player)this.sender;
			String pName = player.getName();
			Boolean multiHomesEnabled = Home.multiHomesEnabled;

			try {
				ResultSet res = SQLManager.query_res("SELECT player_name, world_name FROM " + SQLManager.prefix + "homes WHERE allowed_players = ? OR allowed_players LIKE ? OR allowed_players LIKE ?", pName, pName + ",%", "%," + pName);
				List<Object> homes = new ArrayList<Object>();
				while (res.next()) {
					homes.add(Nicknames.getNick(res.getString("player_name")) + (multiHomesEnabled ? " (" + res.getString("world_name") + ")" : ""));
				}
				res.close();

				if (homes.size() > 0) {
					// tell player to whose homes he's invited
					LogHelper.showInfo("homeInvitedTo#####[ " + Utils.implode(homes, ", "), sender);
				} else {
					// the player is not invited into anybody's home
					LogHelper.showInfo("homeNotInvitedToAnyHome", sender);
				}
			} catch (Throwable e) {
				// something went wrong with our SQL...
				LogHelper.showWarning("internalError", sender);
				LogHelper.logSevere("[CommandsEX] " + _("dbReadError", ""));
				LogHelper.logDebug("Message: " + e.getMessage() + ", cause: " + e.getCause());
			}
		}
	}
	
	/***
	 * LIST - display whose homes a player can visit
	 * @param sender
	 * @param args
	 * @param command
	 * @param alias
	 * @return
	 */
	public static Boolean list(CommandSender sender, String[] args, String command, String alias) {
		if (CommandsEX.sqlEnabled) {
			Player player = (Player)sender;
			
			// check to which homes is player invited, if we didn't spam too much :)
			if (!Utils.checkCommandSpam(player, "home-list")) {
				// create ExecutorService to manage threads                        
				ExecutorService threadExecutor = Executors.newFixedThreadPool(1);
				threadExecutor.execute(new ListInvitedHomes(sender)); // execute the LIST command thread
				threadExecutor.shutdown(); // shutdown worker threads
			}
		} else {
			// no database, no homes
			LogHelper.showInfo("homeNoDatabase", sender);
		}
		
        return true;
	}
	
	/***
	 * ILIST - display everyone invited to player's home
	 * @param sender
	 * @param args
	 * @param command
	 * @param alias
	 * @return
	 */
	public static Boolean ilist(CommandSender sender, String[] args, String command, String alias) {
		if (CommandsEX.sqlEnabled) {
			Player player = (Player)sender;
			
			// check who is invited to player's home, if we didn't spam too much :)
			if (!Utils.checkCommandSpam(player, "home-ilist")) {
				try {
					String pName = player.getName();
					// holds players invited to homes in nickname form
					List<String> nickPlayers = new ArrayList<String>();
					ResultSet res;
					if (multiHomesEnabled) {
						res = SQLManager.query_res("SELECT allowed_players FROM " + SQLManager.prefix + "homes WHERE player_name = ? AND world_name = ?", pName, player.getWorld().getName());
					} else {
						res = SQLManager.query_res("SELECT allowed_players FROM " + SQLManager.prefix + "homes WHERE player_name = ?", pName);
					}
					
					while (res.next()) {
						for (String s : Utils.separateCommaList(res.getString("allowed_players").replace(",", ", "))){
							nickPlayers.add(Nicknames.getNick(s));
						}
					}
					res.close();

					if (nickPlayers.size() > 0) {
						// tell player who is invited to his home
						LogHelper.showInfo("homeInvotedToYourHome" + (multiHomesEnabled ? "[ #####homeInCurrentWorld" : "") + "#####[: " + Utils.implode(nickPlayers, ", "), sender);
					} else {
						// the player is not invited into anybody's home
						LogHelper.showInfo("homeNoPlayersInvited1" + (multiHomesEnabled ? "#####homeInCurrentWorld#####[ " : "") + "#####homeNoPlayersInvited2", sender);
					}
				} catch (Throwable e) {
					// something went wrong with our SQL...
					LogHelper.showWarning("internalError", sender);
					LogHelper.logSevere("[CommandsEX] " + _("dbReadError", ""));
					LogHelper.logDebug("Message: " + e.getMessage() + ", cause: " + e.getCause());
				}
			}
		} else {
			// no database, no homes
			LogHelper.showInfo("homeNoDatabase", sender);
		}
		
        return true;
	}
	
	/***
	 * LISTALL - lists all homes created on the server
	 * @param sender
	 * @param args
	 * @param command
	 * @param alias
	 * @return
	 */
	public static Boolean listall(CommandSender sender, String[] args, String command, String alias) {
		if (CommandsEX.sqlEnabled) {
			Player player = (Player)sender;
			
			// check who is invited to player's home, if we didn't spam too much :)
			if (!Utils.checkCommandSpam(player, "home-listall")) {
				try {
					// holds the names of players with houses in nickname form
					List<Object> homes = new ArrayList<Object>();
					ResultSet res;
					res = SQLManager.query_res("SELECT player_name, world_name FROM " + SQLManager.prefix + "homes");
					while (res.next()) {
						homes.add(Nicknames.getNick(res.getString("player_name")) + (multiHomesEnabled ? " (" + res.getString("world_name") + ")" : ""));
					}
					res.close();

					if (homes.size() > 0) {
						// show all homes
						LogHelper.showInfo("homeAllHomes#####[" + Utils.implode(homes, ", "), sender);
					} else {
						// no homes on the server
						LogHelper.showInfo("homeNoPlayersInvited1" + (multiHomesEnabled ? "#####[ #####homeInCurrentWorld#####[ " : "") + "#####homeNoPlayersInvited2", sender);
					}
				} catch (Throwable e) {
					// something went wrong with our SQL...
					LogHelper.showWarning("internalError", sender);
					LogHelper.logSevere("[CommandsEX] " + _("dbReadError", ""));
					LogHelper.logDebug("Message: " + e.getMessage() + ", cause: " + e.getCause());
				}
			}
		} else {
			// no database, no homes
			LogHelper.showInfo("homeNoDatabase", sender);
		}
		
        return true;
	}
	
	/***
	 * PUBLIC - makes player's home public
	 * @param sender
	 * @param args
	 * @param command
	 * @param alias
	 * @return
	 */
	public static Boolean make_public(CommandSender sender, String[] args, String command, String alias) {
		if (CommandsEX.sqlEnabled) {
			Player player = (Player)sender;
			
			// make player's home public, if we didn't spam too much :)
			if (!Utils.checkCommandSpam(player, "home-public")) {
				try {
					String pName = player.getName();
					Boolean homeFound = false;
					ResultSet res;

					// let's see if the player has any home set
					if (multiHomesEnabled) {
						res = SQLManager.query_res("SELECT id_home FROM " + SQLManager.prefix + "homes WHERE player_name = ? AND world_name = ?", pName, player.getWorld().getName());
					} else {
						res = SQLManager.query_res("SELECT id_home FROM " + SQLManager.prefix + "homes WHERE player_name = ?", pName);
					}
					
					while (res.next()) {
						SQLManager.query("UPDATE " + SQLManager.prefix + "homes SET is_public = 1 WHERE id_home = ?", res.getInt("id_home"));
						homeFound = true;
					}
					res.close();

					if (homeFound) {
						// inform player about successful operation
						LogHelper.showInfos(sender, new String[] {"homeYourHome#####[ " + (multiHomesEnabled ? "#####homeInCurrentWorld#####[ " : "") + "#####homeMadePublic1", "homeMadePublic2#####[" + pName});
					} else {
						// if we're here, it means no home was found for our player in current world... so we tell him
						if (multiHomesEnabled) {
							LogHelper.showInfos(sender, new String[] {"homeNoHomeOwnerCurrentWorld1", "homeNoHomeOwnerCurrentWorld2"});
						} else {
							LogHelper.showInfos(sender, new String[] {"homeNoHomeOwner1", "homeNoHomeOwner2"});
						}
					}
				} catch (Throwable e) {
					// something went wrong with our SQL...
					LogHelper.showWarning("internalError", sender);
					LogHelper.logSevere("[CommandsEX] " + _("dbReadError", ""));
					LogHelper.logDebug("Message: " + e.getMessage() + ", cause: " + e.getCause());
				}
			}
		} else {
			// no database, no homes
			LogHelper.showInfo("homeNoDatabase", sender);
		}
		
        return true;
	}
	
	/***
	 * PRIVATE - makes player's home private
	 * @param sender
	 * @param args
	 * @param command
	 * @param alias
	 * @return
	 */
	public static Boolean make_private(CommandSender sender, String[] args, String command, String alias) {
		if (CommandsEX.sqlEnabled) {
			Player player = (Player)sender;
			
			// make player's home public, if we didn't spam too much :)
			if (!Utils.checkCommandSpam(player, "home-private")) {
				try {
					String pName = player.getName();
					Boolean homeFound = false;
					ResultSet res;

					// let's see if the player has any home set
					if (multiHomesEnabled) {
						res = SQLManager.query_res("SELECT id_home FROM " + SQLManager.prefix + "homes WHERE player_name = ? AND world_name = ?", pName, player.getWorld().getName());
					} else {
						res = SQLManager.query_res("SELECT id_home FROM " + SQLManager.prefix + "homes WHERE player_name = ?", pName);
					}
					
					while (res.next()) {
						SQLManager.query("UPDATE " + SQLManager.prefix + "homes SET is_public = 0 WHERE id_home = ?", res.getInt("id_home"));
						homeFound = true;
					}
					res.close();

					if (homeFound) {
						// inform player about successful operation
						LogHelper.showInfo("homeYourHome#####[ " + (multiHomesEnabled ? "#####homeInCurrentWorld#####[ " : "") + "#####homeMadePrivate", sender);
					} else {
						// if we're here, it means no home was found for our player in current world... so we tell him
						if (multiHomesEnabled) {
							LogHelper.showInfos(sender, new String[] {"homeNoHomeOwnerCurrentWorld1", "homeNoHomeOwnerCurrentWorld2"});
						} else {
							LogHelper.showInfos(sender, new String[] {"homeNoHomeOwner1", "homeNoHomeOwner2"});
						}
					}
				} catch (Throwable e) {
					// something went wrong with our SQL...
					LogHelper.showWarning("internalError", sender);
					LogHelper.logSevere("[CommandsEX] " + _("dbReadError", ""));
					LogHelper.logDebug("Message: " + e.getMessage() + ", cause: " + e.getCause());
				}
			}
		} else {
			// no database, no homes
			LogHelper.showInfo("homeNoDatabase", sender);
		}
		
        return true;
	}
	
	/***
	 * DELETE - removes player's home
	 * @param sender
	 * @param args
	 * @param command
	 * @param alias
	 * @return
	 */
	public static Boolean delete(CommandSender sender, String[] args, String command, String alias) {
		if (CommandsEX.sqlEnabled) {
			Player player = (Player)sender;
			
			// delete player's home, if we didn't spam too much :)
			if (!Utils.checkCommandSpam(player, "home-delete")) {
				try {
					// check if we're having player's name passed as a parameter, in which case that'll be our home to delete
					String pName = "";
					if (args.length > 1) {
						pName = args[1];
					} else {
						pName = player.getName();
					}
					List<Object> homeIds = new ArrayList<Object>();
					ResultSet res;

					// let's see if the player has any home set
					if (multiHomesEnabled) {
						res = SQLManager.query_res("SELECT id_home FROM " + SQLManager.prefix + "homes WHERE player_name = ? AND world_name = ?", pName, player.getWorld().getName());
					} else {
						res = SQLManager.query_res("SELECT id_home FROM " + SQLManager.prefix + "homes WHERE player_name = ?", pName);
					}
					
					// assemble list of home IDs to delete
					while (res.next()) {
						homeIds.add(res.getInt("id_home"));
					}
					res.close();
					
					// remove homes
					Integer hSize = homeIds.size();
					if (hSize > 0) {
						String[] qMarks = new String[hSize];
						Arrays.fill(qMarks, "?");
						SQLManager.query("DELETE FROM " + SQLManager.prefix + "homes WHERE id_home IN ("+ Utils.implode(qMarks, ",") +")", homeIds);
						// inform player about successful operation
						LogHelper.showInfo("home#####[ " + (multiHomesEnabled ? "#####homeInCurrentWorld#####[ " : "") + "#####homeDeleted", sender);
					} else {
						if (args.length == 1) {
							if (multiHomesEnabled) {
								LogHelper.showInfos(sender, new String[] {"homeNoHomeOwnerCurrentWorld1", "homeNoHomeOwnerCurrentWorld2"});
							} else {
								LogHelper.showInfos(sender, new String[] {"homeNoHomeOwner1", "homeNoHomeOwner2"});
							}
						} else {
							LogHelper.showInfo("[" + Nicknames.getNick(pName) + " #####homeNoHomeVisitor" + (multiHomesEnabled ? "#####homeInCurrentWorld" : ""), sender);
						}
					}
				} catch (Throwable e) {
					// something went wrong with our SQL...
					LogHelper.showWarning("internalError", sender);
					LogHelper.logSevere("[CommandsEX] " + _("dbReadError", ""));
					LogHelper.logDebug("Message: " + e.getMessage() + ", cause: " + e.getCause());
				}
			}
		} else {
			// no database, no homes
			LogHelper.showInfo("homeNoDatabase", sender);
		}
		
        return true;
	}

	/***
	 * single-purpose class used to run /home iclear command, since it can take a while to compare
	 * all homes against offline player records
	 */
	static class ClearOldHomes implements Runnable {
		
		private Integer daysToPast;
		private CommandSender sender;
		
		public ClearOldHomes(CommandSender s, Integer d) {
			this.sender = s;
			this.daysToPast = d;
		}
		
		@Override
		public void run() {
			try {
				ResultSet res = SQLManager.query_res("SELECT id_home, player_name FROM " + SQLManager.prefix + "homes");
				List<Object> toClear = new ArrayList<Object>();
				// convert days into seconds
				Integer daysInSeconds = (this.daysToPast * 24 * 60 * 60);
				// get current Unix timestamp
				Integer stamp = Utils.getUnixTimestamp(0L);

				while (res.next()) {
					// get player's last join time
					OfflinePlayer o = CommandsEX.plugin.getServer().getOfflinePlayer(res.getString("player_name"));
					Integer lastJoinTime = Utils.getUnixTimestamp(o.getLastPlayed());
					
					// compare last join time with our requested number of days to past
					if ((stamp - lastJoinTime) >= daysInSeconds) {
						toClear.add(res.getInt("id_home"));
					}
				}
				res.close();

				if (toClear.size() > 0) {
					// we have found some homes to clean up, ask player to confirm deletion
					Home.clearHomesConfirmation.put(this.sender.getName(), toClear);
					LogHelper.showInfo("homeClearConfirm1#####[" + toClear.size() + "#####[ #####homeClearConfirm2#####[" + this.daysToPast + "#####[ #####homeClearConfirm3", sender);
				} else {
					// no homes found matching criteria
					LogHelper.showInfo("homeClearNoHomes", sender);
				}
			} catch (Throwable e) {
				// something went wrong with our SQL...
				LogHelper.showWarning("internalError", sender);
				LogHelper.logSevere("[CommandsEX] " + _("dbReadError", ""));
				LogHelper.logDebug("Message: " + e.getMessage() + ", cause: " + e.getCause());
			}
		}
	}
	
	/***
	 * ICLEAR - clears all homes older than given number of days from the database
	 * @param sender
	 * @param args
	 * @param command
	 * @param alias
	 * @return
	 */
	public static Boolean iclear(CommandSender sender, String[] args, String command, String alias) {
		if (CommandsEX.sqlEnabled) {
			Player player = (Player)sender;
			
			// select all homes to be cleared up, if we didn't spam too much :)
			if (!Utils.checkCommandSpam(player, "home-iclear")) {
				if (args.length == 1) {
					// no arguments, provide command help
					Commands.showCommandHelpAndUsage(sender, "cex_home", "home_iclear");
					return true;
				}
				
				// check if the number of days is integer value
				if (!args[1].matches(CommandsEX.intRegex)) {
					LogHelper.showInfo("homeClearDaysNotNumeric", sender);
					return true;
				}
				
				// create ExecutorService to manage threads                        
				ExecutorService threadExecutor = Executors.newFixedThreadPool(1);
				threadExecutor.execute(new ClearOldHomes(sender, Integer.parseInt(args[1]))); // execute the ICLEAR command thread
				threadExecutor.shutdown(); // shutdown worker threads
			}
		} else {
			// no database, no homes
			LogHelper.showInfo("homeNoDatabase", sender);
		}
		
        return true;
	}
	
	/***
	 * TCLEAR - teleports player to first home that would be cleared if /home iclear <days> would be run
	 * @param sender
	 * @param args
	 * @param command
	 * @param alias
	 * @return
	 */
	public static Boolean tclear(CommandSender sender, String[] args, String command, String alias) {
		if (CommandsEX.sqlEnabled) {
			Player player = (Player)sender;
			
			// teleport to first clear-up-eligible home, if we didn't spam too much :)
			if (!Utils.checkCommandSpam(player, "home-tclear")) {
				if (args.length == 1) {
					// no arguments, provide command help
					Commands.showCommandHelpAndUsage(sender, "cex_home", "home_tclear");
					return true;
				}
				
				// check if the number of days is integer value
				if (!args[1].matches(CommandsEX.intRegex)) {
					LogHelper.showInfo("homeClearDaysNotNumeric", sender);
					return true;
				}
				
				try {
					ResultSet res = SQLManager.query_res("SELECT player_name, world_name, x, y, z, yaw, pitch FROM " + SQLManager.prefix + "homes");
					// convert days into seconds
					Integer daysInSeconds = (Integer.parseInt(args[1]) * 24 * 60 * 60);
					// get current Unix timestamp
					Integer stamp = Utils.getUnixTimestamp(0L);

					while (res.next()) {
						// get player's last join time
						OfflinePlayer o = CommandsEX.plugin.getServer().getOfflinePlayer(res.getString("player_name"));
						Integer lastJoinTime = Utils.getUnixTimestamp(o.getLastPlayed());
						
						// compare last join time with our requested number of days to past
						if ((stamp - lastJoinTime) >= daysInSeconds) {
							// match found, teleport player
							Teleportation.delayedTeleport(player, new Location(CommandsEX.plugin.getServer().getWorld(res.getString("world_name")), res.getInt("x"), res.getInt("y"), res.getInt("z"), (float) res.getDouble("yaw"), (float) res.getDouble("pitch")) );
							// tell player where he is
							LogHelper.showInfo("homeWelcomeTo#####[" + Nicknames.getNick(res.getString("player_name")) + "#####homePlayersHome", sender);
							res.close();
							return true;
						}
					}
					res.close();

					// if we get here, there were no homes found matching criteria
					LogHelper.showInfo("homeClearNoHomes", sender);
				} catch (Throwable e) {
					// something went wrong with our SQL...
					LogHelper.showWarning("internalError", sender);
					LogHelper.logSevere("[CommandsEX] " + _("dbReadError", ""));
					LogHelper.logDebug("Message: " + e.getMessage() + ", cause: " + e.getCause());
				}
			}
		} else {
			// no database, no homes
			LogHelper.showInfo("homeNoDatabase", sender);
		}
		
        return true;
	}
	
	/***
	 * HELP - displays homes usage
	 * @param sender
	 * @param args
	 * @param command
	 * @param alias
	 * @return
	 */
	public static Boolean help(CommandSender sender, String[] args, String command, String alias) {
		if (CommandsEX.sqlEnabled) {
			// show help
			LogHelper.showInfos(sender, "homeHelp1", "homeHelp2", "homeHelp3", "homeHelp4", "homeHelp5", "homeHelp6", "homeHelp7", "homeHelp8", "homeHelp9", "homeHelp10", "homeHelp11", "homeHelp12", "homeHelp13", "homeHelp14", "homeHelp15", "homeHelp16");
		} else {
			// no database, no homes
			LogHelper.showInfo("homeNoDatabase", sender);
		}
		
        return true;
	}
}