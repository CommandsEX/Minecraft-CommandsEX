package com.github.zathrus_writer.commandsex.commands;


import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.handlers.Handler_nanosuit;
import com.github.zathrus_writer.commandsex.helpers.Common;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Permissions;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_armor extends Common {
	/***
	 * ARMOR - allows player to ignore incomming damage if they have nanosuit equipped
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (PlayerHelper.checkIsPlayer(sender) && Permissions.checkPerms((Player) sender, "cex.nanosuit") && !Utils.checkCommandSpam((Player) sender, "god-armor")) {
			// check if the player is nano-suited
			String pName = sender.getName();
			if (Handler_nanosuit.suitedPlayers.containsKey(pName)) {
				Map<String, Object> m = Handler_nanosuit.suitedPlayers.get(pName);
				m.put("godMode", (((m.get("godMode") != null) && m.get("godMode").equals(1)) ? 0 : 1));
				Handler_nanosuit.suitedPlayers.put(pName, m);
				
				god(sender, new String[] {}, "god", alias, true);
				LogHelper.showInfo("nanoSuitGodMode", sender);
			}
		}
        return true;
	}
}
