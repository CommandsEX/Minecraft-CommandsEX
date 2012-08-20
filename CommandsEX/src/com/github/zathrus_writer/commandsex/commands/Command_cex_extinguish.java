package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Nicknames;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_extinguish {
	
	/***
	 * Extinguish - Allows a player (or console) to extinguish themself or another player.
	 * @author iKeirNez
	 * @param sender
	 * @param args
	 * @return
	 */

	public static Boolean run(CommandSender sender, String alias, String[] args){
		
		if (sender instanceof Player){
			Player player = (Player) sender;
			if (Utils.checkCommandSpam(player, "cex_extinguish")){
				return true;
			}
		}

		if (args.length == 0){
			if (sender instanceof Player){
				Player player = (Player) sender;
				player.setFireTicks(0);
				LogHelper.showInfo("extExtinguished", player, ChatColor.GREEN);
			} else {
				LogHelper.showInfo("playerNameMissing", sender, ChatColor.RED);
			}
		} else if (args.length == 1){
			Player toExt = Bukkit.getPlayer(args[0]);
			if (toExt != null){
				// Prevents the player from recieving 2 messages if they do /ext <their-player-name>
				if (toExt != sender){
					if ((!(sender instanceof Player)) || ((Player) sender).hasPermission("cex.extinguish.others")){
						toExt.setFireTicks(0);
						LogHelper.showInfo("extExtinguishedBySomeoneElse#####[ " + Nicknames.getNick(sender.getName()), toExt, ChatColor.GREEN);
						LogHelper.showInfo("extExtinguishedSomeoneElse#####[ " + Nicknames.getNick(toExt.getName()), sender, ChatColor.GREEN);
					} else {
						LogHelper.showInfo("extOtherNoPerm", sender, ChatColor.RED);
					}
				} else {
					toExt.setFireTicks(0);
					LogHelper.showInfo("extExtinguished", sender, ChatColor.GREEN);
				}
			} else {
				LogHelper.showInfo("invalidPlayer", sender, ChatColor.RED);
			}
		} else {
			LogHelper.showInfo("incorrectUsage", sender, ChatColor.RED);
		}

		return true;
	}
}
