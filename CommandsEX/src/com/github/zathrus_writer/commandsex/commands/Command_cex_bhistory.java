package com.github.zathrus_writer.commandsex.commands;


import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Bans;
import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.Permissions;

public class Command_cex_bhistory extends Bans {
	/***
	 * BHISTORY - displays player's ban history
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
				hasPerms = Permissions.checkPerms((Player)sender, "cex.bhistory");
			}
			
			// permissions ok
			if (hasPerms) {
				showhistory(sender, args, "cex_bhistory", alias);
			}
		} else {
			// show usage
			Commands.showCommandHelpAndUsage(sender, "cex_bhistory", alias);
		}
        return true;
	}
}
