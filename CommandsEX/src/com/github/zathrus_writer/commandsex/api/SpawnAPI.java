package com.github.zathrus_writer.commandsex.api;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Spawning;

public class SpawnAPI {

	/**
	 * Is the server set to have a spawn per world, or a global spawn point
	 * @return Return's true for global spawn, false for per world spawn
	 */
	
	public static boolean isGlobalSpawnEnabled(){
		return CommandsEX.getConf().getBoolean("perWorldSpawn");
	}
	
	/**
	 * Enables or Disables Global Spawn
	 * @param set True for global spawn, false for per world spawn
	 */
	
	public static void setGlobalSpawn(boolean set){
		CommandsEX.getConf().set("perWorldSpawn", !set);
	}
	
	/**
	 * If global spawn is enabled, this is the world players should be teleported to
	 * @param world The world to set the global spawn to
	 */
	
	public static void setGlobalSpawnWorld(String world){
		World w = Bukkit.getWorld(world);
		
		if (w == null){
			LogHelper.logDebug("Plugin tried to set default spawn world to " + world + " but that world does not exist");
			return;
		}
		
		CommandsEX.getConf().set("globalSpawnWorld", world);
	}
	
	/**
	 * Gets the global spawn world, if global spawning is enabled
	 * @return Returns the name of the world that global spawn is set to
	 */
	
	public static String getGlobalSpawnWorld(){
		return CommandsEX.getConf().getString("globalSpawnWorld");
	}
	
	/**
	 * Get's a worlds spawn
	 * @param world
	 * @return
	 */
	
	public static Location getSpawn(String world){
		return Spawning.getWorldSpawn(Bukkit.getWorld(world));
	}
	
	/**
	 * Sets a worlds spawn
	 * @param world The world to set the spawn for
	 * @param loc The location to set the spawn to
	 */
	
	public static void setSpawn(String world, Location loc){
		if (Spawning.worldSpawns.containsKey(world)){
			Spawning.worldSpawns.remove(world);
		}
		
		Spawning.worldSpawns.put(world, loc);
	}
	
	/**
	 * Saves the spawning database
	 */
	
	public static void saveDatabase(){
		Spawning.saveDatabase();
	}
}
