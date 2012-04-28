package com.github.zathrus_writer.commandsex.commands;


import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Permissions;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;
import com.github.zathrus_writer.commandsex.helpers.Promotions;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_time2rank extends Promotions {

	/***
	 * TIME2RANK - shows how much time has a player left until he'll be auto-promoted to a higher rank
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (PlayerHelper.checkIsPlayer(sender) && !Utils.checkCommandSpam((Player) sender, "time2rank") && Permissions.checkPerms((Player)sender, "cex.time2rank")) {
			time2rank(sender, args, "cex_time2rank", alias);
		}
        return true;
	}
}
