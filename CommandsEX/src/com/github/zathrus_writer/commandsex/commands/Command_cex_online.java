package com.github.zathrus_writer.commandsex.commands;


import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Permissions;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_online {
	
	/***
	 * ONLINE - displays list of all players that are currently online
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		Boolean hasPerms = true;
		if (sender instanceof Player) {
			hasPerms = Permissions.checkPerms((Player)sender, "cex.online");
		}
		
		// permissions ok, list players
		if (hasPerms) {
			if (args.length == 0) {
				// list all players
				int playersOnline = CommandsEX.plugin.getServer().getOnlinePlayers().length;
				String[] players = new String[playersOnline];
				for(int i = 0; i < playersOnline; i++){
				    players[i] = CommandsEX.plugin.getServer().getOnlinePlayers()[i].getName();
				}

				LogHelper.showInfo("onlinePlayers#####[" + Utils.implode(players, ","), sender);
			} else {
				// check if a given player is online
				Player p = Bukkit.getServer().getPlayer(args[0]);
				if (p == null) {
					LogHelper.showInfo("[" + args[0] + " #####isOffline", sender);
				} else {
					LogHelper.showInfo("[" + p.getName() + " #####isOnline", sender);
				}
			}
		}

        return true;
	}
}
