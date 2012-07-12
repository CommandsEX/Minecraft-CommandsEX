package com.github.zathrus_writer.commandsex.commands;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.Permissions;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_broadcast {
	/***
	 * BROADCAST - improved version of /say that will display the message comming from a player to everyone - with his name in the front of line
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (PlayerHelper.checkIsPlayer(sender)) {
			if (args.length > 0) {
				Player player = (Player)sender;
				
				if (!Utils.checkCommandSpam(player, "broadcast") && Permissions.checkPerms(player, "cex.broadcast")) {
					Bukkit.getServer().broadcastMessage(ChatColor.BLUE + "[" + ChatColor.GREEN + "Broadcast" + ChatColor.BLUE +  "]"+ ChatColor.GREEN + ": " + Utils.implode(args, " "));
					//Bukkit.getServer().broadcastMessage(ChatColor.LIGHT_PURPLE + "[" + player.getName() + "]: " + Utils.implode(args, " "));
				}
			} else {
				// show usage
				Commands.showCommandHelpAndUsage(sender, "cex_broadcast", alias);
			}
		}
        return true;
	}
}
