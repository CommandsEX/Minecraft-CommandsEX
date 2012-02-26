package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.Teleportation;

public class Command_cex_tphere extends Teleportation {
	/***
	 * TPHERE - teleports another player to the sender
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (CommandsEX.checkIsPlayer(sender)) {
			Player player = (Player)sender;

			// check permissions and call to action
			if (CommandsEX.checkPerms(player, "OR", "cex.tp", "cex.tphere")) {
				tp_common(sender, args, "tphere", alias);
			}
		}
        return true;
	}
}
