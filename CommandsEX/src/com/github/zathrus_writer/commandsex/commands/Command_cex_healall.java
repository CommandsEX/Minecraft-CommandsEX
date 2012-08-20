package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Nicknames;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_healall {

	/***
	 * Healall - Heals all players on the server.
	 * @author iKeirNez
	 * @param sender
	 * @param args
	 * @return
	 */
	
	public static Boolean run(CommandSender sender, String alias, String[] args){

		if (sender instanceof Player){
			Player player = (Player) sender;
			if (Utils.checkCommandSpam(player, "cex_healall")){
				return true;
			}
		}
		
		if (args.length == 0){
			for (Player p : Bukkit.getOnlinePlayers()){
				p.setHealth(20);
				LogHelper.showInfo("healHealedBySomeoneElse#####[" + Nicknames.getNick(sender.getName()), p, ChatColor.AQUA);
			}
			LogHelper.showInfo("healAllSuccess", sender, ChatColor.GREEN);
		} else {
			Commands.showCommandHelpAndUsage(sender, "cex_healall", alias);
		}
		
		return true;
	}
	
}
