package com.github.zathrus_writer.commandsex.commands;


import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_suicide {	
	/***
	 * SUICIDE - wrapper for Bukkit's /kill command
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (PlayerHelper.checkIsPlayer(sender) && !Utils.checkCommandSpam((Player) sender, "suicide")) {
			Player player = (Player)sender;
			player.performCommand("kill");
		}
        return true;
	}
}
