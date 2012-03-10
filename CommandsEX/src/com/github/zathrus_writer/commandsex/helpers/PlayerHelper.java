package com.github.zathrus_writer.commandsex.helpers;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerHelper {
	/***
	 * Checks if the CommandSender is a Player, gives the sender error message if he's not and returns Boolean value.
	 * @param cs
	 * @return
	 */
	public static Boolean checkIsPlayer(CommandSender cs) {
		if (cs instanceof Player) {
			return true;
		}

		LogHelper.showWarning("inWorldCommandOnly", cs);
		return false;
	}
}
