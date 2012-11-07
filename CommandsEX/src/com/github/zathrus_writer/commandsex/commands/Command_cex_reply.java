package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.api.messaging.Messaging;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_reply {

	/**
	 * Reply - Send a message to the last player to message you
	 * @author iKeirNez
	 * @param sender
	 * @param alias
	 * @param args
	 * @return
	 */
	
	public static Boolean run(CommandSender sender, String alias, String[] args){
		
		if (!Command_cex_message.lastMessageFrom.containsKey(sender.getName())){
			LogHelper.showWarning("messageReplyEmpty", sender);
			return true;
		}
		
		Player target = Bukkit.getPlayerExact(Command_cex_message.lastMessageFrom.get(sender.getName()));
		
		if (target == null){
			LogHelper.showWarning("messageReplyOffline", sender);
			return true;
		}

		String message = Utils.collectArgs(args, 0);
		Messaging.sendMessage(sender, target, message, true);
		
		return true;
	}
	
}
