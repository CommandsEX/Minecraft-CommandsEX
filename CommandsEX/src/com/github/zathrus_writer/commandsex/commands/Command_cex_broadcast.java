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
					String toBroadcast = "";
					if(args[0].equalsIgnoreCase("-t")) { // ahem no == :P
						toBroadcast += Utils.replaceChatColors(args[1]) + Utils.replaceChatColors(Utils.implode(args, " "));
					} else if(args[0].equalsIgnoreCase("-p")) {
						toBroadcast = ChatColor.LIGHT_PURPLE + "[" + player.getName() + "]: " + Utils.replaceChatColors(Utils.implode(args, " "));
					} else {
						toBroadcast = ChatColor.BLUE + "[" + ChatColor.GREEN + "Broadcast" + ChatColor.BLUE +  "]"+ ChatColor.GREEN + ": " + Utils.replaceChatColors(Utils.implode(args, " "));
					}
					Bukkit.getServer().broadcastMessage(toBroadcast);
				}
			} else {
				// show usage
				Commands.showCommandHelpAndUsage(sender, "cex_broadcast", alias);
			}
		}
        return true;
	}
}
