package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.api.afk.Afk;
import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;

/**
 * A simple command to toggle AFK status
 * @author iKeirNez
 */

public class Command_cex_afk {

	public static Boolean run(CommandSender sender, String alias, String[] args){
		if (!PlayerHelper.checkIsPlayer(sender)){
			return true;
		}
		
		Player player = (Player) sender;
		
		if (args.length > 0){
			Commands.showCommandHelpAndUsage(sender, "cex_afk", alias);
			return true;
		}
		
		Afk.toggleAfk(player);
		
		return true;
	}
	
}
