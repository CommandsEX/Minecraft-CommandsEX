package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Commands;

public class Command_cex_kill {
	
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if(args.length == 1) {
			Player player = Bukkit.getServer().getPlayer(args[0]);
			player.setHealth(0);
			sender.sendMessage(ChatColor.AQUA + "You have killed " + player);
			player.sendMessage(ChatColor.AQUA + "You have been killed by " + sender);
		} else {
			Commands.showCommandHelpAndUsage(sender, "cex_kill", alias);
		}
		return true;
	}
}
