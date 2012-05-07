package com.github.zathrus_writer.commandsex.commands;


import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Permissions;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;
import com.github.zathrus_writer.commandsex.helpers.Weather;

public class Command_cex_storm extends Weather {
	/***
	 * STORM - toggles storm in the current world
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (PlayerHelper.checkIsPlayer(sender)) {
			if (Permissions.checkPerms((Player)sender, "cex.weather.storm")) {
				storm(sender, args);
			}
		}
        return true;
	}
}
