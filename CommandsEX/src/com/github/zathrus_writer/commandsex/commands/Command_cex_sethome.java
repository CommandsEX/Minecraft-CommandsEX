package com.github.zathrus_writer.commandsex.commands;


import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Home;
import com.github.zathrus_writer.commandsex.helpers.Permissions;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;

public class Command_cex_sethome extends Home {
	/***
	 * SETHOME - sets home location for a player provided he's played the required amount of time on the server.
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (PlayerHelper.checkIsPlayer(sender)) {
			Player player = (Player)sender;
			if (Permissions.checkPerms(player, "cex.sethome")) {
				setHome(sender, args, "sethome", alias);
			}
		}
        return true;
	}
}
