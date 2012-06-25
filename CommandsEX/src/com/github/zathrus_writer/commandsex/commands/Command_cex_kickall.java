package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_kickall {

	/***
	 * KICKALL - Kicks all players without permission from the server, with customizable message
	 * @param sender
	 * @param args
	 * @return
	 */
	
	public static Boolean run(CommandSender sender, String alias, String[] args){
		
		if (sender instanceof Player){
			Player player = (Player) sender;
			if (Utils.checkCommandSpam(player, "cex_kickall")){
				return true;
			}
		}
		
		if (!(sender instanceof Player) || sender.hasPermission("cex.kickall")){
			String kickMsg = "kickAllSuccess";
			if (args.length > 0) {
				kickMsg = "kickAllSuccessReason#####[" + " " + kickMsg + Utils.implode(args, " ");
			}
			
			for (Player player : Bukkit.getOnlinePlayers()){
				if (!player.hasPermission("cex.bypass.kick") && player != sender){
					player.kickPlayer(kickMsg);
				}
			}

			LogHelper.showInfo("kickAllSuccessReason#####[" + " " + kickMsg, sender, ChatColor.GREEN);
		}
		
		return true;
	}
	
}
