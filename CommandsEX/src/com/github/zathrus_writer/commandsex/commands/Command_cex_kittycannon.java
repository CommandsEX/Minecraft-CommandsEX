package com.github.zathrus_writer.commandsex.commands;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_kittycannon {
	
	/***
	 * KITTYCANNON - shoots a cat from your self!
	 *  HAPPY 1000th (BUKKIT DEV) DOWNLOAD
	 *  @author Kezz101
	 *  @params sender
	 *  @params args
	 *  @return
	 */
	
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		
		if(!(PlayerHelper.checkIsPlayer(sender))) {
			return true;
		}
		
		if (sender instanceof Player){
			Player player = (Player) sender;
			if (Utils.checkCommandSpam(player, "cex_kittycannon")){
				return true;
			}
		}
		
		if (args.length == 0){
			// Variables
			Random random = new Random();
			final int explosionStrength = CommandsEX.getConf().getInt("KittyCannonExplosionStrength");
			
			Player player = (Player)sender;
			// Spawn an ocelot
			final Ocelot ocelot = (Ocelot) player.getWorld().spawnCreature(player.getEyeLocation(), EntityType.OCELOT);;
			// Get the amount of types of Ocelot exist and choose a random one
			final int i = random.nextInt(Ocelot.Type.values().length);
			// Set the type of cat spawned to the value above
			ocelot.setCatType(Ocelot.Type.values()[i]);
			ocelot.setTamed(true);
			// This launches the Ocelot
			ocelot.setVelocity(player.getEyeLocation().getDirection().multiply(2));
			LogHelper.showInfo("kittycannoncreated", sender);
			// Explode the Ocelot after an amount of time.
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(CommandsEX.plugin, new Runnable() {
				public void run() {
					final Location loc = ocelot.getLocation();
					ocelot.remove();
					loc.getWorld().createExplosion(loc, explosionStrength);
				}
			}, 20L);
		} else {
			Commands.showCommandHelpAndUsage(sender, "cex_kittycannon", alias);
		}
		return true;
	}
}
