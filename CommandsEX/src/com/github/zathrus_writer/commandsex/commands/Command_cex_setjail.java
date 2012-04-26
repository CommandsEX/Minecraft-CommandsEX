package com.github.zathrus_writer.commandsex.commands;


import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Jails;
import com.github.zathrus_writer.commandsex.helpers.Permissions;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;

public class Command_cex_setjail extends Jails {
	/***
	 * SETJAIL - sets a jail location in the current world
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (PlayerHelper.checkIsPlayer(sender)) {
			Player player = (Player)sender;

			// check permissions and call to action
			if (Permissions.checkPerms(player, "cex.setjail")) {
				setjail(sender, args, "setjail", alias);
			}
		}
        return true;
	}
}
