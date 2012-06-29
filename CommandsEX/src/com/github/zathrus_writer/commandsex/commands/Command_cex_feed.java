package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_feed {
	
	/***
	 * FEED - fills a players food level
	 * @author Kezz101
	 * @param sender
	 * @param args
	 * @return
	 */
	
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		
		if (sender instanceof Player){
			Player player = (Player) sender;
			if (Utils.checkCommandSpam(player, "cex_feed")){
				return true;
			}
		}
		
		if (!(sender instanceof Player) && (args.length == 0)) {
			LogHelper.showWarning("playerNameMissing", sender);
			return true;
		}

		Player beingFed;
		
		// Check they want to feed someone else
		if ((args.length > 0) && (!(sender instanceof Player) || ((Player) sender).hasPermission("cex.feed.others"))) {

			// Change player
			beingFed = Bukkit.getPlayer(args[0]);
			
			// Check they are online
			if (beingFed==null) {
				LogHelper.showWarning("invalidPlayer", sender);
				return true;
			}
		} else {
			LogHelper.showInfo("feedOthersNoPerm", sender, ChatColor.RED);
			return true;
		}
	
		// Set food level
		beingFed.setFoodLevel(20);
			
		// Send message(s)
		if (sender.getName().equals(beingFed.getName())) {
			LogHelper.showInfo("feedFed", beingFed);
		} else {
			LogHelper.showInfo("feedFedBySomeoneElse#####" + "[" + sender.getName(), beingFed);
			LogHelper.showInfo("feedFedSomeoneElse#####" + "[" + beingFed.getName(), sender);
		}
		
		return true;
	}
}
