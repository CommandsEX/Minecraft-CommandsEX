package com.github.zathrus_writer.commandsex.commands;

import org.bukkit.command.CommandSender;

import com.github.zathrus_writer.commandsex.Vault;
import com.github.zathrus_writer.commandsex.helpers.LogHelper;
import static com.github.zathrus_writer.commandsex.Language._;

public class Command_cex_balance {
	public static Boolean run(CommandSender sender, String alias, String[] args) {
		
		if(Vault.ecoEnabled() != true) {
			 LogHelper.logSevere(_("economyNotFound", ""));
			 LogHelper.showInfo("economyNotFound#####", sender);
		}
		
		String player = sender.getName();
		
		double balance = Vault.econ.getBalance(player);
		
		LogHelper.showInfo("economyBalance#####" + balance, sender);
		
		return true;
	}
}
