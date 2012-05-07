package com.github.zathrus_writer.commandsex.commands;


import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Permissions;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;
import com.github.zathrus_writer.commandsex.helpers.Weather;

public class Command_cex_rain extends Weather {
	/***
	 * RAIN - toggles rain/snow in the current world
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (PlayerHelper.checkIsPlayer(sender)) {	
			if (Permissions.checkPerms((Player)sender, "cex.weather.rain")) {
				rain(sender, args);
			}
		}
        return true;
	}
}
