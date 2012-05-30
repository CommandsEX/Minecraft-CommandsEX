package com.github.zathrus_writer.commandsex.commands;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Bans;
import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Permissions;

public class Command_cex_ban extends Bans {
	/***
	 * BAN - bans a player for specific time period, optionally storing a reason
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		// check if we have any parameters
		if (args.length > 0) {
			
			Player beingBanned = Bukkit.getServer().getPlayerExact(args[0]);
			
			// check permissions and roll it :)
			Boolean hasPerms = true;
			Boolean hasBypass = true;
			if (sender instanceof Player) {
				hasPerms = Permissions.checkPerms((Player)sender, "cex.ban");
				hasBypass = Permissions.checkPerms((Player)beingBanned, "cex.bypass.ban");
			}
			
			// permissions ok
			if (hasBypass) {
				LogHelper.showInfo("[" + beingBanned + " #####bansBanBypassMessage", sender, ChatColor.RED);
				return true;
			}
			if (hasPerms) {
				ban(sender, args, "cex_ban", alias);
			}
		} else {
			// show usage
			Commands.showCommandHelpAndUsage(sender, "cex_ban", alias);
		}
        return true;
	}
}
