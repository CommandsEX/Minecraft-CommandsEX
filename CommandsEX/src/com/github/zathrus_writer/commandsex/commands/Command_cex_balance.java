package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.command.CommandSender;

public class Command_cex_balance {
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		Double balance = Economy.getBalance(Player sender);
		
		return true;
	}
}
