package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;

public class Command_cex_feedall {

	/***
	 * Feedall - Feeds all players on the server
	 * @author iKeirNez
	 * @param sender
	 * @param args
	 * @return
	 */
	
	public static Boolean run(CommandSender sender, String alias, String[] args){
		
		String fedBy;
		
		if (sender instanceof Player){
			Player player = (Player) sender;
			fedBy = player.getName();
		} else {
			fedBy = "*CONSOLE*";
		}
		
		if (args.length == 0){
			for (Player p : Bukkit.getOnlinePlayers()){
				p.setFoodLevel(20);
				LogHelper.showInfo("feedFedBySomeoneElse#####[" + fedBy, p, ChatColor.AQUA);
			}
			LogHelper.showInfo("feedAllSuccess", sender, ChatColor.GREEN);
		} else {
			Commands.showCommandHelpAndUsage(sender, "cex_feedall", alias);
		}
		
		return true;
	}
	
}
