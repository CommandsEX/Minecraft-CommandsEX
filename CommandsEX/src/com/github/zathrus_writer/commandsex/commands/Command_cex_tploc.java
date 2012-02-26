package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.Teleportation;

public class Command_cex_tploc extends Teleportation {
	/***
	 * TPLOC - teleports player to given coordinates
	 * @param sender
	 * @param args
	 * @return
	 */
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		LOGGER.info("tploc started");
		if (CommandsEX.checkIsPlayer(sender)) {
			Player player = (Player)sender;
			LOGGER.info("tploc 1");
			// first of all, check permissions
			if (CommandsEX.checkPerms(player)) {
				LOGGER.info("tploc 2");
				// alternative usage, all 3 coords separated by comma in 1 argument
		    	if (args.length == 1) {
		        	if (args[0].contains(",")) {
		        		args = args[0].split(",");
		        	} else {
		        		// no commas found in the argument, return error
		        		player.sendMessage(ChatColor.RED + langTpInvalidArgument);
		        		return false;
		        	}
		        }
		    	
		        if (args.length <= 0) {
		        	LOGGER.info("tploc 3");
		        	// no coordinates
		        	CommandsEX.showCommandHelpAndUsage(sender, "cex_tploc", alias);
		        } else if (args.length != 3) {
		        	LOGGER.info("tploc 4");
		        	// too few or too many arguments
		        	player.sendMessage(ChatColor.RED + langTpMissingCoords);
		        	return false;
		        } else if (!args[0].matches(CommandsEX.intRegex) || !args[1].matches(CommandsEX.intRegex) || !args[2].matches(CommandsEX.intRegex)) {
		        	LOGGER.info("tploc 5");
		        	// one of the coordinates is not a number
		        	player.sendMessage(ChatColor.RED + langTpCoordsMustBeNumeric);
		        } else {
		        	// all ok here, we can TP the player
		        	try {
		        		LOGGER.info("tploc 6");
		        		player.teleport(new Location(player.getWorld(), new Double(args[0]), new Double(args[1]), new Double(args[2])));
		        	} catch (Exception e) {
		        		LOGGER.info("tploc 7");
		        		player.sendMessage(ChatColor.RED + CommandsEX.langInternalError);
		        		LOGGER.severe("["+ CommandsEX.pdfFile.getName() +"]: TPLOC returned an unexpected error for player " + player.getName() + ". Error message: " + e.getMessage());
		        		return false;
		        	}
		        }
			}
		}
		LOGGER.info("tploc 8");
        return true;
	}
}
