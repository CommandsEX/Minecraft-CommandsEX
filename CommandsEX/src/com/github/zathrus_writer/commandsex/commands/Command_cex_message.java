package com.github.zathrus_writer.commandsex.commands;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.api.messaging.Messaging;
import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_message {

	static HashMap<String, String> lastMessageFrom = new HashMap<String, String>();
	
	/**
	 * Message - Privately Message another Player
	 * @author iKeirNez
	 * @param sender
	 * @param alias
	 * @param args
	 * @return
	 */
	
	public static Boolean run(CommandSender sender, String alias, String[] args){
		if (args.length < 2){
			Commands.showCommandHelpAndUsage(sender, "cex_message", alias);
			return true;
		}
		
		Player target = Bukkit.getPlayer(args[0]);
		
		if (target == null){
			LogHelper.showWarning("invalidPlayer", sender);
			return true;
		}
		
		if (target.equals(sender)){
			LogHelper.showWarning("messageSelf", sender);
			return true;
		}
		
		String message = Utils.collectArgs(args, 1);
		Messaging.sendMessage(sender, target, message, false);
		
		return true;
	}
	
}
