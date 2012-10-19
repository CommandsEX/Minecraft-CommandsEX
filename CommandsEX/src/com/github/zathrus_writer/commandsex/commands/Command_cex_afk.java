package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.command.CommandSender;

import com.github.zathrus_writer.commandsex.helpers.AFK;
import com.github.zathrus_writer.commandsex.helpers.Commands;

/**
 * A simple command to toggle AFK status
 * @author iKeirNez
 */

public class Command_cex_afk extends AFK {

	public static Boolean run(CommandSender sender, String alias, String[] args){
		if (args.length > 0){
			Commands.showCommandHelpAndUsage(sender, "cex_afk", alias);
			return true;
		}
		
		toggleAfk(sender.getName());
		
		return true;
	}
	
}
