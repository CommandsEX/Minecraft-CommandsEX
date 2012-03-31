package com.github.zathrus_writer.commandsex.commands;


import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Chat;
import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.Permissions;

public class Command_cex_mute extends Chat {
	/***
	 * MUTE - mutes a player indefinitelly or for a given time period
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
				hasPerms = Permissions.checkPerms((Player)sender, "cex.mute");
			}
			
			// permissions ok, mute the player
			if (hasPerms) {
				mute(sender, args, "mute", alias);
			}
		} else {
			// show usage
			Commands.showCommandHelpAndUsage(sender, "cex_mute", alias);
		}
        return true;
	}
}
