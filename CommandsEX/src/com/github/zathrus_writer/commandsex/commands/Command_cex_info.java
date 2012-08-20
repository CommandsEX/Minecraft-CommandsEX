package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.Nicknames;
import com.github.zathrus_writer.commandsex.helpers.Utils;

public class Command_cex_info {
	
	/***
	 * INFO - displays text from info.txt
	 * @param sender
	 * @param args
	 * @return
	 */
	
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		
		// Command Variables
		Player player = (Player)sender;
		String[] infos = CommandsEX.getConf().getString("info").replace("{playername}", Nicknames.getNick(player)).split("\\{newline\\}");
		
		for (String info : infos) {
			player.sendMessage(Utils.replaceChatColors(info));
		}
		
		return true;
	}
}
