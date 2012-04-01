package com.github.zathrus_writer.commandsex.commands;


import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.Common;
import com.github.zathrus_writer.commandsex.helpers.Permissions;

public class Command_cex_freeze extends Common {
	/***
	 * FREEZE - freezes a player, making him unable to perform physical actions on the server
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		// check if we have any parameters
		if (args.length > 0) {
			// check permissions and roll it :)
			Boolean hasPerms = true;
			if (sender instanceof Player) {
				hasPerms = Permissions.checkPerms((Player)sender, "cex.freeze");
			}
			
			// permissions ok, mute the player
			if (hasPerms) {
				freeze(sender, args, "freeze", alias);
			}
		} else {
			// show usage
			Commands.showCommandHelpAndUsage(sender, "cex_freeze", alias);
		}
        return true;
	}
}
