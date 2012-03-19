package com.github.zathrus_writer.commandsex.helpers;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
		Player player = (Player)sender;
		World world = player.getWorld();

		player.teleport(world.getSpawnLocation());
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
		Location loc = player.getLocation();
		World world = player.getWorld();

		world.setSpawnLocation((int)loc.getX(), (int)loc.getY(), (int)loc.getZ());
		LogHelper.showInfo("spawnSet", sender);
		
        return true;
	}
}