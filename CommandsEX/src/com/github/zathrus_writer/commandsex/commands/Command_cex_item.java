package com.github.zathrus_writer.commandsex.commands;


import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_item {
	/***
	 * ITEM - wrapper for Bukkit's /give <Player>, automatically substituting <Player> with the command sender
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (PlayerHelper.checkIsPlayer(sender)) {
			Player player = (Player)sender;
			
			if (!Utils.checkCommandSpam(player, "item")) {
				player.performCommand("give " + player.getName() + " " + Utils.implode(args, " "));
			}
		}
        return true;
	}
}
