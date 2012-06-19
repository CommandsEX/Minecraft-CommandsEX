package com.github.zathrus_writer.commandsex.commands;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.LogHelper;

import static com.github.zathrus_writer.commandsex.Language._;

public class Command_cex_kickall {

	/***
	 * KICKALL - Kicks all players without permission from the server, with customizable message
	 * @param sender
	 * @param args
	 * @return
	 */
	
	public static Boolean run(CommandSender sender, String alias, String[] args){
		
		if (!(sender instanceof Player) || sender.hasPermission("cex.kickall")){
			if (args.length == 0){
				for (Player player : Bukkit.getOnlinePlayers()){
					if (!player.hasPermission("cex.bypass.kick") && player != sender){
						player.kickPlayer(_("kickGenericReason", player.getName()));
					}
				}
				
				LogHelper.showInfo("kickAllSuccess", sender, ChatColor.GREEN);
			} else {
				String kickMessage = StringUtils.join(args, ' ');
				
				for (Player player : Bukkit.getOnlinePlayers()){
					if (!player.hasPermission("cex.bypass.kick") && player != sender){
						player.kickPlayer(kickMessage);
					}
				}
				
				LogHelper.showInfo("kickAllSuccessReason#####[" + " " + kickMessage, sender, ChatColor.GREEN);
			}
		}
		
		return true;
	}
	
}
