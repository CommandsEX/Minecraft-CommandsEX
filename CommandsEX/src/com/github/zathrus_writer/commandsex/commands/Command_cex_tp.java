package com.github.zathrus_writer.commandsex.commands;


import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Permissions;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;
import com.github.zathrus_writer.commandsex.helpers.Teleportation;

public class Command_cex_tp extends Teleportation {
	/***
	 * TP - teleports one player to another (either via arguments or the command sender to the player given)
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (PlayerHelper.checkIsPlayer(sender)) {
			Player player = (Player)sender;

			// check permissions and call to action
			if (args.length > 1) {
				// teleporting players one to another
				if (Permissions.checkPerms(player, "cex.tp")) {
					tp_common(sender, args, "tp", alias);
				}
			} else {
				// teleporting sender to another player
				if (Permissions.checkPerms(player, "OR", "cex.tp", "cex.tpto")) {
					tp_common(sender, args, "tp", alias);
				}
			}
		}
        return true;
	}
}
