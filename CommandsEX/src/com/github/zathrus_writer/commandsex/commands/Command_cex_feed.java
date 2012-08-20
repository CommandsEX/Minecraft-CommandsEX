package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Nicknames;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_feed {

	/***
	 * FEED - fills a players food level
	 * @author iKeirNez
	 * @param sender
	 * @param args
	 * @return
	 */
	
	public static Boolean run(CommandSender sender, String alias, String[] args){
	
		if (sender instanceof Player){
			Player player = (Player) sender;
			if (Utils.checkCommandSpam(player, "cex_feed")){
				return true;
			}
		}
		
		if (args.length == 0){
			if (!(sender instanceof Player)){
				Commands.showCommandHelpAndUsage(sender, "cex_feed", alias);
				return true;
			}
			
			Player player = (Player) sender;
			player.setFoodLevel(20);
			LogHelper.showInfo("feedFed", sender, ChatColor.AQUA);
		} else if (args.length == 1){
			Player beingFed = Bukkit.getPlayer(args[0]);
			
			if (beingFed == null){
				LogHelper.showInfo("invalidPlayer", sender, ChatColor.RED);
				return true;
			}

			beingFed.setFoodLevel(20);
			
			if (sender.getName().equalsIgnoreCase(beingFed.getName())){
				LogHelper.showInfo("feedFed", sender, ChatColor.AQUA);
			} else if ((!(sender instanceof Player)) || (((Player) sender).hasPermission("cex.feed.others"))){
				LogHelper.showInfo("feedFedBySomeoneElse#####[" + Nicknames.getNick(sender.getName()), beingFed, ChatColor.AQUA);
				LogHelper.showInfo("feedFedSomeoneElse#####[" + Nicknames.getNick(beingFed.getName()), sender, ChatColor.AQUA);
			} else {
				LogHelper.showInfo("feedOthersNoPerm", sender, ChatColor.RED);
				return true;
			}
		} else {
			Commands.showCommandHelpAndUsage(sender, "cex_feed", alias);
			return true;
		}
		return true;
	}
}
