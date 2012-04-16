package com.github.zathrus_writer.commandsex.commands;


import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Bans;
import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.Permissions;

public class Command_cex_banip extends Bans {
	/***
	 * BANIP - bans an IP for specific time period, optionally storing a reason
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
				hasPerms = Permissions.checkPerms((Player)sender, "cex.banip");
			}
			
			// permissions ok
			if (hasPerms) {
				banip(sender, args, "cex_banip", alias);
			}
		} else {
			// show usage
			Commands.showCommandHelpAndUsage(sender, "cex_banip", alias);
		}
        return true;
	}
}
