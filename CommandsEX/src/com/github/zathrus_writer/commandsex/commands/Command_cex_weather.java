package com.github.zathrus_writer.commandsex.commands;


import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.Permissions;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;
import com.github.zathrus_writer.commandsex.helpers.Weather;

public class Command_cex_weather extends Weather {
	/***
	 * WEATHER - wrapper for weather effects
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (PlayerHelper.checkIsPlayer(sender)) {
			// do we have any parameters?
			if (args.length > 0) {
				
				// try to determine if we have a timeout value
				Integer timeout = 0;
				
				if (args.length > 1) {
					try {
						timeout = Integer.parseInt(args[1]);
					} catch (Throwable e) {}
				}
				
				// check what we want to do
				if (args[0].equalsIgnoreCase("rain") && Permissions.checkPerms((Player)sender, "cex.weather.rain")) {
					rain(sender, ((timeout > 0) ? new String[] {"" + timeout} : new String[] {}), true);
				}
				
				if ((args[0].equalsIgnoreCase("thunder") || args[0].equalsIgnoreCase("storm")) && Permissions.checkPerms((Player)sender, "cex.weather.storm")) {
					storm(sender, ((timeout > 0) ? new String[] {"" + timeout} : new String[] {}), true);
				}
				
				if ((args[0].equalsIgnoreCase("sun") || args[0].equalsIgnoreCase("sunny")) && Permissions.checkPerms((Player)sender, "cex.weather.rain")) {
					sun(sender, ((timeout > 0) ? new String[] {"" + timeout} : new String[] {}), true);
				}
				
			} else {
				// show usage
				Commands.showCommandHelpAndUsage(sender, "cex_weather", alias);
			}
		}

        return true;
	}
}
