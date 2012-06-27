package com.github.zathrus_writer.commandsex.commands;


import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.handlers.Handler_nanosuit;
import com.github.zathrus_writer.commandsex.helpers.Common;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Permissions;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_cloak extends Common {
	/***
	 * CLOAK - allows player to become invisible if they have nanosuit equipped
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (PlayerHelper.checkIsPlayer(sender) && Permissions.checkPerms((Player) sender, "cex.nanosuit") && !Utils.checkCommandSpam((Player) sender, "god-cloak")) {
			// check if the player is nano-suited
			String pName = sender.getName();
			if (Handler_nanosuit.suitedPlayers.containsKey(pName)) {
				inv(sender, args, "cloak", "cloak", true);
				LogHelper.showInfo("nanoSuitInvisibleMode", sender);
			} else {
				LogHelper.showInfo("nanoSuitNotActivated", sender, ChatColor.RED);
			}
		}
        return true;
	}
}
