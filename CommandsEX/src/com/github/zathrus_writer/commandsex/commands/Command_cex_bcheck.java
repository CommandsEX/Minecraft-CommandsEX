package com.github.zathrus_writer.commandsex.commands;


import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Bans;
import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.Permissions;

public class Command_cex_bcheck extends Bans {
	/***
	 * BCHECK - checks if the given player is currently banned, for what time and why
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
				hasPerms = Permissions.checkPerms((Player)sender, "cex.bcheck");
			}
			
			// permissions ok
			if (hasPerms) {
				checkban(sender, args, "cex_bcheck", alias);
			}
		} else {
			// show usage
			Commands.showCommandHelpAndUsage(sender, "cex_bcheck", alias);
		}
        return true;
	}
}
