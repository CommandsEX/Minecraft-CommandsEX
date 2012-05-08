package com.github.zathrus_writer.commandsex.commands;


import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.Permissions;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;
import com.github.zathrus_writer.commandsex.helpers.Time;

public class Command_cex_time extends Time {
	/***
	 * TIME - changes time in the current world
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (PlayerHelper.checkIsPlayer(sender)) {
			// do we have any parameters?
			if (args.length > 1) {
				if (Permissions.checkPerms((Player)sender, "cex.time.set")) {
					setTime(sender, args);
				}
			} else {
				// show usage
				Commands.showCommandHelpAndUsage(sender, "cex_time", alias);
			}
		}

        return true;
	}
}
