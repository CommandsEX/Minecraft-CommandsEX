package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.Teleportation;

public class Command_cex_tpto extends Teleportation {
	/***
	 * TPTO - teleports sender to another player
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (CommandsEX.checkIsPlayer(sender)) {
			Player player = (Player)sender;

			// check permissions and call to action
			if (CommandsEX.checkPerms(player, "OR", "cex.tp", "cex.tpto")) {
				tp_common(sender, args, "tp", alias);
			}
		}
        return true;
	}
}
