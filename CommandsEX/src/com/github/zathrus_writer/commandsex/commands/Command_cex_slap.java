package com.github.zathrus_writer.commandsex.commands;


import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Common;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Permissions;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_slap extends Common {
	/***
	 * SLAP - slaps a player, sending them into air, optionally using give slap velocity
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (!(sender instanceof Player) || (!Utils.checkCommandSpam((Player) sender, "slap"))) {
			// check if we have any parameters
			if (args.length == 0) {
				LogHelper.showInfo("playerSlapProvideName", sender);
				return true;
			}
			
			if (((sender instanceof Player) && Permissions.checkPerms((Player)sender, "cex.slap")) || !(sender instanceof Player)) {
				slap(sender, args, "slap", alias);
			}
		}
        return true;
	}
}
