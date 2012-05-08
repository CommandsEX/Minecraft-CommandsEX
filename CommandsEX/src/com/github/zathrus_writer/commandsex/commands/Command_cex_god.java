package com.github.zathrus_writer.commandsex.commands;


import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Common;
import com.github.zathrus_writer.commandsex.helpers.Permissions;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_god extends Common {
	/***
	 * GOD - allows player to ignore incomming damage
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (PlayerHelper.checkIsPlayer(sender) && !Utils.checkCommandSpam((Player) sender, "god")) {
			// check if we have any parameters
			if (
					(args.length == 0) && Permissions.checkPerms((Player)sender, "cex.god")
					||
					(args.length > 0) && Permissions.checkPerms((Player)sender, "cex.god.others")
			) {
				god(sender, args, "god", alias);
			}
		}
        return true;
	}
}
