package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.Commands;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;

public class Command_cex_nanosuit {

	public static Boolean run(CommandSender sender, String alias, String[] args){
		
		if (PlayerHelper.checkIsPlayer(sender)){
			if (args.length != 0){
				Commands.showCommandHelpAndUsage(sender, "cex_nanosuit", alias);
				return true;
			}
			
			LogHelper.showInfo("nanoSuitHelp1", sender, ChatColor.AQUA);
			LogHelper.showInfo("nanoSuitHelp2", sender, ChatColor.AQUA);
			LogHelper.showInfo("nanoSuit" + (CommandsEX.getConf().getBoolean("nanoSuitPumpkin") ? "Pumpkin" : "Helmet"), sender, ChatColor.AQUA);
			LogHelper.showInfo("nanoSuitChestplate", sender, ChatColor.AQUA);
			LogHelper.showInfo("nanoSuitLeggings", sender, ChatColor.AQUA);
			LogHelper.showInfo("nanoSuitBoots", sender, ChatColor.AQUA);
		}
		
		return true;
	}
	
}
