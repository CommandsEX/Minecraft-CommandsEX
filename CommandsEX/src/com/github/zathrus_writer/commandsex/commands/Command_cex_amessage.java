package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_amessage {

	/**
	 * AMessage - Sends an anonymous message to a player
	 * @author iKeirNez
	 * @param sender
	 * @param alias
	 * @param args
	 * @return
	 */
	
	public static Boolean run(CommandSender sender, String alias, String[] args){
		
		if (args.length < 2){
			Commands.showCommandHelpAndUsage(sender, "cex_amessage", alias);
			return true;
		}
		
		Player target = Bukkit.getPlayer(args[0]);
		
		if (target == null){
			LogHelper.showWarning("invalidPlayer", sender);
			return true;
		}
		
		if (target == sender){
			LogHelper.showWarning("aMessageSelf", sender);
			return true;
		}
		
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i < args.length; i++){
			sb.append(args[i] + " ");
		}
		
		String message = sb.toString();
		target.sendMessage(Utils.replaceChatColors(CommandsEX.getConf().getString("aMessagePrefix")) + message);
		LogHelper.showInfo("aMessageSent", sender);
		
		return true;
	}
	
}
