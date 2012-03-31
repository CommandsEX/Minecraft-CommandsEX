package com.github.zathrus_writer.commandsex.commands;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Permissions;
import static com.github.zathrus_writer.commandsex.Language._;

public class Command_cex_kick {
	/***
	 * KICK - kicks a player out from the server, optionally providing a custom reason
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		// check if we have any parameters
		if (args.length > 0) {
			// check permissions and roll it :)
			Boolean hasPerms = true;
			if (sender instanceof Player) {
				hasPerms = Permissions.checkPerms((Player)sender, "cex.kick");
			}
			
			// permissions ok, kick them out
			if (hasPerms) {
				// check if requested player is online
				Player p = Bukkit.getServer().getPlayer(args[0]);
				String pName = "";
				String leaveReason = "";
				if (p == null) {
					// requested player not found
					LogHelper.showWarning("invalidPlayer", sender);
					return true;
				} else {
					pName = p.getName();
				}
				
				// if we have more than 1 argument, build up the leave string
				if (args.length > 1) {
					for (Integer i = 1; i < args.length; i++) {
						leaveReason = leaveReason + " " + args[i];
					}
				}
				
				// kick player and tell everyone if set up in the config file
				p.kickPlayer(leaveReason.equals("") ? _("kickGenericReason", "") : leaveReason);
				if (!CommandsEX.getConf().getBoolean("silentKicks")) {
					CommandsEX.plugin.getServer().broadcastMessage(ChatColor.LIGHT_PURPLE + (!leaveReason.equals("") ? (pName + " " + _("kickBeingKickedForMessage", "") + leaveReason) : pName + " " + _("kickBeingKickedMessage", "")));
				}
			}
		} else {
			// show usage
			Commands.showCommandHelpAndUsage(sender, "cex_kick", alias);
		}
        return true;
	}
}
