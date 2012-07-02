package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;
import com.github.zathrus_writer.commandsex.helpers.Time;

public class Command_cex_ptime extends Time {
	/***
	 * Player Time - changes the players time
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (PlayerHelper.checkIsPlayer(sender)) {
			Player player = (Player) sender;
			// do we have any parameters?
			
			if (args.length == 0 || args[0].equalsIgnoreCase("view")){
				LogHelper.showInfo("playerTimeCurrentTime1#####[" + player.getWorld().getTime() + " #####playerTimeCurrentTime2", sender, ChatColor.AQUA);
			} else if (args.length > 1 || args[0].equalsIgnoreCase("reset")) {
				if (player.hasPermission("cex.ptime.set")) {
					setPlayerTime(sender, args);
				} else {
					LogHelper.showInfo("playerTimeSetNoPerm", sender, ChatColor.RED);
				}
			} else {
				// show usage
				Commands.showCommandHelpAndUsage(sender, "cex_ptime", alias);
			}
		}

        return true;
	}
}
