package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Permissions;

public class Command_cex_feed {
	
	/***
	 * FEED - fills a players food level
	 * @author Kezz101
	 * @param sender
	 * @param args
	 * @return
	 */
	
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		
		// Check they are not console
		if(!(sender instanceof Player)) {
			LogHelper.logWarning("inWorldCommandOnly");
			return true;
		}
		
		// Main variables
		Player cmdSender = (Player)sender;
		Player beingFed = (Player)sender;
		
		// Check they want to feed someone else
		if(args.length > 0 && Permissions.checkPerms(cmdSender, "cex.feed.others")) {
			
			// Change player
			beingFed=Bukkit.getServer().getPlayerExact(args[0]);
			
			// Check they are online
			if(beingFed==null) {
				LogHelper.showWarning("noSuchPlayer", cmdSender);
				return true;
			}
			
			// Feed
			beingFed.setFoodLevel(20);
			
			// Alert sender/beingFed
			LogHelper.showInfo("feedFedBySomeoneElse#####" + "[" + cmdSender.getName(), beingFed);
			LogHelper.showInfo("feedFedSomeoneElse#####" + "[" + beingFed.getName(), cmdSender);
			
			return true;
			
		}
		
		// Set food level
		beingFed.setFoodLevel(20);
		
		// Send message
		LogHelper.showInfo("feedFed", beingFed);
		
		return true;
	}
}
