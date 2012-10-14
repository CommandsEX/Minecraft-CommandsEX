package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.util.Vector;

import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;

public class Command_cex_fireball {

	/**
	 * Fireball - Fires a fireball at your eye location
	 * @param sender
	 * @param alias
	 * @param args
	 * @return
	 */
	
	public static Boolean run(CommandSender sender, String alias, String[] args){
		if (!PlayerHelper.checkIsPlayer(sender)){
			return true;
		}
		
		boolean smallFireball = false;
		if (args.length == 1){
			if (args[0].equalsIgnoreCase("small")){
				smallFireball = true;
			} else {
				Commands.showCommandHelpAndUsage(sender, "cex_fireball", alias);
				return true;
			}
		}
		
		Player player = (Player) sender;
		Location eye = player.getEyeLocation().add(0.5, 0.5, 0.5);
		Vector v = eye.getDirection().multiply(2);
		Fireball entity = player.getWorld().spawn(eye.add(v.getX(), v.getY(), v.getZ()), (!smallFireball ? Fireball.class : SmallFireball.class));
		entity.setShooter(player);
		entity.setVelocity(eye.getDirection().multiply(2));
		
		LogHelper.showInfo("fireballSuccess", sender);
		return true;
	}
	
}
