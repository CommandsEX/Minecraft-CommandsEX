package com.github.zathrus_writer.commandsex.helpers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.CommandsEX;

/***
 * Contains set of commands to be executed for world's spawn getting/setting purposes.
 * @author zathrus-writer
 *
 */
public class Spawning {
	
	private static FileConfiguration spawnConfig = null;
	private static File spawnConfigFile = null;
	
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
		
		if (sender instanceof Player){
			if (Utils.checkCommandSpam((Player) sender, "spawn-go")){
				return true;
			}
		}

		World world;

		if (CommandsEX.getConf().getBoolean("perWorldSpawn")){
			world = target.getWorld();
		} else {
			if (!getSpawns().contains("globalSpawnWorld")){
				getSpawns().set("globalSpawnWorld", Bukkit.getWorlds().get(0).getName());
			}

			world = Bukkit.getWorld(getSpawns().getString("globalSpawnWorld"));
		}

		Location spawn = world.getSpawnLocation();

		double x = spawn.getX();
		double y = spawn.getY();
		double z = spawn.getZ();

		// set yaw to 0 if it does not exist
		if (!getSpawns().contains(world.getName() + ".yaw")){
			getSpawns().set(world.getName() + ".yaw", 0);
		}

		// set pitch to 0 if it does not exist
		if (!getSpawns().contains(world.getName() + ".pitch")){
			getSpawns().set(world.getName() + ".pitch", 0);
		}

		float yaw = (float) getSpawns().getDouble(world.getName() + ".yaw");
		float pitch = (float) getSpawns().getDouble(world.getName() + ".pitch");

		Location l = new Location(world, x, y, z, yaw, pitch);
		Teleportation.delayedTeleport(target, l);

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
	
			if (CommandsEX.getConf().getBoolean("perWorldSpawn")){
				getSpawns().set("globalSpawnWorld", null);
			} else {
				getSpawns().set("globalSpawnWorld", world.getName());
			}
			
			world.setSpawnLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
			getSpawns().set(world.getName() + ".yaw", loc.getYaw());
			getSpawns().set(world.getName() + ".pitch", loc.getPitch());
			saveSpawns();
			
			if (CommandsEX.getConf().getBoolean("perWorldSpawn")){
				LogHelper.showInfo("spawnSetWorld", sender);
			} else {
				LogHelper.showInfo("spawnSetGlobal", sender);
			}
		}
		
        return true;
	}
	
	public static FileConfiguration getSpawns() {
	    if (spawnConfig == null) {
	        reloadSpawns();
	    }
	    return spawnConfig;
	}
	
	public static void saveSpawns() {
	    if (spawnConfig == null || spawnConfigFile == null) {
	    return;
	    }
	    try {
	        getSpawns().save(spawnConfigFile);
	    } catch (IOException ex) {
	        CommandsEX.plugin.getLogger().log(Level.SEVERE, "Could not save config to " + spawnConfigFile, ex);
	    }
	}
	
	public static void reloadSpawns() {
	    if (spawnConfigFile == null) {
	    spawnConfigFile = new File(CommandsEX.plugin.getDataFolder(), "data/spawns.yml");
	    }
	    spawnConfig = YamlConfiguration.loadConfiguration(spawnConfigFile);
	 
	    // Look for defaults in the jar
	    InputStream defConfigStream = CommandsEX.plugin.getResource("spawns.yml");
	    if (defConfigStream != null) {
	        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	        spawnConfig.setDefaults(defConfig);
	    }
	}
}