package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.LogHelper;

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
		//Basic setup
		SimpleCommandMap scm = new SimpleCommandMap(sender.getServer());
		Player target = Bukkit.getServer().getPlayer(args[0]);
		Command cmd = scm.getCommand(args[1]);
		String[] cmdArgs = {};
		int i = 0;
		
		for(String s : args) {
			if(!(s.equals(target.getName()) || s.equals(cmd.getLabel()))) {
				cmdArgs[i] = s;
				i++;
			}
		}
		
		//Checks
		if(target == null) {
			LogHelper.showWarning("invalidPlayer", sender);
			return true;
		}
		if(cmd == null) {
			LogHelper.showWarning("invalidCommand", sender);
			return true;
		}
		
		//Execute!
		cmd.execute((CommandSender)target, cmd.getLabel(), cmdArgs);
		
		return true;
	}

}
