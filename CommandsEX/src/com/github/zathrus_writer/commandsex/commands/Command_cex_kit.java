package com.github.zathrus_writer.commandsex.commands;


import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Kits;
import com.github.zathrus_writer.commandsex.helpers.Permissions;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;

public class Command_cex_kit extends Kits {
	/***
	 * KIT - gives player a predefined kit or lists all available kits if no parameters are present
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (args.length == 0) {
			list(sender, args);
		} else if (
				(PlayerHelper.checkIsPlayer(sender) && (args.length == 1) && Permissions.checkPerms((Player)sender, "cex.kits.use"))
				||
				((args.length > 1) && (((sender instanceof Player) && Permissions.checkPerms((Player)sender, "cex.kits.use.others")) || !(sender instanceof Player)) )
		) {
			give(sender, args);
		}
        return true;
	}
}
