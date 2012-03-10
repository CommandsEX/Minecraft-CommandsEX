package com.github.zathrus_writer.commandsex.commands;

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
		        		LogHelper.showWarning("tpInvalidArgument", sender);
		        		return false;
		        	}
		        }
		    	
		        if (args.length <= 0) {
		        	// no coordinates
		        	Commands.showCommandHelpAndUsage(sender, "cex_tploc", alias);
		        } else if (args.length != 3) {
		        	// too few or too many arguments
		        	LogHelper.showWarning("tpMissingCoords", sender);
		        	return false;
		        } else if (!args[0].matches(CommandsEX.intRegex) || !args[1].matches(CommandsEX.intRegex) || !args[2].matches(CommandsEX.intRegex)) {
		        	// one of the coordinates is not a number
		        	LogHelper.showWarning("tpCoordsMustBeNumeric", sender);
		        } else {
		        	// all ok here, we can TP the player
		        	try {
		        		player.teleport(new Location(player.getWorld(), new Double(args[0]), new Double(args[1]), new Double(args[2])));
		        	} catch (Throwable e) {
		        		LogHelper.showWarning("internalError", sender);
		        		LogHelper.logSevere("[CommandsEX]: TPLOC returned an unexpected error for player " + player.getName() + ".");
		        		LogHelper.logDebug("Message: " + e.getMessage() + ", cause: " + e.getCause());
		        		return false;
		        	}
		        }
			}
		}
        return true;
	}
}
