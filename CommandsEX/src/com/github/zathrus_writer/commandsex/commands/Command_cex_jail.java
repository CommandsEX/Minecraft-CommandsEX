package com.github.zathrus_writer.commandsex.commands;


import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.Jails;
import com.github.zathrus_writer.commandsex.helpers.Permissions;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;

public class Command_cex_jail extends Jails {
	/***
	 * JAIL - freezes a player, making him unable to perform physical actions on the server and moves him to a jail location
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (PlayerHelper.checkIsPlayer(sender)) {
			Player player = (Player)sender;

			// check permissions and call to action
			if (args.length > 0) {
				if (Permissions.checkPerms(player, "cex.jail")) {
					jail(sender, args, "jail", alias);
				}
			} else {
				// show usage
				Commands.showCommandHelpAndUsage(sender, "cex_jail", alias);
			}
		}
        return true;
	}
}
