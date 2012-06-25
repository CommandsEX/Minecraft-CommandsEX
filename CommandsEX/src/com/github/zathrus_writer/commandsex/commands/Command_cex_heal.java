package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Permissions;

public class Command_cex_heal {

	/***
	 * HEAL - fills a players health level
	 * @author iKeirNez
	 * @param sender
	 * @param args
	 * @return
	 */
	
	public static Boolean run(CommandSender sender, String alias, String[] args){
		
		if (!(sender instanceof Player) && (args.length == 0)) {
			LogHelper.showWarning("playerNameMissing", sender);
			return true;
		}

		Player beingHealed;
		
		// Check they want to feed someone else
		if ((args.length > 0) && (!(sender instanceof Player) || Permissions.checkPerms((Player) sender, "cex.heal.others"))) {

			// Change player
			beingHealed = Bukkit.getPlayer(args[0]);
			
			// Check they are online
			if (beingHealed == null) {
				LogHelper.showWarning("invalidPlayer", sender);
				return true;
			}
		} else {
			beingHealed = (Player) sender;
		}
	
		// Set food level
		beingHealed.setFoodLevel(20);
			
		// Send message(s)
		if (sender.getName().equals(beingHealed.getName())) {
			LogHelper.showInfo("healHealed", beingHealed);
		} else {
			LogHelper.showInfo("healHealedBySomeoneElse#####" + "[" + sender.getName(), beingHealed);
			LogHelper.showInfo("healHealedSomeoneElse#####" + "[" + beingHealed.getName(), sender);
		}
		return true;
	}
}
