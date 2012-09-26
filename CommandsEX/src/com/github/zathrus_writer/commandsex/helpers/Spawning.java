package com.github.zathrus_writer.commandsex.helpers;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.SQLManager;

/***
 * Contains set of commands to be executed for world's spawn getting/setting purposes.
 * @author iKeirNez
 */

public class Spawning {
	
	/**
	 * How it works!
	 * -------------
	 * On startup spawns are loaded into the database if they have not already been loaded in and are also loaded from the database
	 * into a HashMap, when the spawn command is used the worldSpawn is collected from the HashMap. If any changes/additions need
	 * to be made to spawns, they are made in the HashMap. When CommandsEX is disabled, any worlds that were in the database that
	 * are not in the HashMap are deleted, any new ones are added and any changed spawns are changed in the database. This helps
	 * to prevent lag and constant database getting, setting and adding.
	 */
	
	public static HashMap<String, Location> worldSpawns = new HashMap<String, Location>();
	
	public static void init(CommandsEX p){
		// delete old spawns.yml config file
		File oldSpawnConfig = new File(CommandsEX.plugin.getDataFolder() + "/data/spawns.yml");
		if (oldSpawnConfig.exists()){
			LogHelper.logDebug("Old spawns.yml file detected in data directory, deleting...");
			oldSpawnConfig.delete();
		}
		
		// delete old data directory if empty
		File oldDataDirectory = new File(CommandsEX.plugin.getDataFolder() + "/data");
		if (oldDataDirectory.listFiles().length < 1){
			LogHelper.logDebug("Old data folder detected and empty, deleting...");
			oldDataDirectory.delete();
		}
		
		if (CommandsEX.sqlEnabled){
			SQLManager.query((SQLManager.sqlType.equals("mysql") ? "" : "BEGIN; ") + "CREATE TABLE IF NOT EXISTS " + SQLManager.prefix + "spawns (world_name varchar(32), x double precision, y double precision, z double precision, yaw float(32), pitch float(32))" + (SQLManager.sqlType.equals("mysql") ? " ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='stores world spawns'" : "; COMMIT;"));
			ResultSet rs = SQLManager.query_res("SELECT * FROM " + SQLManager.prefix + "spawns");

			// add all worlds that are in the database to the inDatabase list
			try {
				while (rs.next()){
					if (Bukkit.getWorld(rs.getString("world_name")) != null){
						worldSpawns.put(rs.getString("world_name"), new Location(Bukkit.getWorld(rs.getString("world_name")), rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"), rs.getFloat("yaw"), rs.getFloat("pitch")));
					}
				}
				
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

			if (CommandsEX.getConf().getBoolean("perWorldSpawn")){
				// load each worlds current spawn point if they have not already been added to the database
				for (World w : Bukkit.getWorlds()){
					if (!worldSpawns.containsKey(w.getName())){
						Location spawn = w.getSpawnLocation();
						worldSpawns.put(w.getName(), spawn);
					}
				}
				
				// set the default world if it has not already been set
				if (!CommandsEX.getConf().contains("globalSpawnWorld")){
					CommandsEX.getConf().set("globalSpawnWorld", Bukkit.getWorlds().get(0).getName());
					CommandsEX.plugin.saveConfig();
				}
			} else {
				// load default worlds spawn into the config
				World w = Bukkit.getWorld(CommandsEX.getConf().getString("globalSpawnWorld"));
				
				// if the world in the config does not exist
				if (w == null){
					LogHelper.logDebug("globalSpawnWorld in config.yml does not exist, resetting to default");
					CommandsEX.getConf().set("globalSpawnWorld", Bukkit.getWorlds().get(0).getName());
					w = Bukkit.getWorld(CommandsEX.getConf().getString("globalSpawnWorld"));
				}
				
				// add the default world to the database if it has not already been added
				if (!worldSpawns.containsKey(w.getName())){
					Location spawn = w.getSpawnLocation();
					worldSpawns.put(w.getName(), spawn);
				}
			}
			
			for (String s : worldSpawns.keySet()){
				if (Bukkit.getWorlds().contains(s)){
					// add all worlds in the database to the worldSpawns hashmap
					World w = Bukkit.getWorld(s);
					ResultSet res = SQLManager.query_res("SELECT world_name, x, y, z, yaw, pitch FROM " + SQLManager.prefix + "spawns WHERE world_name = ?", s);
					
					try {
						worldSpawns.put(s, new Location(w, res.getDouble("x"), res.getDouble("y"), res.getDouble("z"), res.getFloat("yaw"), res.getFloat("pitch")));
						res.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
			
			// save the database when the server is shutdown
			CommandsEX.onDisableFunctions.add("com.github.zathrus_writer.commandsex.helpers.Spawning#####onDisable");
		}
	}
	
	/**
	 * Save the database when the server is shutdown
	 * @param p
	 */
	
	public static void onDisable(CommandsEX p){
		saveDatabase();
	}

	/***
	 * DOSPAWN - teleports player to current world's spawn location
	 * @param sender
	 * @param args
	 * @param command
	 * @param alias
	 * @return
	 */
	public static Boolean doSpawn(CommandSender sender, String[] args, String command, String alias) {
		if (sender instanceof Player){
			if (Utils.checkCommandSpam((Player) sender, "spawn-go")){
				return true;
			}
		}
		
		// so far, this is a very simple function that will be expanded to allow random spawns and similar features
		Player target = null;

		if (args.length > 0){
			if (sender.hasPermission("cex.spawn.others")){
				target = Bukkit.getPlayer(args[0]);
			} else {
				LogHelper.showInfo("spawnOthersNoPerm", sender, ChatColor.RED);
				return true;
			}
		} else {
			target = (Player) sender;
		}
		
		if (target == null){
			LogHelper.showWarning("invalidPlayer", sender);
			return true;
		}
		
		String world;

		if (CommandsEX.getConf().getBoolean("perWorldSpawn")){
			world = target.getWorld().getName();
		} else {
			world = CommandsEX.getConf().getString("globalSpawnWorld");
		}

		
		Teleportation.delayedTeleport(target, worldSpawns.get(world));

		if (CommandsEX.getConf().getBoolean("perWorldSpawn")){
			if (sender.equals(target)){
				LogHelper.showInfo("spawnGoWorld", sender);
			} else {
				LogHelper.showInfo("[" + target.getName() + " #####spawnGoWorldOtherConfirm", sender);
				LogHelper.showInfo("[" + sender.getName() + " #####spawnGoWorldOtherNotify", target);
			}
		} else {
			if (sender.equals(target)){
				LogHelper.showInfo("spawnGoGlobal", sender);
			} else {
				LogHelper.showInfo("[" + target.getName() + " #####spawnGoGlobalOtherConfirm", sender);
				LogHelper.showInfo("[" + sender.getName() + " #####spawnGoGlobalOtherNotify", target);
			}
		}
		return true;
	}
	
	/***
	 * SETSPAWN - sets spawn location to the place where player is currently standing at
	 * @param sender
	 * @param args
	 * @param command
	 * @param alias
	 * @return
	 */
	public static Boolean setSpawn(CommandSender sender, String[] args, String command, String alias) {
		// so far, this is a very simple function that will be expanded to allow random spawns and similar features
		
		Player player = (Player)sender;
		if (!Utils.checkCommandSpam(player, "spawn-set")) {
			Location loc = player.getLocation();
			World world = player.getWorld();
	
			if (!CommandsEX.getConf().getBoolean("perWorldSpawn")){
				CommandsEX.getConf().set("globalSpawnWorld", world.getName());
			}

			if (worldSpawns.containsKey(world.getName())){
				worldSpawns.remove(world.getName());
			}
			
			worldSpawns.put(world.getName(), loc);
			
			if (CommandsEX.getConf().getBoolean("perWorldSpawn")){
				LogHelper.showInfo("spawnSetWorld", sender);
			} else {
				LogHelper.showInfo("spawnSetGlobal", sender);
			}
		}
		
        return true;
	}
	
	/**
	 * Function to save the worldSpawns hashmap to the CommandsEX database
	 */
	
	public static void saveDatabase(){
		// add or update spawns that are in the hashmap
		for (String w : worldSpawns.keySet()){
			ResultSet rs = SQLManager.query_res("SELECT world_name FROM " + SQLManager.prefix + "spawns WHERE world_name = ?", w);
			
			try {
				Location loc = worldSpawns.get(w);
				
				if (!rs.next()){
					SQLManager.query("INSERT " + (SQLManager.sqlType.equals("mysql") ? "" : "OR REPLACE ") + "INTO " + SQLManager.prefix + "spawns (world_name, x, y, z, yaw, pitch) SELECT ? AS world_name, ? AS x, ? AS y, ? AS z, ? AS yaw, ? AS pitch", w, loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
				} else {
					SQLManager.query("UPDATE " + SQLManager.prefix + "spawns SET world_name = ?, x = ?, y = ?, z = ?, yaw = ?, pitch = ?", w, loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
				}
				
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		// delete all entries in the database that are no longer in the worldSpawns hashmap
		ResultSet rs = SQLManager.query_res("SELECT world_name FROM " + SQLManager.prefix + "spawns");
		try {
			while (rs.next()){
				String wName = rs.getString("world_name");
				if (!worldSpawns.containsKey(wName)){
					SQLManager.query("DELETE FROM " + SQLManager.prefix + "spawns WHERE world_name = ?", wName);
				}
			}
			
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}