package com.github.zathrus_writer.commandsex.commands;


import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.Common;
import com.github.zathrus_writer.commandsex.helpers.Permissions;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_playtime extends Common {

	/***
	 * PLAYTIME - tells a player or admin how long they played on the server
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		// check if we have any parameters
		Boolean hasPerms = true;

		// check if someone is not spamming the command too much
		if (sender instanceof Player) {
			if (Utils.checkCommandSpam((Player) sender, "playtime")) {
				return true;
			}
		}
		
		if (args.length == 0) {
			// check permissions and roll it :)
			if (PlayerHelper.checkIsPlayer(sender)) {
				hasPerms = Permissions.checkPerms((Player)sender, "cex.playtime");
			} else {
				return true;
			}
		} else if (args.length == 1) {
			// check if we can see other players' playtimes
			hasPerms = Permissions.checkPerms((Player)sender, "cex.playtime.all");
		} else {
			// show usage
			Commands.showCommandHelpAndUsage(sender, "cex_playtime", alias);
			return true;
		}

		// permissions ok
		if (hasPerms) {
			playtime(sender, args, "cex_playtime", alias);
		}
		
        return true;
	}
}
