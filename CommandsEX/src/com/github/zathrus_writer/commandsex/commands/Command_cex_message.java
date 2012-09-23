package com.github.zathrus_writer.commandsex.commands;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.Nicknames;

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
		
		// get all args after the targets name
		StringBuilder sb = new StringBuilder();
		for (int i = 1; i < args.length; i++){
			sb.append(args[i]);
		}
		
		String message = sb.toString();
		target.sendMessage(ChatColor.GRAY + "(" + Nicknames.getNick(sender.getName()) + " -> " + Nicknames.getNick(target.getName()) + ") " + ChatColor.AQUA + message);
		sender.sendMessage(ChatColor.GRAY + "(" + Nicknames.getNick(sender.getName()) + " -> " + Nicknames.getNick(target.getName()) + ") " + ChatColor.AQUA + message);
		
		if (lastMessageFrom.containsKey(target.getName())){
			lastMessageFrom.remove(target.getName());
		}
		
		lastMessageFrom.put(target.getName(), sender.getName());
		return true;
	}
	
}
