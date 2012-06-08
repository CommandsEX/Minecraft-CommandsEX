package com.github.zathrus_writer.commandsex.commands;


import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Permissions;
import com.github.zathrus_writer.commandsex.helpers.Utils;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;

public class Command_cex_ping {
	/***
	 * PING - answers PONG
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		// check if we can ping the server :P
		if ((sender instanceof Player) && (Utils.checkCommandSpam((Player)sender, "ping") || !Permissions.checkPerms((Player) sender, "cex.ping"))) {
			return true;
		}
		LogHelper.showInfo("PONG!", sender);

		return true;
	}
}
