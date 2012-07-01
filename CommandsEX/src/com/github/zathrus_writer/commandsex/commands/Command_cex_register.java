package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.command.CommandSender;

import com.github.zathrus_writer.commandsex.CommandsEX;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import com.github.zathrus_writer.commandsex.helpers.PlayerHelper;

public class Command_cex_register {

	public Boolean run(CommandSender sender, String alias, String[] args){
		
		if (PlayerHelper.checkIsPlayer(sender)){
			if (!CommandsEX.sqlEnabled){
				LogHelper.showWarning("authNoDatabase", sender);
				return true;
			} else {

			}
		}
		
		return true;
	}
	
}
