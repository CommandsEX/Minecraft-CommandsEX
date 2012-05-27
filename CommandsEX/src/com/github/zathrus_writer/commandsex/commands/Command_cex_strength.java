package com.github.zathrus_writer.commandsex.commands;


import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.handlers.Handler_nanosuit;
import com.github.zathrus_writer.commandsex.helpers.Common;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Permissions;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_strength extends Common {
	/***
	 * STRENGTH - multiplies player's hit damage when NanoSuit is activated
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (PlayerHelper.checkIsPlayer(sender) && Permissions.checkPerms((Player) sender, "cex.nanosuit") && !Utils.checkCommandSpam((Player) sender, "strength-armor")) {
			// check if the player is nano-suited
			String pName = sender.getName();
			if (Handler_nanosuit.suitedPlayers.containsKey(pName)) {
				if (Handler_nanosuit.powered.contains(pName)) {
					Handler_nanosuit.powered.remove(pName);
				} else {
					Handler_nanosuit.powered.add(pName);
				}
				LogHelper.showInfo("nanoSuitStrengthMode", sender);
			}
		}
        return true;
	}
}
