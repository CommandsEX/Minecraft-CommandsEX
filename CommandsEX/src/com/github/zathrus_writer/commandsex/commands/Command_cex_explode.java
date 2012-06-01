package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Permissions;

public class Command_cex_explode {
	
	/***
	 * EXPLODE - kills a player with EXPLOSIONS
	 * @param sender
	 * @param args
	 * @return
	 */
	
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (!(sender instanceof Player) || ((sender instanceof Player) && Permissions.checkPerms((Player) sender, "cex.explode"))) {
			if(args.length > 0) {
				// get variables about the player 
				Player exploded = Bukkit.getServer().getPlayer(args[0]);
				Location loc = exploded.getLocation();
				
				// smite the player
				exploded.getWorld().createExplosion(loc, -1);
				exploded.setHealth(0);
				
				// show the sender a message
				LogHelper.showInfo("explodePlayer#####[" + exploded.getName(), sender);
				
				// config variable
				Boolean showMessageOnExplode = CommandsEX.getConf().getBoolean("showMessageOnExplode");
				
				// show who smited the smitee (is that a word)
				if(showMessageOnExplode == true) {
					LogHelper.showWarning("explodeRecieveExplode#####[" + sender.getName(), exploded);
				}
				
			} else {
				Commands.showCommandHelpAndUsage(sender, "cex_explode", alias);
			}
		}
		return true;
	}
}
