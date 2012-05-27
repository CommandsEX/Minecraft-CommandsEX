package com.github.zathrus_writer.commandsex.commands;


import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Common;
import com.github.zathrus_writer.commandsex.helpers.Permissions;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_inv extends Common {
	/***
	 * INV - allows player to become invisible
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (PlayerHelper.checkIsPlayer(sender) && Permissions.checkPerms((Player) sender, "cex.inv") && !Utils.checkCommandSpam((Player) sender, "inv")) {
			inv(sender, args, "inv", "inv");
		}
        return true;
	}
}
