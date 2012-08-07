package com.github.zathrus_writer.commandsex.helpers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.SQLManager;

/***
 * Contains set of commands to be executed for world's spawn getting/setting purposes.
 * @author zathrus-writer
 *
 */
public class Spawning {
	/***
	 * DOSPAWN - teleports player to current world's spawn location
	 * @param sender
	 * @param args
	 * @param command
	 * @param alias
	 * @return
	 */
	public static Boolean doSpawn(CommandSender sender, String[] args, String command, String alias) {
		// so far, this is a very simple function that will be expanded to allow random spawns and similar features
		Player player = (Player) sender;
		if (!Utils.checkCommandSpam(player, "spawn-go")) {
			World world;
			
			if (CommandsEX.getConf().getBoolean("perWorldSpawn")){
				world = player.getWorld();
			} else {
				world = Bukkit.getWorlds().get(0);
			}
			
			Location l = world.getSpawnLocation();
			l.setYaw((float) CommandsEX.getConf().getDouble("spawnYaw"));
			l.setPitch((float) CommandsEX.getConf().getDouble("spawnPitch"));

			Teleportation.delayedTeleport(player, l);
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
	
			SQLManager.query("CREATE TABLE IF NOT EXISTS " + SQLManager.prefix + "spawning (id_spawn integer " + (SQLManager.sqlType.equals("mysql") ? "unsigned " : "") + "NOT NULL" + (SQLManager.sqlType.equals("mysql") ? " AUTO_INCREMENT" : "") + ", world_name varchar(32) " + (SQLManager.sqlType.equals("mysql") ? "unsigned " : "") + " NOT NULL, x double " + (SQLManager.sqlType.equals("mysql") ? "unsigned " : "") + " NOT NULL, y double " + (SQLManager.sqlType.equals("mysql") ? "unsigned " : "") + " NOT NULL, z double " + (SQLManager.sqlType.equals("mysql") ? "unsigned " : "") + " NOT NULL, yaw double " + (SQLManager.sqlType.equals("mysql") ? "unsigned " : "") + " NOT NULL, pitch double " + (SQLManager.sqlType.equals("mysql") ? "unsigned " : "") + " NOT NULL, PRIMARY KEY (id_ban))" + (SQLManager.sqlType.equals("mysql") ? " ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='holds spawn points for each world' AUTO_INCREMENT=1" : ""));
			
			LogHelper.showInfo("spawnSet", sender);
		}
		
        return true;
	}
}