package com.github.zathrus_writer.commandsex.commands;


import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Permissions;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;
import com.github.zathrus_writer.commandsex.helpers.Warps;

public class Command_cex_listwarps extends Warps {
	/***
	 * LISTWARPS - list all available (public/private/own) warps on the server
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (PlayerHelper.checkIsPlayer(sender)) {
			Player player = (Player)sender;
			if (Permissions.checkPerms(player, "cex.warp")) {
				list(sender, args, "listwarps", alias);
			}
		}
        return true;
	}
}
