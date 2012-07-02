package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;
import com.github.zathrus_writer.commandsex.helpers.Time;

public class Command_cex_time extends Time {
	/***
	 * TIME - changes time in the current world
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (PlayerHelper.checkIsPlayer(sender)) {
			Player player = (Player) sender;
			// do we have any parameters?
			
			if (args.length == 0 || args[0].equalsIgnoreCase("view")){
				LogHelper.showInfo("timeCurrentTime1#####[" + player.getWorld().getTime() + " #####timeCurrentTime2", sender, ChatColor.AQUA);
			} else if (args.length > 1) {
				if (player.hasPermission("cex.time.set")) {
					setTime(sender, args);
				} else {
					LogHelper.showInfo("timeSetNoPerm", sender, ChatColor.RED);
				}
			} else {
				// show usage
				Commands.showCommandHelpAndUsage(sender, "cex_time", alias);
			}
		}

        return true;
	}
}
