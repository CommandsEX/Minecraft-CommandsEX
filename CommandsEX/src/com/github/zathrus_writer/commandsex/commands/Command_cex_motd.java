package com.github.zathrus_writer.commandsex.commands;


import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.handlers.Handler_motd;
import com.github.zathrus_writer.commandsex.helpers.Permissions;

public class Command_cex_motd extends Handler_motd {
	/***
	 * MOTD - displays message of the day for the player
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		// check permissions and roll it :)
		Boolean hasPerms = true;
		if (sender instanceof Player) {
			hasPerms = Permissions.checkPerms((Player)sender, "cex.motd");
		}
		
		// permissions ok
		if (hasPerms) {
			displayMOTD(sender);
		}
        return true;
	}
}
