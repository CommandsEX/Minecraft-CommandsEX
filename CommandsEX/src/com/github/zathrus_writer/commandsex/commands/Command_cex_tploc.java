package com.github.zathrus_writer.commandsex.commands;

import static com.github.zathrus_writer.commandsex.Language._;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Permissions;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;
import com.github.zathrus_writer.commandsex.helpers.Teleportation;

public class Command_cex_tploc extends Teleportation {
	/***
	 * TPLOC - teleports player to given coordinates
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (PlayerHelper.checkIsPlayer(sender)) {
			Player player = (Player)sender;
			// first of all, check permissions
			if (Permissions.checkPerms(player, "cex.tploc")) {
				// alternative usage, all 3 coords separated by comma in 1 argument
		    	if (args.length == 1) {
		        	if (args[0].contains(",")) {
		        		args = args[0].split(",");
		        	} else {
		        		// no commas found in the argument, return error
		        		player.sendMessage(ChatColor.RED + _("tpInvalidArgument", player.getName()));
		        		return false;
		        	}
		        }
		    	
		        if (args.length <= 0) {
		        	// no coordinates
		        	Commands.showCommandHelpAndUsage(sender, "cex_tploc", alias);
		        } else if (args.length != 3) {
		        	// too few or too many arguments
		        	player.sendMessage(ChatColor.RED + _("tpMissingCoords", player.getName()));
		        	return false;
		        } else if (!args[0].matches(CommandsEX.intRegex) || !args[1].matches(CommandsEX.intRegex) || !args[2].matches(CommandsEX.intRegex)) {
		        	// one of the coordinates is not a number
		        	player.sendMessage(ChatColor.RED + _("tpCoordsMustBeNumeric", player.getName()));
		        } else {
		        	// all ok here, we can TP the player
		        	try {
		        		player.teleport(new Location(player.getWorld(), new Double(args[0]), new Double(args[1]), new Double(args[2])));
		        	} catch (Exception e) {
		        		player.sendMessage(ChatColor.RED + _("internalError", player.getName()));
		        		LogHelper.logSevere("[CommandsEX]: TPLOC returned an unexpected error for player " + player.getName() + ".");
		        		LogHelper.logDebug("Message: " + e.getMessage());
		        		return false;
		        	}
		        }
			}
		}
        return true;
	}
}
