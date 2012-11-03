package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_csudo {

	/**
	 * Csudo - Simple command to send a chat message as another player
	 * @author iKeirNez
	 */
	
	public static Boolean run(CommandSender sender, String alias, String[] args){
		if (args.length < 2){
			Commands.showCommandHelpAndUsage(sender, "cex_csudo", alias);
			return true;
		}
		
		Player target = Bukkit.getPlayer(args[0]);
		
		if (target == null){
			LogHelper.showWarning("invalidPlayer", sender);
			return true;
		}
		
		String message = Utils.collectArgs(args, 1);
		target.chat(message);
		
		return true;
	}
}
