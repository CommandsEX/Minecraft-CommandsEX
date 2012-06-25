package com.github.zathrus_writer.commandsex.commands;


import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Permissions;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;
import com.github.zathrus_writer.commandsex.helpers.Teleportation;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_tpto extends Teleportation {
	/***
	 * TPTO - teleports sender to another player
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (PlayerHelper.checkIsPlayer(sender)) {
			Player player = (Player)sender;

			if (Utils.checkCommandSpam(player, "cex_calculator")){
				return true;
			}

			// check permissions and call to action
			if (Permissions.checkPerms(player, "OR", "cex.tp", "cex.tpto")) {
				tp_common(sender, args, "tp", alias);
			}
		}
		return true;
	}
}
