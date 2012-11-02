package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Utils;

/***
 * CMDU - will run a command as another player
 * @author Kezz101
 * @param sender
 * @param alias
 * @param args
 * @return
 */

public class Command_cex_cmdu {
	
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		if (args.length < 2){
			Commands.showCommandHelpAndUsage(sender, "cex_cmdu", alias);
			return true;
		}
		
		Player target = Bukkit.getServer().getPlayer(args[0]);
		// Collects the command and arguments into a string, ready for dispatching
		String cmd = Utils.collectArgs(args, 1);
		
		//Checks
		if(target == null) {
			LogHelper.showWarning("invalidPlayer", sender);
			return true;
		}
		
		if(Bukkit.getPluginCommand(args[1]) == null) {
			LogHelper.showWarning("invalidCommand", sender);
			return true;
		}
		
		//Execute!
		Bukkit.dispatchCommand(target, cmd);
		
		return true;
	}

}
