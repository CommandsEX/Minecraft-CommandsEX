package com.github.zathrus_writer.commandsex.commands;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;

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
		
		// Variables
		Random random = new Random();
		final int explosionStrength = CommandsEX.getConf().getInt("KittyCannonExplosionStrength");
		
		// Complicated stuff... cbb to explain
		Player player = (Player)sender;
		final Ocelot ocelot = (Ocelot) player.getWorld().spawnCreature(player.getEyeLocation(), EntityType.OCELOT);;
		final int i = random.nextInt(Ocelot.Type.values().length);
		ocelot.setCatType(Ocelot.Type.values()[i]);
		ocelot.setTamed(true);
		ocelot.setVelocity(player.getEyeLocation().getDirection().multiply(2));
		LogHelper.showInfo("kittycannoncreated", sender);
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(CommandsEX.plugin, new Runnable() {
			public void run() {
				final Location loc = ocelot.getLocation();
				ocelot.remove();
				loc.getWorld().createExplosion(loc, explosionStrength);
			}
		}, 20L);
		
		return true;
	}
}
