package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;

public class Command_cex_healall {

	/***
	 * Healall - Heals all players on the server.
	 * @author iKeirNez
	 * @param sender
	 * @param args
	 * @return
	 */
	
	public static Boolean run(CommandSender sender, String alias, String[] args){
		
		String healedBy;
		
		if (sender instanceof Player){
			Player p = (Player) sender;
			healedBy = p.getName();
		} else {
			healedBy = "*CONSOLE*";
		}
		
		if (args.length == 0){
			for (Player p : Bukkit.getOnlinePlayers()){
				p.setHealth(20);
				LogHelper.showInfo("healHealedBySomeoneElse#####[" + healedBy, p, ChatColor.AQUA);
			}
			LogHelper.showInfo("healAllSuccess", sender, ChatColor.GREEN);
		} else {
			Commands.showCommandHelpAndUsage(sender, "cex_healall", alias);
		}
		
		return true;
	}
	
}
